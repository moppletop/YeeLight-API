package com.moppletop.yeelight.api.music;

import lombok.Value;

@Value
public class RemoteMusicServer implements MusicServer {

    String host;
    int port;

    @Override
    public void handleLight(int id) {
        // no need to do anything since it's remote
    }
}
