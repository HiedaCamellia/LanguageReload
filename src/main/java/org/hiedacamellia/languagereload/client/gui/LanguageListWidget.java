package org.hiedacamellia.languagereload.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.hiedacamellia.languagereload.core.mixin.EntryListWidgetAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class LanguageListWidget extends ObjectSelectionList<LanguageEntry> {
    private final Component title;
    private final LanguageSelectScreen screen;

    public LanguageListWidget(Minecraft client, LanguageSelectScreen screen, int width, int height, Component title) {
        super(client, width, height, 48, height - 55 + 4, 24);
        this.title = title;
        this.screen = screen;

        setRenderHeader(true, (int) (9f * 1.5f));
        centerListVertically = false;
    }

    @Override
    protected void renderHeader(GuiGraphics context, int x, int y) {
        var headerText = title.copy().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        int headerPosX = x + width / 2 - minecraft.font.width(headerText) / 2;
        int headerPosY = Math.min(y0 + 3, y);
        context.drawString(minecraft.font, headerText, headerPosX, headerPosY, 0xFFFFFF, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var selectedEntry = this.getSelected();
        if (selectedEntry == null) return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == GLFW_KEY_SPACE || keyCode == GLFW_KEY_ENTER) {
            selectedEntry.toggle();
            this.setFocused(null);
            ((ILanguageOptionsScreen) screen).languagereload_focusEntry(selectedEntry);
            return true;
        }

        if (Screen.hasShiftDown()) {
            if (keyCode == GLFW_KEY_DOWN) {
                selectedEntry.moveDown();
                return true;
            }
            if (keyCode == GLFW_KEY_UP) {
                selectedEntry.moveUp();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Remove focusing on entry click
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) return false;

        var entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry == null && button == 0) return true;

        if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
            var focusedEntry = this.getFocused();
            if (focusedEntry != entry && focusedEntry instanceof ContainerEventHandler parentElement)
                parentElement.setFocused(null);
            this.setDragging(true);
            return true;
        }

        return ((EntryListWidgetAccessor) this).languagereload_isScrolling();
    }

//    @Override
//    @Nullable
//    public LanguageEntry getEntryAtPosition(double x, double y) {
//        int halfRowWidth = this.getRowWidth() / 2;
//        int center = x0 + width / 2;
//        int minX = center - halfRowWidth;
//        int maxX = center + halfRowWidth;
//        int m = Mth.floor(y - y0) - headerHeight + (int) this.getScrollAmount() - 4 + 2;
//        int entryIndex = m / itemHeight;
//        var scrollbarX = this.getScrollbarPosition();
//        var entryCount = this.getItemCount();
//        return (x < scrollbarX && x >= minX && x <= maxX && entryIndex >= 0 && m >= 0
//                && entryIndex < entryCount ? this.children().get(entryIndex) : null);
//    }

    public LanguageSelectScreen getScreen() {
        return screen;
    }

    public int getRowHeight() {
        return itemHeight;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    public int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    public void updateScroll() {
        this.setScrollAmount(this.getScrollAmount());
    }

    public int getX0() {
        return x0;
    }
    public int getY0() {
        return y0;
    }
    public int getHeaderHeight() {
        return headerHeight;
    }

    public int getItemHeight(){
        return itemHeight;
    }

    public int getItemCountA(){
        return getItemCount();
    }
    public List<LanguageEntry> getChildren(){
        return children();
    }
}
