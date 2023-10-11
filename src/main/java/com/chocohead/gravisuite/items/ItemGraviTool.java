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
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.*;
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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ItemGraviTool extends ItemTool implements IElectricItem, IItemModelProvider {
  protected static final String ITEM_NAME = "graviTool";

  protected static final double ROTATE = 50.0;
  protected static final double HOE = 50.0;
  protected static final double TAP = 50.0;
  protected static final double SCREW = 500.0;

  public ItemGraviTool() {
    super(ToolMaterial.IRON, Collections.emptySet());

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", ITEM_NAME)).setUnlocalizedName(ITEM_NAME);

    this.efficiency = 16.0F;

    this.setMaxDamage(27);
    this.setCreativeTab(IC2.tabIC2);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerModels(ItemName name) {
    ModelLoader.setCustomMeshDefinition(this, itemStack -> {
      GraviToolMode mode;

      if (ItemGraviTool.hasToolMode(itemStack)) {
        mode = ItemGraviTool.readToolMode(itemStack);
      } else {
        mode = GraviToolMode.HOE;
      }

      return mode.model;
    });

    for (GraviToolMode mode : GraviToolMode.VALUES) {
      ModelBakery.registerItemVariants(this, mode.model);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  public static boolean hasToolMode(ItemStack stack) {
    if (!stack.hasTagCompound())
      return false;

    //noinspection DataFlowIssue
    return stack.getTagCompound().hasKey("toolMode", Constants.NBT.TAG_INT);
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
  public @Nonnull String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(5);
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull String getUnlocalizedName(ItemStack stack) {
    return this.getUnlocalizedName();
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull String getItemStackDisplayName(ItemStack stack) {
    if (hasToolMode(stack))
      return Localization.translate(
        "gravisuite.graviTool.set",
        Localization.translate(this.getUnlocalizedName(stack)),
        Localization.translate(readToolMode(stack).translationName)
      );

    return Localization.translate(this.getUnlocalizedName(stack));
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!IC2.keyboard.isModeSwitchKeyDown(player))
      return super.onItemRightClick(world, player, hand);

    ItemStack itemStack = StackUtil.get(player, hand);

    if (world.isRemote) {
      IC2.audioManager.playOnce(player, PositionSpec.Hand, "gravisuite:toolChange.ogg", true, IC2.audioManager.getDefaultVolume());
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    GraviToolMode toolMode = readNextToolMode(itemStack);
    saveToolMode(itemStack, toolMode);

    Gravisuite.messagePlayer(player, "gravisuite.graviTool.changeTool", toolMode.color, toolMode.translationName);

    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    ItemStack itemStack = StackUtil.get(player, hand);

    switch (readToolMode(itemStack)) {
      case WRENCH:
        return this.onWrenchUse(itemStack, player, world, pos, side) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      case SCREWDRIVER:
        return this.onScrewdriverUse(itemStack, player, world, pos) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      default:
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
    if (!player.canPlayerEdit(pos.offset(side), side, stack))
      return false;

    if (!hasNecessaryPower(stack, HOE, player))
      return false;

    UseHoeEvent event = new UseHoeEvent(player, stack, world, pos);

    if (MinecraftForge.EVENT_BUS.post(event))
      return false;

    if (event.getResult() == Result.ALLOW)
      return checkNecessaryPower(stack, HOE, player, true);

    IBlockState blockState = Util.getBlockState(world, pos);
    Block block = blockState.getBlock();

    if (side == EnumFacing.DOWN)
      return false;

    if (!world.isAirBlock(pos.up()))
      return false;

    if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
      return this.setHoedBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());

    if (block == Blocks.DIRT) {
      switch (blockState.getValue(BlockDirt.VARIANT)) {
        case DIRT:
          return this.setHoedBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());
        case COARSE_DIRT:
          return this.setHoedBlock(stack, player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
      }
    }

    return false;
  }

  protected boolean setHoedBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state) {
    if (!checkNecessaryPower(stack, HOE, player, true))
      return false;

    world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

    if (!world.isRemote)
      world.setBlockState(pos, state, 11);

    return true;
  }

  protected boolean onTreeTapUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    IBlockState blockState = Util.getBlockState(world, pos);
    TileEntity tileEntity = world.getTileEntity(pos);

    if (side.getAxis() != Axis.Y && tileEntity instanceof TileEntityBarrel) {
      TileEntityBarrel barrel = (TileEntityBarrel)tileEntity;

      if (barrel.getActive())
        return false;

      if (!checkNecessaryPower(stack, TAP, player, true))
        return false;

      if (world.isRemote)
        return true;

      barrel.setActive(true);
      barrel.onPlaced(stack, null, side.getOpposite());

      return true;
    }

    if (blockState.getBlock() != BlockName.rubber_wood.getInstance())
      return false;

    if (!hasNecessaryPower(stack, TAP, player))
      return false;

    if (ItemTreetap.attemptExtract(player, world, pos, side, blockState, null))
      return checkNecessaryPower(stack, TAP, player);

    return false;
  }

  protected boolean onWrenchUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    IBlockState blockState = Util.getBlockState(world, pos);
    Block block = blockState.getBlock();

    if (block.isAir(blockState, world, pos))
      return false;

    if (!(block instanceof IWrenchable))
      return false;

    IWrenchable wrenchable = (IWrenchable)block;

    EnumFacing currentFacing = wrenchable.getFacing(world, pos);
    EnumFacing newFacing;

    if (!IC2.keyboard.isAltKeyDown(player)) {
      if (player.isSneaking())
        newFacing = side.getOpposite();
      else
        newFacing = side;
    } else {
      Axis axis = side.getAxis();

      if (
        side.getAxisDirection() == AxisDirection.POSITIVE && !player.isSneaking() ||
        side.getAxisDirection() == AxisDirection.NEGATIVE && player.isSneaking()
      )
        newFacing = currentFacing.rotateAround(axis);
      else
        newFacing = currentFacing.rotateAround(axis).rotateAround(axis).rotateAround(axis);
    }

    if (currentFacing != newFacing) {
      if (!hasNecessaryPower(stack, ROTATE, player))
        return false;

      if (wrenchable.setFacing(world, pos, newFacing, player))
        return checkNecessaryPower(stack, ROTATE, player);
    }

    if (!wrenchable.wrenchCanRemove(world, pos, player))
      return false;

    if (!hasNecessaryPower(stack, ROTATE, player))
      return false;

    if (world.isRemote)
      return checkNecessaryPower(stack, ROTATE, player);

    TileEntity tileEntity = world.getTileEntity(pos);

    if (ConfigUtil.getBool(MainConfig.get(), "protection/wrenchLogging")) {
      IC2.log.info(
        LogCategory.PlayerActivity, "Player %s used a wrench to remove the %s (%s) at %s.",
        player.getGameProfile().getName() + "/" + player.getGameProfile().getId(),
        blockState,
        tileEntity != null ? tileEntity.getClass().getSimpleName().replace("TileEntity", "") : "no te",
        Util.formatPosition(world, pos)
      );
    }

    int experience = 0;
    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP playerMP = (EntityPlayerMP)player;

      experience = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);

      if (experience < 0)
        return false;
    }

    block.onBlockHarvested(world, pos, blockState, player);

    if (!block.removedByPlayer(blockState, world, pos, player, true))
      return false;

    block.onBlockDestroyedByPlayer(world, pos, blockState);

    List<ItemStack> drops = wrenchable.getWrenchDrops(world, pos, blockState, tileEntity, player, 0);

    for (ItemStack drop : drops) {
      StackUtil.dropAsEntity(world, pos, drop);
    }

    if (experience > 0 && !player.capabilities.isCreativeMode)
      block.dropXpOnBlockBreak(world, pos, experience);

    return checkNecessaryPower(stack, ROTATE, player);
  }

  protected boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, BlockPos blockPos) {
    IBlockState blockState = Util.getBlockState(world, blockPos);
    Block block = blockState.getBlock();

    if (block.isAir(blockState, world, blockPos))
      return false;

    if (!(block instanceof BlockHorizontal))
      return false;

    if (!checkNecessaryPower(stack, SCREW, player))
      return false;

    EnumFacing facing = blockState.getValue(BlockHorizontal.FACING);

    if (player.isSneaking())
      facing = facing.rotateYCCW();
    else
      facing = facing.rotateY();

    world.setBlockState(blockPos, blockState.withProperty(BlockHorizontal.FACING, facing));

    return true;
  }

  public static boolean hasNecessaryPower(ItemStack stack, double usage, EntityPlayer player) {
    ElectricItem.manager.chargeFromArmor(stack, player);
    return Util.isSimilar(ElectricItem.manager.discharge(stack, usage, Integer.MAX_VALUE, true, false, true), usage);
  }

  protected static boolean checkNecessaryPower(ItemStack stack, double usage, EntityPlayer player) {
    return checkNecessaryPower(stack, usage, player, false);
  }

  protected static boolean checkNecessaryPower(ItemStack stack, double usage, EntityPlayer player, boolean supressSound) {
    if (!ElectricItem.manager.use(stack, usage, player)) {
      IC2.platform.messagePlayer(player, Localization.translate("gravisuite.graviTool.noEnergy"));
      return false;
    }

    if (!supressSound && player.world.isRemote)
      IC2.audioManager.playOnce(player, PositionSpec.Hand, "gravisuite:wrench.ogg", true, IC2.audioManager.getDefaultVolume());

    return true;
  }

  @Override
  @ParametersAreNonnullByDefault
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return false;
  }

  @Override
  @ParametersAreNonnullByDefault
  public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    return true;
  }

  @Override
  @ParametersAreNonnullByDefault
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
  @ParametersAreNonnullByDefault
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return false;
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
    return HashMultimap.create();
  }

  @Override
  @ParametersAreNonnullByDefault
  public @Nonnull EnumRarity getForgeRarity(ItemStack stack) {
    return EnumRarity.UNCOMMON;
  }

  @Override
  @ParametersAreNonnullByDefault
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
    public final TextFormatting color;
    private static final GraviToolMode[] VALUES = values();

    GraviToolMode(TextFormatting color) {
      this.model = new ModelResourceLocation("gravisuite:" + ITEM_NAME.toLowerCase(Locale.ENGLISH) + '/' + this.name().toLowerCase(Locale.ENGLISH), null);
      this.translationName = "gravisuite." +  ITEM_NAME + "." + this.name().toLowerCase(Locale.ENGLISH);
      this.color = color;
    }

    public static GraviToolMode getFromID(int id) {
      return VALUES[id % VALUES.length];
    }
  }
}
