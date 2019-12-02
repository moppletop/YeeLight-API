# YeeLight Java API

## Overview

This library is a **Java** implementation of [YeeLight's](https://www.yeelight.com/) Third Party Protocol.
In order to enable your lights to use this library, you must enable [LAN Mode](https://www.yeelight.com/faqs/lan_control) via the official app.
This library was built on the [specification published by YeeLight](https://www.yeelight.com/download/Yeelight_Inter-Operation_Spec.pdf).

## How to install

Well at the moment you'll have to build it yourself, I'll try and get this in maven central at some point

## Usage

### Building the API

```java
        // All values in this example are the defaults
        YeeApiBuilder builder = new YeeApiBuilder();

        // This is the one element of the builder that is required
        // For flexibility this library does not package a JSON serialiser/deserialiser,
        // you will have to implement these two methods yourself with your library of choice.
        // Though the implementation used MUST support the class schema not
        // matching the JSON exactly and vice-versa, any unknowns should be set to null
        // There is an example of how to use this class in the tests: com.moppletop.yeelight.api.util.JacksonJSONProvider
        builder.jsonProvider(new JSONProvider() {
            @Override
            public String serialise(Object obj) {
                return ...;
            }

            @Override
            public <T> T deserialise(String json, Class<T> classOfT) {
                return ...;
            }
        })
                
        // Here you can override YeeLight specific configurations
        // For 99% of uses this isn't needed as the defaults are either
        // required as per the specification or are sensible defaults
        // There are multiple ways to set these values, they are chosen in
        // the order: set in the builder -> environment variable -> default
        builder.configuration(YeeConfiguration.builder()
                .searchUdpAddress("239.255.255.250") // YEE_SEARCH_UDP_ADDRESS
                .searchUdpPort(1982)                 // YEE_SEARCH_UDP_PORT
                .searchUdpResponsePort(1983)         // YEE_SEARCH_UDP_RESPONSE_PORT
                .build())
                
        // When calling .build(), if this is true. api.discoverLights with a timeout of 1000ms
        // will be called automatically, set to false if you want to handle this yourself
        builder.autoDiscovery(true)
        
        // Finalises the build and starts the UDP discovery thread
        YeeApi api = builder.build();
```

### Changing the colour of the lights

```java
        YeeApi api = new YeeApiBuilder()
                .jsonProvider(...)       // Remember you would have to implement this yourself
                .autoDiscovery(false)    // Turn auto discovery off
                .build();
        
        try {
            // This will send a multi-cast UDP request to your local network to discover any
            // lights connected to the network. Do be aware that when passing in a value greater than 0
            // this method will block until that number of milliseconds has passed, waiting for lights to respond
            // Passing in no parameters, the timeout will default to 1000ms
            api.discoverLights(500);
        } catch (IOException e) {
            // handle error
        }
        
        // getLights() returns an immutable collection of all lights currently known to the library
        // Due to how internally these lights are managed. Do not cache the instance of YeeLight as
        // it's underlying object may change without warning meaning the state will not be updated,
        // always use getLights() or getLight(int id)
        api.getLights().forEach(yeeLight -> {
            
            // To change the colour of the light you would use the setRgb method
            api.setRgb(id, red, green, blue, duration);
            
            // This would change the lights to red over 3 seconds
            api.setRgb(yeeLight.getId(), 255, 0, 0, YeeDuration.seconds(3));

            // This would change the lights to light blue/cyan instantly
            api.setRgb(yeeLight.getId(), 0, 255, 255, YeeDuration.instant());
        });
```

YeeApi contains _almost_ all methods supported under the third party protocol.

## TODO Complete this
