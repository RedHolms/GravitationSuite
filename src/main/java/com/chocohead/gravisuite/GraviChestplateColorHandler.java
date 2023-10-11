//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class GraviChestplateColorHandler implements IItemColor {
  static void register() {
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
      new GraviChestplateColorHandler(),
      GraviItem.GRAVI_CHESTPLATE.getInstance()
    );
  }

  @Override
  public int colorMultiplier(ItemStack stack, int tintIndex) {
    ItemArmor armor = (ItemArmor)stack.getItem();
    return tintIndex > 0 ? -1 : armor.getColor(stack);
  }
}
