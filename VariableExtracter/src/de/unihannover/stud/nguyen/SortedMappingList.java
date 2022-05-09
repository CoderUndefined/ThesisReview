package de.unihannover.stud.nguyen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.unihannover.stud.nguyen.StringOperations.checkString;
import static de.unihannover.stud.nguyen.StringOperations.checkValidVariableName;

/**
 * List that contains Mappings
 */
class SortedMappingList {
  List<Mapping> mappingList;

  /**
   * Constructor for the SortedMappingList which contains variable names and occurences.
   * Also sorted them right away
   *
   * @param charMap Existing char map
   */
  public SortedMappingList(Map<String, Long> charMap) {
    List<Mapping> mappingList = new ArrayList<>();
    for (String string : charMap.keySet()) {
      if (checkString(string)) {
        mappingList.add(new Mapping(string, charMap.get(string)));
      }
    }

    Collections.sort(mappingList);
    this.mappingList = mappingList;
  }

  /**
   * Basic constructor if another sortedMappingList exists.
   *
   * @param sortedMappingList Previous sortedMappingList
   */
  public SortedMappingList(SortedMappingList sortedMappingList) {
    this.mappingList = sortedMappingList.getMappingList();
  }

  /**
   * Empty constructor that initializes a list.
   */
  public SortedMappingList() {
    this.mappingList = new ArrayList<>();
  }

  /**
   * Constructs a sorted output for the mapping list then stringifies it.
   *
   * @return Stringified and sorted output of the list
   */
  public String toString(ClassificationMap classificationMap, boolean nonEnglish,
                         boolean b, boolean extra, boolean abr) {
    int SOFTLIMIT = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for (Mapping m : mappingList) {
      if(!checkValidVariableName(m.string)) {
        continue;
      }


      if(abr) {
        if (m.number > SOFTLIMIT && classificationMap.partialAbrSet.contains(m.string) && b) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
        else if (m.number > SOFTLIMIT && classificationMap.weakAbrSet.contains(m.string)) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
      }
      else {

        if (m.number > SOFTLIMIT && classificationMap.nonEnglishSet.contains(m.string) && nonEnglish) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
        else if (m.number > SOFTLIMIT && classificationMap.partialEnglishSet.contains(m.string) && b) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
        else if (m.number > SOFTLIMIT && classificationMap.weakEnglishSet.contains(m.string)) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
        else if (m.number > SOFTLIMIT && classificationMap.nonEnglishPlusOneSet.contains(m.string)
                && extra && !b && !nonEnglish) {
          stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
        }
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Constructs a sorted output for the mapping list then stringifies it.
   *
   * @return Stringified and sorted output of the list
   */
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Mapping m : mappingList) {
      stringBuilder.append(m.string).append(" => ").append(m.number).append("\n");
    }
    return stringBuilder.toString();
  }

  /**
   * Get mapping list
   *
   * @return mappingList. Has variable names and occurences, sorted.
   */
  public List<Mapping> getMappingList() {
    return mappingList;
  }
}
