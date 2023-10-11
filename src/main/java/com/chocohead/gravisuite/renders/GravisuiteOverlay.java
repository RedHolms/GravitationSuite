//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.renders;

import com.chocohead.gravisuite.GraviConfig;
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
  public GravisuiteOverlay() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onRender(TickEvent.RenderTickEvent event) {
    Minecraft minecraft = Minecraft.getMinecraft();

    if (!GraviConfig.GravisuiteOverlayEnabled)
      return;

    if (minecraft.world == null)
      return;

    if (!minecraft.inGameHasFocus)
      return;

    if (!Minecraft.isGuiEnabled())
      return;

    if (minecraft.gameSettings.showDebugInfo)
      return;

    ItemStack chestplateStack = minecraft.player.inventory.armorItemInSlot(EntityEquipmentSlot.CHEST.getSlotIndex());
    Item chestplateItem = chestplateStack.getItem();

    FontRenderer fontRenderer = minecraft.fontRenderer;

    String energyLevel;
    int energyLevelWidth;

    String status = null;
    int statusWidth = 0;

    float chargeLevel = -1;

    if (chestplateItem instanceof ItemVajra) {
      chargeLevel = (float)( ElectricItem.manager.getCharge(chestplateStack) * 100.0 );
      chargeLevel /= ElectricItem.manager.getMaxCharge(chestplateStack);
    } else if (chestplateItem instanceof ItemAdvancedElectricJetpack) {
      IJetpack jetpack = (IJetpack)chestplateItem;

      chargeLevel = (float)( jetpack.getChargeLevel(chestplateStack) * 100.0 );

      if (ItemAdvancedElectricJetpack.isJetpackOn(chestplateStack)) {
        String hoverModeStatus = "";

        if (ItemAdvancedElectricJetpack.isHovering(chestplateStack))
          hoverModeStatus = Localization.translate("gravisuite.message.hover");

        status = TextFormatting.GREEN + Localization.translate("gravisuite.message.jetpackEngine", TextFormatting.YELLOW + hoverModeStatus);
        statusWidth = fontRenderer.getStringWidth(Localization.translate("gravisuite.message.jetpackEngine", hoverModeStatus));
      }
    }

    if (chargeLevel == -1)
      return;

    energyLevel = Localization.translate("gravisuite.message.energy", getEnergyStatus(chargeLevel));
    energyLevelWidth = fontRenderer.getStringWidth(Localization.translate("gravisuite.message.energy", Integer.toString(Math.round(chargeLevel))));

    int fontHeight = fontRenderer.FONT_HEIGHT;

    int statusX = 0;
    int energyX = 0;

    int firstLineY = 0;
    int secondLineY = 0;

    ScaledResolution scaledResolution = new ScaledResolution(minecraft);
    int scaledWidth = scaledResolution.getScaledWidth();
    int scaledHeight = scaledResolution.getScaledHeight();

    switch (GraviConfig.GravisuiteOverlayPosition) {
      case 1: // Upper Left
        statusX = 2;
        energyX = 2;

        firstLineY = 2;
        secondLineY = 5 + fontHeight;
        break;
      case 2: // Upper Right
        if (status != null)
          statusX = scaledWidth - statusWidth - 2;

        energyX = scaledWidth - energyLevelWidth - 2;

        firstLineY = 2;
        secondLineY = 5 + fontHeight;
        break;
      case 3: // Bottom Left
        statusX = 2;
        energyX = 2;

        firstLineY = scaledHeight - 2 - fontHeight;
        secondLineY = firstLineY - 3 - fontHeight;
        break;
      case 4: // Bottom Right
        if (status != null)
          statusX = scaledWidth - statusWidth - 2;

        energyX = scaledWidth - energyLevelWidth - 2;

        firstLineY = scaledHeight - 2 - fontHeight;
        secondLineY = firstLineY - 3 - fontHeight;
        break;
      default:
        throw new IllegalStateException("Invalid value of HUD pos: expected 1-4, got " + GraviConfig.GravisuiteOverlayPosition + "!");
    }

    if (status != null) {
      minecraft.ingameGUI.drawString(fontRenderer, status, statusX, firstLineY, 16777215);
      minecraft.ingameGUI.drawString(fontRenderer, energyLevel, energyX, secondLineY, 16777215);
    } else {
      minecraft.ingameGUI.drawString(fontRenderer, energyLevel, energyX, firstLineY, 16777215);
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
