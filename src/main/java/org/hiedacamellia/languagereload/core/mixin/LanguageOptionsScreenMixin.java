package org.hiedacamellia.languagereload.core.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageManager;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Stream;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageOptionsScreenMixin extends OptionsSubScreen implements ILanguageOptionsScreen {
    @Unique private LanguageListWidget availableLanguageList;
    @Unique private LanguageListWidget selectedLanguageList;
    @Unique private EditBox searchBox;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, Options options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLangCode = languageManager.getSelected();
        if (!currentLangCode.equals(LanguageReload.NO_LANGUAGE))
            selectedLanguages.add(currentLangCode);
        selectedLanguages.addAll(Config.getInstance().fallbacks);
        languageManager.getLanguages().forEach((code, language) ->
                languageEntries.put(code, new LanguageEntry(this::refresh, code, language, selectedLanguages)));

        layout.setHeaderHeight(48);
        layout.setFooterHeight(53);
    }

    @Inject(method = "addContents", at = @At("HEAD"), cancellable = true)
    void onInitBody(CallbackInfo ci) {
        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageListWidget(minecraft, it(), listWidth, height, Component.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(minecraft, it(), listWidth, height, Component.translatable("pack.selected.title"));
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        layout.addBody(availableLanguageList);
        layout.addBody(selectedLanguageList);
        refresh();

        ci.cancel();
    }

    @Override
    protected void addTitle() {
        searchBox = new EditBox(minecraft.font, width / 2 - 100, 22, 200, 20, searchBox, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                } else super.setFocused(focused);
            }
        };
        searchBox.setChangedListener(__ -> refresh());

        var header = layout.addHeader(DirectionalLayoutWidget.vertical().spacing(5));
        header.getMainPositioner().alignHorizontalCenter();
        header.add(new ComponentWidget(title, ComponentRenderer));
        header.add(searchBox);
    }

    @Inject(method = "initTabNavigation", at = @At("HEAD"), cancellable = true)
    protected void onInitTabNavigation(CallbackInfo ci) {
        super.initTabNavigation();

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList.position(listWidth, layout);
        selectedLanguageList.position(listWidth, layout);
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        availableLanguageList.updateScroll();
        selectedLanguageList.updateScroll();

        ci.cancel();
    }

    @Inject(method = "onDone", at = @At("HEAD"), cancellable = true)
    private void onDone(CallbackInfo ci) {
        if (client == null) return;
        client.setScreen(parent);

        var language = selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(LanguageReload.NO_LANGUAGE, new LinkedList<>());
        } else {
            var fallbacks = new LinkedList<>(selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }

        ci.cancel();
    }

    @Unique
    private void refresh() {
        refreshList(selectedLanguageList, selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull));
        refreshList(availableLanguageList, languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = searchBox.getComponent().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().getDisplayComponent().getString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Unique
    private void refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
        var selectedEntry = list.getSelectedOrNull();
        list.setSelected(null);
        list.children().clear();
        entries.forEach(entry -> {
            list.children().add(entry);
            entry.setParent(list);
            if (entry == selectedEntry) {
                list.setSelected(entry);
            }
        });
        list.updateScroll();
    }

    @Override
    protected void setInitialFocus() {
        focusSearch();
    }

    @Unique
    private void focusSearch() {
        switchFocus(GuiNavigationPath.of(searchBox, this));
    }

    @Override
    public void languagereload_focusList(LanguageListWidget list) {
        switchFocus(GuiNavigationPath.of(list, this));
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        switchFocus(GuiNavigationPath.of(entry, entry.getParent(), this));
    }

    @Unique
    LanguageSelectScreen it() {
        return (LanguageSelectScreen) (Object) this;
    }

    LanguageSelectScreenMixin(Screen parent, GameOptions options, Component title) {
        super(parent, options, title);
    }
}
