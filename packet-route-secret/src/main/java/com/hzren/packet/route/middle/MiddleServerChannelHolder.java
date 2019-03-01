package com.hzren.packet.route.middle;

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
public class MiddleServerChannelHolder {
    private static AtomicInteger ID_GEN = new AtomicInteger();
    public static NioEventLoopGroup GROUP = new NioEventLoopGroup(2);

    public static ConcurrentHashMap<Integer, NioSocketChannel> remoteChannelMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, NioSocketChannel> clientChannelMap = new ConcurrentHashMap<>();


    public static int putClientChannel(NioSocketChannel socketChannel){
        int id = ID_GEN.incrementAndGet();
        clientChannelMap.put(id, socketChannel);
        return id;
    }

    public static void newProxyChannel(int id){
        log.info("创建一条Target链接");
        Bootstrap bootstrap = new Bootstrap().group(GROUP)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .channel(NioSocketChannel.class)
                .handler(new RemoteChannelInitializer(id));

        try {
            ChannelFuture future = bootstrap
                    .connect(Config.SPS_IP, Config.SPS_PORT).sync();
            future.awaitUninterruptibly();
            if (future.isDone() && future.isSuccess()){
                NioSocketChannel channel = (NioSocketChannel) future.channel();
                remoteChannelMap.put(id, channel);
                return;
            }
            log.error("链接失败");
            throw new RuntimeException("Connect target server fail");
        } catch (Exception e) {
            log.error("新建Target通道失败", e);
            throw new RuntimeException(e);
        }
    }

}
