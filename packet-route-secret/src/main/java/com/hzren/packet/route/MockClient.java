package com.hzren.packet.route;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author tuomasi
 * Created on 2018/12/4.
 */
public class MockClient {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap().group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .channel(NioSocketChannel.class)
                .handler(new MockClientChannelInitalizer());
        ChannelFuture future = bootstrap.connect("127.0.0.1", Config.CS_PORT);
        future.sync();

        future.awaitUninterruptibly();
        Thread.sleep(300000L);
    }
}
