package com.hzren.packet.route.middle;

import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import com.hzren.packet.route.base.HeartBeatHandler;
import com.hzren.packet.route.base.SecretMsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

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
        ch.closeFuture().addListener(new RemoteChannelCloseListener(index));
        //out
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        //in
        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 30));
        //out
        pipeline.addLast("secretMsgEncoder", new SecretMsgEncoder());
        //in
        pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
        pipeline.addLast("remoteMessageHandler", new RemoteMessageHandler(index));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(index));
    }
}
