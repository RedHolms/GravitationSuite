//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.chocohead.gravisuite.Gravisuite;
import com.google.common.base.CaseFormat;
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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
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

public class ItemAdvancedChainsaw extends ItemElectricTool {
  protected static final String NAME = "advancedChainsaw";

  public ItemAdvancedChainsaw() {
    super(null, 100, HarvestLevel.Iron, EnumSet.of(ToolClass.Axe, ToolClass.Sword, ToolClass.Shears));
    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", NAME)).setUnlocalizedName(NAME);
    this.maxCharge = 45000;
    this.transferLimit = 500;
    this.tier = 2;
    this.efficiency = 30.0F;
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("gravisuite:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, NAME), null));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote && IC2.keyboard.isModeSwitchKeyDown(player)) {
      ItemStack stack = StackUtil.get(player, hand);
      NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
      if (nbt.getBoolean("disableShear")) {
        nbt.setBoolean("disableShear", false);
        Gravisuite.messagePlayer(player, "gravisuite.advancedChainsaw.shear", TextFormatting.DARK_GREEN, Localization.translate("gravisuite.message.on"));
      } else {
        nbt.setBoolean("disableShear", true);
        Gravisuite.messagePlayer(player, "gravisuite.advancedChainsaw.shear", TextFormatting.DARK_RED, Localization.translate("gravisuite.message.off"));
      }

      return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    } else {
      return super.onItemRightClick(world, player, hand);
    }
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    ElectricItem.manager.use(stack, this.operationEnergyCost, attacker);
    if (attacker instanceof EntityPlayer && target instanceof EntityCreeper && target.getHealth() <= 0.0F) {
      IC2.achievements.issueAchievement((EntityPlayer)attacker, "killCreeperChainsaw");
    }

    return true;
  }

  @SubscribeEvent
  public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();
    if (!player.world.isRemote) {
      Entity entity = event.getTarget();
      ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
      if (stack != null && stack.getItem() == this && entity instanceof IShearable && !StackUtil.getOrCreateNbtData(stack).getBoolean("disableShear") && ElectricItem.manager.use(stack, this.operationEnergyCost, player)) {
        IShearable target = (IShearable)entity;
        BlockPos pos = new BlockPos(entity);
        if (target.isShearable(stack, entity.world, pos)) {
          List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

          EntityItem item;
          for(Iterator var8 = drops.iterator(); var8.hasNext(); item.motionZ += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F)) {
            ItemStack drop = (ItemStack)var8.next();
            item = entity.entityDropItem(drop, 1.0F);
            item.motionY += (double)(itemRand.nextFloat() * 0.05F);
            item.motionX += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F);
          }
        }
      }

    }
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
    World world = player.world;
    if (world.isRemote) {
      return false;
    } else if (StackUtil.getOrCreateNbtData(stack).getBoolean("disableShear")) {
      return false;
    } else {
      Block block = world.getBlockState(pos).getBlock();
      if (block instanceof IShearable) {
        IShearable target = (IShearable)block;
        if (target.isShearable(stack, world, pos) && ElectricItem.manager.use(stack, this.operationEnergyCost, player)) {
          List<ItemStack> drops = target.onSheared(stack, world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
          Iterator var8 = drops.iterator();

          while(var8.hasNext()) {
            ItemStack drop = (ItemStack)var8.next();
            StackUtil.dropAsEntity(world, pos, drop);
          }

          player.addStat(StatList.getBlockStats(block), 1);
        }
      }

      return false;
    }
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
    if (StackUtil.getOrCreateNbtData(stack).getBoolean("disableShear")) {
      tooltip.add(TextFormatting.DARK_RED + Localization.translate("gravisuite.advancedChainsaw.shear", Localization.translate("gravisuite.message.off")));
    } else {
      tooltip.add(TextFormatting.DARK_GREEN + Localization.translate("gravisuite.advancedChainsaw.shear", Localization.translate("gravisuite.message.on")));
    }

  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
    if (slot != EntityEquipmentSlot.MAINHAND) {
      return super.getAttributeModifiers(slot, stack);
    } else {
      Multimap<String, AttributeModifier> ret = HashMultimap.create();
      if (ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 13.0, 0));
      }

      return ret;
    }
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
