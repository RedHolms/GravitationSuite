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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemUltimateLappack extends ItemGeneralLappack {
  public ItemUltimateLappack() {
    super("ultimateLappack", "ultimate_lappack", 6.0E7, 100000.0, 4);
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull EnumRarity getForgeRarity(ItemStack stack) {
    return EnumRarity.RARE;
  }
}
