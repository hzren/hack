package com.hzren.packet.route.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;


@Sharable
public class ByteBufTrimHandler extends ChannelOutboundHandlerAdapter
{
	private static final Logger	log	= LoggerFactory.getLogger(ByteBufTrimHandler.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
	{
		if (msg instanceof ByteBuf)
		{
			ByteBuf cast = (ByteBuf) msg;
			int length = cast.readableBytes();
			if (length != cast.capacity())
			{
				ByteBuf buf = ctx.alloc().buffer(length);
				buf.writeBytes(cast);
				cast.release();
				super.write(ctx, buf, promise);
			} else
			{
				super.write(ctx, msg, promise);
			}
		} else
		{
			throw new IllegalArgumentException("msg should be Bytebuf");
		}
	}
}
