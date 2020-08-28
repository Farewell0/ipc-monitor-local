package com.starnet.ipcmonitorlocal.service.impl;

import com.starnet.ipcmonitorlocal.database.mapper.IpcMapper;
import com.starnet.ipcmonitorlocal.database.model.IpcEntity;
import com.starnet.ipcmonitorlocal.service.IpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IpcServiceImpl
 *
 * @author wzz
 * @date 2020/8/27 21:10
 **/
@Slf4j
@Service
public class IpcServiceImpl implements IpcService {
    @Autowired
    private IpcMapper ipcMapper;

    @Override
    public IpcEntity findById(int id) {
        return ipcMapper.selectById(id);
    }

    @Override
    public List<IpcEntity> findAll() {
        return ipcMapper.selectAll();
    }

    @Override
    public void create(IpcEntity ipcEntity) {
        ipcMapper.insert(ipcEntity);
    }

    @Override
    public void update(IpcEntity ipcEntity) {
        ipcMapper.update(ipcEntity);
    }
}
