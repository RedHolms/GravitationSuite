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
import java.lang.reflect.Modifier;
import java.util.Set;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

public class GraviKeys extends Keyboard {
  public static final Keyboard.IKeyWatcher FlyKey;

  static {
    FlyKey = new KeyWatcher(GraviKeys.Key.fly);
  }

  public static void init() {
    IC2.keyboard.addKeyWatcher(FlyKey);
  }

  public static boolean isFlyKeyDown(EntityPlayer player) {
    return IC2.keyboard.isKeyDown(player, FlyKey);
  }

  private static class KeyWatcher implements Keyboard.IKeyWatcher {
    private final GraviKeys.Key m_key;

    KeyWatcher(GraviKeys.Key key) {
      this.m_key = key;
    }

    @Override
    public Keyboard.Key getRepresentation() {
      return this.m_key.m_key;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void checkForKey(Set<Keyboard.Key> pressedKeys) {
      if (GameSettings.isKeyDown(this.m_key.binding)) {
        pressedKeys.add(this.getRepresentation());
      }
    }
  }

  private enum Key {
    fly(33, "Gravi Fly Key");

    @SideOnly(Side.CLIENT)
    private KeyBinding binding;
    private final Keyboard.Key m_key;

    Key(int keyID, String description) {
      m_key = addKey(name());

      if (IC2.platform.isRendering()) {
        binding = new KeyBinding(description, keyID, "Gravisuite");
        ClientRegistry.registerKeyBinding(binding);
      }
    }

    // Returns Keyboard.Key.keys field object
    // Also makes it non-final
    private static Field getKeysField() {
      try {
        Field field = ReflectionUtil.getField(Keyboard.Key.class, "keys");

        // Make field non-final
        ReflectionUtil.getField(Field.class, "modifiers").setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
      } catch (Exception exception) {
        throw new RuntimeException("Error reflecting keys field!", exception);
      }
    }

    private Keyboard.Key addKey(String name) {
      Keyboard.Key key = EnumHelper.addEnum(Keyboard.Key.class, name, new Class[0]);
      ReflectionUtil.setValue(null, getKeysField(), ArrayUtils.add(Keyboard.Key.keys, key));
      return key;
    }
  }
}
