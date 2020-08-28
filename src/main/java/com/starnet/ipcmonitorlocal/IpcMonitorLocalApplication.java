package com.starnet.ipcmonitorlocal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.starnet.ipcmonitorlocal.database.mapper")
@SpringBootApplication
public class IpcMonitorLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpcMonitorLocalApplication.class, args);
    }
}
