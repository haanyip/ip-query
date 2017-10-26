package com.maxmind.db;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface NodeCache {

    public interface Loader {
        JsonNode load(int key) throws IOException;
    }

    public JsonNode get(int key, Loader loader) throws IOException;

}
