package com.syosetu.downloader.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Novel 
{
    @Id
    private String ncode;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String titleKr;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String summaryKr;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String genre;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String genreKr;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String keyword;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String keywordKr;

    @Column(nullable = false)
    private String downloadYn;

    @Column(nullable = true)
    private String grade;
}