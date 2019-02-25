package com.hzren.packet.route.front;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufTrimHandler;
import com.hzren.packet.route.base.HeartBeatHandler;
import com.hzren.packet.route.base.ProxyChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
class BackendChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        boolean need = false;
        for (int i = 0; i < BackendChannnelManager.proxyChannels.length; i++){
            if (BackendChannnelManager.proxyChannels[i] == null){
                BackendChannnelManager.proxyChannels[i] = new ProxyChannel(ch, null);
                need = true;
                break;
            }
        }
        if (!need){
            ch.close();
            return;
        }
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 10, 30));
        pipeline.addLast("trimHandler", new ByteBufTrimHandler());
        pipeline.addLast("messageDecodeHandler", new LengthFieldBasedFrameDecoder(Config.MAX_PACKET_LENGTH, 0, 4, 0,4 ));
        pipeline.addLast("backendMessageHandler", new BackendMessageHandler());
        pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
    }
}
