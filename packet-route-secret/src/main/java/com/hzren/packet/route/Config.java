package com.hzren.packet.route;

/**
 * @author tuomasi
 * Created on 2018/12/3.
 */
public interface Config {
    int SECRET_KEY = 678987;

    int CS_PORT = 9970;

    int MS_PORT = 443;
    String MS_IP = "47.100.6.73";
//我的
//    String SPS_IP = "97.64.18.222";
//量化
    String SPS_IP = "144.168.62.7";
    int SPS_PORT = 9980;

    String SS_IP = "127.0.0.1";
    int SS_PORT = 443;

    int MAX_PACKET_LENGTH = Integer.MAX_VALUE;

    int MAX_FRAME_LENGTH = MAX_PACKET_LENGTH - 8;

}
