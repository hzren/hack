package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.ExceptionHandler;
import com.hzren.packet.route.base.HeartBeatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

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

        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 30));
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("proxyMessageHandler", new ProxyMessageHandler(id));
        pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
        pipeline.addLast("exceptionHandler", new ExceptionHandler(id));
    }
}
