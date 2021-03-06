package com.hzren.packet.route.backend;

import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public class RemoteChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final int index;

    public RemoteChannelInitializer(int index){
        this.index = index;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //out
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());

        //in
        pipeline.addLast("remoteMessageHandler", new RemoteMessageHandler(index));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(index));
    }
}
