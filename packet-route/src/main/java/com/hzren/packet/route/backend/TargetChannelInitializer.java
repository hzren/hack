package com.hzren.packet.route.backend;

import com.hzren.packet.route.base.ByteBufTrimHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
class TargetChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        int id = BackendServerChannelHolder.putClientChannel(ch);

        ChannelPipeline pipeline = ch.pipeline();
        ch.closeFuture().addListener(new TargetChannelCloseListener(id));
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("clientMessageHandler", new TargetMessageHandler(id));
    }
}
