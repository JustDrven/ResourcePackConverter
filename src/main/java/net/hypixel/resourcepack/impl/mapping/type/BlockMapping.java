package net.hypixel.resourcepack.impl.mapping.type;

import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.impl.mapping.Mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class BlockMapping extends Mapping {

    public BlockMapping(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    protected void load() {
        JsonObject blocks = Util.readJsonResource(getPackConverter().getGson(), "/blocks.json");
        if (blocks == null)
            return;

        for (Map.Entry<String, JsonElement> entry : blocks.entrySet())
            this.mapping.put(entry.getKey(), entry.getValue().getAsString());

    }

}


