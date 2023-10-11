//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import com.chocohead.gravisuite.items.ItemCraftingThings.CraftingThingType;
import com.chocohead.gravisuite.items.ItemGraviChestplate;
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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public final class GraviRecipes {
  @SuppressWarnings("DataFlowIssue")
  static void addCraftingRecipes() {
    Rezepte.registerRecipe(
      new ResourceLocation("gravisuite", "gravicolouring"),
      new ArmorDyeingRecipe(new ArmorDyeingRecipe.RecipeInputClass(ItemGraviChestplate.class))
    );

    addShapedRecipe(
      expandStack(GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR_COVER), 3),

      "APA",
      "CCC",
      "APA",

      'A', IC2Items.getItem("crafting", "alloy"),
      'P', IC2Items.getItem("crafting", "iridium"),
      'C', IC2Items.getItem("crafting", "carbon_plate")
    );

    addShapedRecipe(
      expandStack(GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR), 3),

      "SSS",
      "CGC",
      "SSS",

      'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR_COVER),
      'G', "ingotGold",
      'C', IC2Items.getItem("cable", "type:glass,insulation:0")
    );

    addShapedRecipe(
      GraviItem.CRAFTING.getItemStack(CraftingThingType.COOLING_CORE),

      "CSC",
      "HPH",
      "CSC",

      'C', IC2Items.getItem("hex_heat_storage"),
      'S', IC2Items.getItem("advanced_heat_exchanger"),
      'H', IC2Items.getItem("heat_plating"),
      'P', IC2Items.getItem("crafting", "iridium")
    );

    addShapedRecipe(
      GraviItem.CRAFTING.getItemStack(CraftingThingType.GRAVITATION_ENGINE),

      "TST",
      "CHC",
      "TST",

      'T', IC2Items.getItem("te", "tesla_coil"),
      'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR),
      'C', GraviItem.CRAFTING.getItemStack(CraftingThingType.COOLING_CORE),
      'H', IC2Items.getItem("te", "hv_transformer")
    );

    if (!GraviConfig.DisableAdvancedLappackCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.ADVANCED_LAPPACK.getInstance()),

        "P",
        "C",
        "E",

        'P', StackUtil.copyWithWildCard(IC2Items.getItem("energy_pack")),
        'C', IC2Items.getItem("crafting", "advanced_circuit"),
        'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal"))
      );
    }

    if (!GraviConfig.DisableAdvancedJetpackCraft) {
      addShapedRecipe(
        GraviItem.CRAFTING.getItemStack(CraftingThingType.ENGINE_BOOSTER),
        "GAG",
        "COC",
        "AVA",

        'G', Items.GLOWSTONE_DUST,
        'A', IC2Items.getItem("crafting", "alloy"),
        'C', IC2Items.getItem("crafting", "advanced_circuit"),
        'O', IC2Items.getItem("upgrade", "overclocker"),
        'V', IC2Items.getItem("advanced_heat_vent")
      );

      addShapedRecipe(
        new ItemStack(GraviItem.ADVANCED_JETPACK.getInstance()),

        "CJC",
        "BLB",
        "GAG",

        'C', IC2Items.getItem("crafting", "carbon_plate"),
        'J', StackUtil.copyWithWildCard(IC2Items.getItem("jetpack_electric")),
        'B', GraviItem.CRAFTING.getItemStack(CraftingThingType.ENGINE_BOOSTER),
        'L', StackUtil.copyWithWildCard(new ItemStack(GraviItem.ADVANCED_LAPPACK.getInstance())),
        'G', IC2Items.getItem("cable", "type:glass,insulation:0"),
        'A', IC2Items.getItem("crafting", "advanced_circuit")
      );
    }

    if (!GraviConfig.DisableAdvancedNanoChestplateCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.ADVANCED_NANO_CHESTPLATE.getInstance()),

        "CJC",
        "CNC",
        "GAG",

        'C', IC2Items.getItem("crafting", "carbon_plate"),
        'J', StackUtil.copyWithWildCard(new ItemStack(GraviItem.ADVANCED_JETPACK.getInstance())),
        'N', StackUtil.copyWithWildCard(IC2Items.getItem("nano_chestplate")),
        'G', IC2Items.getItem("cable", "type:glass,insulation:0"),
        'A', IC2Items.getItem("crafting", "advanced_circuit")
      );
    }

    if (!GraviConfig.DisableUltimateLappackCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.ULTIMATE_LAPPACK.getInstance()),

        "CPC",
        "CLC",
        "CSC",

        'C', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal")),
        'P', IC2Items.getItem("crafting", "iridium"),
        'L', StackUtil.copyWithWildCard(IC2Items.getItem("lappack")),
        'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR)
      );

      addShapedRecipe(
        new ItemStack(GraviItem.ULTIMATE_LAPPACK.getInstance()),

        "CPC",
        "CLC",
        "CSC",

        'C', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal")),
        'P', IC2Items.getItem("crafting", "iridium"),
        'L', StackUtil.copyWithWildCard(new ItemStack(GraviItem.ADVANCED_LAPPACK.getInstance())),
        'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR)
      );
    }

    if (!GraviConfig.DisableGraviChestplateCraft) {
      addShapedColourRecipe(
        new ItemStack(GraviItem.GRAVI_CHESTPLATE.getInstance()),

        "SAS",
        "DBD",
        "SCS",

        'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR),
        'A', StackUtil.copyWithWildCard(IC2Items.getItem("quantum_chestplate")),
        'D', GraviItem.CRAFTING.getItemStack(CraftingThingType.GRAVITATION_ENGINE),
        'B', IC2Items.getItem("te", "hv_transformer"),
        'C', StackUtil.copyWithWildCard(new ItemStack(GraviItem.ULTIMATE_LAPPACK.getInstance()))
      );
    }

    if (!GraviConfig.DisableAdvancedDrillCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.ADVANCED_DRILL.getInstance()),

        "ODO",
        "COC",

        'O', IC2Items.getItem("upgrade", "overclocker"),
        'D', StackUtil.copyWithWildCard(IC2Items.getItem("diamond_drill")),
        'C', IC2Items.getItem("crafting", "advanced_circuit")
      );
    }

    if (!GraviConfig.DisableAdvancedChainsawCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.ADVANCED_CHAINSAW.getInstance()),
        " D ",
        "OCO",
        "AOA",

        'D', "gemDiamond",
        'O', IC2Items.getItem("upgrade", "overclocker"),
        'C', StackUtil.copyWithWildCard(IC2Items.getItem("chainsaw")),
        'A', IC2Items.getItem("crafting", "advanced_circuit")
      );
    }

    if (!GraviConfig.DisableGraviToolCraft) {
      addShapedRecipe(
        new ItemStack(GraviItem.GRAVITOOL.getInstance()),

        "PHP",
        "AEA",
        "WCT",

        'P', IC2Items.getItem("crafting", "carbon_plate"),
        'H', StackUtil.copyWithWildCard(IC2Items.getItem("electric_hoe")),
        'A', IC2Items.getItem("crafting", "alloy"),
        'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal")),
        'W', StackUtil.copyWithWildCard(IC2Items.getItem("electric_wrench")),
        'C', IC2Items.getItem("crafting", "advanced_circuit"),
        'T', StackUtil.copyWithWildCard(IC2Items.getItem("electric_treetap"))
      );
    }

    if (!GraviConfig.DisableVajraCraft) {
      addShapedRecipe(
        GraviItem.CRAFTING.getItemStack(CraftingThingType.MAGNETRON),

        "ICI",
        "CSC",
        "ICI",

        'I', "plateIron",
        'C', "plateCopper",
        'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR)
      );

      addShapedRecipe(
        GraviItem.CRAFTING.getItemStack(CraftingThingType.VAJRA_CORE),

        " M ",
        "PTP",
        "SHS",

        'M', GraviItem.CRAFTING.getItemStack(CraftingThingType.MAGNETRON),
        'P', IC2Items.getItem("crafting", "iridium"),
        'T', IC2Items.getItem("te", "tesla_coil"),
        'S', GraviItem.CRAFTING.getItemStack(CraftingThingType.SUPERCONDUCTOR),
        'H', IC2Items.getItem("te", "hv_transformer")
      );

      addShapedRecipe(
        new ItemStack(GraviItem.VAJRA.getInstance()),

        "PEP",
        "CVC",
        "ALA",

        'P', "plateIron",
        'E', StackUtil.copyWithWildCard(IC2Items.getItem("energy_crystal")),
        'C', IC2Items.getItem("crafting", "carbon_plate"),
        'V', GraviItem.CRAFTING.getItemStack(CraftingThingType.VAJRA_CORE),
        'A', IC2Items.getItem("crafting", "alloy"),
        'L', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal"))
      );
    }
  }

  static void changeQuantumRecipe() {
    assert GraviConfig.ReplaceQuantumArmorCraft;

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

              'N', StackUtil.copyWithWildCard(new ItemStack(GraviItem.ADVANCED_NANO_CHESTPLATE.getInstance())),
              'A', IC2Items.getItem("crafting", "alloy"),
              'I', IC2Items.getItem("crafting", "iridium"),
              'L', StackUtil.copyWithWildCard(IC2Items.getItem("lapotron_crystal"))
            );
          }

          @ParametersAreNonnullByDefault
          public boolean matches(InventoryCrafting inv, World world) {
            return this.replacement.matches(inv, world);
          }

          @ParametersAreNonnullByDefault
          public @Nonnull ItemStack getCraftingResult(InventoryCrafting inv) {
            return this.replacement.getCraftingResult(inv);
          }

          public boolean canFit(int width, int height) {
            return this.replacement.canFit(width, height);
          }

          public @Nonnull ItemStack getRecipeOutput() {
            return this.replacement.getRecipeOutput();
          }

          @ParametersAreNonnullByDefault
          public @Nonnull NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
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
