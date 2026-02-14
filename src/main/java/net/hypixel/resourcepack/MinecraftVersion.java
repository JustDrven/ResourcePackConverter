package net.hypixel.resourcepack;

import java.util.EnumSet;

public enum MinecraftVersion {

    v1_13("1.13", 4),
    v1_14("1.14", 4),
    v1_20("1.20", 15),
    v1_21_2("1.21.2", 42);

    public static final EnumSet<MinecraftVersion> VALUES = EnumSet.allOf(MinecraftVersion.class);

    private final String gameVersionName;
    private final int packFormat;

    MinecraftVersion(String gameVersionName, int packFormat) {
        this.gameVersionName = gameVersionName;
        this.packFormat = packFormat;
    }

    public static MinecraftVersion getByName(String name) {
        return VALUES.stream()
                .filter((version) -> version.getGameVersionName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static MinecraftVersion getLatest() {
        Object[] array = VALUES.toArray();
        return (MinecraftVersion) array[array.length - 1];
    }

    public String getGameVersionName() {
        return gameVersionName;
    }

    public int getPackFormat() {
        return packFormat;
    }

}
