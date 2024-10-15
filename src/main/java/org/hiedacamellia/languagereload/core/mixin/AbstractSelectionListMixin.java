package org.hiedacamellia.languagereload.core.mixin;


import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.util.Mth;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @Inject(method = "getEntryAtPosition(DD)Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;", at = @At("RETURN"), cancellable = true)
    private void getEntryAtPosition(double x, double y, CallbackInfoReturnable<LanguageEntry> cir) {
        if(((AbstractSelectionList)(Object)this) instanceof LanguageListWidget){
            int halfRowWidth = ((LanguageListWidget)(Object)this).getRowWidth() / 2;
            int center = ((LanguageListWidget)(Object)this).getX0() + ((LanguageListWidget) (Object) this).getWidth() / 2;
            int minX = center - halfRowWidth;
            int maxX = center + halfRowWidth;
            var scrollbarPositionX = ((LanguageListWidget) (Object) this).getScrollbarPosition();
            int m = Mth.floor(y - ((LanguageListWidget) (Object) this).getY0()) - ((LanguageListWidget) (Object) this).getHeaderHeight() + (int) ((LanguageListWidget) (Object) this).getScrollAmount() - 4 + 2;
            int entryIndex = m / ((LanguageListWidget) (Object) this).getItemHeight();
            cir.setReturnValue (x < scrollbarPositionX && x >= minX && x <= maxX && entryIndex >= 0 && m >= 0
                    && entryIndex <  ((LanguageListWidget) (Object) this).getItemCountA() ? ((LanguageListWidget) (Object) this).getChildren().get(entryIndex) : null);

        }
    }

}
