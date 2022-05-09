// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import de.unihannover.stud.nguyen.IOOperations;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static de.unihannover.stud.nguyen.StringOperations.initDictionaryAndAbbreviation;
import static de.unihannover.stud.nguyen.StringOperations.reverseAbbreviationMap;

public class OnApplicationStartup extends PreloadingActivity {

  /**
   * Preload assets when starting the IDE.
   * This may require a significantly increased heap to fit the assets
   *
   * @param indicator loading bar indicator, not used as loading times are quick
   */
  @Override
  public void preload(@NotNull ProgressIndicator indicator) {
    PersistentData persistentData = PersistentData.getInstance();
    if(persistentData.cacheFolder.equals("") ||
            persistentData.fullFolder.equals("") ||
            persistentData.dictionaryFilename.equals("") ||
            persistentData.abbreviationFileName.equals("")) {
      Global.loadingError = true;
//      persistentData.firstStart = true;
    }
    else  {
      try {
        // test loading

        boolean check = initDictionaryAndAbbreviation(
                persistentData.dictionaryFilename, persistentData.abbreviationFileName);

        if (!check) {
          throw new Exception();
        }

        reverseAbbreviationMap(persistentData.abbreviationFileName);

        String cacheFolder = persistentData.cacheFolder;
        String fullFolder = persistentData.fullFolder;

        File file = new File(cacheFolder);
        System.out.println(file.getAbsolutePath());

        Global.cacheFolderPath = cacheFolder;
        Global.fullFolderPath = fullFolder;

        // check full Folder. Also check if files are present
        
        String integrityString = IOOperations.readFile(new File(
                Global.fullFolderPath + File.separator + "integrity.txt"));

        if(integrityString == null) {
          throw new Exception();
        }
        if(!integrityString.equals(Global.integrityString)) {
          throw new Exception();
        }

        File threeDirNeg = new File(Global.fullFolderPath + File.separator + "-3");
        if (!threeDirNeg.exists() || !threeDirNeg.isDirectory()) {
          throw new Exception();
        }
        File fourDirNeg = new File(Global.fullFolderPath + File.separator + "-4");
        if (!fourDirNeg.exists() || !fourDirNeg.isDirectory()) {
          throw new Exception();
        }
        File fiveDirNeg = new File(Global.fullFolderPath + File.separator + "-5");
        if (!fiveDirNeg.exists() || !fiveDirNeg.isDirectory()) {
          throw new Exception();
        }




        // check cache Folder
        for(int i = 1; i <= 5; i++) {
          String cacheString = IOOperations.readFile(new File(
                  Global.cacheFolderPath + File.separator + "sclasslist"+ i +".txt"));
          if (cacheString == null) {
            throw new Exception();
          }
        }

        if (!cacheFolder.equals("") && !fullFolder.equals("")) {
          indicator.setIndeterminate(false);
          indicator.setFraction(0.1);
          Global.cacheMap = IOOperations.loaderFunction(
                  cacheFolder + File.separator + "sclasslist");
          // consumes too much RAM. Lighter version needed
          indicator.setFraction(0.95);
        }
        System.out.println("load");
        Global.loadingFinished = true;
      }
      catch (Exception e) {
        Global.loadingError = true;
        e.printStackTrace();
      }
    }
  }
}
