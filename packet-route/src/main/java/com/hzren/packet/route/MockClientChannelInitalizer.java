package com.hzren.packet.route;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

import java.util.concurrent.TimeUnit;

/**
 * @author tuomasi
 * Created on 2018/12/5.
 */
public class MockClientChannelInitalizer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ByteBuf msg = ch.alloc().buffer(1024);
        msg.writeBytes("Hello, world, clow now".getBytes());
        ChannelFuture future = ch.writeAndFlush(msg);
        ch.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                ch.close();
            }
        }, 5, TimeUnit.SECONDS);
    }
}
