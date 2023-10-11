//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.chocohead.gravisuite.Gravisuite;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.item.tool.ToolClass;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class ItemAdvancedChainsaw extends ItemElectricTool {
  protected static final String ITEM_NAME = "advancedChainsaw";
  protected static final String MODEL_NAME = "advanced_chainsaw";

  public ItemAdvancedChainsaw() {
    super(null, 100, HarvestLevel.Iron, EnumSet.of(ToolClass.Axe, ToolClass.Sword, ToolClass.Shears));

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", ITEM_NAME)).setUnlocalizedName(ITEM_NAME);

    this.maxCharge = 45000;
    this.transferLimit = 500;
    this.tier = 2;
    this.efficiency = 30.0F;

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(
      this, 0,
      new ModelResourceLocation("gravisuite:" + MODEL_NAME, null)
    );
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (world.isRemote)
      return super.onItemRightClick(world, player, hand);

    if (!IC2.keyboard.isModeSwitchKeyDown(player))
      return super.onItemRightClick(world, player, hand);

    ItemStack itemStack = StackUtil.get(player, hand);

    NBTTagCompound nbt = StackUtil.getOrCreateNbtData(itemStack);

    boolean disableShear = nbt.getBoolean("disableShear");

    disableShear = !disableShear;
    nbt.setBoolean("disableShear", disableShear);

    if (disableShear)
      Gravisuite.messagePlayer(player, "gravisuite.advancedChainsaw.shear", TextFormatting.DARK_RED, Localization.translate("gravisuite.message.off"));
    else
      Gravisuite.messagePlayer(player, "gravisuite.advancedChainsaw.shear", TextFormatting.DARK_GREEN, Localization.translate("gravisuite.message.on"));

    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    ElectricItem.manager.use(stack, this.operationEnergyCost, attacker);

    if (
      attacker instanceof EntityPlayer &&
      target instanceof EntityCreeper &&
      target.getHealth() <= 0.0F
    )
      IC2.achievements.issueAchievement((EntityPlayer)attacker, "killCreeperChainsaw");

    return true;
  }

  @SubscribeEvent
  public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();

    if (player.world.isRemote)
      return;

    Entity entity = event.getTarget();
    ItemStack itemStack = player.inventory.getStackInSlot(player.inventory.currentItem);

    if (itemStack.getItem() != this)
      return;

    if (!(entity instanceof IShearable))
      return;

    if (StackUtil.getOrCreateNbtData(itemStack).getBoolean("disableShear"))
      return;

    if (!ElectricItem.manager.use(itemStack, this.operationEnergyCost, player))
      return;

    IShearable shearable = (IShearable)entity;
    BlockPos entityPosition = new BlockPos(entity);

    if (!shearable.isShearable(itemStack, entity.world, entityPosition))
      return;

    List<ItemStack> drops = shearable.onSheared(
      itemStack, entity.world, entityPosition,
      EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStack)
    );

    for (ItemStack drop : drops) {
      EntityItem item = entity.entityDropItem(drop, 1.0F);

      if (item == null)
        continue;

      item.motionY += itemRand.nextFloat() * 0.05F;
      item.motionX += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
      item.motionZ += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
    }
  }

  @Override
  @ParametersAreNonnullByDefault
  public boolean onBlockStartBreak(ItemStack itemStack, BlockPos pos, EntityPlayer player) {
    World world = player.world;

    if (world.isRemote)
      return false;

    if (StackUtil.getOrCreateNbtData(itemStack).getBoolean("disableShear"))
      return false;

    Block block = world.getBlockState(pos).getBlock();

    if (block instanceof IShearable) {
      IShearable shearable = (IShearable)block;

      if (!shearable.isShearable(itemStack, world, pos))
        return false;

      if (!ElectricItem.manager.use(itemStack, this.operationEnergyCost, player))
        return false;

      List<ItemStack> drops = shearable.onSheared(
        itemStack, world, pos,
        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStack)
      );

      for (ItemStack drop : drops) {
        StackUtil.dropAsEntity(world, pos, drop);
      }

      player.addStat(
        Objects.requireNonNull(StatList.getBlockStats(block)), 1
      );
    }

    return false;
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

  @Override
  @SideOnly(Side.CLIENT)
  @ParametersAreNonnullByDefault
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
    if (StackUtil.getOrCreateNbtData(stack).getBoolean("disableShear")) {
      tooltip.add(TextFormatting.DARK_RED + Localization.translate("gravisuite.advancedChainsaw.shear", Localization.translate("gravisuite.message.off")));
    } else {
      tooltip.add(TextFormatting.DARK_GREEN + Localization.translate("gravisuite.advancedChainsaw.shear", Localization.translate("gravisuite.message.on")));
    }
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
    if (slot != EntityEquipmentSlot.MAINHAND)
      return super.getAttributeModifiers(slot, stack);

    Multimap<String, AttributeModifier> modifiers = HashMultimap.create();

    if (ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
      modifiers.put(
        SharedMonsterAttributes.ATTACK_SPEED.getName(),
        new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", this.attackSpeed, 0)
      );

      modifiers.put(
        SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
        new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 13.0, 0)
      );
    }

    return modifiers;
  }

  @Override
  protected String getIdleSound(EntityLivingBase player, ItemStack stack) {
    return "Tools/Chainsaw/ChainsawIdle.ogg";
  }

  @Override
  protected String getStopSound(EntityLivingBase player, ItemStack stack) {
    return "Tools/Chainsaw/ChainsawStop.ogg";
  }
}
