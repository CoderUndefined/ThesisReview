// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Implementation of the Configurable interface. Allows to apply, reset and check for modification
 * of the text fields. Data is loaded from the PersistentData class which is persistent
 */
public class Settings implements Configurable {

  private SettingsComponent settingsComponent;

  /**
   * Returns displayed name in the Settings panel
   *
   * @return "Variable Replacer Settings"
   */
  @Override
  public String getDisplayName() {
    return "Variable Replacer Settings";
  }

  /**
   * Create component when "Variable Replacer Settings" is selected in the Settings Panel
   *
   * @return JPanel from SettingsComopnent
   */
  @Override
  public @Nullable JComponent createComponent() {
    settingsComponent = new SettingsComponent();
    return settingsComponent.getJPanel();
  }

  /**
   * Check if data is modified
   *
   * @return true if one of the two text fields are modified
   */
  @Override
  public boolean isModified() {
    PersistentData persistentData = PersistentData.getInstance();
    boolean modified0 =
            !settingsComponent.getCacheFolderField().equals(persistentData.cacheFolder);
    boolean modified1 =
            !settingsComponent.getFullFolderField().equals(persistentData.fullFolder);
    boolean modified2 =
            !settingsComponent.getDictionaryField().equals(persistentData.dictionaryFilename);
    boolean modified3 =
            !settingsComponent.getAbbreviationField().equals(persistentData.abbreviationFileName);

    return modified0 || modified1 || modified2 || modified3;
  }

  /**
   * Save the content from the text fields, persistently.
   */
  @Override
  public void apply() {
    PersistentData persistentData = PersistentData.getInstance();
    persistentData.cacheFolder = settingsComponent.getCacheFolderField();
    persistentData.fullFolder = settingsComponent.getFullFolderField();
    persistentData.dictionaryFilename = settingsComponent.getDictionaryField();
    persistentData.abbreviationFileName = settingsComponent.getAbbreviationField();

    persistentData.firstStart =
            !(persistentData.cacheFolder.equals("") &&
                    persistentData.fullFolder.equals("") &&
                    persistentData.dictionaryFilename.equals("") &&
                    persistentData.abbreviationFileName.equals(""));
  }

  /**
   * Reset to the previously saved settings
   */
  @Override
  public void reset() {
    Configurable.super.reset();
    PersistentData persistentData = PersistentData.getInstance();
    settingsComponent.setCacheFolderField(persistentData.cacheFolder);
    settingsComponent.setFullFolderField(persistentData.fullFolder);
    settingsComponent.setDictionaryField(persistentData.dictionaryFilename);
    settingsComponent.setAbbreviationField(persistentData.abbreviationFileName);
  }

  /**
   * Dispose the UI
   */
  @Override
  public void disposeUIResources() {
    Configurable.super.disposeUIResources();
  }
}

// Based off AppSettingsConfigurable from
// Credits to JetBrains s.r.o