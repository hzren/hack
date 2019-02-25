package com.hzren.packet.route.base;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;

/**
 * @author tuomasi
 * Created on 2019/2/23.
 */
@AllArgsConstructor
public class ProxyChannel {
    public final NioSocketChannel channel;
    public ChannelFuture future;
}
