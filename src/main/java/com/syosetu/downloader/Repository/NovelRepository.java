package com.syosetu.downloader.Repository;

import java.util.List;

import com.syosetu.downloader.Entity.Novel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel, Long>
{
    Novel findByNcode(String ncode);
    List<Novel> findByDownloadYn(String downloadYn);
}