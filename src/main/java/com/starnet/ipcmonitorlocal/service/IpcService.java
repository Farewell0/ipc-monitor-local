package com.starnet.ipcmonitorlocal.service;

import com.starnet.ipcmonitorlocal.database.model.IpcEntity;

import java.util.List;

/**
 * IpcService
 *
 * @author wzz
 * @date 2020/8/27 21:06
 **/
public interface IpcService {
    IpcEntity findById(int id);

    List<IpcEntity> findAll();

    void create(IpcEntity ipcEntity);

    void update(IpcEntity ipcEntity);
}
