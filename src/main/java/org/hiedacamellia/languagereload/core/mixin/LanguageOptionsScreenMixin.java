package org.hiedacamellia.languagereload.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageManager;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageOptionsScreenMixin extends OptionsSubScreen implements ILanguageOptionsScreen {
    @Unique private LanguageListWidget availableLanguageList;
    @Unique private LanguageListWidget selectedLanguageList;
    @Unique private EditBox searchBox;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<>();


    LanguageOptionsScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @Unique
    private LanguageSelectScreen it() {
        return (LanguageSelectScreen) (Object) this;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, Options options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLangCode = languageManager.getSelected();
        if (!currentLangCode.equals(LanguageReload.NO_LANGUAGE))
            selectedLanguages.add(currentLangCode);
        selectedLanguages.addAll(ClientConfig.fallbacks);
        languageManager.getLanguages().forEach((code, language) ->
                languageEntries.put(code, new LanguageEntry(this::refresh, code, language, selectedLanguages)));
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void onInit(CallbackInfo ci) {
        searchBox = new EditBox(minecraft.font, width / 2 - 100, 22, 200, 20, searchBox, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                } else super.setFocused(focused);
            }
        };
        searchBox.setResponder(__ -> refresh());
        addWidget(searchBox);
        setInitialFocus(searchBox);

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageListWidget(minecraft, it(), listWidth, height, Component.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(minecraft, it(), listWidth, height, Component.translatable("pack.selected.title"));
        availableLanguageList.setLeftPos(width / 2 - 4 - listWidth);
        selectedLanguageList.setLeftPos(width / 2 + 4);
        addWidget(availableLanguageList);
        addWidget(selectedLanguageList);
        refresh();

        addRenderableWidget(minecraft.options.forceUnicodeFont().createButton(minecraft.options, width / 2 - 155, height - 28, 150));
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, this::onDone)
                .bounds(width / 2 - 155 + 160, height - 28, 150, 20)
                .build());

        super.init();
        ci.cancel();
    }

    @Unique
    private void onDone(Button button) {
        if (minecraft == null) return;
        minecraft.setScreen(lastScreen);

        var language = selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(LanguageReload.NO_LANGUAGE, new LinkedList<>());
        } else {
            var fallbacks = new LinkedList<>(selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }
    }

    @Override
    public void languagereload_focusList(LanguageListWidget list) {
        changeFocus(ComponentPath.path(list, this));
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        changeFocus(ComponentPath.path(entry, entry.getParent(), this));
    }

    @Unique
    private void focusSearch() {
        changeFocus(ComponentPath.path(searchBox, this));
    }

    @Unique
    private void refresh() {
        refreshList(selectedLanguageList, selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull));
        refreshList(availableLanguageList, languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = searchBox.getValue().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().toComponent().getString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Unique
    private void refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
        var selectedEntry = list.getSelected();
        list.setSelected(null);
        list.children().clear();
        entries.forEach(entry -> {
            list.children().add(entry);
            entry.setParent(list);
            if (entry == selectedEntry) {
                list.setSelected(entry);
            }
        });
        list.setScrollAmount(list.getScrollAmount());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void onRender(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderDirtBackground(context);

        availableLanguageList.render(context, mouseX, mouseY, delta);
        selectedLanguageList.render(context, mouseX, mouseY, delta);
        searchBox.render(context, mouseX, mouseY, delta);

        context.drawString(minecraft.font, title, width / 2, 8, 0xFFFFFF);
        context.drawString(minecraft.font, LanguageSelectScreen.WARNING_LABEL, width / 2, height - 46, 0x808080);

        super.render(context, mouseX, mouseY, delta);
        ci.cancel();
    }

    @Override
    public void tick() {
        searchBox.tick();
    }

    @ModifyExpressionValue(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/navigation/CommonInputs;selected(I)Z"))
    boolean disableVanillaSelectWithToggleKeys(boolean ignoredOriginal) {
        return false;
    }
}
