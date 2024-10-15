package org.hiedacamellia.languagereload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.hiedacamellia.languagereload.core.access.IAdvancementsScreen;
import org.hiedacamellia.languagereload.core.mixin.*;

import java.util.LinkedList;


@Mod(LanguageReload.MODID)
public class LanguageReload {
    public static final String MODID = "languagereload";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static boolean shouldSetSystemLanguage = false;
    public static final String NO_LANGUAGE = "*";

    public LanguageReload() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static void reloadLanguages() {
        var client = Minecraft.getInstance();

        // Reload language manager
        client.getLanguageManager().onResourceManagerReload(client.getResourceManager());

        // Update window title and chat
        client.updateTitle();
        client.gui.getChat().rescaleChat();

        // Update book and advancements screens
        if (client.screen instanceof BookViewScreen bookScreen) {
            ((BookScreenAccessor) bookScreen).languagereload_setCachedPageIndex(-1);
        } else if (client.screen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).languagereload_recreateWidgets();
        }

        if (client.level != null) {
            // Update signs
            var chunkManager = (ClientChunkManagerAccessor) client.level.getChunkSource();
            var chunks = ((ClientChunkMapAccessor)(Object) chunkManager.languagereload_getChunks()).languagereload_getChunks();
            for (int i = 0; i < chunks.length(); i++) {
                var chunk = chunks.get(i);
                if (chunk == null) continue;
                for (var blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                    ((SignTextAccessor) sign.getFrontText()).languagereload_setOrderedMessages(null);
                    ((SignTextAccessor) sign.getBackText()).languagereload_setOrderedMessages(null);
                }
            }

            // Update text displays
            for (var entity : client.level.entitiesForRendering()) {
                if (entity instanceof Display.TextDisplay textDisplay) {
                    ((TextDisplayEntityAccessor) textDisplay).languagereload_setTextLines(null);
                }
            }
        }
    }

    public static void setLanguage(String language, LinkedList<String> fallbacks) {
        var client = Minecraft.getInstance();
        var languageManager = client.getLanguageManager();
        if(!ClientConfig.SPEC.isLoaded())return;


        var languageIsSame = languageManager.getSelected().equals(language);
        var fallbacksAreSame = ClientConfig.fallbacks.equals(fallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        ClientConfig.PREVIOUS_LANGUAGE.set(languageManager.getSelected());
        ClientConfig.PREVIOUS_FALLBACKS.set(ClientConfig.fallbacks);
        ClientConfig.LANGUAGE.set(language);
        ClientConfig.FALLBACKS.set(fallbacks);
        ClientConfig.SPEC.save();

        languageManager.setSelected(language);
        client.options.languageCode = language;
        client.options.save();
        ClientConfig.load();
        reloadLanguages();
    }
}
