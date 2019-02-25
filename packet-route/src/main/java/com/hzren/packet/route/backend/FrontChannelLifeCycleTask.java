package com.hzren.packet.route.backend;

import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.ProxyChannel;
import com.hzren.packet.route.base.VirtualChannel;
import com.hzren.packet.route.utils.Util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author tuomasi
 * Created on 2019/2/22.
 */
public class FrontChannelLifeCycleTask implements Runnable {

    @Override
    public void run() {
        processChannelMsg();
        processCommandMsg();
    }

    private void processChannelMsg(){
        for (VirtualChannel value : BackendServerChannelHolder.targetChannelMap.values()) {
            ConcurrentLinkedQueue<ByteBufMsg> queue = value.byteBufMsgs;
            ByteBufMsg next = queue.peek();
            if (next == null){
                continue;
            }
            if (next.future == null){
                int index = value.index % BackendChannelManager.proxyChannels.length;
                ProxyChannel channel = BackendChannelManager.proxyChannels[index];
                if (channel != null){
                    next.future = value.channel.writeAndFlush(next.msg);
                }
                continue;
            }else if (next.future.isSuccess()){
                queue.remove();
            }else if (next.future.cause() != null){
                BackendServerChannelHolder.targetChannelMap.remove(value.index);
                value.channel.close();
                BackendChannelManager.commandMsg.add(new ByteBufMsg(Util.getCloseMsg(value.index), null));
            }else {
                continue;
            }
        }
    }

    private void processCommandMsg(){
        for (;;) {
            ByteBufMsg msg = BackendChannelManager.commandMsg.peek();
            if (msg == null){
                return;
            }
            if (msg.future == null){
                for (int i = 0; i < BackendChannelManager.proxyChannels.length; i++){
                    ProxyChannel channel = BackendChannelManager.proxyChannels[i];
                    if (channel != null){
                        msg.future = channel.channel.writeAndFlush(msg.msg);
                    }else if (msg.future.isSuccess()){
                        BackendChannelManager.commandMsg.remove();
                    }else if (channel.future.cause() != null){
                        msg.future = null;
                    }
                    return;
                }
            }
        }
    }
}
