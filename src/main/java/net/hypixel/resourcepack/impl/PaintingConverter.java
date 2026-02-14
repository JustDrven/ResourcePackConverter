package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the conversion of the singular painting image to split images from 1.14
 */
public class PaintingConverter extends Converter {
    private final Map<Location, String> paintings = new HashMap<>();

    public PaintingConverter(PackConverter packConverter) {
        super(packConverter);
        registerPaintings();
    }

    /**
     * Registers the painting locations, 1 unit here = 16 pixels on the default 256x256 image
     */
    private void registerPaintings() {
        // 1x1 paintings
        paintings.put(new Location(0, 0, 1, 1), "kebab");
        paintings.put(new Location(1, 0, 1, 1), "aztec");
        paintings.put(new Location(2, 0, 1, 1), "alban");
        paintings.put(new Location(3, 0, 1, 1), "aztec2");
        paintings.put(new Location(4, 0, 1, 1), "bomb");
        paintings.put(new Location(5, 0, 1, 1), "plant");
        paintings.put(new Location(6, 0, 1, 1), "wasteland");

        // 2x1 paintings
        paintings.put(new Location(0, 2, 2, 1), "pool");
        paintings.put(new Location(2, 2, 2, 1), "courbet");
        paintings.put(new Location(4, 2, 2, 1), "sea");
        paintings.put(new Location(6, 2, 2, 1), "sunset");
        paintings.put(new Location(8, 2, 2, 1), "creebet");

        // 1x2 paintings
        paintings.put(new Location(0, 4, 1, 2), "wanderer");
        paintings.put(new Location(1, 4, 1, 2), "graham");

        // 4x2 painting
        paintings.put(new Location(0, 6, 4, 2), "fighters");

        // 2x2 paintings
        paintings.put(new Location(0, 8, 2, 2), "match");
        paintings.put(new Location(2, 8, 2, 2), "bust");
        paintings.put(new Location(4, 8, 2, 2), "stage");
        paintings.put(new Location(6, 8, 2, 2), "void");
        paintings.put(new Location(8, 8, 2, 2), "skull_and_roses");
        paintings.put(new Location(10, 8, 2, 2), "wither");

        // 4x4 paintings
        paintings.put(new Location(0, 12, 4, 4), "pointer");
        paintings.put(new Location(4, 12, 4, 4), "pigscene");
        paintings.put(new Location(8, 12, 4, 4), "burning_skull");

        // 4x3 paintings
        paintings.put(new Location(12, 4, 4, 3), "skeleton");
        paintings.put(new Location(12, 7, 4, 3), "donkey_kong");

        // 1x1 back image
        paintings.put(new Location(12, 0, 1, 1), "back");
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_14;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path imagePath = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "painting" + File.separator + "paintings_kristoffer_zetterstrand.png");
        if (!Files.exists(imagePath)) return;

        BufferedImage image = ImageIO.read(imagePath.toFile());

        Path paintingsDir = imagePath.getParent();
        int multiplier = image.getWidth() / 16;
        paintings.forEach((location, output) -> {
            BufferedImage subImage = image.getSubimage(location.x * multiplier, location.y * multiplier, location.width * multiplier, location.height * multiplier);
            Path outputFile = paintingsDir.resolve(output + ".png");
            try {
                ImageIO.write(subImage, "png", outputFile.toFile());
                if (PackConverter.DEBUG) {
                    System.out.println("      Exported painting " + outputFile.getFileName());
                }
            } catch (IOException t) {
                System.out.println("      Failed to convert painting " + outputFile.getFileName());
                Util.propagate(t);
            }
        });

        // Remove the old painting image
        Files.delete(imagePath);
    }

    private static class Location {
        private final int width;
        private final int height;
        int x;
        int y;

        Location(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y && width == location.width && height == location.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }
    }

}
