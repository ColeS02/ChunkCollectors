package com.unclecole.chunkcollectors.database.serializer;

import com.unclecole.chunkcollectors.ChunkCollectors;

public class Serializer {


    /**
     * Saves your class to a .json file.
     */
    public void save(Object instance) {
        ChunkCollectors.getPersist().save(instance);
    }

    /**
     * Loads your class from a json file
     *
   */
    public <T> T load(T def, Class<T> clazz, String name) {
        return ChunkCollectors.getPersist().loadOrSaveDefault(def, clazz, name);
    }



}
