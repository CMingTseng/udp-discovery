package com.afollestad.pifeeder;

import android.annotation.SuppressLint;

import com.afollestad.udpdiscovery.AdapterFactory;
import com.afollestad.udpdiscovery.Entity;
import com.afollestad.udpdiscovery.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@SuppressLint("DefaultLocale")
public class MessageUnitTest {

    Gson gson;
    Entity entity;
    Message message;

    @Before public void setup() {
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapterFactory(AdapterFactory.create())
                .create();

        entity = Entity.create()
                .address("192.168.68.2")
                .name("RaspberryPi")
                .build();

        Map<String, String> payload = new HashMap<>(2);
        payload.put("body", "Hello!");
        payload.put("sent", "1483826236342");
        message = Message.create()
                .from(entity)
                .payload(payload)
                .build();
    }

    @Test public void test_serialize() throws UnsupportedEncodingException {
        String entityJson = String.format("{\"address\":\"%s\",\"name\":\"%s\"}",
                "192.168.68.2", "RaspberryPi");
        String payloadJson = String.format("{\"body\":\"%s\",\"sent\":\"%s\"}",
                "Hello!", "1483826236342");
        String comparison = String.format("{\"from\":%s,\"payload\":%s}",
                entityJson, payloadJson);
        String json = gson.toJson(message, Message.class);
        assertFalse("The JSON is too long! (more than 1024 bytes)", json.getBytes("UTF-8").length > 1024);
        assertEquals(json, comparison);
    }

    @Test public void test_deserialize() {
        String entityJson = String.format("{\"address\":\"%s\",\"name\":\"%s\"}",
                "192.168.68.2", "RaspberryPi");
        String payloadJson = String.format("{\"body\":\"%s\",\"sent\":\"%s\"}",
                "Hello!", "1483826236342");
        String comparison = String.format("{\"from\":%s,\"payload\":%s}",
                entityJson, payloadJson);

        Message deserialized = gson.fromJson(comparison, Message.class);
        assertEquals(deserialized, message);
    }
}