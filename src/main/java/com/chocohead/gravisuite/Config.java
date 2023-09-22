//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.items.ItemVajra;
import com.chocohead.gravisuite.renders.GravisuiteOverlay;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

final class Config {
  private static final String HUD = "HUD settings";
  private static final String CRAFTING = "Recipe settings";
  static boolean canCraftAdvJetpack;
  static boolean canCraftAdvNano;
  static boolean canCraftGravi;
  static boolean canCraftAdvLappack;
  static boolean canCraftUltiLappack;
  static boolean canCraftAdvDrill;
  static boolean canCraftAdvChainsaw;
  static boolean canCraftGraviTool;
  static boolean canCraftVajra;
  static boolean shouldReplaceQuantum;

  static void loadConfig(File configFile, boolean client) {
    Gravisuite.log.info("Loading GS Config from " + configFile.getAbsolutePath());
    Configuration config = new Configuration(configFile);

    try {
      config.load();
      if (client) {
        GravisuiteOverlay.hudEnabled = config.get(HUD, "enableHud", true).getBoolean(true);
        GravisuiteOverlay.hudPos = getHudPosition(config);
      }

      canCraftAdvJetpack = !config.get(CRAFTING, "Disable Advanced Jetpack recipe", false).getBoolean(false);
      canCraftAdvNano = !config.get(CRAFTING, "Disable Advanced NanoChestPlate recipe", false).getBoolean(false);
      shouldReplaceQuantum = config.get(CRAFTING, "Change the Quantumsuit BodyArmour recipe", true).getBoolean(true);
      canCraftGravi = !config.get(CRAFTING, "Disable GraviChestPlate recipe", false).getBoolean(false);
      canCraftAdvLappack = !config.get(CRAFTING, "Disable AdvancedLappack recipe", false).getBoolean(false);
      canCraftUltiLappack = !config.get(CRAFTING, "Disable UltimateLappack recipe", false).getBoolean(false);
      canCraftAdvDrill = !config.get(CRAFTING, "Disable Advanced Diamond Drill recipe", false).getBoolean(false);
      canCraftAdvChainsaw = !config.get(CRAFTING, "Disable Advanced Chainsaw recipe", false).getBoolean(false);
      canCraftGraviTool = !config.get(CRAFTING, "Disable GraviTool recipe", false).getBoolean(false);
      canCraftVajra = !config.get(CRAFTING, "Disable Vajra recipe", false).getBoolean(false);
      ItemVajra.accurateEnabled = !config.get("Vajra settings", "Disable Vajra accurate mode", false).getBoolean(false);
    } catch (Exception var7) {
      Gravisuite.log.fatal("Fatal error reading config file.", var7);
      throw new RuntimeException(var7);
    } finally {
      if (config.hasChanged()) {
        config.save();
      }
    }
  }

  private static byte getHudPosition(Configuration config) {
    return (byte)((config.get(HUD, "hudPosition", 1).getInt(1) - 1) % 4 + 1);
  }
}
