package com.syosetu.downloader.PK;

import java.io.Serializable;

import lombok.Data;

@Data
public class NovelDetailPK implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String ncode;
    private int episode;        
}