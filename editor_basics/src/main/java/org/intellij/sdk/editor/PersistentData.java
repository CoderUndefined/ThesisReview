// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// remember these things:
// cache location
// full folder location (that one with a million files or so)

/**
 * Persistent data that is saved even when the IDE is shut down.
 * The values below are default values if nothing else is found, such as
 * when the plugin is started for the first time.
 */
@State(name = "org.intellij.sdk.editor.PersistentData", storages = @Storage("Settings.xml"))
public class PersistentData implements PersistentStateComponent<PersistentData> {

  public String cacheFolder = "";
  public String fullFolder = "";
  public String dictionaryFilename = "";
  public String abbreviationFileName = "";
  public boolean firstStart = false;

  /**
   * Get the current instance of PersistentData
   * @return current instance of PersistentData
   */
  public static PersistentData getInstance() {
    return ApplicationManager.getApplication().getService(PersistentData.class);
  }

  /**
   * Get the current state of PersistentData
   * @return current state of PersistentData
   */
  @Override
  public @Nullable
  PersistentData getState() {
    return this;
  }

  /**
   * Load the persistent data from the hard drive
   * @param state state of PersistentData
   */
  @Override
  public void loadState(@NotNull PersistentData state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}

// TODO: First time startup prompt