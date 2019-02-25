package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class BackendServerChannelHolder {
    private static AtomicInteger ID_GEN = new AtomicInteger();

    public static ConcurrentHashMap<Integer, NioSocketChannel> proxyChannelMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, NioSocketChannel> remoteChannelMap = new ConcurrentHashMap<>();
    public static NioEventLoopGroup GROUP = new NioEventLoopGroup(1);

    public static int putProxyChannel(NioSocketChannel socketChannel){
        int id = ID_GEN.incrementAndGet();
        proxyChannelMap.put(id, socketChannel);
        return id;
    }

    public static void newRemoteChannel(int index){
        log.info("新创建一条Target链接:" + index);
        Bootstrap bootstrap = new Bootstrap().group(GROUP)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .channel(NioSocketChannel.class)
                .handler(new RemoteChannelInitializer(index));

        try {
            ChannelFuture future = bootstrap
                    .handler(new RemoteChannelInitializer(index))
                    .connect(Config.SS_IP, Config.SS_PORT).sync();
            future.awaitUninterruptibly();
            if (future.isDone() && future.isSuccess()){
                NioSocketChannel channel = (NioSocketChannel) future.channel();
                remoteChannelMap.put(index, channel);
                return;
            }
            log.error("链接失败");
            throw new RuntimeException("Connect target server fail");
        } catch (Exception e) {
            log.error("新建Target通道失败:" + index, e);
            throw new RuntimeException(e);
        }
    }

}
