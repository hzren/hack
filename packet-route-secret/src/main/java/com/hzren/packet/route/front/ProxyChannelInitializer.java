package com.hzren.packet.route.front;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import com.hzren.packet.route.base.HeartBeatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public class ProxyChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final int index;

    public ProxyChannelInitializer(int index){
        this.index = index;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ch.closeFuture().addListener(new ProxyChannelCloseListener(index));

        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 30));
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("proxyMessageHandler", new ProxyMessageHandler(index));
        pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
        pipeline.addLast("exceptionHandler", new ExceptionHandler(index));
    }
}
