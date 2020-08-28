package com.starnet.ipcmonitorlocal.web.controller;

import com.starnet.ipcmonitorlocal.database.model.IpcEntity;
import com.starnet.ipcmonitorlocal.service.IpcService;
import com.starnet.ipcmonitorlocal.web.model.HttpResponse;
import com.starnet.ipcmonitorlocal.web.model.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IpcController
 *
 * @author wzz
 * @date 2020/8/28 9:31
 **/
@Slf4j
@RestController
public class IpcController {

    @Autowired
    private IpcService ipcService;

    @GetMapping("/ipc/list")
    public HttpResponse<List<IpcEntity>> listAll() {
        List<IpcEntity> ipcEntities = ipcService.findAll();
        if (null == ipcEntities) {
            return new HttpResponse<>(HttpStatus.SERVER_ERROR);
        }
        return new HttpResponse<>(HttpStatus.OK.getCode(), HttpStatus.OK.getMessage(), ipcEntities);
    }

    @GetMapping("/ipc/{id}")
    public HttpResponse<IpcEntity> get(@PathVariable int id) {
        IpcEntity ipcEntity = ipcService.findById(id);
        if (null == ipcEntity) {
            return new HttpResponse<>(HttpStatus.SERVER_ERROR);
        }
        return new HttpResponse<>(HttpStatus.OK.getCode(), HttpStatus.OK.getMessage(), ipcEntity);
    }

    @PostMapping("/ipc/create")
    public HttpResponse create(@RequestBody IpcEntity ipcEntity) {
        if (null == ipcEntity) {
            return new HttpResponse(HttpStatus.REQUEST_PARAMS_ERROR);
        }
        ipcService.create(ipcEntity);
        return new HttpResponse(HttpStatus.OK);
    }

    @PostMapping("/ipc/update")
    public HttpResponse update(@RequestBody IpcEntity ipcEntity) {
        if (null == ipcEntity || null == ipcEntity.getId()) {
            return new HttpResponse(HttpStatus.REQUEST_PARAMS_ERROR);
        }
        ipcService.update(ipcEntity);
        return new HttpResponse(HttpStatus.OK);
    }
}
