//
// ! Code in this file is not original !
//
// Original code author is Chocohead
// Code was decompiled by RedHolms
// Check original mod by link: https://legacy.curseforge.com/minecraft/mc-mods/gravitation-suite
//

package com.chocohead.gravisuite;

import ic2.core.IC2;
import ic2.core.util.Keyboard;
import ic2.core.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

public final class GraviKeys extends Keyboard {
  private static final Keyboard.IKeyWatcher FLY_KEY;

  static void addFlyKey() {
    IC2.keyboard.addKeyWatcher(FLY_KEY);
  }

  public static boolean isFlyKeyDown(EntityPlayer player) {
    return IC2.keyboard.isKeyDown(player, FLY_KEY);
  }

  static {
    FLY_KEY = new KeyWatcher(GraviKeys.GraviKey.fly);
  }

  private static class KeyWatcher implements Keyboard.IKeyWatcher {
    private final GraviKey key;

    public KeyWatcher(GraviKey key) {
      this.key = key;
    }

    public Keyboard.Key getRepresentation() {
      return this.key.key;
    }

    @SideOnly(Side.CLIENT)
    public void checkForKey(Set<Keyboard.Key> pressedKeys) {
      if (GameSettings.isKeyDown(this.key.binding)) {
        pressedKeys.add(this.getRepresentation());
      }

    }
  }

  private enum GraviKey {
    fly(33, "Gravi Fly Key");

    private final Keyboard.Key key = this.addKey(this.name());
    @SideOnly(Side.CLIENT)
    private KeyBinding binding;

    private static Field getKeysField() {
      try {
        Field field = ReflectionUtil.getField(Keyboard.Key.class, "keys");
        ReflectionUtil.getField(Field.class, new String[]{"modifiers"}).setInt(field, field.getModifiers() & -17);
        return field;
      } catch (Exception exception) {
        throw new RuntimeException("Error reflecting keys field!", exception);
      }
    }

    GraviKey(int keyID, String description) {
      if (IC2.platform.isRendering()) {
        ClientRegistry.registerKeyBinding(this.binding = new KeyBinding(description, keyID, "gravisuite".substring(0, 1).toUpperCase(Locale.ENGLISH) + "gravisuite".substring(1)));
      }

    }

    private Keyboard.Key addKey(String name) {
      Keyboard.Key key = EnumHelper.addEnum(Keyboard.Key.class, name, new Class[0]);
      ReflectionUtil.setValue(null, getKeysField(), ArrayUtils.add(Key.keys, key));
      return key;
    }
  }
}
