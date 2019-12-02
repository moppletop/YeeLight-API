package com.moppletop.yeelight.api.discovery;

import com.moppletop.yeelight.api.YeeConfiguration;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.manager.YeeManager;
import com.moppletop.yeelight.api.util.PacketUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class DiscoveryUDPListener implements Runnable {

    private final YeeManager manager;
    private final DatagramSocket socket;
    private final byte[] buffer;

    public DiscoveryUDPListener(YeeManager manager) throws IOException {
        this.manager = manager;

        this.socket = new DatagramSocket(manager.getConfiguration().getSearchUdpResponsePort(), InetAddress.getByName("192.168.0.7"));

        this.buffer = new byte[512]; // TODO don't guess this value
    }

    public void discoverLights() throws IOException {
        YeeConfiguration configuration = manager.getConfiguration();
        InetAddress address = InetAddress.getByName(configuration.getSearchUdpAddress());

        byte[] packetData = PacketUtil.serialisePacket(
                "M-SEARCH",
                "HOST: " + configuration.getSearchUdpAddress() + ':' + configuration.getSearchUdpPort(),
                "MAN: \"ssdp:discover\"",
                "ST: wifi_bulb"
        );

        socket.send(new DatagramPacket(packetData, packetData.length, address, configuration.getSearchUdpPort()));
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Arrays.fill(buffer, (byte) ' '); // TODO is white-spacing/trimming really the best way to do this?
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                System.out.println(String.format("Received a UDP packet from %s:%s of size %s", address.getHostAddress(), port, packet.getData().length));

                YeeLight light = YeeLight.of(PacketUtil.deserialisePacket(packet.getData()));
                System.out.println(light);
                manager.registerLight(light);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
