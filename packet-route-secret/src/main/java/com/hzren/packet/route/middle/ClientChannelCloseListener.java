package com.hzren.packet.route.middle;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/4.
 */
@Slf4j
public class ClientChannelCloseListener implements ChannelFutureListener {

    private final int index;

    public ClientChannelCloseListener(int index){
        this.index = index;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Client Channel关闭...index:" + index);
        MiddleServerChannelHolder.clientChannelMap.remove(index);
        NioSocketChannel proxyChannel = MiddleServerChannelHolder.remoteChannelMap.remove(index);
        if (proxyChannel != null){
            proxyChannel.close();
        }
    }
}
