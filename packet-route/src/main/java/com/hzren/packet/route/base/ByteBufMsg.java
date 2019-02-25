package com.hzren.packet.route.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;

/**
 * @author tuomasi
 * Created on 2019/2/22.
 */
@AllArgsConstructor
public class ByteBufMsg {
    public final ByteBuf msg;
    public ChannelFuture future;
}
