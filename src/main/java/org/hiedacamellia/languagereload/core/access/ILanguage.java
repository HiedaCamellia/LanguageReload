package org.hiedacamellia.languagereload.core.access;


import net.minecraft.client.resources.language.ClientLanguage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ILanguage {
    void languagereload_setTranslationStorage(ClientLanguage translationStorage);

    ClientLanguage languagereload_getTranslationStorage();
}
