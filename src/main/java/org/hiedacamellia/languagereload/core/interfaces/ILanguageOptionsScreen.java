package org.hiedacamellia.languagereload.core.interfaces;

import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ILanguageOptionsScreen {
    void languagereload_focusList(LanguageListWidget list);

    void languagereload_focusEntry(LanguageEntry entry);
}
