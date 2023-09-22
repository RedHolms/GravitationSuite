//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite.items;

import ic2.core.block.state.IIdProvider;
import ic2.core.init.BlocksItems;
import ic2.core.item.ItemMulti;
import ic2.core.ref.ItemName;
import java.util.Locale;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCraftingThings extends ItemMulti<ItemCraftingThings.CraftingTypes> {
  protected static final String NAME = "crafting";

  public ItemCraftingThings() {
    super(null, CraftingTypes.class);
    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", NAME)).setUnlocalizedName(NAME);
  }

  @SideOnly(Side.CLIENT)
  protected void registerModel(int meta, ItemName name, String extraName) {
    ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation("gravisuite:crafting/" + ItemCraftingThings.CraftingTypes.getFromID(meta).getName(), null));
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  public enum CraftingTypes implements IIdProvider {
    SUPERCONDUCTOR_COVER(0),
    SUPERCONDUCTOR(1),
    COOLING_CORE(2),
    GRAVITATION_ENGINE(3),
    MAGNETRON(4),
    VAJRA_CORE(5),
    ENGINE_BOOSTER(6);

    private final String name;
    private final int ID;
    private static final CraftingTypes[] VALUES = values();

    CraftingTypes(int ID) {
      this.name = this.name().toLowerCase(Locale.ENGLISH);
      this.ID = ID;
    }

    public String getName() {
      return this.name;
    }

    public int getId() {
      return this.ID;
    }

    public static CraftingTypes getFromID(int ID) {
      return VALUES[ID % VALUES.length];
    }
  }
}
