package com.hzren.packet.route.front;

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
class FrontServer implements Config {

    private ServerBootstrap clientServerSb;

    public void startClientServer(){
        this.clientServerSb = startServer("Client", Config.PORT_FOR_CLIENT, 2, new ClientChannelInitializer());
    }

    private ServerBootstrap startServer(String type, int port, int threadNum, ChannelInitializer<NioSocketChannel> initializer){
        log.info("Starting " + type + ", workerThread num : " + threadNum);
        NioEventLoopGroup worker = new NioEventLoopGroup(threadNum);
        worker.setIoRatio(100);
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.WRITE_SPIN_COUNT, 3);
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024);
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
        clientServerSb.group().shutdownGracefully();
    }

}
