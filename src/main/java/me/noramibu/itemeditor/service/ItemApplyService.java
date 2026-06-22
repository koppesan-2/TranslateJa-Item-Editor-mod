package me.noramibu.itemeditor.service;

import me.noramibu.itemeditor.util.ItemEditorText;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class ItemApplyService {

    public ApplyResult apply(Minecraft minecraft, ItemStack stack) {
        if (minecraft.player == null) {
            return ApplyResult.failure(ItemEditorText.str("apply.no_player"));
        }

        int selectedSlot = minecraft.player.getInventory().getSelectedSlot();
        ItemStack previous = minecraft.player.getInventory().getItem(selectedSlot).copy();
        ItemStack copy = stack.copy();

        minecraft.player.getInventory().setItem(selectedSlot, copy.copy());
        if (ClientInventorySyncService.syncSlot(minecraft, selectedSlot, copy)) {
            if (minecraft.getSingleplayerServer() == null) {
                return ApplyResult.success(ItemEditorText.str("apply.creative_success"));
            }
            return ApplyResult.success(ItemEditorText.str("apply.singleplayer_success"));
        }
        minecraft.player.getInventory().setItem(selectedSlot, previous);

        return ApplyResult.failure(ItemEditorText.str("apply.multiplayer_preview_only"));
    }

    public record ApplyResult(boolean success, String message) {
        public static ApplyResult success(String message) {
            return new ApplyResult(true, message);
        }

        public static ApplyResult failure(String message) {
            return new ApplyResult(false, message);
        }
    }
}
