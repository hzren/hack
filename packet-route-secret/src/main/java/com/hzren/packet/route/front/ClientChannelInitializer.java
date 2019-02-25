package com.hzren.packet.route.front;

import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        int id = FrontServerChannelHolder.putClientChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
        ch.closeFuture().addListener(new ClientChannelCloseListener(id));
        //建立代理通道
        FrontServerChannelHolder.newProxyChannel(id);
        //规整消息
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        //路由消息
        pipeline.addLast("clientMessageHandler", new ClientMessageHandler(id));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(id));
    }
}
