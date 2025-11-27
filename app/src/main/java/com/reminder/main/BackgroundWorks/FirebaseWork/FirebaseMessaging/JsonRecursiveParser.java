package com.reminder.main.BackgroundWorks.FirebaseWork.FirebaseMessaging;

import com.google.gson.*;
import java.util.*;

public class JsonRecursiveParser {

    private static final Gson gson = new Gson();

    public static Map<String, Object> parseToMap(String json) {
        JsonElement element = JsonParser.parseString(json);
        return parseObject(element.getAsJsonObject());
    }

    private static Map<String, Object> parseObject(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            JsonElement value = jsonObject.get(key);
            map.put(key, parseElement(value));
        }

        return map;
    }

    private static Object parseElement(JsonElement element) {

        // If primitive → return its value
        if (element.isJsonPrimitive()) {
            JsonPrimitive p = element.getAsJsonPrimitive();

            if (p.isBoolean())    return p.getAsBoolean();
            if (p.isNumber())     return p.getAsNumber();
            if (p.isString()) {
                String str = p.getAsString();
                // If the string itself is JSON, recurse
                if (isJson(str)) {
                    return parseElement(JsonParser.parseString(str));
                }
                return str;
            }
        }

        // If it's an object → recurse to Map
        if (element.isJsonObject()) {
            return parseObject(element.getAsJsonObject());
        }

        // If it's an array → return List<Object>
        if (element.isJsonArray()) {
            return parseArray(element.getAsJsonArray());
        }

        return null;
    }

    private static List<Object> parseArray(JsonArray array) {
        List<Object> list = new ArrayList<>();

        for (JsonElement element : array) {
            list.add(parseElement(element));
        }

        return list;
    }

    private static boolean isJson(String str) {
        try {
            JsonParser.parseString(str);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
