package com.moppletop.yeelight.api.manager;

import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.model.YeeResponse;
import com.moppletop.yeelight.api.util.PacketUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class YeeLightConnection implements Runnable, Closeable {

    private final YeeManager manager;
    @Getter
    private final YeeLight light;
    private Socket socket;

    // TODO think about maybe using AtomicReference for managing the locking of the socket
    private final Object socketLock = new Object();

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            StringBuilder builder = new StringBuilder(64);
            int buffer;

            while ((buffer = inputStream.read()) != -1) {
                builder.append((char) buffer);

                if (PacketUtil.isEndOfPacket(builder)) {
                    interpretPacket(builder.toString());
                    builder = new StringBuilder(64);
                }
            }
        } catch (SocketException ex) {
            System.out.println("Socket exception for " + light.getId() + " on thread " + Thread.currentThread().getName());
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (socket == null) {
            return;
        }

        synchronized (socketLock) {
            System.out.println("Socket closed for " + light.getId());
            socket.close();
        }
    }

    public void setSocket() throws IOException {
        setSocket(new Socket(light.getHost(), light.getPort()));
    }

    public void setSocket(Socket newSocket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            close();
        }

        this.socket = newSocket;
        this.socket.setSoTimeout(0);
        this.socket.setKeepAlive(true);

        Thread thread = new Thread(this);
        thread.setName("YeeLight - TCP Listener #" + light.getId());
        thread.setDaemon(true);
        thread.start();
    }

    public void send(YeeCommand command) throws IOException {
        if (socket == null || socket.isClosed()) {
            setSocket();
            System.out.println("Created socket for " + light.getId());
        }

        String packet = manager.getJsonProvider().serialise(command);

        System.out.println(packet);

        synchronized (socketLock) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(packet.getBytes());
            outputStream.write(PacketUtil.getNewLine().getBytes());
            outputStream.flush();
        }
    }

    private void interpretPacket(String packet) {
        YeeResponse response = manager.getJsonProvider().deserialise(packet, YeeResponse.class);
        System.out.println(response);
        manager.readCommand(light, response);
    }
}
