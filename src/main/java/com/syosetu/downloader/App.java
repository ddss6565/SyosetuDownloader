package com.syosetu.downloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.syosetu.downloader.Entity.Novel;
import com.syosetu.downloader.Repository.NovelRepository;
import com.syosetu.downloader.Util.Downloader;
import com.syosetu.downloader.VO.DownloadQueueVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class App implements CommandLineRunner
{
	private final long delay = 500;

	@Autowired
	private Downloader downloader;

	@Autowired
	private NovelRepository novelRepository;

	@Autowired
	private DownloadScheduling downloadScheduling;

	public static void main(String... args) 
	{
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws IOException
	{
		List<Novel> novelList = novelRepository.findByDownloadYn("N");
		List<String> failList = new ArrayList<String>();
		try
		{
			int cnt = 0;

			for(Novel novel : novelList)
			{
				try
				{
					Thread.sleep(delay); 
					System.out.println(novel.getNcode() + " : " + cnt++ + "/" + novelList.size());
					downloader.getNovel(novel.getNcode());
					downloadScheduling.downloadList.add(DownloadQueueVO.builder().ncode(novel.getNcode()).status("N").build());
				}
				catch(Exception e)
				{
					try
					{
						e.printStackTrace();
						failList.add(novel.getNcode());
						downloader.delay += 15;
						System.out.println(downloader.delay);
					}
					catch(Exception e1)
					{

					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Files.write(Paths.get("./failLog.txt"), (String.join("\r\n", failList)).getBytes());
		}
		downloadScheduling.exit();
	}

}
