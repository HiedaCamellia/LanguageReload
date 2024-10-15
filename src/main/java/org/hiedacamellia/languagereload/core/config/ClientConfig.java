package org.hiedacamellia.languagereload.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.hiedacamellia.languagereload.LanguageReload;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Mod.EventBusSubscriber(modid = LanguageReload.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {

    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue MULTILINGUAL_ITEM_SEARCH = BUILDER
            .define("multilingualItemSearch", true);

    public static final ForgeConfigSpec.ConfigValue<List<String>> FALLBACKS = BUILDER
            .define("fallbacks", new ArrayList<>());

    public static final ForgeConfigSpec.ConfigValue<List<String>> PREVIOUS_FALLBACKS = BUILDER
            .define("previousFallbacks", new ArrayList<>());

    public static final ForgeConfigSpec.ConfigValue<String> LANGUAGE = BUILDER
            .define("language", "");

    public static final ForgeConfigSpec.ConfigValue<String> PREVIOUS_LANGUAGE = BUILDER
            .define("previousLanguage", "");

    public static boolean multilingualItemSearch = false;
    public static LinkedList<String> fallbacks = new LinkedList<>();
    public static LinkedList<String> previousFallbacks = new LinkedList<>();
    public static String language = "*";
    public static String previousLanguage = "*";

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event){
        load();
    }

    public static void load(){
        multilingualItemSearch = MULTILINGUAL_ITEM_SEARCH.get();
        fallbacks = new LinkedList<>(FALLBACKS.get());
        previousFallbacks = new LinkedList<>(PREVIOUS_FALLBACKS.get());
        language = LANGUAGE.get();
        previousLanguage = PREVIOUS_LANGUAGE.get();
    }

    public static ForgeConfigSpec SPEC = BUILDER.build();

}
