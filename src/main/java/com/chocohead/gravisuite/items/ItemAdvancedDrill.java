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
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ItemDrill;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAdvancedDrill extends ItemDrill {
  protected static final Material[] MATERIALS;
  protected static final String NAME = "advancedDrill";

  public ItemAdvancedDrill() {
    super(null, 160, HarvestLevel.Iridium, 45000, 500, 2, ItemAdvancedDrill.DrillMode.NORMAL.drillSpeed);
    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", NAME)).setUnlocalizedName(NAME);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerModels(ItemName name) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("gravisuite:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, NAME), null));
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
    if (IC2.keyboard.isModeSwitchKeyDown(player)) {
      ItemStack stack = StackUtil.get(player, hand);
      if (!world.isRemote) {
        DrillMode mode = readNextDrillMode(stack);
        saveDrillMode(stack, mode);
        Gravisuite.messagePlayer(player, "gravisuite.advancedDrill.mode", mode.colour, mode.translationName);
        this.efficiency = mode.drillSpeed;
        this.operationEnergyCost = mode.energyCost;
      }

      return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    } else {
      return super.onItemRightClick(world, player, hand);
    }
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
    IBlockState state = world.getBlockState(pos);
    return state.getBlock().canHarvestBlock(world, pos, player) && (skipEffectivity || isEffective(state.getMaterial())) && state.getPlayerRelativeBlockHardness(player, world, pos) != 0.0F;
  }

  protected static boolean isEffective(Material material) {
    Material[] materials = MATERIALS;
    int materialsCount = materials.length;

    for(int i = 0; i < materialsCount; ++i) {
      Material option = materials[i];
      if (material == option) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
    World world;
    if (readDrillMode(stack) == ItemAdvancedDrill.DrillMode.BIG_HOLES && !(world = player.world).isRemote) {
      Collection<BlockPos> blocks = getBrokenBlocks(player, this.rayTrace(world, player, true));
      if (!blocks.contains(pos) && canBlockBeMined(world, pos, player, true)) {
        blocks.add(pos);
      }

      boolean powerRanOut = false;
      Iterator var7 = blocks.iterator();

      while(var7.hasNext()) {
        BlockPos blockPos = (BlockPos)var7.next();
        if (!ItemGraviTool.hasNecessaryPower(stack, this.operationEnergyCost, player)) {
          powerRanOut = true;
          break;
        }

        if (world.isBlockLoaded(blockPos)) {
          IBlockState state = world.getBlockState(blockPos);
          Block block = state.getBlock();
          if (!block.isAir(state, world, blockPos)) {
            int experience;
            if (player instanceof EntityPlayerMP) {
              experience = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)player, blockPos);
              if (experience < 0) {
                return false;
              }
            } else {
              experience = 0;
            }

            block.onBlockHarvested(world, blockPos, state, player);
            if (player.isCreative()) {
              if (block.removedByPlayer(state, world, blockPos, player, false)) {
                block.onBlockDestroyedByPlayer(world, blockPos, state);
              }
            } else {
              if (block.removedByPlayer(state, world, blockPos, player, true)) {
                block.onBlockDestroyedByPlayer(world, blockPos, state);
                block.harvestBlock(world, player, blockPos, state, world.getTileEntity(blockPos), stack);
                if (experience > 0) {
                  block.dropXpOnBlockBreak(world, blockPos, experience);
                }
              }

              stack.onBlockDestroyed(world, state, blockPos, player);
            }

            world.playEvent(2001, blockPos, Block.getStateId(state));
            ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, blockPos));
          }
        }
      }

      if (powerRanOut) {
        IC2.platform.messagePlayer(player, "gravisuite.advancedDrill.ranOut");
      }

      return true;
    } else {
      return super.onBlockStartBreak(stack, pos, player);
    }
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
    tooltip.add(TextFormatting.GOLD + Localization.translate("gravisuite.advancedDrill.mode", TextFormatting.WHITE + Localization.translate(readDrillMode(stack).translationName)));
  }

  static {
    MATERIALS = new Material[]{Material.ROCK, Material.GRASS, Material.GROUND, Material.SAND, Material.CLAY};
  }

  public enum DrillMode {
    NORMAL(TextFormatting.DARK_GREEN, 35.0F, 160.0),
    LOW_POWER(TextFormatting.GOLD, 16.0F, 80.0),
    FINE(TextFormatting.AQUA, 10.0F, 50.0),
    BIG_HOLES(TextFormatting.LIGHT_PURPLE, 16.0F, 160.0);

    public final String translationName;
    public final TextFormatting colour;
    public final double energyCost;
    public final float drillSpeed;
    private static final DrillMode[] VALUES = values();

    DrillMode(TextFormatting colour, float speed, double energyCost) {
      this.translationName = "gravisuite.advancedDrill." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
      this.energyCost = energyCost;
      this.drillSpeed = speed;
      this.colour = colour;
    }

    public static DrillMode getFromID(int ID) {
      return VALUES[ID % VALUES.length];
    }
  }
}
