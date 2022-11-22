package com.moppletop.yeelight.api.music;

import com.moppletop.yeelight.api.manager.YeeLightConnection;
import com.moppletop.yeelight.api.manager.YeeManager;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

@Getter
@Slf4j
public class BuiltInMusicServer implements MusicServer, Runnable, Closeable {

    private final YeeManager manager;
    private final int port;
    private final ServerSocket serverSocket;
    private final LinkedList<Integer> expectedConnections;

    public BuiltInMusicServer(YeeManager manager, int port) throws IOException {
        this.manager = manager;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.expectedConnections = new LinkedList<>();

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("YeeLight - BuiltInMusicServer #" + port);
        thread.start();
    }

    @Override
    public void handleLight(int id) {
        expectedConnections.add(id);
    }

    @Override
    @SneakyThrows
    public String getHost() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();

                if (expectedConnections.isEmpty()) {
                    log.error("We weren't expecting a connection but received one from {}", socket.getInetAddress().getHostAddress());
                    continue;
                }

                YeeLightConnection connection = manager.getLights().get(expectedConnections.removeFirst());
                connection.setSocket(socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
