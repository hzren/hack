package com.hzren.packet.route.front;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.utils.Util;
import com.sun.jndi.toolkit.url.Uri;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
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
        log.info("转发来自Client的消息,index:" + index + ",长度:" + buf.readableBytes());
        Util.writeMsgs(FrontServerChannelHolder.proxyChannelMap.get(index), Util.wrapMsg(buf, Config.SECRET_KEY));
    }
}
