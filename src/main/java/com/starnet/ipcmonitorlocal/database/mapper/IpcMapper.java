package com.starnet.ipcmonitorlocal.database.mapper;

import com.starnet.ipcmonitorlocal.database.model.IpcEntity;

import java.util.List;

/**
 * IpcMapper
 *
 * @author wzz
 * @date 2020/8/27 21:09
 **/
public interface IpcMapper {

    IpcEntity selectById(int id);

    List<IpcEntity> selectAll();

    void insert(IpcEntity ipcEntity);

    void update(IpcEntity ipcEntity);
}
