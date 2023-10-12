package com.appium.device;

import com.appium.manager.ATDRunner;
import com.appium.manager.AppiumServerManager;
import com.appium.plugin.PluginClI;
import com.appium.utils.Api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Devices {
    private static List<Device> instance;

    private Devices() {

    }

    @SneakyThrows
    public static List<Device> getConnectedDevices() {
        if (instance == null) {
            System.out.println(Thread.currentThread().getId());
            AppiumServerManager appiumServerManager = new AppiumServerManager();
            String remoteWDHubIP = appiumServerManager.getRemoteWDHubIP();
            URL url = new URL(remoteWDHubIP);
            String response = new Api().getResponse(url.getProtocol()
                    + "://" + url.getHost() + ":" + url.getPort() + "/device-farm/api/devices");
            instance = new ArrayList<>(Arrays.asList(new ObjectMapper().readValue(response, Device[].class)));
        }
        if(ATDRunner.getCloudName().contains("devicefarm"))
            instance = fetchDevicesFromList(instance);

        return instance;
    }


    public static List<Device> fetchDevicesFromList(List<Device> connectedDevices) throws JsonProcessingException {

        for(HashMap<String, String> deviceData : PluginClI.getDeviceListFromCaps()) {

            for (Device device: connectedDevices) {
                if(!device.getUdid().equalsIgnoreCase(deviceData.get("udid")))
                    connectedDevices.remove(device);
            }
            //connectedDevices.removeIf(device -> deviceData.get("udid")!=device.getUdid());
        }

        return connectedDevices;
    }

}
