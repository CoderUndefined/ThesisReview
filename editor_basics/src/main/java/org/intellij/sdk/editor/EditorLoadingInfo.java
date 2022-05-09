// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Shows a non-clickable info when right clicking while the cache is loading
 * Do not show once loading is finished.
 */
public class EditorLoadingInfo extends AnAction {

  /**
   * Does nothing, it "has to be implemented"
   *
   * @param e ignored for this time
   */
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {}

  /**
   * Show this while the cache is still loading. Once finished, do not show
   *
   * @param e ignored for this time
   */
  @Override
  public void update(@NotNull final AnActionEvent e) {
    if(!Global.loadingFinished) {
      e.getPresentation().setEnabled(false);
    }
    else {
      e.getPresentation().setEnabledAndVisible(false);
    }
  }
}
