//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.renders;

import com.chocohead.gravisuite.GS_Items;
import com.chocohead.gravisuite.items.ItemAdvancedDrill;
import com.chocohead.gravisuite.items.ItemAdvancedDrill.DrillMode;
import ic2.core.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PrettyUtil implements IResourceManagerReloadListener {
  public static final TextureAtlasSprite[] DESTROY_BLOCK_ICONS = new TextureAtlasSprite[10];
  public static final Minecraft mc = Minecraft.getMinecraft();
  private static final Field CUR_BLOCK_DAMAGE_MP = getCBDMP();

  private static Field getCBDMP() {
    Field field = ReflectionUtil.getField(PlayerControllerMP.class, "e", "field_78770_f", "curBlockDamageMP");
    if (field == null) {
      throw new RuntimeException("Cannot find curBlockDamageMP!");
    } else {
      return field;
    }
  }

  public PrettyUtil() {
    MinecraftForge.EVENT_BUS.register(this);
    IResourceManager resourceManager = mc.getResourceManager();
    if (resourceManager instanceof IReloadableResourceManager) {
      ((IReloadableResourceManager)resourceManager).registerReloadListener(this);
    } else {
      throw new IllegalStateException("ResourceManager is not reloadable?!");
    }
  }

  public void onResourceManagerReload(IResourceManager resourceManager) {
    TextureMap texturemap = mc.getTextureMapBlocks();

    for(byte icon = 0; icon < DESTROY_BLOCK_ICONS.length; ++icon) {
      DESTROY_BLOCK_ICONS[icon] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + icon);
    }

  }

  @SubscribeEvent
  public void renderAdditionalBlockBounds(DrawBlockHighlightEvent event) {
    if (event.getSubID() == 0 && event.getTarget().typeOfHit == Type.BLOCK) {
      EntityPlayer player = event.getPlayer();
      ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
      if (stack != null && stack.getItem() == GS_Items.ADVANCED_DRILL.getInstance() && ItemAdvancedDrill.readDrillMode(stack) == DrillMode.BIG_HOLES) {
        drawAdditionalBlockbreak(event.getContext(), player, event.getPartialTicks(), ItemAdvancedDrill.getBrokenBlocks(player, event.getTarget()));
      }
    }

  }

  public static void drawAdditionalBlockbreak(RenderGlobal context, EntityPlayer player, float partialTicks, Collection<BlockPos> blocks) {

    for (BlockPos pos : blocks) {
      context.drawSelectionBox(
        player,
        new RayTraceResult(new Vec3d(0.0, 0.0, 0.0), null, pos),
        0, partialTicks
      );
    }

    if (mc.playerController.getIsHittingBlock()) {
      drawBlockDamageTexture(player, blocks, partialTicks);
    }

  }

  private static float get_curBlockDamageMP(PlayerControllerMP controller) {
    try {
      return CUR_BLOCK_DAMAGE_MP.getFloat(controller);
    } catch (IllegalArgumentException var2) {
      throw new RuntimeException("curBlockDamageMP is not a float?! Turns out it was a " + CUR_BLOCK_DAMAGE_MP.getType(), var2);
    } catch (IllegalAccessException var3) {
      throw new RuntimeException("One job...", var3);
    }
  }

  public static void drawBlockDamageTexture(Entity entity, Collection<BlockPos> blocks, float partialTicks) {
    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
    int progress = (int)(get_curBlockDamageMP(mc.playerController) * 10.0F) - 1;
    if (progress >= 0) {
      TextureAtlasSprite sprite = DESTROY_BLOCK_ICONS[progress];
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

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
      Iterator it = blocks.iterator();

      while(true) {
        BlockPos pos;
        IBlockState state;
        do {
          if (!it.hasNext()) {
            Tessellator.getInstance().draw();
            worldRenderer.pos(0.0, 0.0, 0.0);
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
            return;
          }

          pos = (BlockPos)it.next();
          state = world.getBlockState(pos);
        } while(state.getMaterial() == Material.AIR);

        Block block = state.getBlock();
        boolean hasBreak = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
        if (!hasBreak) {
          TileEntity te = world.getTileEntity(pos);
          hasBreak = te != null && te.canRenderBreaking();
        }

        if (!hasBreak) {
          mc.getBlockRendererDispatcher().renderBlockDamage(state, pos, sprite, world);
        }
      }
    }
  }
}
