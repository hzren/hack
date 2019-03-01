package com.hzren.packet.route.middle;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import com.hzren.packet.route.base.SecretMsgDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        int id = MiddleServerChannelHolder.putClientChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
        ch.closeFuture().addListener(new ClientChannelCloseListener(id));
        //建立代理通道
        MiddleServerChannelHolder.newProxyChannel(id);
        //out
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        //in
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("secretMsgDecoder", new SecretMsgDecoder(id));
        pipeline.addLast("clientMessageHandler", new ClientMessageHandler(id));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(id));
    }
}
