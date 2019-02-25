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

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        for (int i = 0; i < 30; i++) {
            try {
                Bootstrap bootstrap = new Bootstrap().group(group)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                        .channel(NioSocketChannel.class)
                        .handler(new MockClientChannelInitalizer());
                ChannelFuture future = bootstrap.connect("47.96.172.5", 8080);
                future.sync();

                future.awaitUninterruptibly();
                Thread.sleep(3000L);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
