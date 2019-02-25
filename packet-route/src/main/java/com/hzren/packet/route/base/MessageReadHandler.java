package com.hzren.packet.route.base;

import com.hzren.packet.route.utils.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * this handler read radius packet and store in {@ByteBuffer}, after read complete, it will fire channelRead event with parm type
 * ArrayList<ByteBuffer>
 * 
 * @author hzren
 * 
 * */
public class MessageReadHandler extends SimpleChannelInboundHandler<ByteBuf>
{
	private static final Logger	LOGGER						= LoggerFactory.getLogger(MessageReadHandler.class);
	public static final int		READ_STAGE_MAX_PACKETS		= 16;
	public static final short	MESSAGE_HEADER_LENGTH		= 4;
	public static final short	RADIUS_PACKET_MAXLEN		= 4096;
	public static final short	RADIUS_PACKET_MIN_LENGTH	= 20;
	private static final int	LENGTH_DEFAULT_VAL			= -1;

	private int					length						= LENGTH_DEFAULT_VAL;
	private ByteBuf				packet;

	private boolean readBody(ByteBuf buf, ArrayList<ByteBuffer> out)
	{
		int readable = buf.readableBytes();
		int readed = packet.readableBytes();
		if (readable + readed < length)
		{
			packet.writeBytes(buf);
			return false;
		} else
		{
			packet.writeBytes(buf, length - readed);
			byte[] bytes = new byte[length];
			packet.readBytes(bytes);
			packet.clear();
			out.add(ByteBuffer.wrap(bytes));
			length = -1;
			return true;
		}
	}

	private boolean readLengthHeader(ChannelHandlerContext ctx, ByteBuf buf) throws IOException
	{
		int readable = buf.readableBytes();
		int readed = packet.readableBytes();
		if (readable + readed >= MESSAGE_HEADER_LENGTH)
		{
			packet.writeBytes(buf, MESSAGE_HEADER_LENGTH - readed);
			byte[] header_bytes = new byte[MESSAGE_HEADER_LENGTH];
			packet.getBytes(0, header_bytes);
			int length = Util.getRadiusPacketLength(header_bytes);
			if (length > RADIUS_PACKET_MAXLEN || length < RADIUS_PACKET_MIN_LENGTH)
			{
				String msg = "invalid RADIUS request, size : " + length;
				LOGGER.error(msg);
				throw new IOException(msg);
			}
			this.length = length;
			return true;
		} else
		{
			packet.writeBytes(buf);
			return false;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		LOGGER.error("exception caught", cause);
		ctx.channel().close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		packet.release();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		packet = ctx.alloc().buffer(RADIUS_PACKET_MAXLEN);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
	{
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception
	{
		ByteBuf buf = msg;
		ArrayList<ByteBuffer> results = new ArrayList<ByteBuffer>(2);
		if (length == -1)
		{
			while (readLengthHeader(ctx, buf) && readBody(buf, results))
				;
		} else
		{
			while (readBody(buf, results) && readLengthHeader(ctx, buf))
				;
		}
		if (!results.isEmpty())
		{
			ctx.fireChannelRead(results);
		}
	}
}
