package org.hiedacamellia.languagereload.core.access;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAdvancementsTab {
    void languagereload_recreateWidgets();
}
