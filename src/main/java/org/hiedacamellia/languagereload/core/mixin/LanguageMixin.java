package org.hiedacamellia.languagereload.core.mixin;


import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import org.hiedacamellia.languagereload.core.access.ILanguage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fixes Server Translation API incompatibility (#56)
@Mixin(value = Language.class, priority = 990)
public class LanguageMixin implements ILanguage {
    @Unique private @Nullable ClientLanguage translationStorage = null;
    @Unique private static @Nullable ClientLanguage translationStorageOnSetInstance = null;


    @Inject(method = "inject", at = @At("HEAD"))
    private static void onSetInstance(Language language, CallbackInfo ci) {
        if (language instanceof ClientLanguage translationStorage) {
            translationStorageOnSetInstance = translationStorage;
        }
    }

    @Inject(method = "inject", at = @At("TAIL"))
    private static void afterSetInstance(Language language, CallbackInfo ci) {
        ((ILanguage) language).languagereload_setTranslationStorage(translationStorageOnSetInstance);
        translationStorageOnSetInstance = null;
    }

    @Override
    public void languagereload_setTranslationStorage(ClientLanguage translationStorage) {
        this.translationStorage = translationStorage;
    }

    @Override
    public ClientLanguage languagereload_getTranslationStorage() {
        return translationStorage;
    }
}
