package com.starnet.ipcmonitorlocal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FFmpegProperties
 *
 * @author wzz
 * @date 2020/8/19 14:29
 **/
@Data
@Component
@ConfigurationProperties(prefix = "ffmpeg")
public class FFmpegProperties {

    private String windowsFfmpegPath;
    private String windowsFfProbePath;
    private String acodec;
    private String vcodec;
    private String logLevel;
    private String outputFormat;
    private Integer pushStreamNum;
    private Integer tryPullStreamNum;

    private String pushAddrPrefix;
}
