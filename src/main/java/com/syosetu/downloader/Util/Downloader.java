package com.syosetu.downloader.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.WString;
import com.syosetu.downloader.Entity.Novel;
import com.syosetu.downloader.Entity.NovelDetail;
import com.syosetu.downloader.EzTransXP.EzTransXP;
import com.syosetu.downloader.Repository.NovelDetailRepository;
import com.syosetu.downloader.Repository.NovelRepository;
import com.syosetu.downloader.RestApi.RequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service
public class Downloader 
{
    @Autowired
    private RequestWrapper requestWrapper;

    public long delay = 50;

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private NovelDetailRepository novelDetailRepository;

    @Autowired
    private EzTransXP ezTransXP;

    public void getNovel(String ncode) throws Exception
    {
        String response = "";
        ResponseEntity<String> responseEntity = null;
        try
        {
            responseEntity = requestWrapper.getCustomRestTemplate().getForEntity("https://ncode.syosetu.com/novelview/infotop/ncode/" + ncode + "/", String.class);
            response = responseEntity.getBody();
        }
        catch(final HttpClientErrorException e)
        {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
            {
                Novel novel = Novel.builder()
                    .ncode(ncode)
                    .build();
                novelRepository.delete(novel);
                throw new Exception("");
            }
        }

        Document body = Jsoup.parse(response);

        String title = body.selectFirst("#contents_main > h1 > a").text().trim();
        String summary  = body.selectFirst("#noveltable1 > tbody > tr:nth-child(1) > td").html().replace("<br>", "\r\n").trim();
        String genre = body.selectFirst("#noveltable1 > tbody > tr:nth-child(4) > td") != null ? body.selectFirst("#noveltable1 > tbody > tr:nth-child(4) > td").text().trim() : "";
        String keyword = body.selectFirst("#noveltable1 > tbody > tr:nth-child(3) > td") != null ? body.selectFirst("#noveltable1 > tbody > tr:nth-child(3) > td").text().trim() : "";

        Novel novel = novelRepository.findByNcode(ncode);
        novel.setNcode(ncode);
        novel.setTitle(title);
        novel.setTitleKr(Translation(title));
        novel.setSummary(summary);
        novel.setSummaryKr(Translation(summary));
        novel.setGenre(genre);
        novel.setGenreKr(Translation(genre));
        novel.setKeyword(keyword);
        novel.setKeywordKr(Translation(keyword));
        novel.setDownloadYn("N");
        novelRepository.save(novel);

        Elements episodeList = Jsoup.parse(requestWrapper.getCustomRestTemplate().getForObject("https://ncode.syosetu.com/" + ncode + "/", String.class)).select(".novel_sublist2 .subtitle");

        int cnt = 1;
        for (Element episode : episodeList)
        {
            if(novelDetailRepository.findByNcodeAndEpisode(ncode, cnt) != null) 
            {
                cnt++;
                continue;
            }
            Thread.sleep(delay);

            String writeDateTime = episode.parent().selectFirst(".long_update").text().trim();

            Map<String, String> map = getNovelDetail(ncode, cnt);

            System.out.println(cnt + "/" + episodeList.size());

            NovelDetail novelDetail = NovelDetail.builder()
                                .ncode(ncode)
                                .episode(cnt)
                                .subTitle(episode.text())
                                .subTitleKr(Translation(episode.text()))
                                .content(map.get("sList"))
                                .contentKr(map.get("tList"))
                                .writeDateTime(writeDateTime)
                                .build();
            novelDetailRepository.save(novelDetail);
            cnt++;
        }
    }

    public Map<String, String> getNovelDetail(String ncode, int i) throws Exception
    {
        Map<String, String> map = new HashMap<String, String>();

        String response = requestWrapper.getCustomRestTemplate().getForObject("https://ncode.syosetu.com/" + ncode + "/" + i, String.class);
        Document body = Jsoup.parse(response);

        List<String> sList = new ArrayList<String>();
        List<String> tList = new ArrayList<String>();
        
        for(Element p : body.select("#novel_honbun p"))
        {
            sList.add(p.text());
            tList.add(Translation(p.text()));
        }
        map.put("sList", String.join("\r\n", sList));
        map.put("tList", String.join("\r\n", tList));

        return map;
    }

    public String Translation(String s) throws Exception
    {
        return ezTransXP.J2K_TranslateMMNTW(0, new WString(s)).toString();
    }
}