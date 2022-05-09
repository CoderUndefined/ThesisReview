// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * Shows a warning if the user has not set the cache and full directories.
 * This should not appear once the user has set these directories
 */
public class FirstTimeStartup extends AnAction {

  /**
   * Shows a warning if the user has not set the cache and full directories.
   * This should not appear once the user has set these directories
   *
   * @param e not used here
   */
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    boolean firstTime = PersistentData.getInstance().firstStart;

    if(!firstTime) {
      Messages.showWarningDialog(
              "Please change the cache and full directories in the settings menu",
              "Warning: First Time Setup Required");
    }
  }

  /**
   * Should only appear when the directories are not set.
   *
   * @param e current instance of the presentation?
   */
  @Override
  public void update(@NotNull final AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(!PersistentData.getInstance().firstStart);
  }
}
