package com.unclecole.chunkcollectors.database;

import com.unclecole.chunkcollectors.database.serializer.Serializer;
import com.unclecole.chunkcollectors.objects.CollectorObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkCollectorData {

    public static transient ChunkCollectorData instance = new ChunkCollectorData();

    public static HashMap<String, CollectorObject> chunkCollectorData = new HashMap<>();
    public static HashMap<String, String> chunkData = new HashMap<>();

    public static void save() {
        new Serializer().save(instance);
    }

    public static void load() {
        new Serializer().load(instance, ChunkCollectorData.class, "chunkcollectordata");
    }

}