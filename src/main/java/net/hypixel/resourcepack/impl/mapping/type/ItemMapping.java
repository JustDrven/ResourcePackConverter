package net.hypixel.resourcepack.impl.mapping.type;

import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.impl.mapping.Mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class ItemMapping extends Mapping {

    public ItemMapping(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    protected void load() {
        JsonObject items = Util.readJsonResource(getPackConverter().getGson(), "/items.json");
        if (items == null)
            return;

        for (Map.Entry<String, JsonElement> entry : items.entrySet())
            this.mapping.put(entry.getKey(), entry.getValue().getAsString());

    }

}
