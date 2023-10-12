package com.appium.plugin;

import com.appium.capabilities.Capabilities;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class PluginClI {
    public String subcommand;
    public String address;
    public String basePath;
    public int port;
    public ArrayList<String> usePlugins;
    public Plugin plugin;
    public ArrayList<Object> extraArgs;
    public boolean allowCors;
    public ArrayList<Object> allowInsecure;
    public int callbackPort;
    public boolean debugLogSpacing;
    public ArrayList<Object> denyInsecure;
    public int keepAliveTimeout;
    public boolean localTimezone;
    public String loglevel;
    public boolean logNoColors;
    public boolean logTimestamp;
    public boolean longStacktrace;
    public boolean noPermsCheck;
    public boolean relaxedSecurityEnabled;
    public boolean sessionOverride;
    public boolean strictCaps;
    public ArrayList<Object> useDrivers;
    public String tmpDir;
    public Meta meta;
    public int $loki;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class DeviceFarm {
        public String platform;
        public String androidDeviceType;
        public String iosDeviceType;
        public JsonNode cloud;
        public boolean skipChromeDownload;
        public JsonNode derivedDataPath;
    }

    public static class Meta {
        public int revision;
        public long created;
        public int version;
        public long updated;
    }

    public static class Plugin {
        @JsonProperty("device-farm")
        @JsonAlias("deviceFarm")
        @Getter
        public DeviceFarm deviceFarm;
    }

    public String getPlatFormName() {
        return getPlugin().getDeviceFarm().getPlatform();
    }

    public boolean isCloudExecution() {
        return getPlugin().getDeviceFarm().getCloud() != null;
    }

    public String getCloudName() {
        return PluginClI.getInstance().getPlugin().getDeviceFarm()
                       .getCloud().get("cloudName").textValue();
    }


    public static String getCloudNameFromCaps(Capabilities capabilities) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(capabilities.getCapabilities().get("serverConfig").toString());
        return jsonNode.path("server").path("plugin").path("device-farm").path("cloud").path("cloudName").asText();

    }

    public static List<HashMap<String, String>> getDeviceListFromCaps() throws JsonProcessingException {
        List<HashMap<String, String>> deviceData = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(Capabilities.getInstance().getCapabilities().get("serverConfig").toString());
        jsonNode = jsonNode.path("server").path("plugin").path("device-farm").path("cloud").path("devices");

        if (jsonNode.isArray()) {
            for (JsonNode element : jsonNode) {
                HashMap<String, String> map = new HashMap<>();
                map.put("udid", element.get("value").asText());
                deviceData.add(map);
            }
        }
        return deviceData;
    }


    private static PluginClI instance;

    @SneakyThrows
    public static PluginClI getInstance() {
        if (instance == null) {
            PluginCliRequest plugin = new PluginCliRequest();
            instance = plugin.getCliArgs();
        }
        return instance;
    }
}