package com.moppletop.yeelight.api.util;

import com.moppletop.yeelight.api.json.JSONSerializer;
import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

@Slf4j
public class TestTCPServer implements Runnable, Closeable {

    private final ServerSocket serverSocket;
    private final JSONSerializer jsonProvider;

    @SneakyThrows
    public TestTCPServer(int port, JSONSerializer jsonProvider) {
        this.serverSocket = new ServerSocket(port);
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
        ByteArrayOutputStream streamBuffer = new ByteArrayOutputStream(64);
        int buffer;

        while (!serverSocket.isClosed()) {
            buffer = inputStream.read();

            streamBuffer.write(buffer);

            if (PacketUtil.isEndOfPacket(buffer)) {
                log.info("{}", streamBuffer);

                YeeCommand command = jsonProvider.deserialize(streamBuffer.toByteArray(), YeeCommand.class);
                YeeResponse response = new YeeResponse(command.getId(), null, new Object[]{"ok"}, null, null);

                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(jsonProvider.serialise(response));
                outputStream.write(PacketUtil.getNewLine().getBytes());
                outputStream.flush();

                response = new YeeResponse(command.getId(), "props", null, Collections.singletonMap("ct", command.getParams()[0].toString()), null);

                outputStream.write(jsonProvider.serialise(response));
                outputStream.write(PacketUtil.getNewLine().getBytes());
                outputStream.flush();

                streamBuffer.reset();
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
