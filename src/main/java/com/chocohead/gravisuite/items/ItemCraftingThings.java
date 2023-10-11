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

public class ItemCraftingThings extends ItemMulti<ItemCraftingThings.CraftingThingType> {
  public static final String ITEM_NAME = "crafting";

  public ItemCraftingThings() {
    super(null, CraftingThingType.class);

    BlocksItems.registerItem(this, new ResourceLocation("gravisuite", ITEM_NAME)).setUnlocalizedName(ITEM_NAME);
  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void registerModel(int meta, ItemName name, String extraName) {
    ModelLoader.setCustomModelResourceLocation(
      this, meta,
      new ModelResourceLocation("gravisuite:crafting/" + CraftingThingType.getFromID(meta).getName(), null)
    );
  }

  @Override
  public String getUnlocalizedName() {
    return "gravisuite." + super.getUnlocalizedName().substring(4);
  }

  public enum CraftingThingType implements IIdProvider {
    SUPERCONDUCTOR_COVER,
    SUPERCONDUCTOR,
    COOLING_CORE,
    GRAVITATION_ENGINE,
    MAGNETRON,
    VAJRA_CORE,
    ENGINE_BOOSTER;

    private static final CraftingThingType[] VALUES = values();

    private final String m_name;
    private final int m_id;

    CraftingThingType() {
      m_name = name().toLowerCase(Locale.ENGLISH);
      m_id = ordinal();
    }

    public String getName() {
      return m_name;
    }

    public int getId() {
      return m_id;
    }

    public static CraftingThingType getFromID(int id) {
      return VALUES[id % VALUES.length];
    }
  }
}
