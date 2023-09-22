//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.renders.PrettyUtil;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class GraviChestplateColourHandler implements IItemColor {
  static void register() {
    PrettyUtil.mc.getItemColors().registerItemColorHandler(new GraviChestplateColourHandler(), GS_Items.GRAVI_CHESTPLATE.getInstance());
  }

  @Override
  public int colorMultiplier(ItemStack stack, int tintIndex) {
    return tintIndex > 0 ? -1 : ((ItemArmor)stack.getItem()).getColor(stack);
  }
}
