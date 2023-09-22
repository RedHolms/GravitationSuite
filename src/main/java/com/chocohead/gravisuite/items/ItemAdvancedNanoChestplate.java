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
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAdvancedNanoChestplate extends ItemAdvancedElectricJetpack {
  protected static final ItemStack WATER_CELL = IC2Items.getItem("fluid_cell", "water");
  protected static final ItemStack EMPTY_CELL = IC2Items.getItem("fluid_cell");
  protected static final byte TICK_RATE = 20;
  protected byte ticker;

  public ItemAdvancedNanoChestplate() {
    super("advancedNanoChestplate");
  }

  @Override
  public void onArmorTick(World world, EntityPlayer player, ItemStack armour) {
    super.onArmorTick(world, player, armour);
    byte prevTicker = this.ticker;
    this.ticker = (byte)(prevTicker + 1);
    if (prevTicker % TICK_RATE == 0 && player.isBurning() && ElectricItem.manager.canUse(armour, 50000.0)) {
      Iterator it = player.inventory.mainInventory.iterator();

      while(it.hasNext()) {
        ItemStack stack = (ItemStack)it.next();
        if (!StackUtil.isEmpty(stack) && StackUtil.checkItemEquality(WATER_CELL, stack.copy()) && StackUtil.storeInventoryItem(EMPTY_CELL, player, false)) {
          stack.shrink(1);
          ElectricItem.manager.discharge(stack, 50000.0, Integer.MAX_VALUE, true, false, false);
          player.extinguish();
          break;
        }
      }
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
