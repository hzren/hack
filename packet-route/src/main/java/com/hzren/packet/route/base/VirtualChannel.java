package com.hzren.packet.route.base;

import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author tuomasi
 * Created on 2019/2/22.
 */
@AllArgsConstructor
public class VirtualChannel {
    public final Integer index;
    public final NioSocketChannel channel;
    public final ConcurrentLinkedQueue<ByteBufMsg> byteBufMsgs;
}
