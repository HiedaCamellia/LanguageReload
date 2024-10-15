package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.hiedacamellia.languagereload.core.access.IAdvancementsTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin implements IAdvancementsTab {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Map<Advancement, AdvancementWidget> widgets;

    @Override
    public void languagereload_recreateWidgets() {
        widgets.replaceAll((advancement, widget) -> {
            var newWidget = new AdvancementWidget(
                    ((AdvancementWidgetAccessor) widget).languagereload_getTab(),
                    minecraft,
                    advancement,
                    ((AdvancementWidgetAccessor) widget).languagereload_getDisplay()
            );
            newWidget.setProgress(((AdvancementWidgetAccessor) widget).languagereload_getProgress());
            ((AdvancementWidgetAccessor) newWidget).languagereload_setParent(((AdvancementWidgetAccessor) widget).languagereload_getParent());
            ((AdvancementWidgetAccessor) newWidget).languagereload_setChildren(((AdvancementWidgetAccessor) widget).languagereload_getChildren());
            return newWidget;
        });
    }
}
