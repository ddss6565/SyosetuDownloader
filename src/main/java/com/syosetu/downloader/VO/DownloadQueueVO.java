package com.syosetu.downloader.VO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadQueueVO 
{
    private String ncode;
    private String status;
}