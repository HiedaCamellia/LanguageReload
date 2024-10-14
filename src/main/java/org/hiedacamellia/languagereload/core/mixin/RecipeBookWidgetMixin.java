package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.locale.Language;
import org.hiedacamellia.languagereload.LanguageReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(RecipeBookComponent.class)
public class RecipeBookWidgetMixin {
    @Inject(method = "pirateSpeechForThePeople", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/LanguageManager;setSelected(Ljava/lang/String;)V"))
    void onLanguageSwitching$cancel(String search, CallbackInfo ci) {
        LanguageReload.setLanguage("en_pt", new LinkedList<>() {{
            add(Language.DEFAULT);
        }});
        ci.cancel();
    }
}
