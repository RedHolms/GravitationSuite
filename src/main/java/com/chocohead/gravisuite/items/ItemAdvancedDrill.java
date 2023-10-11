//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import com.chocohead.gravisuite.Gravisuite;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ItemDrill;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemAdvancedDrill extends ItemDrill {
  protected static final String ITEM_NAME = "advancedDrill";
  protected static final String MODEL_NAME = "advanced_drill";

  protected static final Material[] MATERIALS;

  static {
    MATERIALS = new Material[] {
      Material.ROCK, Material.GRASS, Material.GROUND, Material.SAND, Material.CLAY
    };
  }

  public ItemAdvancedDrill() {
    super(null, 160, HarvestLevel.Iridium, 45000, 500, 2, ItemAdvancedDrill.DrillMode.NORMAL.drillSpeed);

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", ITEM_NAME)).setUnlocalizedName(ITEM_NAME);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(
      this, 0,
      new ModelResourceLocation("gravisuite:" + MODEL_NAME, null)
    );
  }

  public static DrillMode readDrillMode(ItemStack stack) {
    return ItemAdvancedDrill.DrillMode.getFromID(StackUtil.getOrCreateNbtData(stack).getInteger("toolMode"));
  }

  public static DrillMode readNextDrillMode(ItemStack stack) {
    return ItemAdvancedDrill.DrillMode.getFromID(StackUtil.getOrCreateNbtData(stack).getInteger("toolMode") + 1);
  }

  public static void saveDrillMode(ItemStack stack, DrillMode mode) {
    StackUtil.getOrCreateNbtData(stack).setInteger("toolMode", mode.ordinal());
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!IC2.keyboard.isModeSwitchKeyDown(player))
      return super.onItemRightClick(world, player, hand);

    ItemStack itemStack = StackUtil.get(player, hand);

    if (world.isRemote)
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);

    DrillMode drillMode = readNextDrillMode(itemStack);
    saveDrillMode(itemStack, drillMode);

    this.efficiency = drillMode.drillSpeed;
    this.operationEnergyCost = drillMode.energyCost;

    Gravisuite.messagePlayer(player, "gravisuite.advancedDrill.mode", drillMode.color, drillMode.translationName);

    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
  }

  public static Collection<BlockPos> getBrokenBlocks(EntityPlayer player, RayTraceResult ray) {
    return getBrokenBlocks(player, ray.getBlockPos(), ray.sideHit);
  }

  protected static Collection<BlockPos> getBrokenBlocks(EntityPlayer player, BlockPos pos, EnumFacing side) {
    assert side != null;

    int xMove = 1;
    int yMove = 1;
    int zMove = 1;
    switch (side.getAxis()) {
      case X:
        xMove = 0;
        break;
      case Y:
        yMove = 0;
        break;
      case Z:
        zMove = 0;
        break;
    }

    World world = player.world;
    Collection<BlockPos> list = new ArrayList<>(9);

    for(int x = pos.getX() - xMove; x <= pos.getX() + xMove; ++x) {
      for(int y = pos.getY() - yMove; y <= pos.getY() + yMove; ++y) {
        for(int z = pos.getZ() - zMove; z <= pos.getZ() + zMove; ++z) {
          BlockPos potential = new BlockPos(x, y, z);
          if (canBlockBeMined(world, potential, player, false)) {
            list.add(potential);
          }
        }
      }
    }

    return list;
  }

  protected static boolean canBlockBeMined(World world, BlockPos pos, EntityPlayer player, boolean skipEffectivity) {
    IBlockState blockState = world.getBlockState(pos);

    if (!blockState.getBlock().canHarvestBlock(world, pos, player))
      return false;

    if (!skipEffectivity && !isEffective(blockState.getMaterial()))
      return false;

    return blockState.getPlayerRelativeBlockHardness(player, world, pos) != 0.0F;
  }

  protected static boolean isEffective(Material material) {
    for (Material effectiveMaterial : MATERIALS) {
      if (material == effectiveMaterial)
        return true;
    }

    return false;
  }

  @Override
  @ParametersAreNonnullByDefault
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
    World world = player.world;

    if (readDrillMode(stack) != DrillMode.BIG_HOLES || world.isRemote)
      return super.onBlockStartBreak(stack, pos, player);

    Collection<BlockPos> brokenBlocks = getBrokenBlocks(player, this.rayTrace(world, player, true));

    if (!brokenBlocks.contains(pos) && canBlockBeMined(world, pos, player, true))
      brokenBlocks.add(pos);

    boolean powerRanOut = false;

    for (BlockPos brokenBlockPos : brokenBlocks) {
      if (!ItemGraviTool.hasNecessaryPower(stack, this.operationEnergyCost, player)) {
        powerRanOut = true;
        break;
      }

      if (!world.isBlockLoaded(brokenBlockPos))
        continue;

      IBlockState brokenBlockState = world.getBlockState(brokenBlockPos);
      Block brokenBlock = brokenBlockState.getBlock();

      if (brokenBlock.isAir(brokenBlockState, world, brokenBlockPos))
        continue;

      int experience = 0;

      if (player instanceof EntityPlayerMP) {
        EntityPlayerMP playerMP = (EntityPlayerMP)player;

        experience = ForgeHooks.onBlockBreakEvent(
          world, playerMP.interactionManager.getGameType(),
          playerMP, brokenBlockPos
        );

        if (experience < 0)
          return false;
      }

      brokenBlock.onBlockHarvested(world, brokenBlockPos, brokenBlockState, player);

      if (player.isCreative()) {
        if (brokenBlock.removedByPlayer(brokenBlockState, world, brokenBlockPos, player, false))
          brokenBlock.onBlockDestroyedByPlayer(world, brokenBlockPos, brokenBlockState);
      } else {
        if (brokenBlock.removedByPlayer(brokenBlockState, world, brokenBlockPos, player, true)) {
          brokenBlock.onBlockDestroyedByPlayer(world, brokenBlockPos, brokenBlockState);
          brokenBlock.harvestBlock(
            world, player, brokenBlockPos,
            brokenBlockState, world.getTileEntity(brokenBlockPos), stack
          );

          if (experience > 0)
            brokenBlock.dropXpOnBlockBreak(world, brokenBlockPos, experience);
        }

        stack.onBlockDestroyed(world, brokenBlockState, brokenBlockPos, player);
      }

      world.playEvent(2001, brokenBlockPos, Block.getStateId(brokenBlockState));

      // May be bug (casting to EntityPlayerMP)
      ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, brokenBlockPos));
    }

    if (powerRanOut)
      IC2.platform.messagePlayer(player, "gravisuite.advancedDrill.ranOut");

    return true;
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
    tooltip.add(
      TextFormatting.GOLD + Localization.translate(
        "gravisuite.advancedDrill.mode",
        TextFormatting.WHITE + Localization.translate(readDrillMode(stack).translationName)
      )
    );
  }

  public enum DrillMode {
    NORMAL    ("normal", TextFormatting.DARK_GREEN, 35.0F, 160.0),
    LOW_POWER ("lowPower", TextFormatting.GOLD, 16.0F, 80.0),
    FINE      ("fine", TextFormatting.AQUA, 10.0F, 50.0),
    BIG_HOLES ("bigHoles", TextFormatting.LIGHT_PURPLE, 16.0F, 160.0);

    private static final DrillMode[] VALUES = values();

    public final String translationName;
    public final TextFormatting color;
    public final float drillSpeed;
    public final double energyCost;

    DrillMode(String translationName, TextFormatting color, float speed, double energyCost) {
      this.translationName = "gravisuite.advancedDrill." + translationName;
      this.color = color;
      this.drillSpeed = speed;
      this.energyCost = energyCost;
    }

    public static DrillMode getFromID(int id) {
      return VALUES[id % VALUES.length];
    }
  }
}
