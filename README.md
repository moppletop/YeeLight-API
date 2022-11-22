# YeeLight Java API

## Overview

This library is a **Java** implementation of [YeeLight's](https://www.yeelight.com/) Third Party Protocol.
In order to enable your lights to use this library, you must enable [LAN Mode](https://www.yeelight.com/faqs/lan_control) via the official app.
This library was built on the [specification published by YeeLight](https://www.yeelight.com/download/Yeelight_Inter-Operation_Spec.pdf).

## Usage

### Building the API

```java
        // All values in this example are the defaults
        YeeApiBuilder builder = new YeeApiBuilder();
                
        // When calling .build(), if this is true. api.discoverLights with a timeout of 1000ms
        // will be called automatically, set to false if you want to handle this yourself
        builder.autoDiscovery(true);
        
        // Finalises the build and starts the UDP discovery thread
        YeeApi api = builder.build();
```

### Changing the colour of the lights

```java
        YeeApi api = new YeeApiBuilder()
                .build();

        // getLights() returns an immutable collection of all lights currently known to the library..
        // The objects in this collection are a snapshot of the current state of the lights and will not be updated as
        // state changes.
        api.getLights().forEach(yeeLight -> {
            
            // To change the colour of the light you would use the setRgb method
            api.setRgb(id, red, green, blue, duration);
            
            // This would change the lights to red over 3 seconds
            api.setRgb(yeeLight.getId(), 255, 0, 0, YeeDuration.seconds(3));

            // This would change the lights to light blue/cyan instantly
            api.setRgb(yeeLight.getId(), 0, 255, 255, YeeDuration.instant());
        });
```

YeeApi contains _almost_ all methods supported under the third party protocol. Scenes are not supported.
