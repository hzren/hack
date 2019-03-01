package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class BackendServer implements Config {

    public static final NioEventLoopGroup worker = new NioEventLoopGroup(2);

    private ServerBootstrap bootstrap;

    public void startProxyServer(){
        this.bootstrap = startServer("ProxyChannelServer", Config.SPS_PORT, new ProxyChannelInitializer());
    }

    private ServerBootstrap startServer(String type, int port, ChannelInitializer<NioSocketChannel> initializer){
        log.info("Starting " + type);
        worker.setIoRatio(100);
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.WRITE_SPIN_COUNT, 10);
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 1024 * 1024);
            bootstrap.group(worker, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.WARN))
                    .childHandler(initializer);
            log.info("Open " + type + " port : " + port);
            bootstrap.bind(port).sync();
            log.info("Front " + type + " channel on port : " + port);
            return bootstrap;
        } catch (Exception e)
        {
            log.error("Failed to start " + type + " command channel.", e);
            throw new RuntimeException("Failed to start " + type + " command channel.", e);
        }
    }

    public void close(){
        bootstrap.group().shutdownGracefully();
    }

}
