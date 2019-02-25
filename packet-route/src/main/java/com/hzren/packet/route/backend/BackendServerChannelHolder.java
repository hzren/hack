package com.hzren.packet.route.backend;

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
class BackendServerChannelHolder {
    private static AtomicInteger ID_GEN = new AtomicInteger();
    static ConcurrentHashMap<Integer, VirtualChannel> targetChannelMap = new ConcurrentHashMap<>();

    static int putClientChannel(NioSocketChannel socketChannel){
        int id = ID_GEN.incrementAndGet();
        targetChannelMap.put(id, new VirtualChannel(id, socketChannel, new ConcurrentLinkedQueue<>()));
        return id;
    }

    static void addToBackendMsg(int index, List<ByteBufMsg> msgs){
        VirtualChannel channel = targetChannelMap.get(index);
        for (ByteBufMsg msg : msgs) {
            channel.byteBufMsgs.add(msg);
        }
    }

}
