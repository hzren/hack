package com.hzren.packet.route.front;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.*;
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
        //out
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 30));
        pipeline.addLast("secretMsgEncoder", new SecretMsgEncoder());
        //in
        pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
        //in
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("secretMsgDecoder", new SecretMsgDecoder(index));
        pipeline.addLast("proxyMessageHandler", new ProxyMessageHandler(index));

        pipeline.addLast("exceptionHandler", new ExceptionHandler(index));
    }
}
