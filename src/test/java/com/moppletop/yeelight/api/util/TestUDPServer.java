package com.moppletop.yeelight.api.util;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestUDPServer implements Runnable, Closeable {

    private final int responsePort;
    private final DatagramSocket socket;
    private final byte[] buffer;

    @SneakyThrows
    public TestUDPServer(int requestPort, int responsePort) {
        this.responsePort = responsePort;
        this.socket = new DatagramSocket(requestPort);
        this.socket.setSoTimeout(0);
        this.buffer = new byte[512];

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            byte[] response = PacketUtil.serialisePacket(
                    "",
                    "Cache-Control: max-age=3600",
                    "Date: ",
                    "Ext: ",
                    "Location: yeelight://" + InetAddress.getLocalHost().getHostAddress() + ":12345",
                    "Server: Mocked",
                    "id: 007B",
                    "model: mocked",
                    "fw_ver: 1",
                    "support: get_prop set_default set_power toggle set_bright start_cf stop_cf",
                    "power: on",
                    "bright: 50",
                    "color_mode: 2",
                    "ct: 4000",
                    "rgb: 16711680",
                    "hue: 100",
                    "sat: 35",
                    "name: mocked_bulb"
            );

            packet = new DatagramPacket(response, response.length, packet.getAddress(), responsePort);
            socket.send(packet);
        }
    }

    @Override
    public void close() {
        socket.close();
    }
}
