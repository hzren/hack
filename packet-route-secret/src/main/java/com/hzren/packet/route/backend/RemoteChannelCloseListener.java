package com.hzren.packet.route.backend;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/4.
 */
@Slf4j
public class RemoteChannelCloseListener implements ChannelFutureListener {

    private final int index;

    public RemoteChannelCloseListener(int index){
        this.index = index;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Remote Channel链接关闭...index:" + index);
        BackendServerChannelHolder.remoteChannelMap.remove(index);
        NioSocketChannel channel = BackendServerChannelHolder.proxyChannelMap.remove(index);
        if (channel != null){
            channel.close();
        }
    }
}
