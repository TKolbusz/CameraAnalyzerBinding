# Camera Analyzer Binding

This extension adds support for the Camera Analyzer.

## Supported Things

This bundle adds the following thing types:

| Thing              | ThingTypeID | Description                                        |
| ------------------ | ----------- | -------------------------------------------------- |
| Camera Analyzer | cameraanalyzer | Camera Analyzer Binding                            |

## Discovery

This extension does not support autodiscovery. The things need to be added manually.


## Thing Configuration

The following parameters are valid for all thing types:

| Parameter | Type    | Required | Default if omitted | Description                                                                |
| --------- | ------- | -------- | ------------------ | -------------------------------------------------------------------------- |
| api_url   | string  | yes      | localhost:5000     | The api URL                                                                |
| refresh   | integer | no       | 1000               | Poll interval in milliseconds. Increase this if you encounter connection errors |

## Channels

| Channel ID       | Item Type            | Read only | Description                                                   |
| ---------------- | -------------------- | --------- | ------------------------------------------------------------- |
| power            | Switch               | false     | Turn the camera analyzer on or off                            |
| events           | String               | true      | Camera Events stream                                          |

## Full Example

### Thing Configuration

```
Thing cameraanalyzer:cameraanalyzer:d6217a31 "Garage-Camera" [api_url="ip:port"]
```

### Item Configuration

```
Switch Power		"Power" 	                		 		{channel="cameraanalyzer:cameraanalyzer:d6217a31:power"}
String Events "Camera Event: [%s]"     {channel="cameraanalyzer:cameraanalyzer:d6217a31:events"}  


```
