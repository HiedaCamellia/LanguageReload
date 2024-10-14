package org.hiedacamellia.languagereload.core.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import org.hiedacamellia.languagereload.core.access.ILanguage;
import org.hiedacamellia.languagereload.core.access.ITranslationStorage;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(TranslatableContents.class)
abstract class TranslatableTextContentMixin implements ComponentContents {
    @Unique private @Nullable String previousTargetLanguage;
    @Unique private final Map<String, List<FormattedText>> separatedecomposedPartsCache = Maps.newHashMap();
    @Unique private @Nullable List<FormattedText> saveddecomposedParts;

    @Shadow @Final private String key;
    @Shadow private @Nullable Language decomposedWith;
    @Shadow private List<FormattedText> decomposedParts;

    @Inject(method = "decompose", at = @At("RETURN"))
    void onUpdatedecomposedParts(CallbackInfo ci) {
        if (!ClientConfig.multilingualItemSearch) return;
        if (decomposedWith == null) return;

        var decomposedPartstorage = ((ILanguage) decomposedWith).languagereload_getTranslationStorage();
        if (decomposedPartstorage == null) return;

        var targetLanguage = ((ITranslationStorage) decomposedPartstorage).languagereload_getTargetLanguage();
        if (Objects.equals(previousTargetLanguage, targetLanguage)) return;

        if (targetLanguage == null) {
            previousTargetLanguage = null;
            decomposedParts = saveddecomposedParts;
            saveddecomposedParts = null;
            return;
        }

        if (previousTargetLanguage == null) {
            saveddecomposedParts = decomposedParts;
        }
        previousTargetLanguage = targetLanguage;
        decomposedParts = separatedecomposedPartsCache.computeIfAbsent(targetLanguage, k -> {
            var string = decomposedWith.getOrDefault(key);
            try {
                var builder = new ImmutableList.Builder<FormattedText>();
                this.decomposeTemplate(string, builder::add);
                return builder.build();
            } catch (TranslatableFormatException e) {
                return ImmutableList.of(FormattedText.of(string));
            }
        });
    }

    @Inject(method = "decompose", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/locale/Language;getComponent(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;"))
    void onUpdatedecomposedParts$clearCache(CallbackInfo ci) {
        previousTargetLanguage = null;
        separatedecomposedPartsCache.clear();
        saveddecomposedParts = null;
    }

    @Shadow protected abstract void decomposeTemplate(String translation, Consumer<FormattedText> partsConsumer);
}
