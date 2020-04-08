package com.syosetu.downloader.Repository;

import java.util.List;

import com.syosetu.downloader.Entity.NovelDetail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelDetailRepository extends JpaRepository<NovelDetail, Long>
{
    NovelDetail findByNcodeAndEpisode(String ncode, int episode);
    List<NovelDetail> findByNcodeOrderByEpisodeAsc(String ncode);
}