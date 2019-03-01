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
public class Util {

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
		List<ByteBuf> list = new ArrayList<>();
		while (src.readableBytes() > Config.MAX_FRAME_LENGTH){
			ByteBuf wrapMsg = ByteBufAllocator.DEFAULT.buffer(Config.MAX_PACKET_LENGTH);
			wrapMsg.writeInt(Config.MAX_FRAME_LENGTH + 4).writeInt(secretKey).writeBytes(src.readBytes(Config.MAX_FRAME_LENGTH));
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

	public static ByteBuf negative(ByteBuf src, ByteBufAllocator allocator){
		ByteBuf dest = allocator.buffer(src.readableBytes());
		while (src.readableBytes() != 0){
			dest.writeByte(-1 - src.readByte());
		}
		src.release();

		return dest;
	}

	public static void main(String[] args) {
		byte b = 38;
		byte b1 = -1 -38;
		System.out.println(b);
		System.out.println(b1);
		ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1);
		buf.writeByte(b);


		int size = 256;
		ByteBuf bb = ByteBufAllocator.DEFAULT.buffer(size);
		for (int i = 0; i < size; i++){
			bb.writeByte(i);
		}
		System.out.println();
		System.out.println("----以下为原始----------");
		for (int i = 0; i < size; i++){
			System.out.print(bb.getByte(i) + ",");
		}
		System.out.println();
		System.out.println("----以下为取负数----------");
		bb = negative(bb, ByteBufAllocator.DEFAULT);
		for (int i = 0; i < size; i++){
			System.out.print(bb.getByte(i) + ",");
		}
		System.out.println();
		System.out.println("----以下为取负数的负数回到原始----------");
		bb = negative(bb, ByteBufAllocator.DEFAULT);
		for (int i = 0; i < size; i++){
			System.out.print(bb.getByte(i) + ",");
		}
	}


}
