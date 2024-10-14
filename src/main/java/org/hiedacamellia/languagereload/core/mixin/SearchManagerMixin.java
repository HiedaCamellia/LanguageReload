package org.hiedacamellia.languagereload.core.mixin;
//
//import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//import net.minecraft.client.multiplayer.SessionSearchTrees;
//import net.minecraft.locale.Language;
//import net.minecraft.network.chat.Component;
//import org.hiedacamellia.languagereload.core.access.ILanguage;
//import org.hiedacamellia.languagereload.core.access.ITranslationStorage;
//import org.hiedacamellia.languagereload.core.config.CommonConfig;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//
//@Mixin(value = SessionSearchTrees.class, priority = 990)
//abstract class SearchManagerMixin {
//    @ModifyExpressionValue(method = "method_60363", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
//    private static String addFallbackTranslationsToSearchTooltips(String original, Component tooltip) {
//        if (!CommonConfig.multilingualItemSearch.get()) return original;
//
//        var translationStorage = ((ILanguage) Language.getInstance()).languagereload_getTranslationStorage();
//        if (translationStorage == null) return original;
//
//        var stringBuilder = new StringBuilder(original);
//        for (String fallbackCode : CommonConfig.fallbacks.get()) {
//            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
//            stringBuilder.append('\n').append(tooltip.getString());
//        }
//
//        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
//        return stringBuilder.toString();
//    }
//}
