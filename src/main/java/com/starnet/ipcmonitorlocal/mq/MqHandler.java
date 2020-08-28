package com.starnet.ipcmonitorlocal.mq;

import com.starnet.ipcmonitorlocal.mq.req.MqRequest;
import com.starnet.ipcmonitorlocal.mq.resp.MqResponse;

/**
 * MqHandler
 *
 * @author wzz
 * @date 2020/8/21 16:06
 **/
public interface MqHandler {

    /**
     * MQHandler的具体实现类的命令
     * @return Command
     */
    Command getCommand();

    /**
     * MqHandler的具体实现类的处理方法
     * @param request 请求命令
     * @param message 请求数据
     * @return MqResponse
     */
    MqResponse process(MqRequest request, String message);

    enum Command {

        /**
         * 推流命令
         */
        START_MONITOR(1),
        /**
         * 停止推流命令
         */
        STOP_MONITOR(2),
        /**
         * 获取ipc列表
         */
        GET_IPC_LIST(3);

        private int type;

        Command(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Command{ [" + this.name() +
                    "] type=" + type +
                    '}';
        }
    }

}
