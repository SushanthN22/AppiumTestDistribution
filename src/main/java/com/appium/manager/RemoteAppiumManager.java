package com.appium.manager;

import com.appium.utils.Api;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.device.Device;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoteAppiumManager implements IAppiumManager {

    @Override
    public void destroyAppiumNode(String host) throws IOException {
        new Api().getResponse("http://" + host + ":4567"
                + "/appium/stop").body().string();

    }

    @Override
    public String getRemoteWDHubIP(String host) throws IOException {
        String hostIP = "http://" + host;
        String appiumRunningPort = new JSONObject(new Api().getResponse(hostIP + ":4567"
                + "/appium/isRunning").body().string()).get("port").toString();
        return hostIP + ":" + appiumRunningPort + "/wd/hub";
    }

    @Override
    public void startAppiumServer(String host) throws Exception {
        System.out.println(
                "**************************************************************************\n");
        System.out.println("Starting Appium Server on host " + host);
        System.out.println(
                "**************************************************************************\n");
        new Api().getResponse("http://" + host + ":4567"
                + "/appium/start").body().string();

        boolean status = Boolean.getBoolean(new JSONObject(new Api().getResponse("http://" + host + ":4567"
                + "/appium/isRunning").body().string()).get("status").toString());
        if (status) {
            System.out.println(
                    "***************************************************************\n");
            System.out.println("Appium Server started successfully on  " + host);
            System.out.println(
                    "****************************************************************\n");
        }
    }

    @Override
    public List<Device> getDevices(String machineIP) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Device> devices = Arrays.asList(mapper.readValue(new URL(
                        "http://" + machineIP + ":4567/devices"),
                Device[].class));
        return new ArrayList<>(devices);
    }

    @Override
    public Device getSimulator(String machineIP, String deviceName, String os) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String url = String.format("http://%s:4567/device/ios/simulators"
                        + "?simulatorName=%s&simulatorOSVersion=%s",
                machineIP, URLEncoder.encode(deviceName, "UTF-8"),
                URLEncoder.encode(os, "UTF-8"));
        Device device = mapper.readValue(new URL(url),
                Device.class);
        return device;
    }

    @Override
    public int getAvailablePort(String hostMachine) throws IOException {
        String url = String.format("http://%s:4567/machine/availablePort", hostMachine);
        Response response = new Api().getResponse(url);
        return Integer.parseInt(response.body().string());
    }
}

