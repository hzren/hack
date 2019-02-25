package com.hzren.packet.route.front;

import com.hzren.packet.route.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        ByteBuf buf = (ByteBuf) msg;
        int secretKey = buf.readInt();
        if (secretKey == 0){
            log.info("收到心跳包,index:" + index);
            return;
        }
        if (secretKey != Config.SECRET_KEY){
            ctx.channel().close();
            return;
        }
        if (buf.readableBytes() == 0){
            log.info("收到心跳包,index:" + index);
            return;
        }
        log.info("转发来自Proxy的消息,index:" + index + ",长度:" + buf.readableBytes());
        FrontServerChannelHolder.clientChannelMap.get(index).writeAndFlush(buf);
    }

}
