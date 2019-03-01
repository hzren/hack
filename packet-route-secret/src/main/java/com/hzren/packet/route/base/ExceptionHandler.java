package com.hzren.packet.route.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2019/2/25.
 */
@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    private final int index;

    public ExceptionHandler(int index){
        this.index = index;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        String localAddress = ctx.channel().localAddress().toString();
        log.error("Channel未知异常, index:" + index + ",localAddress:" + localAddress + ",remoteAddress:" + remoteAddress, cause);
        ctx.channel().close();
    }
}
