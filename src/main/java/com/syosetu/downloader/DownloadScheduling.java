package com.syosetu.downloader;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.syosetu.downloader.Entity.Novel;
import com.syosetu.downloader.Entity.NovelDetail;
import com.syosetu.downloader.Repository.NovelDetailRepository;
import com.syosetu.downloader.Repository.NovelRepository;
import com.syosetu.downloader.VO.DownloadQueueVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DownloadScheduling 
{
    public List<DownloadQueueVO> downloadList = new CopyOnWriteArrayList<DownloadQueueVO>();

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private NovelDetailRepository novelDetailRepository;

    @Scheduled(fixedDelay = 1000)
    public void run() throws IOException
    {
        Iterator<DownloadQueueVO> iter = downloadList.iterator();
        while(iter.hasNext())
        {
            DownloadQueueVO downloadQueueVO = iter.next();
            String ncode = downloadQueueVO.getNcode();
            
            Novel novel = novelRepository.findByNcode(ncode);
            if(novel != null && "N".equals(novel.getDownloadYn()))
            {
                download(ncode);
            }
        }
    }

    public void exit()
    {
        while(true)
        {
            long cnt = downloadList.stream()
                .filter(s -> "N".equals(s.getStatus()))
                .count();
            if(cnt==0)
            {
                System.exit(-1);
            }
        }
    }

    public void download(String ncode) throws IOException
    {
        Novel novel = novelRepository.findByNcode(ncode);

        Path filePath = Paths.get("./" + "novel" + "/" + novel.getGenreKr() + "/" + ncode + "_" + sanitizeFilename(novel.getTitleKr()) + ".txt");
        Files.createDirectories(filePath.getParent());

        Charset charset = Charset.forName("UTF-8");
        
        try(FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
        {
            fileChannel.write(charset.encode(novel.getTitleKr()));
            fileChannel.write(charset.encode("\r\n\r\n\r\n"));
            fileChannel.write(charset.encode(novel.getSummaryKr()));
            fileChannel.write(charset.encode("\r\n\r\n\r\n"));

            List<NovelDetail> list = novelDetailRepository.findByNcodeOrderByEpisodeAsc(ncode);
            for(NovelDetail novelDetail : list)
            {
                fileChannel.write(charset.encode(novelDetail.getSubTitleKr() + "\r\n"));
                fileChannel.write(charset.encode(novelDetail.getWriteDateTime() + "\r\n" + "\r\n"));
                fileChannel.write(charset.encode(novelDetail.getContentKr() + "\r\n" + "\r\n"));
            }
        }

        novel.setDownloadYn("Y");
        novelRepository.save(novel);
    }

    public static String sanitizeFilename(String name)
    {
        return name.replaceAll("[:\\\\/*?|<>]", "");
    }
}