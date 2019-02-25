package com.hzren.packet.route.backend;

import com.hzren.packet.route.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
public class ProxyMessageHandler extends ChannelInboundHandlerAdapter {

    private final int index;

    public ProxyMessageHandler(int index){
        this.index = index;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf body = (ByteBuf) msg;
        int secretKey = body.readInt();
        if (secretKey == 0){
            log.info("收到心跳包,index:" + index);
            return;
        }
        if (secretKey != Config.SECRET_KEY){
            log.error("secret key不匹配,关闭连接,index:" + index + ",ip:" + ctx.channel().remoteAddress().toString());
            body.release();
            ctx.channel().close();
        }
        log.info("转发来自Proxy的消息,index:" + index + ",长度:" + body.readableBytes());
        BackendServerChannelHolder.remoteChannelMap.get(index).writeAndFlush(msg);
    }
}
