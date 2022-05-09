// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import de.unihannover.stud.nguyen.GraphOrigin;

import java.util.List;
import java.util.Map;

/**
 * Temporary global variables that are NOT persistent.
 */
public class Global {
  public static Map<Integer, Map<String, List<GraphOrigin>>> cacheMap;
  public static boolean loadingFinished = false;
  public static boolean loadingError = false;

  public static String integrityString = "qwertyuiop1234567890asdfghjklzxcvbnm";


  public static String fullFolderPath;
  public static String cacheFolderPath;


  public static String ALREADY_EXISTS = "FULL_STRING_ALREADY_IN_DICTIONARY_01";
}
