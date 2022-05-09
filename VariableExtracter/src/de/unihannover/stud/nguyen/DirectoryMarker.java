package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.Objects;

public class DirectoryMarker {

  /**
   * Sets all directories except 1 of n directories as unreadable
   * This is reversible.
   *
   * @param parentDirectory parent directory of all directories
   * @param multiplier the n number
   */
  static void markSomeAsUnreadable(File parentDirectory, int multiplier) {
    int i = 0;

    for(File file : Objects.requireNonNull(parentDirectory.listFiles())) {

      if(file.isDirectory()) {
        if(i % multiplier == 1) {
          file.setReadable(true);
        }
        else {
          file.setReadable(false);
        }
        i++;
      }
    }
  }

  /**
   * Mark all directories as readable
   * @param parentDirectory parent directory of all directories
   */
  static void markAsReadable(File parentDirectory) {
    for(File file : Objects.requireNonNull(parentDirectory.listFiles())) {
      if(file.isDirectory()) {
        file.setReadable(true);
      }
    }
  }

}
