//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.renders;

import com.chocohead.gravisuite.GraviItem;
import com.chocohead.gravisuite.items.ItemAdvancedDrill;
import com.chocohead.gravisuite.items.ItemAdvancedDrill.DrillMode;
import ic2.core.util.ReflectionUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.Collection;

@SideOnly(Side.CLIENT)
public final class PrettyUtil implements IResourceManagerReloadListener {
  public static final TextureAtlasSprite[] BLOCK_DESTROY_STAGES_SPRITES = new TextureAtlasSprite[10];

  private static final Field CURRENT_BLOCK_DAMAGE_MP_FIELD = getMinecraftCurrentBlockDamageMPField();

  private static Field getMinecraftCurrentBlockDamageMPField() {
    Field field = ReflectionUtil.getField(PlayerControllerMP.class, "e", "field_78770_f", "curBlockDamageMP");
    if (field == null) {
      throw new RuntimeException("Cannot find curBlockDamageMP!");
    } else {
      return field;
    }
  }

  public PrettyUtil() {
    MinecraftForge.EVENT_BUS.register(this);

    IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
    if (resourceManager instanceof IReloadableResourceManager) {
      IReloadableResourceManager reloadableResourceManager
        = (IReloadableResourceManager)resourceManager;

      reloadableResourceManager.registerReloadListener(this);
    } else {
      throw new IllegalStateException("ResourceManager is not reloadable?!");
    }
  }

  @ParametersAreNonnullByDefault
  public void onResourceManagerReload(IResourceManager resourceManager) {
    TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

    for (byte icon = 0; icon < BLOCK_DESTROY_STAGES_SPRITES.length; ++icon) {
      BLOCK_DESTROY_STAGES_SPRITES[icon] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + icon);
    }
  }

  @SubscribeEvent
  public void renderAdditionalBlockBounds(DrawBlockHighlightEvent event) {
    if (event.getSubID() == 0 && event.getTarget().typeOfHit == Type.BLOCK) {
      EntityPlayer player = event.getPlayer();
      ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

      if (stack.getItem() == GraviItem.ADVANCED_DRILL.getInstance() && ItemAdvancedDrill.readDrillMode(stack) == DrillMode.BIG_HOLES) {
        drawAdditionalBlockBreak(
          event.getContext(), player, event.getPartialTicks(),
          ItemAdvancedDrill.getBrokenBlocks(player, event.getTarget())
        );
      }
    }
  }

  public static void drawAdditionalBlockBreak(RenderGlobal context, EntityPlayer player, float partialTicks, Collection<BlockPos> blocks) {
    for (BlockPos blockPos : blocks) {
      context.drawSelectionBox(
        player,
        new RayTraceResult(new Vec3d(0.0, 0.0, 0.0), null, blockPos),
        0, partialTicks
      );
    }

    if (Minecraft.getMinecraft().playerController.getIsHittingBlock()) {
      drawBlockDamageTexture(player, blocks, partialTicks);
    }

  }

  private static float getCurrentBlockDamage(PlayerControllerMP controller) {
    try {
      return CURRENT_BLOCK_DAMAGE_MP_FIELD.getFloat(controller);
    } catch (IllegalArgumentException exception) {
      throw new RuntimeException("curBlockDamageMP is not a float?! Turns out it was a " + CURRENT_BLOCK_DAMAGE_MP_FIELD.getType(), exception);
    } catch (IllegalAccessException exception) {
      throw new RuntimeException("One job...", exception);
    }
  }

  public static void drawBlockDamageTexture(Entity entity, Collection<BlockPos> blocks, float partialTicks) {
    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

    int progress = (int)(getCurrentBlockDamage(Minecraft.getMinecraft().playerController) * 10.0F) - 1;

    if (progress >= 0) {
      TextureAtlasSprite sprite = BLOCK_DESTROY_STAGES_SPRITES[progress];

      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.doPolygonOffset(-3.0F, -3.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.pushMatrix();

      BufferBuilder worldRenderer = Tessellator.getInstance().getBuffer();

      worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
      worldRenderer.pos(-x, -y, -z);

      World world = entity.world;

      for (BlockPos blockPos : blocks) {
        IBlockState blockState = world.getBlockState(blockPos);

        if (blockState.getMaterial() == Material.AIR)
          continue;

        Block block = blockState.getBlock();

        boolean canRenderBreaking =
          block instanceof BlockChest ||
          block instanceof BlockEnderChest ||
          block instanceof BlockSign ||
          block instanceof BlockSkull;

        if (!canRenderBreaking) {
          TileEntity tileEntity = world.getTileEntity(blockPos);

          if (tileEntity != null)
            canRenderBreaking = tileEntity.canRenderBreaking();
        }

        if (!canRenderBreaking) {
          Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockDamage(blockState, blockPos, sprite, world);
        }
      }

      Tessellator.getInstance().draw();
      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
    }
  }
}
