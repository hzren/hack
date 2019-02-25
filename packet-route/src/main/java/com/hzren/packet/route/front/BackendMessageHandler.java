package com.hzren.packet.route.front;

import com.hzren.packet.route.base.VirtualChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
class BackendMessageHandler extends MessageToMessageDecoder<ByteBuf> {

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return true;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int index = msg.readInt();
        log.info("收到来自Backend的消息,index:" + index + ",长度:" + msg.readableBytes());
        if (index == 0){
            log.info("收到来自Backend的心跳包!");
            return;
        }
        if (index > 0){
            NioSocketChannel channel = FrontServerChannelHolder.clientChannelMap.get(index).channel;
            if (channel != null){
                ByteBuf res = channel.alloc().buffer(msg.readableBytes());
                res.writeBytes(msg);
                channel.writeAndFlush(res);
            }
        }else {
            index = 0 - index;
            log.info("Backend关闭连接,同步关闭客户端连接!index:"+ index);
            VirtualChannel vc = FrontServerChannelHolder.clientChannelMap.remove(index);
            if (vc != null){
                vc.channel.close();
            }
        }

    }
}
