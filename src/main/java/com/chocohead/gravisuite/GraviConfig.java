//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class GraviConfig {
  private static final String HUD = "HUD settings";
  private static final String CRAFTING = "Recipe settings";
  private static final String VAJRA = "Vajra settings";

  public static boolean DisableAdvancedJetpackCraft;
  public static boolean DisableAdvancedNanoChestplateCraft;
  public static boolean DisableGraviChestplateCraft;
  public static boolean DisableAdvancedLappackCraft;
  public static boolean DisableUltimateLappackCraft;
  public static boolean DisableAdvancedDrillCraft;
  public static boolean DisableAdvancedChainsawCraft;
  public static boolean DisableGraviToolCraft;
  public static boolean DisableVajraCraft;
  public static boolean ReplaceQuantumArmorCraft;
  public static boolean VajraAccurateModeDisabled;

  public static boolean GravisuiteOverlayEnabled;
  public static int GravisuiteOverlayPosition;

  public static void loadConfig(File configFile, boolean isClient) {
    Configuration config = new Configuration(configFile);

    Gravisuite.Log.info("Loading GraviSuite Config from " + configFile.getAbsolutePath());

    try {
      config.load();

      if (isClient) {
        GravisuiteOverlayEnabled  = getBoolean(config, HUD, "enableHud", true);
        GravisuiteOverlayPosition = getHudPosition(config, HUD, "hudPosition", 1);
      }

      DisableAdvancedJetpackCraft         = getBoolean(config, CRAFTING, "Disable Advanced Jetpack recipe", false);
      DisableAdvancedNanoChestplateCraft  = getBoolean(config, CRAFTING, "Disable Advanced NanoChestPlate recipe", false);
      DisableGraviChestplateCraft         = getBoolean(config, CRAFTING, "Disable GraviChestPlate recipe", false);
      DisableAdvancedLappackCraft         = getBoolean(config, CRAFTING, "Disable AdvancedLappack recipe", false);
      DisableUltimateLappackCraft         = getBoolean(config, CRAFTING, "Disable UltimateLappack recipe", false);
      DisableAdvancedDrillCraft           = getBoolean(config, CRAFTING, "Disable Advanced Diamond Drill recipe", false);
      DisableAdvancedChainsawCraft        = getBoolean(config, CRAFTING, "Disable Advanced Chainsaw recipe", false);
      DisableGraviToolCraft               = getBoolean(config, CRAFTING, "Disable GraviTool recipe", false);
      DisableVajraCraft                   = getBoolean(config, CRAFTING, "Disable Vajra recipe", false);
      ReplaceQuantumArmorCraft            = getBoolean(config, CRAFTING, "Change the Quantumsuit BodyArmour recipe", false);
      VajraAccurateModeDisabled           = getBoolean(config, VAJRA, "Disable Vajra accurate mode", false);
    } catch (Exception exception) {
      Gravisuite.Log.fatal("Fatal error reading config file", exception);
      throw new RuntimeException(exception);
    } finally {
      if (config.hasChanged()) {
        config.save();
      }
    }
  }

  private static boolean getBoolean(Configuration config, String category, String name, boolean defaultValue) {
    return config.get(category, name, defaultValue).getBoolean(defaultValue);
  }

  private static int getInt(Configuration config, String category, String name, int defaultValue) {
    return config.get(category, name, defaultValue).getInt(defaultValue);
  }

  private static int getHudPosition(Configuration config, String category, String name, int defaultValue) {
    int rawValue = getInt(config, category, name, defaultValue);
    return (rawValue - 1) % 4 + 1;
  }
}
