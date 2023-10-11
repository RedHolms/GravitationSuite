//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.google.common.base.CaseFormat;
import ic2.core.init.BlocksItems;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.ItemName;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemGeneralLappack extends ItemArmorElectric {
  protected final String name;
  protected final String modelName;

  protected ItemGeneralLappack(String name, String modelName, double maxCharge, double transferLimit, int tier) {
    super(null, null, EntityEquipmentSlot.CHEST, maxCharge, transferLimit, tier);

    this.name = name;
    this.modelName = modelName;

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", name)).setUnlocalizedName(name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(
      this, 0,
      new ModelResourceLocation("gravisuite:" + this.modelName, null)
    );
  }

  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
    return "gravisuite:textures/armour/" + this.name + ".png";
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return true;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return 0.0;
  }

  @Override
  public int getEnergyPerDamage() {
    return 0;
  }
}
