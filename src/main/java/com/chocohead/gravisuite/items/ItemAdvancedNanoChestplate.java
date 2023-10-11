//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAdvancedNanoChestplate extends ItemAdvancedElectricJetpack {
  protected byte ticker;

  protected static final ItemStack WATER_CELL = IC2Items.getItem("fluid_cell", "water");
  protected static final ItemStack EMPTY_CELL = IC2Items.getItem("fluid_cell");
  protected static final byte TICK_RATE = 20;

  public ItemAdvancedNanoChestplate() {
    super("advancedNanoChestplate", "advanced_nano_chestplate");
  }

  @Override
  public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
    super.onArmorTick(world, player, itemStack);

    byte prevTicker = this.ticker;
    this.ticker = (byte)(prevTicker + 1);

    if (prevTicker % TICK_RATE != 0)
      return;

    if (!player.isBurning())
      return;

    if (!ElectricItem.manager.canUse(itemStack, 50000.0))
      return;

    for (ItemStack fluidCell : player.inventory.mainInventory) {
      if (StackUtil.isEmpty(fluidCell))
        continue;

      if (!StackUtil.checkItemEquality(WATER_CELL, fluidCell.copy()))
        continue;

      if (!StackUtil.storeInventoryItem(EMPTY_CELL, player, false))
        continue;

      fluidCell.shrink(1);
      player.extinguish();

      ElectricItem.manager.discharge(fluidCell, 50000.0, Integer.MAX_VALUE, true, false, false);

      break;
    }
  }

  @Override
  public int getEnergyPerDamage() {
    return 800;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return 0.9;
  }
}
