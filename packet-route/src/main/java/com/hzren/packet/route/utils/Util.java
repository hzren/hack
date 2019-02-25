package com.hzren.packet.route.utils;

import com.hzren.packet.route.Config;
import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.VirtualChannel;
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

	public static List<ByteBufMsg> formatRadiusMsg(ByteBuf src, int index, ByteBufAllocator allocator){
		int size = src.readableBytes();
		int step = Config.MAX_FRAME_LENGTH;
		ArrayList<ByteBufMsg> list = new ArrayList<>();

		while (size > step){
			ByteBuf res = allocator.buffer(step + 8);
			res.writeInt(step + 4).writeInt(index).writeBytes(src.readBytes(step));
			size = src.readableBytes();

			list.add(new ByteBufMsg(res, null));
		}
		ByteBuf res = allocator.buffer(size + 8);
		res.writeInt(size + 4).writeInt(index).writeBytes(src);
		list.add(new ByteBufMsg(res, null));
		return list;
	}

	public static ByteBuf getCloseMsg(int index){
		ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(4 + 4);
		//关闭操作传个index的复数过去
		return buf.writeInt(4).writeInt(0 - index);
	}
}
