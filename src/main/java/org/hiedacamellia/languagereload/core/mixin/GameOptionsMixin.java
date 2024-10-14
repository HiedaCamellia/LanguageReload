package org.hiedacamellia.languagereload.core.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import org.hiedacamellia.languagereload.core.config.CommonConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.LinkedList;

@Mixin(Options.class)
abstract class GameOptionsMixin {
    @Shadow @Final private File optionsFile;
    @Shadow public String languageCode;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Minecraft client, File optionsFile, CallbackInfo ci) {
        if (!LanguageReload.shouldSetSystemLanguage) {
            checkConfigLanguage(languageCode);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    void onLoad(CallbackInfo ci) {
        if (!optionsFile.exists()) {
            LanguageReload.shouldSetSystemLanguage = true;
        }
    }

    @Inject(method = "dataFix", at = @At("RETURN"))
    void onUpdate(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        var lang = cir.getReturnValue().getString("lang");
        if (lang.isEmpty()) {
            LanguageReload.shouldSetSystemLanguage = true;
        } else checkConfigLanguage(lang);
    }

    @Unique
    private static void checkConfigLanguage(String language) {
        if (!CommonConfig.language.get().equals(language)) {
            LanguageReload.LOGGER.info(
                    "Game language ({}) and config language ({}) are different. Updating config",
                    language,
                    CommonConfig.language
            );
            CommonConfig.previousLanguage.set(CommonConfig.language.get());
            CommonConfig.previousFallbacks.set(CommonConfig.fallbacks.get());
            CommonConfig.language.set(language);
            CommonConfig.fallbacks.set(new LinkedList<>());
            if (!language.equals(Language.DEFAULT)) {
                var a = CommonConfig.fallbacks.get();a.add(Language.DEFAULT);
                CommonConfig.fallbacks.set(a);
            }
        }
    }
}
