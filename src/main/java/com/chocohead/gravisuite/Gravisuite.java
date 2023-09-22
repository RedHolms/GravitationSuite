//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.renders.GravisuiteOverlay;
import com.chocohead.gravisuite.renders.PrettyUtil;
import ic2.core.init.Localization;
import ic2.core.item.armor.jetpack.JetpackAttachmentRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
  modid = "gravisuite",
  name = "Gravitation Suite",
  dependencies = "required-after:ic2@[2.8.113,);",
  version = "3.1.1",
  acceptedMinecraftVersions = "[1.12,1.12.2]"
)
public final class Gravisuite {
  public static Logger log;

  @EventHandler
  public void load(FMLPreInitializationEvent event) {
    log = event.getModLog();

    Config.loadConfig(event.getSuggestedConfigurationFile(), event.getSide().isClient());
    GraviKeys.addFlyKey();
    GS_Items.buildItems(event.getSide());
    this.registerJetpackBlacklist();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    Recipes.addCraftingRecipes();
    if (event.getSide().isClient()) {
      new PrettyUtil();
      new GravisuiteOverlay();
    }

  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    if (Config.shouldReplaceQuantum) {
      Recipes.changeQuantumRecipe();
    }

    if (event.getSide().isClient()) {
      GraviChestplateColourHandler.register();
    }

  }

  private void registerJetpackBlacklist() {
    JetpackAttachmentRecipe.blacklistedItems.add(GS_Items.ADVANCED_JETPACK.getInstance());
    JetpackAttachmentRecipe.blacklistedItems.add(GS_Items.ADVANCED_NANO_CHESTPLATE.getInstance());
    JetpackAttachmentRecipe.blacklistedItems.add(GS_Items.GRAVI_CHESTPLATE.getInstance());
  }

  public static void messagePlayer(EntityPlayer player, String message, TextFormatting colour, Object... args) {
    Object msg;
    if (player.world.isRemote) {
      if (args.length > 0) {
        msg = new TextComponentTranslation(message, (Object[])getMessageComponents(args));
      } else {
        msg = new TextComponentString(Localization.translate(message));
      }

      PrettyUtil.mc.ingameGUI.getChatGUI().printChatMessage(((ITextComponent)msg).setStyle((new Style()).setColor(colour)));
    } else if (player instanceof EntityPlayerMP) {
      if (args.length > 0) {
        msg = new TextComponentTranslation(message, (Object[])getMessageComponents(args));
      } else {
        msg = new TextComponentString(Localization.translate(message));
      }

      ((EntityPlayerMP)player).sendMessage(((ITextComponent)msg).setStyle((new Style()).setColor(colour)));
    }

  }

  private static ITextComponent[] getMessageComponents(Object... args) {
    ITextComponent[] encodedArgs = new ITextComponent[args.length];

    for(int i = 0; i < args.length; ++i) {
      if (args[i] instanceof String && ((String)args[i]).startsWith("gravisuite.")) {
        encodedArgs[i] = new TextComponentTranslation((String)args[i]);
      } else {
        encodedArgs[i] = new TextComponentString(args[i].toString());
      }
    }

    return encodedArgs;
  }
}
