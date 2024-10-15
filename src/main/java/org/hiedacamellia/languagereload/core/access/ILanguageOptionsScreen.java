package org.hiedacamellia.languagereload.core.access;

import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ILanguageOptionsScreen {
    void languagereload_focusList(LanguageListWidget list);

    void languagereload_focusEntry(LanguageEntry entry);
}
