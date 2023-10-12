package com.appium.device;

import com.appium.capabilities.Capabilities;
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
            instance =  Arrays.asList(new ObjectMapper().readValue(response, Device[].class));
        }
        if(ATDRunner.getCloudName().contains("devicefarm"))
            instance = fetchDevicesFromList(instance);

        return instance;
    }


    public static List<Device> fetchDevicesFromList(List<Device> connectedDevices) throws JsonProcessingException {

        List<Device> deviceArrayList = new ArrayList<>(connectedDevices);


        for(HashMap<String, String> deviceData : PluginClI.getDeviceListFromCaps()){

            connectedDevices.removeIf(device -> deviceData.get("udid")!=device.getUdid());
        }

        return deviceArrayList;
    }

}
