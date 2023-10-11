//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.chocohead.gravisuite.GraviKeys;
import com.chocohead.gravisuite.Gravisuite;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.item.armor.jetpack.IBoostingJetpack;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class ItemAdvancedElectricJetpack extends ItemArmorElectric implements IBoostingJetpack {
  protected final String name;
  protected final String modelName;

  public ItemAdvancedElectricJetpack() {
    this("advancedJetpack", "advanced_jetpack");
  }

  protected ItemAdvancedElectricJetpack(String name, String modelName) {
    this(name, modelName, 3000000.0, 30000.0, 3);
  }

  protected ItemAdvancedElectricJetpack(String name, String modelName, double maxCharge, double transferLimit, int tier) {
    super(null, null, EntityEquipmentSlot.CHEST, maxCharge, transferLimit, tier);

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", name)).setUnlocalizedName(name);

    this.name = name;
    this.modelName = modelName;

    this.setMaxDamage(27);
    this.setMaxStackSize(1);
    this.setNoRepair();
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
  @ParametersAreNonnullByDefault
  public @Nonnull EnumRarity getForgeRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  public static boolean isJetpackOn(ItemStack stack) {
    return StackUtil.getOrCreateNbtData(stack).getBoolean("isFlyActive");
  }

  public static boolean isHovering(ItemStack stack) {
    return StackUtil.getOrCreateNbtData(stack).getBoolean("hoverMode");
  }

  public static boolean switchJetpack(ItemStack stack) {
    NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);

    boolean newMode = !nbt.getBoolean("isFlyActive");
    nbt.setBoolean("isFlyActive", newMode);

    return newMode;
  }

  @Override
  @ParametersAreNullableByDefault
  public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
    assert world != null;
    assert itemStack != null;

    NBTTagCompound nbt = StackUtil.getOrCreateNbtData(itemStack);

    byte toggleTimer = nbt.getByte("toggleTimer");

    if (GraviKeys.isFlyKeyDown(player) && toggleTimer == 0) {
      nbt.setByte("toggleTimer", (byte)10);
      toggleTimer = 10;

      if (!world.isRemote) {
        String state;
        if (switchJetpack(itemStack)) {
          state = TextFormatting.DARK_GREEN + Localization.translate("gravisuite.message.on");
        } else {
          state = TextFormatting.DARK_RED + Localization.translate("gravisuite.message.off");
        }

        Gravisuite.messagePlayer(player, "gravisuite.message.jetpackSwitch", TextFormatting.YELLOW, state);
      }
    }

    if (toggleTimer > 0 && !isJetpackOn(itemStack)) {
      nbt.setByte("toggleTimer", --toggleTimer);
    }
  }

  @Override
  public boolean isJetpackActive(ItemStack stack) {
    return isJetpackOn(stack);
  }

  @Override
  public double getChargeLevel(ItemStack stack) {
    return ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack);
  }

  @Override
  public float getPower(ItemStack stack) {
    return 1.0F;
  }

  @Override
  public float getDropPercentage(ItemStack stack) {
    return 0.05F;
  }

  @Override
  public float getBaseThrust(ItemStack stack, boolean hover) {
    return hover ? 0.65F : 0.3F;
  }

  @Override
  public float getBoostThrust(EntityPlayer player, ItemStack stack, boolean hover) {
    return IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= 60.0 ? (hover ? 0.07F : 0.09F) : 0.0F;
  }

  @Override
  public boolean useBoostPower(ItemStack stack, float boostAmount) {
    return ElectricItem.manager.discharge(stack, 60.0, Integer.MAX_VALUE, true, false, false) > 0.0;
  }

  @Override
  public float getWorldHeightDivisor(ItemStack stack) {
    return 1.0F;
  }

  @Override
  public float getHoverMultiplier(ItemStack stack, boolean upwards) {
    return 0.2F;
  }

  @Override
  public float getHoverBoost(EntityPlayer player, ItemStack stack, boolean up) {
    if (!IC2.keyboard.isBoostKeyDown(player) || !(ElectricItem.manager.getCharge(stack) >= 60.0))
      return 1.0F;

    if (!player.onGround)
      ElectricItem.manager.discharge(stack, 60.0, Integer.MAX_VALUE, true, false, false);

    return 2.0F;
  }

  @Override
  public boolean drainEnergy(ItemStack pack, int amount) {
    return ElectricItem.manager.discharge(pack, amount * 6, Integer.MAX_VALUE, true, false, false) > 0.0;
  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return true;
  }

  @Override
  public int getEnergyPerDamage() {
    return 0;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return 0.0;
  }
}
