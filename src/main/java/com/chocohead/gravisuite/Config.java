//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.items.ItemVajra;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

class Config {
  private static final String HUD = "HUD settings";
  private static final String CRAFTING = "Recipe settings";
  private static final String VAJRA = "Vajra settings";

  public boolean disableAdvancedJetpackCraft;
  public boolean disableAdvancedNanoChestplateCraft;
  public boolean disableGraviChestplateCraft;
  public boolean disableAdvancedLappackCraft;
  public boolean disableUltimateLappackCraft;
  public boolean disableAdvancedDrillCraft;
  public boolean disableAdvancedChainsawCraft;
  public boolean disableGraviToolCraft;
  public boolean disableVajraCraft;
  public boolean replaceQuantumArmorCraft;
  public boolean vajraAccurateModeDisabled;

  public boolean gravisuiteOverlayEnabled;
  public int gravisuiteOverlayPosition;

  private final Configuration m_config;
  private final boolean m_isClient;

  Config(File configFile, boolean isClient) {
    m_config = new Configuration(configFile);
    m_isClient = isClient;
  }

  void loadConfig() {
    Gravisuite.Instance.log.info("Loading GraviSuite Config from " + m_config.getConfigFile().getAbsolutePath());

    try {
      m_config.load();

      if (m_isClient) {
        gravisuiteOverlayEnabled = getBoolean(HUD, "enableHud", true);
        gravisuiteOverlayPosition = getHudPosition(HUD, "hudPosition", 1);
      }

      disableAdvancedJetpackCraft = getBoolean(CRAFTING, "Disable Advanced Jetpack recipe", false);
      disableAdvancedNanoChestplateCraft = getBoolean(CRAFTING, "Disable Advanced NanoChestPlate recipe", false);
      disableGraviChestplateCraft = getBoolean(CRAFTING, "Disable GraviChestPlate recipe", false);
      disableAdvancedLappackCraft = getBoolean(CRAFTING, "Disable AdvancedLappack recipe", false);
      disableUltimateLappackCraft = getBoolean(CRAFTING, "Disable UltimateLappack recipe", false);
      disableAdvancedDrillCraft = getBoolean(CRAFTING, "Disable Advanced Diamond Drill recipe", false);
      disableAdvancedChainsawCraft = getBoolean(CRAFTING, "Disable Advanced Chainsaw recipe", false);
      disableGraviToolCraft = getBoolean(CRAFTING, "Disable GraviTool recipe", false);
      disableVajraCraft = getBoolean(CRAFTING, "Disable Vajra recipe", false);
      replaceQuantumArmorCraft = getBoolean(CRAFTING, "Change the Quantumsuit BodyArmour recipe", false);
      vajraAccurateModeDisabled = getBoolean(VAJRA, "Disable Vajra accurate mode", false);
    } catch (Exception exception) {
      Gravisuite.Instance.log.fatal("Fatal error reading config file", exception);
      throw new RuntimeException(exception);
    } finally {
      if (m_config.hasChanged()) {
        m_config.save();
      }
    }
  }

  private boolean getBoolean(String category, String name, boolean defaultValue) {
    return m_config.get(category, name, defaultValue).getBoolean(defaultValue);
  }

  private int getInt(String category, String name, int defaultValue) {
    return m_config.get(category, name, defaultValue).getInt(defaultValue);
  }

  private int getHudPosition(String category, String name, int defaultValue) {
    int rawValue = getInt(category, name, defaultValue);
    return (rawValue - 1) % 4 + 1;
  }
}
