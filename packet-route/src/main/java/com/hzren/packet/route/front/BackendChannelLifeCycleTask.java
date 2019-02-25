package com.hzren.packet.route.front;

import com.hzren.packet.route.base.ByteBufMsg;
import com.hzren.packet.route.base.ProxyChannel;
import com.hzren.packet.route.base.VirtualChannel;
import com.hzren.packet.route.utils.Util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author tuomasi
 * Created on 2019/2/22.
 */
public class BackendChannelLifeCycleTask implements Runnable {

    @Override
    public void run() {
        checkBackendChannelNum();
        processChannelMsg();
        processCommandMsg();
    }

    private void checkBackendChannelNum(){
        for (int i = 0; i < BackendChannnelManager.proxyChannels.length; i++) {
            if (BackendChannnelManager.proxyChannels[i] == null){
                BackendChannnelManager.startChannel();
            }
        }
    }

    private void processChannelMsg(){
        for (VirtualChannel value : FrontServerChannelHolder.clientChannelMap.values()) {
            ConcurrentLinkedQueue<ByteBufMsg> queue = value.byteBufMsgs;
            ByteBufMsg next = queue.peek();
            if (next == null){
                continue;
            }
            if (next.future == null){
                int index = value.index % BackendChannnelManager.proxyChannels.length;
                ProxyChannel channel = BackendChannnelManager.proxyChannels[index];
                if (channel != null){
                    next.future = value.channel.writeAndFlush(next.msg);
                }
                continue;
            }else if (next.future.isSuccess()){
                queue.remove();
            }else if (next.future.cause() != null){
                FrontServerChannelHolder.clientChannelMap.remove(value.index);
                value.channel.close();
                BackendChannnelManager.commandMsg.add(new ByteBufMsg(Util.getCloseMsg(value.index), null));
            }else {
                continue;
            }
        }
    }

    private void processCommandMsg(){
        for (;;) {
            ByteBufMsg msg = BackendChannnelManager.commandMsg.peek();
            if (msg == null){
                return;
            }
            if (msg.future == null){
                for (int i = 0; i < BackendChannnelManager.proxyChannels.length; i++){
                    ProxyChannel channel = BackendChannnelManager.proxyChannels[i];
                    if (channel != null){
                        msg.future = channel.channel.writeAndFlush(msg.msg);
                    }else if (msg.future.isSuccess()){
                        BackendChannnelManager.commandMsg.remove();
                    }else if (channel.future.cause() != null){
                        msg.future = null;
                    }
                    return;
                }
            }
        }
    }
}
