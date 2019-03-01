package com.hzren.packet.route.middle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    public static final AtomicInteger ID = new AtomicInteger(0);

    private final int index;

    public ClientMessageHandler(int index){
        this.index = index;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("Front-Proxy的消息,index:" + index + ",长度:" + buf.readableBytes());
        MiddleServerChannelHolder.remoteChannelMap.get(index).writeAndFlush(buf);
    }
}
