package com.starnet.ipcmonitorlocal.database.model;

import lombok.Data;

/**
 * IpcEntity
 *
 * @author wzz
 * @date 2020/8/27 21:07
 **/
@Data
public class IpcEntity {
    private Integer id;
    private String name;
    private String company;
    private String account;
    private String password;
    private String rtspAddr;
    private String description;
}
