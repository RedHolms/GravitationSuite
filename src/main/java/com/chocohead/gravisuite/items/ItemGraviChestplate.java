//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;


public class ItemGraviChestplate extends ItemAdvancedElectricJetpack {
  protected static final int DEFAULT_COLOUR = -1;

  public ItemGraviChestplate() {
    super("graviChestplate", 6.0E7, 100000.0, 4);
  }

  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
    return "gravisuite:textures/armour/" + this.name + (type != null ? "Overlay" : "") + ".png";
  }

  @Override
  public void setColor(ItemStack stack, int colour) {
    this.getDisplayNbt(stack, true).setInteger("colour", colour);
  }

  @Override
  public boolean hasColor(ItemStack stack) {
    return this.getColor(stack) != -1;
  }

  @Override
  public int getColor(ItemStack stack) {
    NBTTagCompound nbt = this.getDisplayNbt(stack, false);
    return nbt != null && nbt.hasKey("colour", Constants.NBT.TAG_INT) ? nbt.getInteger("colour") : DEFAULT_COLOUR;
  }

  @Override
  public void removeColor(ItemStack stack) {
    NBTTagCompound nbt = this.getDisplayNbt(stack, false);
    if (nbt != null && nbt.hasKey("colour", Constants.NBT.TAG_INT)) {
      nbt.removeTag("colour");
      if (nbt.hasNoTags()) {
        stack.getTagCompound().removeTag("display");
      }

    }
  }

  protected NBTTagCompound getDisplayNbt(ItemStack stack, boolean create) {
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) {
      if (!create) {
        return null;
      }

      nbt = new NBTTagCompound();
      stack.setTagCompound(nbt);
    }

    NBTTagCompound out;
    if (!nbt.hasKey("display", Constants.NBT.TAG_COMPOUND)) {
      if (!create) {
        return null;
      }

      out = new NBTTagCompound();
      nbt.setTag("display", out);
    } else {
      out = nbt.getCompoundTag("display");
    }

    return out;
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.EPIC;
  }

  @Override
  public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
    super.onArmorTick(world, player, stack);
    player.extinguish();
  }

  @Override
  public boolean isJetpackActive(ItemStack stack) {
    return super.isJetpackActive(stack) && ElectricItem.manager.getCharge(stack) >= 10000.0;
  }

  @Override
  public float getPower(ItemStack stack) {
    return 1.5F;
  }

  @Override
  public float getDropPercentage(ItemStack stack) {
    return 0.01F;
  }

  @Override
  public float getBaseThrust(ItemStack stack, boolean hover) {
    return hover ? 1.0F : 0.5F;
  }

  @Override
  public float getBoostThrust(EntityPlayer player, ItemStack stack, boolean hover) {
    return IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= 834.0 ? (hover ? 0.1F : 0.3F) : 0.0F;
  }

  @Override
  public boolean useBoostPower(ItemStack stack, float boostAmount) {
    return ElectricItem.manager.discharge(stack, 834.0, Integer.MAX_VALUE, true, false, false) > 0.0;
  }

  @Override
  public float getWorldHeightDivisor(ItemStack stack) {
    return 0.91071427F;
  }

  @Override
  public float getHoverMultiplier(ItemStack stack, boolean upwards) {
    return 0.25F;
  }

  @Override
  public float getHoverBoost(EntityPlayer player, ItemStack stack, boolean up) {
    if (IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= 834.0) {
      if (!player.onGround) {
        ElectricItem.manager.discharge(stack, 834.0, Integer.MAX_VALUE, true, false, false);
      }

      return 3.0F;
    } else {
      return 1.0F;
    }
  }

  @Override
  public boolean drainEnergy(ItemStack pack, int amount) {
    return ElectricItem.manager.discharge(pack, 278.0, Integer.MAX_VALUE, true, false, false) > 0.0;
  }

  @Override
  public int getEnergyPerDamage() {
    return 20000;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return 1.1;
  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return true;
  }
}
