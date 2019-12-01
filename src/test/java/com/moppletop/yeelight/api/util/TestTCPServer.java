package com.moppletop.yeelight.api.util;

import com.moppletop.yeelight.api.json.JSONProvider;
import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeResponse;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

public class TestTCPServer implements Runnable, Closeable {

    private final ServerSocket serverSocket;
    private final JSONProvider jsonProvider;

    @SneakyThrows
    public TestTCPServer(int port, JSONProvider jsonProvider) {
        this.serverSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
        this.serverSocket.setSoTimeout(0);
        this.jsonProvider = jsonProvider;

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    @SneakyThrows
    public void run() {
        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        StringBuilder builder = new StringBuilder(64);
        int buffer;

        while (!serverSocket.isClosed()) {
            buffer = inputStream.read();

            builder.append((char) buffer);

            if (PacketUtil.isEndOfPacket(builder)) {
                System.out.println(builder);

                YeeCommand command = jsonProvider.deserialise(builder.toString(), YeeCommand.class);
                YeeResponse response = new YeeResponse(command.getId(), null, new Object[] {"ok"}, null, null);

                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(jsonProvider.serialise(response).getBytes());
                outputStream.write(PacketUtil.getNewLine().getBytes());
                outputStream.flush();

                response = new YeeResponse(null, "props", null, Collections.singletonMap("ct", command.getParams()[0].toString()), null);

                outputStream.write(jsonProvider.serialise(response).getBytes());
                outputStream.write(PacketUtil.getNewLine().getBytes());
                outputStream.flush();

                builder = new StringBuilder(64);
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
