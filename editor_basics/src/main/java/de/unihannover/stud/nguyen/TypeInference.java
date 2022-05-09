package de.unihannover.stud.nguyen;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class JSONCache {

  Map<String, JsonElement> cache;
  List<String> usedList;
  int maxSize = 10000;

  public JSONCache() {
    cache = new ConcurrentHashMap<>();
    usedList = new CopyOnWriteArrayList<>();
  }

  public void put(String key, JsonElement value) {
    if(value == null) {
      cache.put(key, null);
      usedList.add(key);
    }
    else {

      cache.put(key, value);
      usedList.add(key);

      if (cache.size() > maxSize) {
        for(int i = 0; i < 100; i++) {
          String oldestKey = usedList.get(0);
          cache.remove(oldestKey);
          usedList.remove(0);
        }
        System.out.println("List made smaller");
      }
    }
  }

  public JsonElement get(String key) {
    if(!cache.containsKey(key)) {
      return null;
    }
    else if(cache.get(key) == null) {
      return null;
    }
    else {
      return cache.get(key);
    }
  }
}

class Cache {

  Map<String, String> cache;
  List<String> usedList;
  int maxSize = 10000;

  public Cache() {
    cache = new ConcurrentHashMap<>();
    usedList = new CopyOnWriteArrayList<>();
  }

  public void put(String key, String value) {
    if(value == null) {
      cache.put(key, "{\n" +
              "  \"error\": \"Could not predict types for the given source file!\"\n" +
              "}");
      usedList.add(key);
    }
    else {

      cache.put(key, value);
      usedList.add(key);

      if (cache.size() > maxSize) {
        String oldestKey = usedList.get(0);
        cache.remove(oldestKey);
        usedList.remove(0);
      }
    }
  }

  public String get(String key) {
    if(!cache.containsKey(key)) {
      return null;
    }
    else if(cache.get(key) == null) {
      return null;
    }
    else {
      return cache.get(key);
    }
  }
}

