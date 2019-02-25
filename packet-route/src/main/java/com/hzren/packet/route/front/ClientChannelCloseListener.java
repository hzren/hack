package com.hzren.packet.route.front;

import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.VirtualChannel;
import com.hzren.packet.route.utils.Util;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/4.
 */
@Slf4j
class ClientChannelCloseListener implements ChannelFutureListener {

    private final int index;

    public ClientChannelCloseListener(int index){
        this.index = index;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Client链接关闭...index:" + index);
        VirtualChannel channel = FrontServerChannelHolder.clientChannelMap.remove(index);
        if (channel != null){
            log.error("向Backend发送关闭命令,index:" + index);
            BackendChannnelManager.commandMsg.add(new ByteBufMsg(Util.getCloseMsg(index), null));
            return;
        }
        log.info("Client channel Map 里不包含:" + index + "忽略该消息!");
    }
}
