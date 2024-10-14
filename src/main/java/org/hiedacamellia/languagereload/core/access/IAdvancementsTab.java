package org.hiedacamellia.languagereload.core.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IAdvancementsTab {
    void languagereload_recreateWidgets();
}
