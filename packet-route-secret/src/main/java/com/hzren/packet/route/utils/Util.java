package com.hzren.packet.route.utils;

import com.hzren.packet.route.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.joou.UShort;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzren
 * 
 * */
public class Util
{
	public static int netBytes2Int(byte[] data)
	{
		if (data.length != 4)
		{
			throw new IllegalArgumentException("the parm data lenght should be 4");
		}
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.BIG_ENDIAN);
		return buffer.asIntBuffer().get(0);
	}

	/**
	 * the input byte array length should be 4
	 * 
	 * @param header
	 * 
	 * */
	public static int getRadiusPacketLength(byte[] header)
	{
		if (header.length != 4)
		{
			throw new IllegalArgumentException("array length shoud be 4");
		}
		ByteBuffer buffer = ByteBuffer.wrap(header);
		return UShort.valueOf(buffer.asShortBuffer().get(1)).intValue();
	}

	public static List<ByteBuf> wrapMsg(ByteBuf src, int secretKey){
		int step = Config.MAX_FRAME_LENGTH;

		List<ByteBuf> list = new ArrayList<>();
		while (src.readableBytes() > step){
			ByteBuf wrapMsg = ByteBufAllocator.DEFAULT.buffer(step + 8);
			wrapMsg.writeInt(step + 4).writeInt(secretKey).writeBytes(src.readBytes(step));
			list.add(wrapMsg);
		}

		ByteBuf wrapMsg = ByteBufAllocator.DEFAULT.buffer(src.readableBytes() + 8);
		wrapMsg.writeInt(src.readableBytes() + 4).writeInt(secretKey).writeBytes(src);
		list.add(wrapMsg);

		return list;
	}

	public static void writeMsgs(NioSocketChannel channel, List<ByteBuf> msgs){
		for (ByteBuf msg : msgs) {
			channel.write(msg);
		}
		channel.flush();
	}

	public static ByteBuf unwrapMsg(ByteBuf msg){
		int index = msg.readInt();
		return msg;
	}
}
