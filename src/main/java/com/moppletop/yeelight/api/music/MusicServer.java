package com.moppletop.yeelight.api.music;

public interface MusicServer {

    void handleLight(int id);

    String getHost();

    int getPort();

}
