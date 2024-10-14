package org.hiedacamellia.languagereload.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.hiedacamellia.languagereload.LanguageReload;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@EventBusSubscriber(modid = LanguageReload.MODID,bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {

    public static ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue MULTILINGUAL_ITEM_SEARCH = BUILDER
            .define("multilingualItemSearch", true);

    public static final ModConfigSpec.ConfigValue<List<String>> FALLBACKS = BUILDER
            .define("fallbacks", new ArrayList<>());

    public static final ModConfigSpec.ConfigValue<List<String>> PREVIOUS_FALLBACKS = BUILDER
            .define("previousFallbacks", new ArrayList<>());

    public static final ModConfigSpec.ConfigValue<String> LANGUAGE = BUILDER
            .define("language", "");

    public static final ModConfigSpec.ConfigValue<String> PREVIOUS_LANGUAGE = BUILDER
            .define("previousLanguage", "");

    public static boolean multilingualItemSearch = false;
    public static LinkedList<String> fallbacks = new LinkedList<>();
    public static LinkedList<String> previousFallbacks = new LinkedList<>();
    public static String language = "*";
    public static String previousLanguage = "*";

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event){
        multilingualItemSearch = MULTILINGUAL_ITEM_SEARCH.get();
        fallbacks = new LinkedList<>(FALLBACKS.get());
        previousFallbacks = new LinkedList<>(PREVIOUS_FALLBACKS.get());
        language = LANGUAGE.get();
        previousLanguage = PREVIOUS_LANGUAGE.get();
    }

    public static ModConfigSpec SPEC = BUILDER.build();

}
