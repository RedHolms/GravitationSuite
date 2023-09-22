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
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBarrel;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.tool.ItemTreetap;
import ic2.core.ref.BlockName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGraviTool extends ItemTool implements IElectricItem, IItemModelProvider {
  protected static final String NAME = "graviTool";
  protected static final double ROTATE = 50.0;
  protected static final double HOE = 50.0;
  protected static final double TAP = 50.0;
  protected static final double SCREW = 500.0;

  public ItemGraviTool() {
    super(ToolMaterial.IRON, Collections.emptySet());
    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", NAME)).setUnlocalizedName(NAME);
    this.setMaxDamage(27);
    this.setCreativeTab(IC2.tabIC2);
    this.efficiency = 16.0F;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerModels(ItemName name) {
    ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        GraviToolMode mode;
        if (ItemGraviTool.hasToolMode(stack)) {
          mode = ItemGraviTool.readToolMode(stack);
        } else {
          mode = ItemGraviTool.GraviToolMode.HOE;
        }

        return mode.model;
      }
    });
    GraviToolMode[] modes = ItemGraviTool.GraviToolMode.VALUES;
    int modesCount = modes.length;

    for(int i = 0; i < modesCount; ++i) {
      GraviToolMode mode = modes[i];
      ModelBakery.registerItemVariants(this, mode.model);
    }

  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean isFull3D() {
    return true;
  }

  public static boolean hasToolMode(ItemStack stack) {
    return stack.hasTagCompound() && stack.getTagCompound().hasKey("toolMode", Constants.NBT.TAG_INT);
  }

  public static GraviToolMode readToolMode(ItemStack stack) {
    return ItemGraviTool.GraviToolMode.getFromID(StackUtil.getOrCreateNbtData(stack).getInteger("toolMode"));
  }

  public static GraviToolMode readNextToolMode(ItemStack stack) {
    return ItemGraviTool.GraviToolMode.getFromID(StackUtil.getOrCreateNbtData(stack).getInteger("toolMode") + 1);
  }

  public static void saveToolMode(ItemStack stack, GraviToolMode mode) {
    StackUtil.getOrCreateNbtData(stack).setInteger("toolMode", mode.ordinal());
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(5);
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return this.getUnlocalizedName();
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    return hasToolMode(stack) ? Localization.translate("gravisuite.graviTool.set", Localization.translate(this.getUnlocalizedName(stack)), Localization.translate(readToolMode(stack).translationName)) : Localization.translate(this.getUnlocalizedName(stack));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (IC2.keyboard.isModeSwitchKeyDown(player)) {
      ItemStack stack = StackUtil.get(player, hand);
      if (world.isRemote) {
        IC2.audioManager.playOnce(player, PositionSpec.Hand, "gravisuite:toolChange.ogg", true, IC2.audioManager.getDefaultVolume());
      } else {
        GraviToolMode mode = readNextToolMode(stack);
        saveToolMode(stack, mode);
        Gravisuite.messagePlayer(player, "gravisuite.graviTool.changeTool", mode.colour, mode.translationName);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
    } else {
      return super.onItemRightClick(world, player, hand);
    }
  }

  @Override
  public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    ItemStack stack = StackUtil.get(player, hand);
    switch (readToolMode(stack)) {
      case WRENCH:
        return this.onWrenchUse(stack, player, world, pos, side) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      case SCREWDRIVER:
        return this.onScrewdriverUse(stack, player, world, pos) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      default:
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = StackUtil.get(player, hand);
    switch (readToolMode(stack)) {
      case HOE:
        return this.onHoeUse(stack, player, world, pos, facing) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      case TREETAP:
        return this.onTreeTapUse(stack, player, world, pos, facing) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      default:
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
  }

  protected boolean onHoeUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    if (player.canPlayerEdit(pos.offset(side), side, stack) && hasNecessaryPower(stack, HOE, player)) {
      UseHoeEvent event = new UseHoeEvent(player, stack, world, pos);
      if (MinecraftForge.EVENT_BUS.post(event)) {
        return false;
      } else if (event.getResult() == Result.ALLOW) {
        return checkNecessaryPower(stack, HOE, player, true);
      } else {
        IBlockState state = Util.getBlockState(world, pos);
        Block block = state.getBlock();
        if (side != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
          if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            return this.setHoedBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());
          }

          if (block == Blocks.DIRT) {
            switch ((BlockDirt.DirtType)state.getValue(BlockDirt.VARIANT)) {
              case DIRT:
                return this.setHoedBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());
              case COARSE_DIRT:
                return this.setHoedBlock(stack, player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
            }
          }
        }

        return false;
      }
    } else {
      return false;
    }
  }

  protected boolean setHoedBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state) {
    if (checkNecessaryPower(stack, HOE, player, true)) {
      world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (!world.isRemote) {
        world.setBlockState(pos, state, 11);
      }

      return true;
    } else {
      return false;
    }
  }

  protected boolean onTreeTapUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    IBlockState state = Util.getBlockState(world, pos);
    TileEntity te;
    if (side.getAxis() != Axis.Y && (te = world.getTileEntity(pos)) instanceof TileEntityBarrel) {
      TileEntityBarrel barrel = (TileEntityBarrel)te;
      if (!barrel.getActive()) {
        if (checkNecessaryPower(stack, TAP, player, true)) {
          if (!world.isRemote) {
            barrel.setActive(true);
            barrel.onPlaced(stack, null, side.getOpposite());
          }

          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else if (state.getBlock() == BlockName.rubber_wood.getInstance() && hasNecessaryPower(stack, TAP, player)) {
      return ItemTreetap.attemptExtract(player, world, pos, side, state, null) && checkNecessaryPower(stack, TAP, player);
    } else {
      return false;
    }
  }

  protected boolean onWrenchUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    IBlockState state = Util.getBlockState(world, pos);
    Block block = state.getBlock();
    if (block.isAir(state, world, pos)) {
      return false;
    } else {
      if (block instanceof IWrenchable) {
        IWrenchable wrenchable = (IWrenchable)block;
        EnumFacing current = wrenchable.getFacing(world, pos);
        EnumFacing newFacing;
        if (!IC2.keyboard.isAltKeyDown(player)) {
          if (player.isSneaking()) {
            newFacing = side.getOpposite();
          } else {
            newFacing = side;
          }
        } else {
          EnumFacing.Axis axis = side.getAxis();
          if ((player.isSneaking() || side.getAxisDirection() != AxisDirection.POSITIVE) && (!player.isSneaking() || side.getAxisDirection() != AxisDirection.NEGATIVE)) {
            newFacing = current.rotateAround(axis).rotateAround(axis).rotateAround(axis);
          } else {
            newFacing = current.rotateAround(axis);
          }
        }

        if (current != newFacing) {
          if (!hasNecessaryPower(stack, ROTATE, player)) {
            return false;
          }

          if (wrenchable.setFacing(world, pos, newFacing, player)) {
            return checkNecessaryPower(stack, ROTATE, player);
          }
        }

        if (wrenchable.wrenchCanRemove(world, pos, player)) {
          if (!hasNecessaryPower(stack, ROTATE, player)) {
            return false;
          }

          if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (ConfigUtil.getBool(MainConfig.get(), "protection/wrenchLogging")) {
              IC2.log.info(LogCategory.PlayerActivity, "Player %s used a wrench to remove the %s (%s) at %s.", player.getGameProfile().getName() + "/" + player.getGameProfile().getId(), state, te != null ? te.getClass().getSimpleName().replace("TileEntity", "") : "no te", Util.formatPosition(world, pos));
            }

            int experience;
            if (player instanceof EntityPlayerMP) {
              experience = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)player, pos);
              if (experience < 0) {
                return false;
              }
            } else {
              experience = 0;
            }

            block.onBlockHarvested(world, pos, state, player);
            if (!block.removedByPlayer(state, world, pos, player, true)) {
              return false;
            }

            block.onBlockDestroyedByPlayer(world, pos, state);
            List<ItemStack> var13 = wrenchable.getWrenchDrops(world, pos, state, te, player, 0);
            Iterator<ItemStack> var14 = var13.iterator();

            while(var14.hasNext()) {
              ItemStack drop = (ItemStack)var14.next();
              StackUtil.dropAsEntity(world, pos, drop);
            }

            if (!player.capabilities.isCreativeMode && experience > 0) {
              block.dropXpOnBlockBreak(world, pos, experience);
            }
          }

          return checkNecessaryPower(stack, ROTATE, player);
        }
      }

      return false;
    }
  }

  protected boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
    IBlockState state = Util.getBlockState(world, pos);
    Block block = state.getBlock();
    if (!block.isAir(state, world, pos) && block instanceof BlockHorizontal && checkNecessaryPower(stack, 500.0, player)) {
      EnumFacing facing = (EnumFacing)state.getValue(BlockHorizontal.FACING);
      if (player.isSneaking()) {
        facing = facing.rotateYCCW();
      } else {
        facing = facing.rotateY();
      }

      world.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, facing));
      return true;
    } else {
      return false;
    }
  }

  public static boolean hasNecessaryPower(ItemStack stack, double usage, EntityPlayer player) {
    ElectricItem.manager.chargeFromArmor(stack, player);
    return Util.isSimilar(ElectricItem.manager.discharge(stack, usage, Integer.MAX_VALUE, true, false, true), usage);
  }

  protected static boolean checkNecessaryPower(ItemStack stack, double usage, EntityPlayer player) {
    return checkNecessaryPower(stack, usage, player, false);
  }

  protected static boolean checkNecessaryPower(ItemStack stack, double usage, EntityPlayer player, boolean supressSound) {
    if (ElectricItem.manager.use(stack, usage, player)) {
      if (!supressSound && player.world.isRemote) {
        IC2.audioManager.playOnce(player, PositionSpec.Hand, "gravisuite:wrench.ogg", true, IC2.audioManager.getDefaultVolume());
      }

      return true;
    } else {
      IC2.platform.messagePlayer(player, Localization.translate("gravisuite.graviTool.noEnergy"));
      return false;
    }
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return false;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    return true;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public boolean isRepairable() {
    return false;
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return false;
  }

  @Override
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
    return HashMultimap.create();
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if (this.isInCreativeTab(tab)) {
      ElectricItemManager.addChargeVariants(this, items);
    }

  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return false;
  }

  @Override
  public double getMaxCharge(ItemStack stack) {
    return 300000.0;
  }

  @Override
  public int getTier(ItemStack stack) {
    return 2;
  }

  @Override
  public double getTransferLimit(ItemStack stack) {
    return 10000.0;
  }

  public enum GraviToolMode {
    HOE(TextFormatting.DARK_GREEN),
    TREETAP(TextFormatting.GOLD),
    WRENCH(TextFormatting.AQUA),
    SCREWDRIVER(TextFormatting.LIGHT_PURPLE);

    private final ModelResourceLocation model;
    public final String translationName;
    public final TextFormatting colour;
    private static final GraviToolMode[] VALUES = values();

    GraviToolMode(TextFormatting colour) {
      this.model = new ModelResourceLocation("gravisuite:" + NAME.toLowerCase(Locale.ENGLISH) + '/' + this.name().toLowerCase(Locale.ENGLISH), null);
      this.translationName = "gravisuite." +  NAME + "." + this.name().toLowerCase(Locale.ENGLISH);
      this.colour = colour;
    }

    public static GraviToolMode getFromID(int ID) {
      return VALUES[ID % VALUES.length];
    }
  }
}
