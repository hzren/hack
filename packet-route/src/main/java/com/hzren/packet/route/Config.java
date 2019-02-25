package com.hzren.packet.route;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public interface Config {

    int PORT_FOR_CLIENT = 59990;

    String BACKEND_SERVER_IP = "144.168.62.7";
    int BACKEND_SERVER_PORT = 59991;

    String SS_IP = "127.0.0.1";
    int SS_PORT = 443;

    int MAX_FRAME_LENGTH = 64 * 1024;

    int MAX_PACKET_LENGTH = MAX_FRAME_LENGTH + 8;

}
