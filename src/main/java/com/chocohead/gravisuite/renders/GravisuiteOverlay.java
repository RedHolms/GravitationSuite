//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.renders;

import com.chocohead.gravisuite.items.ItemAdvancedElectricJetpack;
import com.chocohead.gravisuite.items.ItemVajra;
import ic2.api.item.ElectricItem;
import ic2.core.init.Localization;
import ic2.core.item.armor.jetpack.IJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GravisuiteOverlay {
  public static boolean hudEnabled = true;
  public static byte hudPos = 1;

  public GravisuiteOverlay() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onRender(TickEvent.RenderTickEvent event) {
    Minecraft mc = PrettyUtil.mc;
    if (hudEnabled && mc.world != null && mc.inGameHasFocus && Minecraft.isGuiEnabled() && !mc.gameSettings.showDebugInfo) {
      ItemStack stack = mc.player.inventory.armorItemInSlot(EntityEquipmentSlot.CHEST.getSlotIndex());
      Item item = stack.getItem();

      FontRenderer fontRenderer = mc.fontRenderer;
      String energyLevel = "";
      int energyLevelWidth = 0;
      String status = "";
      int statusWidth = 0;
      float chargeLevel;
      if (item instanceof ItemVajra) {
        chargeLevel = (float)(ElectricItem.manager.getCharge(stack) / ElectricItem.manager.getMaxCharge(stack) * 100.0);
        energyLevel = Localization.translate("gravisuite.message.energy", getEnergyStatus(chargeLevel));
        energyLevelWidth = fontRenderer.getStringWidth(Localization.translate("gravisuite.message.energy", Integer.toString(Math.round(chargeLevel))));
      } else if (item instanceof ItemAdvancedElectricJetpack) {
        chargeLevel = (float)((IJetpack)item).getChargeLevel(stack) * 100.0F;
        energyLevel = Localization.translate("gravisuite.message.energy", getEnergyStatus(chargeLevel));
        energyLevelWidth = fontRenderer.getStringWidth(Localization.translate("gravisuite.message.energy", Integer.toString(Math.round(chargeLevel))));
        if (ItemAdvancedElectricJetpack.isJetpackOn(stack)) {
          String hoverModeStatus = ItemAdvancedElectricJetpack.isHovering(stack) ? Localization.translate("gravisuite.message.hover") : "";
          status = TextFormatting.GREEN + Localization.translate("gravisuite.message.jetpackEngine", TextFormatting.YELLOW + hoverModeStatus);
          statusWidth = fontRenderer.getStringWidth(Localization.translate("gravisuite.message.jetpackEngine", hoverModeStatus));
        }
      }

      if (!energyLevel.isEmpty()) {
        int fontHeight = fontRenderer.FONT_HEIGHT;
        int yOffset = 1;
        int xPos = 0;
        int yPos = 0;
        int xPos2 = 0;
        int yPos2 = 0;
        int width;
        switch (hudPos) {
          case 1:
            xPos = 2;
            xPos2 = 2;
            yPos = 2;
            yPos2 = 5 + fontHeight;
            break;
          case 2:
            width = (new ScaledResolution(mc)).getScaledWidth();
            if (!status.isEmpty()) {
              xPos = width - statusWidth - 2;
            }

            xPos2 = width - energyLevelWidth - 2;
            yPos = 2;
            yPos2 = 5 + fontHeight;
            break;
          case 3:
            xPos = 2;
            xPos2 = 2;
            yPos = (new ScaledResolution(mc)).getScaledHeight() - 2 - fontHeight;
            yPos2 = yPos - 3 - fontHeight;
            break;
          case 4:
            ScaledResolution size = new ScaledResolution(mc);
            width = size.getScaledWidth();
            if (!status.isEmpty()) {
              xPos = width - statusWidth - 2;
            }

            xPos2 = width - energyLevelWidth - 2;
            yPos = size.getScaledHeight() - 2 - fontHeight;
            yPos2 = yPos - 3 - fontHeight;
            break;
          default:
            throw new IllegalStateException("Invalid value of HUD pos: expected 1-4, got " + hudPos + '!');
        }

        if (!status.isEmpty()) {
          mc.ingameGUI.drawString(fontRenderer, status, xPos, yPos, 16777215);
          mc.ingameGUI.drawString(fontRenderer, energyLevel, xPos2, yPos2, 16777215);
        } else {
          mc.ingameGUI.drawString(fontRenderer, energyLevel, xPos2, yPos, 16777215);
        }
      }
    }

  }

  public static String getEnergyStatus(float energyStatus) {
    if (energyStatus <= 10.0F) {
      return energyStatus <= 5.0F ? TextFormatting.RED + Integer.toString(Math.round(energyStatus)) + '%' : TextFormatting.GOLD + Integer.toString(Math.round(energyStatus)) + '%';
    } else {
      return Integer.toString(Math.round(energyStatus)) + '%';
    }
  }
}
