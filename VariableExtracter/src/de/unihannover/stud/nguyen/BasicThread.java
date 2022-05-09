package de.unihannover.stud.nguyen;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.*;
import java.util.*;

import static de.unihannover.stud.nguyen.AbbreviationChecker.*;

class BasicThread extends Thread {
  List<File> partialFileList;
  Set<String> abbreviationSet;
  AbbreviationTree abbreviationTree;
  boolean active = true;

  public BasicThread(Set<String> abbreviationSet,
                     AbbreviationTree abbreviationTree) {
      partialFileList = new ArrayList<>();
      this.abbreviationSet = abbreviationSet;
      this.abbreviationTree = abbreviationTree;
  }

  public void run() {
    threadcounter = threadcounter + 1;
    traversal(partialFileList, abbreviationSet, abbreviationTree);
    active = false;
    threadcounter = threadcounter - 1;
    if(threadcounter < 0) {
      System.out.println("WARNING COUNTER BELOW 0 " + threadcounter);
    }
  }

  public List<File> getPartialFileList() {
    return partialFileList;
  }

  public boolean isActive() {
    return active;
  }
}
