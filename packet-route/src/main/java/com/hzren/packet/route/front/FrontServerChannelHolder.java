package com.hzren.packet.route.front;

import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.VirtualChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
@Slf4j
class FrontServerChannelHolder {
    private static AtomicInteger ID_GEN = new AtomicInteger();
    static ConcurrentHashMap<Integer, VirtualChannel> clientChannelMap = new ConcurrentHashMap<>();

    static int putClientChannel(NioSocketChannel socketChannel){
        int id = ID_GEN.incrementAndGet();
        clientChannelMap.put(id, new VirtualChannel(id, socketChannel, new ConcurrentLinkedQueue<>()));
        return id;
    }

    static void addToBackendMsg(int index, List<ByteBufMsg> msgs){
        VirtualChannel channel = clientChannelMap.get(index);
        for (ByteBufMsg msg : msgs) {
            channel.byteBufMsgs.add(msg);
        }
    }

}
