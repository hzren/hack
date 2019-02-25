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
public class ProxyChannelCloseListener implements ChannelFutureListener {

    private final int index;

    public ProxyChannelCloseListener(int index){
        this.index = index;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Proxy Channel链接关闭...index:" + index);
        BackendServerChannelHolder.proxyChannelMap.remove(index);
        NioSocketChannel remoteChannel = BackendServerChannelHolder.remoteChannelMap.remove(index);
        if (remoteChannel != null){
            remoteChannel.close();
        }
    }
}
