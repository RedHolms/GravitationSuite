//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.chocohead.gravisuite.GraviConfig;
import com.chocohead.gravisuite.Gravisuite;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ItemVajra extends ItemElectricTool {
  protected static final String ITEM_NAME = "vajra";
  protected static final String MODEL_NAME = "vajra";

  public ItemVajra() {
    super(null, 3333, HarvestLevel.Iridium, EnumSet.of(ToolClass.Pickaxe, ToolClass.Shovel, ToolClass.Axe));

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", ITEM_NAME)).setUnlocalizedName(ITEM_NAME);

    this.maxCharge = 10000000;
    this.transferLimit = 60000;
    this.tier = 3;
    this.efficiency = 20000.0F;
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
  @SideOnly(Side.CLIENT)
  @ParametersAreNonnullByDefault
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
    if (StackUtil.getOrCreateNbtData(stack).getBoolean("accurate"))
      tooltip.add(TextFormatting.GOLD + Localization.translate("gravisuite.vajra.silkTouch", TextFormatting.DARK_GREEN + Localization.translate("gravisuite.message.on")));
    else
      tooltip.add(TextFormatting.GOLD + Localization.translate("gravisuite.vajra.silkTouch", TextFormatting.DARK_RED + Localization.translate("gravisuite.message.off")));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (world.isRemote || !IC2.keyboard.isModeSwitchKeyDown(player))
      return super.onItemRightClick(world, player, hand);

    ItemStack itemStack = StackUtil.get(player, hand);

    NBTTagCompound nbt = StackUtil.getOrCreateNbtData(itemStack);

    boolean newAccurateMode = !nbt.getBoolean("accurate");

    if (newAccurateMode) {
      if (GraviConfig.VajraAccurateModeDisabled) {
        newAccurateMode = false;
        Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouchDisabled", TextFormatting.DARK_RED);
      } else {
        Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouch", TextFormatting.DARK_GREEN, Localization.translate("gravisuite.message.on"));
      }
    } else {
      Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouch", TextFormatting.DARK_RED, Localization.translate("gravisuite.message.off"));
    }

    nbt.setBoolean("accurate", newAccurateMode);

    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
  }

  @Override
  @ParametersAreNonnullByDefault
  public boolean onBlockStartBreak(ItemStack itemStack, BlockPos blockPos, EntityPlayer player) {
    World world = player.getEntityWorld();

    if (GraviConfig.VajraAccurateModeDisabled || !StackUtil.getOrCreateNbtData(itemStack).getBoolean("accurate"))
      return super.onBlockStartBreak(itemStack, blockPos, player);

    if (world.isRemote)
      return super.onBlockStartBreak(itemStack, blockPos, player);

    if (!ElectricItem.manager.canUse(itemStack, this.operationEnergyCost))
      return super.onBlockStartBreak(itemStack, blockPos, player);

    itemStack.addEnchantment(Enchantments.SILK_TOUCH, 10);

    IBlockState blockState = world.getBlockState(blockPos);
    Block block = blockState.getBlock();

    boolean didHarvest = false;

    if (!block.isAir(blockState, world, blockPos) && block.canHarvestBlock(world, blockPos, player)) {
      didHarvest = true;

      int experience = 0;
      if (player instanceof EntityPlayerMP) {
        EntityPlayerMP playerMP = (EntityPlayerMP)player;

        experience = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, blockPos);

        if (experience < 0)
          didHarvest = false;
      }

      if (didHarvest) {
        block.onBlockHarvested(world, blockPos, blockState, player);

        if (player.isCreative()) {
          if (block.removedByPlayer(blockState, world, blockPos, player, false))
            block.onBlockDestroyedByPlayer(world, blockPos, blockState);
        } else {
          if (block.removedByPlayer(blockState, world, blockPos, player, true)) {
            block.onBlockDestroyedByPlayer(world, blockPos, blockState);
            block.harvestBlock(world, player, blockPos, blockState, world.getTileEntity(blockPos), itemStack);

            if (experience > 0)
              block.dropXpOnBlockBreak(world, blockPos, experience);
          }

          itemStack.onBlockDestroyed(world, blockState, blockPos, player);
        }

        ElectricItem.manager.use(itemStack, this.operationEnergyCost, player);

        world.playEvent(2001, blockPos, Block.getStateId(blockState));

        ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, blockPos));
      }
    }

    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
    enchantments.remove(Enchantments.SILK_TOUCH);

    EnchantmentHelper.setEnchantments(enchantments, itemStack);

    return didHarvest;
  }

  public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
    return state.getBlock() != Blocks.BEDROCK;
  }

  public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker) {
    if (!(attacker instanceof EntityPlayer))
      return true;

    if (ElectricItem.manager.use(itemstack, this.operationEnergyCost * 2.0, attacker))
      target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 25.0F);
    else
      target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 1.0F);

    return true;
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull EnumRarity getForgeRarity(ItemStack stack) {
    return EnumRarity.EPIC;
  }
}
