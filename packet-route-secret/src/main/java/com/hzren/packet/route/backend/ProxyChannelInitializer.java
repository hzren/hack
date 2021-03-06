package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import com.hzren.packet.route.base.SecretMsgDecoder;
import com.hzren.packet.route.base.SecretMsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public class ProxyChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        int id = BackendServerChannelHolder.putProxyChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
        ch.closeFuture().addListener(new ProxyChannelCloseListener(id));
        //代理通道
        BackendServerChannelHolder.newRemoteChannel(id);
        //out
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("secretMsgEncoder", new SecretMsgEncoder());
        //in
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("secretMsgDecoder", new SecretMsgDecoder(id));
        pipeline.addLast("proxyMessageHandler", new ProxyMessageHandler(id));
        pipeline.addLast("exceptionHandler", new ExceptionHandler(id));
    }
}
