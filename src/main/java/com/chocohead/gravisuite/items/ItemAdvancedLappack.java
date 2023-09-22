//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemAdvancedLappack extends ItemGeneralLappack {
  public ItemAdvancedLappack() {
    super("advancedLappack", 3000000.0, 30000.0, 3);
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }
}
