package com.hzren.packet.route.backend;

import com.hzren.packet.route.utils.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
class TargetMessageHandler extends ChannelInboundHandlerAdapter {

    private final int index;

    public TargetMessageHandler(int index){
        this.index = index;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("转发来自Client的消息,index:" + index + ",长度:" + buf.readableBytes());
        BackendServerChannelHolder.addToBackendMsg(index, Util.formatRadiusMsg(buf, index, ByteBufAllocator.DEFAULT));
    }
}
