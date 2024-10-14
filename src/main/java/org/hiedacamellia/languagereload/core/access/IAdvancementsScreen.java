package org.hiedacamellia.languagereload.core.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IAdvancementsScreen {
    void languagereload_recreateWidgets();
}
