package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.List;
import java.util.Map;

class TraversalThread extends Thread {
  List<File> list;
  Map<String, Long> stringLongMap;
  boolean running = true;

  public TraversalThread(List<File> list, Map<String, Long> stringLongMap) {
    this.list = list;
    this.stringLongMap = stringLongMap;
  }

  public void run() {
    VariableExtractor.directoryTraversal(list, stringLongMap);
    running = false;
  }

  public boolean isRunning() {
    return running;
  }
}
