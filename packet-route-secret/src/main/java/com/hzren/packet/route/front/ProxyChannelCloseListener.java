package com.hzren.packet.route.front;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/4.
 */
@Slf4j
public class ProxyChannelCloseListener implements ChannelFutureListener {

    private final int index;

    public ProxyChannelCloseListener(int index){
        this.index = index;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Proxy Channel关闭...");
        NioSocketChannel channel = FrontServerChannelHolder.clientChannelMap.remove(index);
        if (channel != null){
            channel.close();
        }
    }
}
