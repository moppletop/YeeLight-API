package com.moppletop.yeelight.api.manager;

import com.moppletop.yeelight.api.YeeException;
import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.model.YeeResponse;
import com.moppletop.yeelight.api.util.PacketUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Represents a connection to a particular light
 */
@Slf4j
public class YeeLightConnection implements Runnable, Closeable {

    // The initial buffer length for the incoming packets
    private static final int TCP_BUFFER_LENGTH = 64;

    private final YeeManager manager;
    @Getter
    private final YeeLight light;
    // The socket used for sending and receiving TCP packets
    // Volatile since it's possible for the socket to be changed while the response thread is checking state
    private volatile Socket socket;
    // Marks whether the thread is running
    private boolean running = true;

    public YeeLightConnection(YeeManager manager, YeeLight light) {
        this.manager = manager;
        this.light = light;

        Thread thread = new Thread(this);
        thread.setName("YeeLight - TCP Listener #" + light.getId());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            // Suspend the thread here until the socket is opened
            if (socket == null || socket.isClosed()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }

                continue;
            }

            try {
                InputStream inputStream = socket.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(TCP_BUFFER_LENGTH);
                int buffer;

                // Get the next byte from the stream
                while ((buffer = inputStream.read()) != -1) {
                    outputStream.write(buffer);

                    // Keep reading until we've reached the end of the packet
                    if (PacketUtil.isEndOfPacket(buffer)) {
                        readPacket(outputStream.toByteArray());
                        // Clear the buffer
                        outputStream.reset();
                    }
                }
            } catch (Exception ex) {
                if (running) {
                    log.error("{}'s thread threw an exception while running, backing off until a new command is sent", light.getId(), ex);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            running = false;
            socket.close();
        }
    }

    // Sets the underlying socket and closes the old one, used for music mode when an internal ServerSocket is setup
    public void setSocket(Socket newSocket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            close();
        }

        this.socket = newSocket;
    }

    // Ensures the socket isn't connected, attempt to establish a connection
    private void ensureSocketConnected() throws IOException {
        if (socket == null || socket.isClosed()) {
            setSocket(new Socket(light.getHost(), light.getPort()));
        }
    }

    public void send(YeeCommand command) throws IOException {
        ensureSocketConnected();

        // Serialise our packet into JSON
        byte[] packet;

        try {
            packet = manager.getJsonSerializer().serialise(command);
        } catch (Exception ex) {
            throw new YeeException(ex);
        }

        log.debug("Sending {} to light {}", packet, light.getId());

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(packet);
        outputStream.write(PacketUtil.getNewLine().getBytes()); // Mark the packet has finished by adding the new line
        outputStream.flush();
    }

    private void readPacket(byte[] packet) {
        log.debug("Received {} from light {}", packet, light.getId());

        // Deserialize the packet into the response object
        YeeResponse response;

        try {
            response = manager.getJsonSerializer().deserialize(packet, YeeResponse.class);
        } catch (Exception ex) {
            throw new YeeException(ex);
        }

        manager.readResponse(light, response);
    }
}
