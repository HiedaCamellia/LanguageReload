package org.hiedacamellia.languagereload.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedList;

public class CommonConfig {

    public static ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue multilingualItemSearch = BUILDER
            .define("multilingualItemSearch", true);

    public static final ModConfigSpec.ConfigValue<LinkedList<String>> fallbacks = BUILDER
            .define("fallbacks", new LinkedList<>());

    public static final ModConfigSpec.ConfigValue<LinkedList<String>> previousFallbacks = BUILDER
            .define("previousFallbacks", new LinkedList<>());

    public static final ModConfigSpec.ConfigValue<String> language = BUILDER
            .define("language", "");

    public static final ModConfigSpec.ConfigValue<String> previousLanguage = BUILDER
            .define("previousLanguage", "");





    public static ModConfigSpec SPEC = BUILDER.build();

}
