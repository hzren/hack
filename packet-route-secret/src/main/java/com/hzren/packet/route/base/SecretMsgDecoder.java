package com.hzren.packet.route.base;

import com.hzren.packet.route.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tuomasi
 * Created on 2019/2/27.
 */
@Slf4j
public class SecretMsgDecoder extends ChannelInboundHandlerAdapter {

    private final int index;

    public SecretMsgDecoder(int index){
        this.index = index;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        int secretKey = buf.readInt();
        if (secretKey == 0){
            log.info("收到心跳包,index:" + index);
            buf.release();
            return;
        }
        if (secretKey != Config.SECRET_KEY){
            String local = ctx.channel().remoteAddress().toString();
            String remote = ctx.channel().localAddress().toString();
            log.error("Secret key不匹配,关闭连接,index:" + index + ",local:" + local + ",remote:" + remote);
            buf.release();
            ctx.channel().close();
            return;
        }
        log.info("获得加密后原始数据,index:" + index + ",length:" + buf.readableBytes());
        super.channelRead(ctx, buf);
    }
}
