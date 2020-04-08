package com.syosetu.downloader.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.syosetu.downloader.PK.NovelDetailPK;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@IdClass(NovelDetailPK.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NovelDetail 
{
    @Id
    private String ncode;
    @Id
    private int episode;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String subTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String subTitleKr;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentKr;

    @Column
    private String writeDateTime;

    @CreatedDate
    private LocalDateTime insertDateTime;

    @LastModifiedDate
    private LocalDateTime updateDateTime;
}