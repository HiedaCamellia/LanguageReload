package org.hiedacamellia.languagereload.core.access;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAdvancementsTab {
    void languagereload_recreateWidgets();
}
