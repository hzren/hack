package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.ProxyChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author tuomasi
 * Created on 2019/2/22.
 */
@Slf4j
class BackendChannelManager {
    static ProxyChannel[] proxyChannels = new ProxyChannel[20];
    static ConcurrentLinkedQueue<ByteBufMsg> commandMsg = new ConcurrentLinkedQueue<>();

    static NioEventLoopGroup worker = new NioEventLoopGroup(1);

    static {
        worker.scheduleAtFixedRate(new FrontChannelLifeCycleTask(), 100, 100, TimeUnit.MILLISECONDS);
    }

    static void startChannel(){
        Bootstrap bootstrap = new Bootstrap()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new FrontChannelInitializer());
        try {
            ChannelFuture future = bootstrap.connect(Config.BACKEND_SERVER_IP, Config.BACKEND_SERVER_PORT)
                    .sync();
            log.info("成功连上Backend Server...");
        } catch (Exception e) {
            log.error("链接服务Backend server失败", e);
        }
    }

}
