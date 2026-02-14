package net.hypixel.resourcepack.impl.mapping;

import net.hypixel.resourcepack.PackConverter;

import java.util.HashMap;
import java.util.Map;

public abstract class Mapping {

    protected final Map<String, String> mapping = new HashMap<>();
    private final PackConverter packConverter;

    public Mapping(PackConverter packConverter) {
        this.packConverter = packConverter;

        load();
    }

    protected abstract void load();

    /**
     * @return remapped or in if not present
     */
    public String remap(String in) {
        return mapping.getOrDefault(in, in);
    }

    public PackConverter getPackConverter() {
        return packConverter;
    }
}

