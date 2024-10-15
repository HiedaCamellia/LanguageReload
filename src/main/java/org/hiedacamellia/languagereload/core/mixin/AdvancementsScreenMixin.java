package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import org.hiedacamellia.languagereload.core.access.IAdvancementsScreen;
import org.hiedacamellia.languagereload.core.access.IAdvancementsTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin extends Screen implements ClientAdvancements.Listener, IAdvancementsScreen {
    @Shadow @Final private Map<Advancement, AdvancementTab> tabs;

    protected AdvancementsScreenMixin(Component title) {
        super(title);
    }

    @Override
    public void languagereload_recreateWidgets() {
        for (var advancementTab : tabs.values()) {
            ((IAdvancementsTab) advancementTab).languagereload_recreateWidgets();
        }
    }
}
