//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.items.ItemAdvancedChainsaw;
import com.chocohead.gravisuite.items.ItemAdvancedDrill;
import com.chocohead.gravisuite.items.ItemAdvancedElectricJetpack;
import com.chocohead.gravisuite.items.ItemAdvancedLappack;
import com.chocohead.gravisuite.items.ItemAdvancedNanoChestplate;
import com.chocohead.gravisuite.items.ItemCraftingThings;
import com.chocohead.gravisuite.items.ItemGraviChestplate;
import com.chocohead.gravisuite.items.ItemGraviTool;
import com.chocohead.gravisuite.items.ItemUltimateLappack;
import com.chocohead.gravisuite.items.ItemVajra;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.IMultiItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum GS_Items {
  ADVANCED_LAPPACK,
  ADVANCED_JETPACK,
  ADVANCED_NANO_CHESTPLATE,
  ULTIMATE_LAPPACK,
  GRAVI_CHESTPLATE,
  ADVANCED_DRILL,
  ADVANCED_CHAINSAW,
  GRAVITOOL,
  VAJRA,
  CRAFTING;

  private Item instance;

  private GS_Items() {
  }

  public <T extends Item & IItemModelProvider> T getInstance() {
    return (T) this.instance;
  }

  public <T extends Enum<T> & IIdProvider> ItemStack getItemStack(T variant) {
    if (this.instance == null) {
      return null;
    } else if (this.instance instanceof IMultiItem) {
      IMultiItem<T> multiItem = (IMultiItem) this.instance;
      return multiItem.getItemStack(variant);
    } else if (variant == null) {
      return new ItemStack(this.instance);
    } else {
      throw new IllegalArgumentException("Not applicable");
    }
  }

  public <T extends Item & IItemModelProvider> void setInstance(T instance) {
    if (this.instance != null) {
      throw new IllegalStateException("Duplicate instances!");
    } else {
      this.instance = instance;
    }
  }

  static void buildItems(Side side) {
    ADVANCED_LAPPACK.setInstance(new ItemAdvancedLappack());
    ADVANCED_JETPACK.setInstance(new ItemAdvancedElectricJetpack());
    ADVANCED_NANO_CHESTPLATE.setInstance(new ItemAdvancedNanoChestplate());
    ULTIMATE_LAPPACK.setInstance(new ItemUltimateLappack());
    GRAVI_CHESTPLATE.setInstance(new ItemGraviChestplate());
    ADVANCED_DRILL.setInstance(new ItemAdvancedDrill());
    ADVANCED_CHAINSAW.setInstance(new ItemAdvancedChainsaw());
    GRAVITOOL.setInstance(new ItemGraviTool());
    VAJRA.setInstance(new ItemVajra());
    CRAFTING.setInstance(new ItemCraftingThings());
    if (side == Side.CLIENT) {
      doModelGuf();
    }

  }

  @SideOnly(Side.CLIENT)
  private static void doModelGuf() {
    GS_Items[] items = values();
    int itemsCount = items.length;

    for(int i = 0; i < itemsCount; ++i) {
      GS_Items item = items[i];
      item.getInstance().registerModels(null);
    }

  }
}
