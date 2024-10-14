package org.hiedacamellia.languagereload.core.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract void debugFeedbackTranslated(String key, Object... args);

    @Shadow
    protected abstract void debugWarningTranslated(String key, Object... args);

    @Unique
    private void processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            var languageManager = minecraft.getLanguageManager();

            var language = languageManager.getLanguage(ClientConfig.previousLanguage);
            var noLanguage = ClientConfig.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                debugWarningTranslated("debug.reload_languages.switch.failure");
            } else {
                LanguageReload.setLanguage(ClientConfig.previousLanguage, (LinkedList<String>) ClientConfig.previousFallbacks);
                var languages = new ArrayList<Component>() {{
                    if (noLanguage)
                        add(Component.literal("\u2205"));
                    if (language != null)
                        add(language.toComponent());
                    addAll(ClientConfig.fallbacks.stream()
                            .map(languageManager::getLanguage)
                            .filter(Objects::nonNull)
                            .map(LanguageInfo::toComponent)
                            .toList());
                }};
                debugFeedbackTranslated("debug.reload_languages.switch.success", ComponentUtils.formatList(languages, Component.literal(", ")));
            }
        } else {
            LanguageReload.reloadLanguages();
            debugFeedbackTranslated("debug.reload_languages.message");
        }
    }

    @Inject(method = "handleDebugKeys", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V",
            ordinal = 6, shift = At.Shift.AFTER))
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        minecraft.gui.getChat().addMessage(Component.translatable("debug.reload_languages.help"));
    }

    @Inject(method = "handleDebugKeys", at = @At("RETURN"), cancellable = true)
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPress", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"),
            cancellable = true)
    private void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3) && key == GLFW.GLFW_KEY_J) {
            if (action != 0)
                processLanguageReloadKeys();
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
            ordinal = 0), cancellable = true)
    private void onOnChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3) && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_J)) {
            ci.cancel();
        }
    }
}
