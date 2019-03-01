package com.hzren.packet.route.base;

import com.hzren.packet.route.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2019/2/27.
 */
@Slf4j
public class SecretMsgEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("编码消息,length:" + buf.readableBytes());
        int sKey = Config.SECRET_KEY;
        if (buf.readableBytes() == 0){
            sKey = 0;
        }
        ByteBuf out = ctx.alloc().buffer(buf.readableBytes() + 8);
        out.writeInt(buf.readableBytes() + 4).writeInt(sKey).writeBytes(buf);
        buf.release();
        super.write(ctx, out, promise);
    }
}
