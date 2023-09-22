//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.items.ItemGraviChestplate;
import com.chocohead.gravisuite.items.ItemCraftingThings.CraftingTypes;
import ic2.api.item.IC2Items;
import ic2.core.init.Rezepte;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.ArmorDyeingRecipe;
import ic2.core.recipe.ColourCarryingRecipe;
import ic2.core.util.StackUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

final class Recipes {
  Recipes() {
  }

  static void addCraftingRecipes() {
    Rezepte.registerRecipe(new ResourceLocation("gravisuite", "gravicolouring"), new ArmorDyeingRecipe(new ArmorDyeingRecipe.RecipeInputClass(ItemGraviChestplate.class)));
    addShapedRecipe(expandStack(GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR_COVER), 3), "APA", "CCC", "APA", 'A', IC2Items.getItem("crafting", "alloy"), 'P', IC2Items.getItem("crafting", "iridium"), 'C', IC2Items.getItem("crafting", "carbon_plate"));
    addShapedRecipe(expandStack(GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR), 3), "SSS", "CGC", "SSS", 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR_COVER), 'G', "ingotGold", 'C', IC2Items.getItem("cable", "type:glass,insulation:0"));
    addShapedRecipe(GS_Items.CRAFTING.getItemStack(CraftingTypes.COOLING_CORE), "CSC", "HPH", "CSC", 'C', IC2Items.getItem("hex_heat_storage"), 'S', IC2Items.getItem("advanced_heat_exchanger"), 'H', IC2Items.getItem("heat_plating"), 'P', IC2Items.getItem("crafting", "iridium"));
    addShapedRecipe(GS_Items.CRAFTING.getItemStack(CraftingTypes.GRAVITATION_ENGINE), "TST", "CHC", "TST", 'T', IC2Items.getItem("te", "tesla_coil"), 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR), 'C', GS_Items.CRAFTING.getItemStack(CraftingTypes.COOLING_CORE), 'H', IC2Items.getItem("te", "hv_transformer"));
    if (Config.canCraftAdvLappack) {
      addShapedRecipe(new ItemStack(GS_Items.ADVANCED_LAPPACK.getInstance()), "P", "C", "E", 'P', StackUtil.copyWithWildCard(IC2Items.getItem("energy_pack")), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal")));
    }

    if (Config.canCraftAdvJetpack) {
      addShapedRecipe(GS_Items.CRAFTING.getItemStack(CraftingTypes.ENGINE_BOOSTER), "GAG", "COC", "AVA", 'G', Items.GLOWSTONE_DUST, 'A', IC2Items.getItem("crafting", "alloy"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'O', IC2Items.getItem("upgrade", "overclocker"), 'V', IC2Items.getItem("advanced_heat_vent"));
      addShapedRecipe(new ItemStack(GS_Items.ADVANCED_JETPACK.getInstance()), "CJC", "BLB", "GAG", 'C', IC2Items.getItem("crafting", "carbon_plate"), 'J', StackUtil.copyWithWildCard(IC2Items.getItem("jetpack_electric")), 'B', GS_Items.CRAFTING.getItemStack(CraftingTypes.ENGINE_BOOSTER), 'L', StackUtil.copyWithWildCard(new ItemStack(GS_Items.ADVANCED_LAPPACK.getInstance())), 'G', IC2Items.getItem("cable", "type:glass,insulation:0"), 'A', IC2Items.getItem("crafting", "advanced_circuit"));
    }

    if (Config.canCraftAdvNano) {
      addShapedRecipe(new ItemStack(GS_Items.ADVANCED_NANO_CHESTPLATE.getInstance()), "CJC", "CNC", "GAG", 'C', IC2Items.getItem("crafting", "carbon_plate"), 'J', StackUtil.copyWithWildCard(new ItemStack(GS_Items.ADVANCED_JETPACK.getInstance())), 'N', StackUtil.copyWithWildCard(IC2Items.getItem("nano_chestplate")), 'G', IC2Items.getItem("cable", "type:glass,insulation:0"), 'A', IC2Items.getItem("crafting", "advanced_circuit"));
    }

    if (Config.canCraftUltiLappack) {
      addShapedRecipe(new ItemStack(GS_Items.ULTIMATE_LAPPACK.getInstance()), "CPC", "CLC", "CSC", 'C', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal")), 'P', IC2Items.getItem("crafting", "iridium"), 'L', StackUtil.copyWithWildCard(IC2Items.getItem("lappack")), 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR));
      addShapedRecipe(new ItemStack(GS_Items.ULTIMATE_LAPPACK.getInstance()), "CPC", "CLC", "CSC", 'C', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal")), 'P', IC2Items.getItem("crafting", "iridium"), 'L', StackUtil.copyWithWildCard(new ItemStack(GS_Items.ADVANCED_LAPPACK.getInstance())), 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR));
    }

    if (Config.canCraftGravi) {
      addShapedColourRecipe(new ItemStack(GS_Items.GRAVI_CHESTPLATE.getInstance()), "SAS", "DBD", "SCS", 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR), 'A', StackUtil.copyWithWildCard(IC2Items.getItem("quantum_chestplate")), 'D', GS_Items.CRAFTING.getItemStack(CraftingTypes.GRAVITATION_ENGINE), 'B', IC2Items.getItem("te", "hv_transformer"), 'C', StackUtil.copyWithWildCard(new ItemStack(GS_Items.ULTIMATE_LAPPACK.getInstance())));
    }

    if (Config.canCraftAdvDrill) {
      addShapedRecipe(new ItemStack(GS_Items.ADVANCED_DRILL.getInstance()), "ODO", "COC", 'O', IC2Items.getItem("upgrade", "overclocker"), 'D', StackUtil.copyWithWildCard(IC2Items.getItem("diamond_drill")), 'C', IC2Items.getItem("crafting", "advanced_circuit"));
    }

    if (Config.canCraftAdvChainsaw) {
      addShapedRecipe(new ItemStack(GS_Items.ADVANCED_CHAINSAW.getInstance()), " D ", "OCO", "AOA", 'D', "gemDiamond", 'O', IC2Items.getItem("upgrade", "overclocker"), 'C', StackUtil.copyWithWildCard(IC2Items.getItem("chainsaw")), 'A', IC2Items.getItem("crafting", "advanced_circuit"));
    }

    if (Config.canCraftGraviTool) {
      addShapedRecipe(new ItemStack(GS_Items.GRAVITOOL.getInstance()), "PHP", "AEA", "WCT", 'P', IC2Items.getItem("crafting", "carbon_plate"), 'H', StackUtil.copyWithWildCard(IC2Items.getItem("electric_hoe")), 'A', IC2Items.getItem("crafting", "alloy"), 'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal")), 'W', StackUtil.copyWithWildCard(IC2Items.getItem("electric_wrench")), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'T', StackUtil.copyWithWildCard(IC2Items.getItem("electric_treetap")));
    }

    if (Config.canCraftVajra) {
      addShapedRecipe(GS_Items.CRAFTING.getItemStack(CraftingTypes.MAGNETRON), "ICI", "CSC", "ICI", 'I', "plateIron", 'C', "plateCopper", 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR));
      addShapedRecipe(GS_Items.CRAFTING.getItemStack(CraftingTypes.VAJRA_CORE), " M ", "PTP", "SHS", 'M', GS_Items.CRAFTING.getItemStack(CraftingTypes.MAGNETRON), 'P', IC2Items.getItem("crafting", "iridium"), 'T', IC2Items.getItem("te", "tesla_coil"), 'S', GS_Items.CRAFTING.getItemStack(CraftingTypes.SUPERCONDUCTOR), 'H', IC2Items.getItem("te", "hv_transformer"));
      addShapedRecipe(new ItemStack(GS_Items.VAJRA.getInstance()), "PEP", "CVC", "ALA", 'P', "plateIron", 'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal")), 'C', IC2Items.getItem("crafting", "carbon_plate"), 'V', GS_Items.CRAFTING.getItemStack(CraftingTypes.VAJRA_CORE), 'A', IC2Items.getItem("crafting", "alloy"), 'L', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal")));
    }

  }

  static void changeQuantumRecipe() {
    assert Config.shouldReplaceQuantum;

    Item quantumSuit = IC2Items.getItem("quantum_chestplate").getItem();

    for (IRecipe recipe : CraftingManager.REGISTRY) {
      if (StackUtil.checkItemEquality(recipe.getRecipeOutput(), quantumSuit) && recipe instanceof AdvRecipe) {
        ForgeRegistries.RECIPES.register(new IRecipe() {
          private final IRecipe replacement;

          {
            this.replacement = new AdvRecipe(
              IC2Items.getItem("quantum_chestplate"),
              "ANA",
              "ILI",
              "IAI",

              'N',
              StackUtil.copyWithWildCard(
                new ItemStack(
                  GS_Items.ADVANCED_NANO_CHESTPLATE.getInstance()
                )
              ),

              'A',
              IC2Items.getItem("crafting", "alloy"),

              'I',
              IC2Items.getItem("crafting", "iridium"),

              'L',
              StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal"))
            );
          }

          public boolean matches(InventoryCrafting inv, World world) {
            return this.replacement.matches(inv, world);
          }

          public ItemStack getCraftingResult(InventoryCrafting inv) {
            return this.replacement.getCraftingResult(inv);
          }

          public boolean canFit(int width, int height) {
            return this.replacement.canFit(width, height);
          }

          public ItemStack getRecipeOutput() {
            return this.replacement.getRecipeOutput();
          }

          public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            return this.replacement.getRemainingItems(inv);
          }

          public IRecipe setRegistryName(ResourceLocation name) {
            return recipe.setRegistryName(name);
          }

          public ResourceLocation getRegistryName() {
            return recipe.getRegistryName();
          }

          public Class<IRecipe> getRegistryType() {
            return recipe.getRegistryType();
          }
        });
        break;
      }
    }

  }

  private static ItemStack expandStack(ItemStack stack, int newSize) {
    return new ItemStack(stack.getItem(), newSize, stack.getMetadata());
  }

  private static void addShapedRecipe(ItemStack output, Object... inputs) {
    ic2.api.recipe.Recipes.advRecipes.addRecipe(output, inputs);
  }

  private static void addShapedColourRecipe(ItemStack output, Object... inputs) {
    ColourCarryingRecipe.addAndRegister(output, inputs);
  }
}
