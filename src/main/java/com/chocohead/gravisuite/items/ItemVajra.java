//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemVajra extends ItemElectricTool {
  protected static final String NAME = "vajra";
  public static boolean accurateEnabled = true;

  public ItemVajra() {
    super(null, 3333, HarvestLevel.Iridium, EnumSet.of(ToolClass.Pickaxe, ToolClass.Shovel, ToolClass.Axe));
    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", NAME)).setUnlocalizedName(NAME);
    this.maxCharge = 10000000;
    this.transferLimit = 60000;
    this.tier = 3;
    this.efficiency = 20000.0F;
  }

  @SideOnly(Side.CLIENT)
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("gravisuite:" + NAME, null));
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
    if (StackUtil.getOrCreateNbtData(stack).getBoolean("accurate")) {
      tooltip.add(TextFormatting.GOLD + Localization.translate("gravisuite.vajra.silkTouch", TextFormatting.DARK_GREEN + Localization.translate("gravisuite.message.on")));
    } else {
      tooltip.add(TextFormatting.GOLD + Localization.translate("gravisuite.vajra.silkTouch", TextFormatting.DARK_RED + Localization.translate("gravisuite.message.off")));
    }

  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote && IC2.keyboard.isModeSwitchKeyDown(player)) {
      ItemStack stack = StackUtil.get(player, hand);
      NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
      if (nbt.getBoolean("accurate")) {
        nbt.setBoolean("accurate", false);
        Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouch", TextFormatting.DARK_RED, Localization.translate("gravisuite.message.off"));
      } else if (accurateEnabled) {
        nbt.setBoolean("accurate", true);
        Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouch", TextFormatting.DARK_GREEN, Localization.translate("gravisuite.message.on"));
      } else {
        Gravisuite.messagePlayer(player, "gravisuite.vajra.silkTouchDisabled", TextFormatting.DARK_RED);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
    } else {
      return super.onItemRightClick(world, player, hand);
    }
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
    World world;
    if (accurateEnabled && StackUtil.getOrCreateNbtData(stack).getBoolean("accurate") && !(world = player.getEntityWorld()).isRemote && ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
      stack.addEnchantment(Enchantments.SILK_TOUCH, 10);
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      boolean didHarvest = false;
      if (!block.isAir(state, world, pos) && block.canHarvestBlock(world, pos, player)) {
        didHarvest = true;
        int experience;
        if (player instanceof EntityPlayerMP) {
          experience = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)player, pos);
          if (experience < 0) {
            didHarvest = false;
          }
        } else {
          experience = 0;
        }

        if (didHarvest) {
          block.onBlockHarvested(world, pos, state, player);
          if (player.isCreative()) {
            if (block.removedByPlayer(state, world, pos, player, false)) {
              block.onBlockDestroyedByPlayer(world, pos, state);
            }
          } else {
            if (block.removedByPlayer(state, world, pos, player, true)) {
              block.onBlockDestroyedByPlayer(world, pos, state);
              block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
              if (experience > 0) {
                block.dropXpOnBlockBreak(world, pos, experience);
              }
            }

            stack.onBlockDestroyed(world, state, pos, player);
          }
        }

        if (didHarvest) {
          ElectricItem.manager.use(stack, this.operationEnergyCost, player);
          world.playEvent(2001, pos, Block.getStateId(state));
          ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, pos));
        }
      }

      Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
      enchants.remove(Enchantments.SILK_TOUCH);
      EnchantmentHelper.setEnchantments(enchants, stack);
      return didHarvest;
    } else {
      return super.onBlockStartBreak(stack, pos, player);
    }
  }

  protected ItemStack getStack(IBlockState state) {
    Item item = Item.getItemFromBlock(state.getBlock());
    return item == null ? null : new ItemStack(item, 1, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
  }

  public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
    return state.getBlock() != Blocks.BEDROCK;
  }

  public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker) {
    if (attacker instanceof EntityPlayer) {
      if (ElectricItem.manager.use(itemstack, this.operationEnergyCost * 2.0, attacker)) {
        target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 25.0F);
      } else {
        target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 1.0F);
      }
    }

    return true;
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.EPIC;
  }
}
