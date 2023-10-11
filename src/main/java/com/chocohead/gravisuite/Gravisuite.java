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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
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
  public static Logger Log;

  @EventHandler
  public void load(FMLPreInitializationEvent event) {
    Log = event.getModLog();

    GraviConfig.loadConfig(event.getSuggestedConfigurationFile(), event.getSide().isClient());
    GraviKeys.init();
    GraviItem.buildItems(event.getSide());

    registerJetpackBlacklist();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    GraviRecipes.addCraftingRecipes();
    if (event.getSide().isClient()) {
      new PrettyUtil();
      new GravisuiteOverlay();
    }
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    if (GraviConfig.ReplaceQuantumArmorCraft) {
      GraviRecipes.changeQuantumRecipe();
    }

    if (event.getSide().isClient()) {
      GraviChestplateColorHandler.register();
    }
  }

  private void registerJetpackBlacklist() {
    JetpackAttachmentRecipe.blacklistedItems.add(GraviItem.ADVANCED_JETPACK.getInstance());
    JetpackAttachmentRecipe.blacklistedItems.add(GraviItem.ADVANCED_NANO_CHESTPLATE.getInstance());
    JetpackAttachmentRecipe.blacklistedItems.add(GraviItem.GRAVI_CHESTPLATE.getInstance());
  }

  public static void messagePlayer(EntityPlayer player, String message, TextFormatting color, Object... args) {
    ITextComponent resultMessage;

    if (args.length > 0) {
      resultMessage = new TextComponentTranslation(message, (Object[]) getMessageComponents(args));
    } else {
      resultMessage = new TextComponentString(Localization.translate(message));
    }

    resultMessage.setStyle(new Style().setColor(color));

    if (player.world.isRemote) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(resultMessage);
    } else if (player instanceof EntityPlayerMP) {
      player.sendMessage(resultMessage);
    }

  }

  private static ITextComponent[] getMessageComponents(Object... args) {
    ITextComponent[] encodedArgs = new ITextComponent[args.length];

    for (int i = 0; i < args.length; ++i) {
      Object arg = args[i];

      if (arg instanceof String) {
        String string = (String) args[i];

        if (string.startsWith("gravisuite.")) {
          encodedArgs[i] = new TextComponentTranslation(string);
          continue;
        }
      }

      encodedArgs[i] = new TextComponentString(arg.toString());
    }

    return encodedArgs;
  }
}
