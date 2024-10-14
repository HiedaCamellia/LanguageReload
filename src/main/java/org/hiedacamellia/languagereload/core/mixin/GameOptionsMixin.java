package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
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
        if (!ClientConfig.language.equals(language)) {
            LanguageReload.LOGGER.info(
                    "Game language ({}) and config language ({}) are different. Updating config",
                    language,
                    ClientConfig.LANGUAGE
            );
            if(!ClientConfig.SPEC.isLoaded())return;
            ClientConfig.PREVIOUS_LANGUAGE.set(ClientConfig.language);
            ClientConfig.PREVIOUS_FALLBACKS.set(ClientConfig.fallbacks);
            ClientConfig.LANGUAGE.set(language);
            ClientConfig.FALLBACKS.set(new LinkedList<>());
            if (!language.equals(Language.DEFAULT)) {
                var a = ClientConfig.fallbacks;
                a.add(Language.DEFAULT);
                ClientConfig.FALLBACKS.set(a);
            }
            ClientConfig.SPEC.save();
        }
    }
}
