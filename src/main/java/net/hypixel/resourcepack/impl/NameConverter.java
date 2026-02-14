package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.impl.mapping.Mapping;
import net.hypixel.resourcepack.impl.mapping.type.BlockMapping;
import net.hypixel.resourcepack.impl.mapping.type.ItemMapping;
import net.hypixel.resourcepack.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class NameConverter extends Converter {

    protected final Mapping blockMapping;
    protected final Mapping itemMapping;

    public NameConverter(PackConverter packConverter) {
        super(packConverter);

        blockMapping = new BlockMapping(packConverter);
        itemMapping = new ItemMapping(packConverter);
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_13;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path models = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "models");
        if (models.resolve("blocks").toFile().exists()) Files.move(models.resolve("blocks"), models.resolve("block"));
        renameAll(blockMapping, ".json", models.resolve("block"));
        if (models.resolve("items").toFile().exists()) Files.move(models.resolve("items"), models.resolve("item"));
        renameAll(itemMapping, ".json", models.resolve("item"));

        Path blockStates = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "blockstates");
        renameAll(itemMapping, ".json", blockStates);

        Path textures = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures");
        if (textures.resolve("blocks").toFile().exists())
            Files.move(textures.resolve("blocks"), textures.resolve("block"));
        renameAll(blockMapping, ".png", textures.resolve("block"));
        renameAll(blockMapping, ".png.mcmeta", textures.resolve("block"));
        if (textures.resolve("items").toFile().exists())
            Files.move(textures.resolve("items"), textures.resolve("item"));
        renameAll(itemMapping, ".png", textures.resolve("item"));
        renameAll(itemMapping, ".png.mcmeta", textures.resolve("item"));
    }

    protected void renameAll(Mapping mapping, String extension, Path path) throws IOException {
        if (path.toFile().exists()) {

            try (Stream<Path> list = Files.list(path)) {

                list.forEach(path1 -> {
                    if (!path1.toString().endsWith(extension)) return;

                    String baseName = path1.getFileName().toString().substring(0, path1.getFileName().toString().length() - extension.length());
                    String newName = mapping.remap(baseName);
                    if (newName != null && !newName.equals(baseName)) {
                        boolean ret = Util.renameFile(path1, newName + extension);

                        if (ret && PackConverter.DEBUG) {
                            System.out.println("      Renamed: " + path1.getFileName().toString() + "->" + newName + extension);
                        } else if (!ret) {
                            System.err.println("      Failed to rename: " + path1.getFileName().toString() + "->" + newName + extension);
                        }

                    }
                });


            }

        }
    }

    public Mapping getBlockMapping() {
        return blockMapping;
    }

    public Mapping getItemMapping() {
        return itemMapping;
    }


}
