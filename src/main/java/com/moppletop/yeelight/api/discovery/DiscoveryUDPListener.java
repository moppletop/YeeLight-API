package com.moppletop.yeelight.api.discovery;

import com.moppletop.yeelight.api.YeeConfiguration;
import com.moppletop.yeelight.api.manager.YeeManager;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.util.PacketUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * This class handles discovery of new lights on the network.
 * When this class is initialised a response listening thread is started which will await for a response
 */
@Slf4j
public class DiscoveryUDPListener implements Runnable, Closeable {

    private final YeeManager manager;

    // The packet read buffer
    private final byte[] buffer = new byte[512];

    // The socket used for sending and receiving multicast UDP discovery packets
    private DatagramSocket socket;
    // Marks whether the discovery thread is running
    private boolean running;

    // A lock to prevent multiple threads attempting to bind at the same time when the socket is initialising
    private final Object socketCreationLock = new Object();

    public DiscoveryUDPListener(YeeManager manager) {
        this.manager = manager;
        this.running = true;

        Thread thread = new Thread(this);
        thread.setName("YeeLight - UDP Listener #" + manager.getConfiguration().getSearchUdpResponsePort());
        thread.setDaemon(true);
        thread.start();
    }

    public void discoverLights(int millisToWait) throws IOException {
        YeeConfiguration configuration = manager.getConfiguration();
        InetAddress address = InetAddress.getByName(configuration.getSearchUdpAddress());

        // Build the SSDP-like request to discover lights on the network
        byte[] packetData = PacketUtil.serialisePacket(
                "M-SEARCH",
                "HOST: " + configuration.getSearchUdpAddress() + ':' + configuration.getSearchUdpPort(),
                "MAN: \"ssdp:discover\"",
                "ST: wifi_bulb"
        );

        ensureSocketConnected();

        socket.send(new DatagramPacket(packetData, packetData.length, address, configuration.getSearchUdpPort()));

        // If we should wait
        if (millisToWait > 0) {
            try {
                Thread.sleep(millisToWait);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                ensureSocketConnected();

                Arrays.fill(buffer, (byte) 0); // Wipe the buffer
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                log.debug("Received a UDP packet from {}:{}", packet.getAddress().getHostAddress(), packet.getPort());

                YeeLight light = YeeLight.of(PacketUtil.deserializePacket(packet.getData()));
                manager.registerLight(light);
            } catch (Exception ex) {
                if (running) {
                    log.error("UDP Discovery Thread threw an exception while running, backing off and attempting to rebind if still running", ex);

                    socket = null;
                    // Back-off for 50ms and try and rebind
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    private synchronized void ensureSocketConnected() throws IOException {
        synchronized (socketCreationLock) {
            if (socket == null) {
                YeeConfiguration configuration = manager.getConfiguration();
                // Here we have to pass in InetAddress.getLocalHost() otherwise the socket will bind to "localhost" not "192.168.0.x"
                this.socket = new DatagramSocket(configuration.getSearchUdpResponsePort(), configuration.getDiscoveryBindAddress());
            }
        }
    }

    // Closes the socket and lets the thread finish
    @Override
    public void close() {
        if (socket != null) {
            running = false;
            socket.close();
        }
    }
}
