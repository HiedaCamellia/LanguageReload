package org.hiedacamellia.languagereload.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LanguageEntry extends ObjectSelectionList.Entry<LanguageEntry> {
    private static final Component DEFAULT_LANGUAGE_TOOLTIP = Component.translatable("language.default.tooltip");

    private static final ResourceLocation TEXTURE = new ResourceLocation(LanguageReload.MODID, "textures/language_selection.png");
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 64;
    private static final int HOVERED_V_OFFSET = 24;

    private final Minecraft client = Minecraft.getInstance();

    private final String code;
    private final LanguageInfo language;
    private final LinkedList<String> selectedLanguages;
    private final Runnable refreshListsAction;

    private final List<AbstractWidget> buttons = new ArrayList<>();
    private final Button addButton = addButton(15, 24, 0, 0, __ -> add());
    private final Button removeButton = addButton(15, 24, 15, 0, __ -> remove());
    private final Button moveUpButton = addButton(11, 11, 31, 0, __ -> moveUp());
    private final Button moveDownButton = addButton(11, 11, 31, 13, __ -> moveDown());

    private LanguageListWidget parentList;

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    private Button addButton(int width, int height, int u, int v, Button.OnPress action) {
        var button = new ImageButton(0, 0, width, height, u, v, HOVERED_V_OFFSET, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, action);
        button.visible = false;
        buttons.add(button);
        return button;
    }

    private boolean isDefault() {
        return code.equals(Language.DEFAULT);
    }

    private boolean isSelected() {
        return selectedLanguages.contains(code);
    }

    private boolean isFirst() {
        return code.equals(selectedLanguages.peekFirst());
    }

    private boolean isLast() {
        return code.equals(selectedLanguages.peekLast());
    }

    private void add() {
        if (isFocused())
            parentList.setFocused(null);
        selectedLanguages.addFirst(code);
        refreshListsAction.run();
    }

    private void remove() {
        if (isFocused())
            parentList.setFocused(null);
        selectedLanguages.remove(code);
        refreshListsAction.run();
    }

    public void toggle() {
        if (!isSelected()) add();
        else remove();
    }

    public void moveUp() {
        if (!isSelected()) return;
        if (isFirst()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index - 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    public void moveDown() {
        if (!isSelected()) return;
        if (isLast()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index + 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    @Override
    public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        x -= 2;
        y -= 2;
        if (hovered || isFocused() || client.options.touchscreen().get()) {
            context.fill(x + 1, y + 1, x + entryWidth - 1, y + entryHeight + 3,
                    (hovered || isFocused()) ? 0xA0909090 : 0x50909090);
            buttons.forEach(button -> button.visible = false);
            renderButtons((button, buttonX, buttonY) -> {
                button.setX(buttonX);
                button.setY(buttonY);
                button.visible = true;
                button.render(context, mouseX, mouseY, tickDelta);
            }, x, y);
            if ((hovered || isFocused()) && isDefault())
                renderDefaultLanguageTooltip(x, y);
        }
        context.drawString(client.font, language.name(), x + 29, y + 3, 0xFFFFFF);
        context.drawString(client.font, language.region(), x + 29, y + 14, 0x808080);
    }

    private void renderButtons(ButtonRenderer renderer, int x, int y) {
        if (isSelected()) {
            renderer.render(removeButton, x, y);
            if (!isFirst()) renderer.render(moveUpButton, x + removeButton.getWidth() + 1, y);
            if (!isLast()) renderer.render(moveDownButton, x + removeButton.getWidth() + 1, y + moveUpButton.getHeight() + 2);
        } else renderer.render(addButton, x + 7, y);
    }

    private void renderDefaultLanguageTooltip(int x, int y) {
        var tooltip = client.font.split(DEFAULT_LANGUAGE_TOOLTIP, parentList.getRowWidth() - 6);
        parentList.getScreen().setTooltipForNextRenderPass(tooltip, (screenWidth, screenHeight, mouseX, mouseY, width, height) -> {
            var pos = new Vector2i(
                    x + 3 + (parentList.getRowWidth() - width - 6) / 2,
                    y + parentList.getRowHeight() + 4);
            if (pos.y > parentList.getBottom() + 2 || pos.y + height + 5 > screenHeight)
                pos.y = y - height - 6;
            return pos;
        }, true);
    }

    @Override
    public Component getNarration() {
        return Component.translatable("narrator.select", language.toComponent());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : buttons)
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                ((ILanguageOptionsScreen) parentList.getScreen()).languagereload_focusList(parentList);
                return true;
            }
        return false;
    }

    public void setParent(LanguageListWidget list) {
        this.parentList = list;
    }

    public LanguageListWidget getParent() {
        return parentList;
    }

    public String getCode() {
        return code;
    }

    public LanguageInfo getLanguage() {
        return language;
    }

    @FunctionalInterface
    private interface ButtonRenderer {
        void render(Button button, int x, int y);
    }
}
