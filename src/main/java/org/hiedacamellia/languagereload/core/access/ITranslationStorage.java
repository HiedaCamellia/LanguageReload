package org.hiedacamellia.languagereload.core.access;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface ITranslationStorage {
    @Nullable String languagereload_getTargetLanguage();

    void languagereload_setTargetLanguage(@Nullable String value);
}
