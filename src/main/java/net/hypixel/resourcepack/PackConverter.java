package net.hypixel.resourcepack;

import net.hypixel.resourcepack.impl.AnimationConverter;
import net.hypixel.resourcepack.impl.ArmorModelConverter;
import net.hypixel.resourcepack.impl.BlockStateConverter;
import net.hypixel.resourcepack.impl.MapIconConverter;
import net.hypixel.resourcepack.impl.ModelConverter;
import net.hypixel.resourcepack.impl.NameConverter;
import net.hypixel.resourcepack.impl.PackMetaConverter;
import net.hypixel.resourcepack.impl.PaintingConverter;
import net.hypixel.resourcepack.impl.ParticleSeparatorConverter;
import net.hypixel.resourcepack.impl.ParticleSizeChangeConverter;
import net.hypixel.resourcepack.impl.SoundsConverter;
import net.hypixel.resourcepack.impl.SpacesConverter;
import net.hypixel.resourcepack.impl.UnicodeFontConverter;
import net.hypixel.resourcepack.pack.Pack;

import joptsimple.OptionSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class PackConverter {

    public static final boolean DEBUG = true;

    protected final OptionSet optionSet;
    protected final Gson gson;
    protected final MinecraftVersion version;

    protected final Map<Class<? extends Converter>, Converter> converters = new LinkedHashMap<>();

    public PackConverter(OptionSet optionSet) {
        this.optionSet = optionSet;

        GsonBuilder gsonBuilder = new GsonBuilder();
        if (!optionSet.has(Options.MINIFY)) {
            gsonBuilder.setPrettyPrinting();
        }

        gson = gsonBuilder.create();
        version = optionSet.valueOf(Options.VERSION);
        if (version == null) {
            System.out.println("Invalid version provided!");
            System.exit(0);
            return;
        }

        registerConverters();

    }

    private void registerConverters() {
        // this needs to be run first, other converters might reference new directory names
        registerConverter(new NameConverter(this));

        for (MinecraftVersion version : MinecraftVersion.VALUES)
            registerConverter(new PackMetaConverter(this, version));


        registerConverter(new ModelConverter(this));
        registerConverter(new SpacesConverter(this));
        registerConverter(new SoundsConverter(this));
        registerConverter(new ParticleSizeChangeConverter(this));
        registerConverter(new ParticleSeparatorConverter(this));
        registerConverter(new BlockStateConverter(this));
        registerConverter(new AnimationConverter(this));
        registerConverter(new MapIconConverter(this));
        registerConverter(new PaintingConverter(this));
        registerConverter(new UnicodeFontConverter(this));
        registerConverter(new ArmorModelConverter(this));
    }

    public void registerConverter(Converter converter) {
        converters.put(converter.getClass(), converter);
    }

    public <T extends Converter> T getConverter(Class<T> clazz) {
        //noinspection unchecked
        return (T) converters.get(clazz);
    }

    public void run() throws IOException {
        try (Stream<Path> list = Files.list(optionSet.valueOf(Options.INPUT_DIR))) {

            list
                    .map(Pack::parse)
                    .filter(Objects::nonNull)
                    .forEach(pack -> {
                        try {
                            System.out.println("Converting " + pack);

                            pack.getHandler().setup();

                            System.out.println("  Running Converters");
                            for (Converter converter : converters.values()) {
                                if (version.ordinal() < converter.getVersion().ordinal()) {
                                    continue;
                                }

                                if (PackConverter.DEBUG) {
                                    System.out.println("    Running " + converter.getClass().getSimpleName());
                                }

                                converter.convert(pack);
                            }

                            pack.getHandler().finish();
                        } catch (Throwable t) {
                            System.err.println("Failed to convert!");
                            Util.propagate(t);
                        }
                    });

        }

    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public String toString() {
        return "PackConverter{" +
                "optionSet=" + optionSet +
                ", converters=" + converters +
                '}';
    }
}