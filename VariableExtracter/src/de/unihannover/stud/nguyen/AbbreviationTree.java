package de.unihannover.stud.nguyen;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbbreviationTree {
  // abbreviation -> type -> popularity -> fullName

  private final Map<String, Map<String, Map<String, Integer>>> tree;

  public AbbreviationTree() {
    this.tree = new ConcurrentHashMap<>();
  }

  /**
   * Insert a full entry with popularity number to the abbreviation tree
   *
   * @param abbreviation abbreviation
   * @param type type if there is one
   * @param fullName full word of the abbreviation
   * @param popularity number of occurrences
   */
  public void insertFull(String abbreviation, String type, String fullName, int popularity) {
    if(!tree.containsKey(abbreviation)) {
      tree.put(abbreviation, new ConcurrentHashMap<>());
    }
    if(!tree.get(abbreviation).containsKey(type)) {
      tree.get(abbreviation).put(type, new ConcurrentHashMap<>());
    }
    if(!tree.get(abbreviation).get(type).containsKey(fullName)) {
      tree.get(abbreviation).get(type).put(fullName, popularity);
    }
    else {
      tree.get(abbreviation).get(type).put(fullName, popularity);
    }
  }

  /**
   * Insert an entry to the abbreviation tree
   *
   * @param abbreviation abbreviation
   * @param type type if there is one
   * @param fullName full word of the abbreviation
   */
  public void insert(String abbreviation, String type, String fullName) {
    if(!tree.containsKey(abbreviation)) {
      tree.put(abbreviation, new ConcurrentHashMap<>());
    }
    if(!tree.get(abbreviation).containsKey(type)) {
      tree.get(abbreviation).put(type, new ConcurrentHashMap<>());
    }
    if(!tree.get(abbreviation).get(type).containsKey(fullName)) {
      tree.get(abbreviation).get(type).put(fullName, 1);
    }
    else {
      int oldPopularity = tree.get(abbreviation).get(type).get(fullName);
      tree.get(abbreviation).get(type).replace(fullName, oldPopularity + 1);
    }
  }

  /**
   * Get the top 5 results.
   *
   * @param lastMap the map
   * @param suffix suffix that will be readded if there was one
   * @return top 5 results
   */
  private List<String> sortedTopFiveList(Map<String, Integer> lastMap, String suffix) {
    if(suffix == null) {
      suffix = "";
    }

    String firstString = null;
    int firstPopularity = -1;

    String secondString = null;
    int secondPopularity = -2;

    String thirdString = null;
    int thirdPopularity = -3;

    String fourthString = null;
    int fourthPopularity = -4;

    String fifthString = null;
    int fifthPopularity = -5;

    for(String potentialFullWord : lastMap.keySet()) {
      int popularity = lastMap.get(potentialFullWord);
      if(popularity > firstPopularity) {
        fifthString = fourthString;
        fifthPopularity = fourthPopularity;

        fourthString = thirdString;
        fourthPopularity = thirdPopularity;

        thirdString = secondString;
        thirdPopularity = secondPopularity;

        secondString = firstString;
        secondPopularity = firstPopularity;

        firstString = potentialFullWord;
        firstPopularity = popularity;
      }
      else if(popularity > secondPopularity) {
        fifthString = fourthString;
        fifthPopularity = fourthPopularity;

        fourthString = thirdString;
        fourthPopularity = thirdPopularity;

        thirdString = secondString;
        thirdPopularity = secondPopularity;

        secondString = potentialFullWord;
        secondPopularity = popularity;
      }
      else if(popularity > thirdPopularity) {
        fifthString = fourthString;
        fifthPopularity = fourthPopularity;

        fourthString = thirdString;
        fourthPopularity = thirdPopularity;

        thirdString = potentialFullWord;
        thirdPopularity = popularity;
      }
      else if(popularity > fourthPopularity) {
        fifthString = fourthString;
        fifthPopularity = fourthPopularity;

        fourthString = potentialFullWord;
        fourthPopularity = popularity;
      }
      else if(popularity > fifthPopularity) {
        fifthString = potentialFullWord;
        fifthPopularity = popularity;
      }
    }

    List<String> outputList = new ArrayList<>();
    if(firstString == null) {
      return null;
    }
    else if(secondString == null) {
      outputList.add(reAddSuffix(firstString, suffix));
      return outputList;
    }
    else if(thirdString == null) {
      outputList.add(reAddSuffix(firstString, suffix));
      outputList.add(reAddSuffix(secondString, suffix));
      return outputList;
    }
    else if(fourthString == null) {
      outputList.add(reAddSuffix(firstString, suffix));
      outputList.add(reAddSuffix(secondString, suffix));
      outputList.add(reAddSuffix(thirdString, suffix));
      return outputList;
    }
    else if(fifthString == null) {
      outputList.add(reAddSuffix(firstString, suffix));
      outputList.add(reAddSuffix(secondString, suffix));
      outputList.add(reAddSuffix(thirdString, suffix));
      outputList.add(reAddSuffix(fourthString, suffix));
      return outputList;
    }
    else {
      outputList.add(reAddSuffix(firstString, suffix));
      outputList.add(reAddSuffix(secondString, suffix));
      outputList.add(reAddSuffix(thirdString, suffix));
      outputList.add(reAddSuffix(fourthString, suffix));
      outputList.add(reAddSuffix(fifthString, suffix));
      return outputList;
    }

  }

  /**
   * Add the suffix to the string
   *
   * @param string original String
   * @param suffix suffix
   * @return String with suffix
   */
  private String reAddSuffix(String string, String suffix) {
    if(string.endsWith("y") && (suffix.equals("s") || suffix.equals("es"))) {
      return string.substring(0, string.length()-1) + "ies";
    }
    else if(string.endsWith("x") && (suffix.equals("s") || suffix.equals("es"))) {
      return string + "es";
    }
    else if(string.endsWith("o") && (suffix.equals("s") || suffix.equals("es"))) {
      return string + "es";
    }
    else if(string.endsWith("s") && (suffix.equals("s") || suffix.equals("es"))) {
      return string + "es";
    }
    else if(string.endsWith("sh") && (suffix.equals("s") || suffix.equals("es"))) {
      return string + "es";
    }
    else if(string.endsWith("ch") && (suffix.equals("s") || suffix.equals("es"))) {
      return string + "es";
    }

    else {
      return string + suffix;
    }
  }

  // this assumes all three are known

  /**
   * Get a base from of the string, which can be reversed
   *
   * @param abbreviation string, usually an abbreviation
   * @return base form
   */
  public List<String> getReversibleBaseForm(String abbreviation) {
    String baseForm = null;
    String suffix = null;

    if(abbreviation.endsWith("ies") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3) + "y")) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3) + "y";
      suffix = "ies";
    }
    else if(abbreviation.endsWith("oes") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "es";
    }
    else if(abbreviation.endsWith("ses") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "es";
    }
    else if(abbreviation.endsWith("shes") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "es";
    }
    else if(abbreviation.endsWith("ches") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "es";
    }
    else if(abbreviation.endsWith("xes") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "es";
    }
    else if(abbreviation.endsWith("s") &&
            !abbreviation.endsWith("ss") &&
            this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "s";
    }


    else if(abbreviation.endsWith("_0") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_0";
    }
    else if(abbreviation.endsWith("_1") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_1";
    }
    else if(abbreviation.endsWith("_2") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_2";
    }
    else if(abbreviation.endsWith("_3") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_3";
    }
    else if(abbreviation.endsWith("_4") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_4";
    }
    else if(abbreviation.endsWith("_5") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_5";
    }
    else if(abbreviation.endsWith("_6") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_6";
    }
    else if(abbreviation.endsWith("_7") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_7";
    }
    else if(abbreviation.endsWith("_8") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_8";
    }
    else if(abbreviation.endsWith("_9") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_9";
    }

    else if(abbreviation.endsWith("0") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_0";
    }
    else if(abbreviation.endsWith("1") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_1";
    }
    else if(abbreviation.endsWith("2") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_2";
    }
    else if(abbreviation.endsWith("3") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_3";
    }
    else if(abbreviation.endsWith("4") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_4";
    }
    else if(abbreviation.endsWith("5") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_5";
    }
    else if(abbreviation.endsWith("6") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_6";
    }
    else if(abbreviation.endsWith("7") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_7";
    }
    else if(abbreviation.endsWith("8") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_8";
    }
    else if(abbreviation.endsWith("9") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-1))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-1);
      suffix = "_9";
    }



    else if(abbreviation.endsWith("_1d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3);
      suffix = "_1d";
    }
    else if(abbreviation.endsWith("_2d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3);
      suffix = "_2d";
    }
    else if(abbreviation.endsWith("_3d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3);
      suffix = "_3d";
    }
    else if(abbreviation.endsWith("_4d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3);
      suffix = "_4d";
    }
    else if(abbreviation.endsWith("_5d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-3))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-3);
      suffix = "_5d";
    }

    else if(abbreviation.endsWith("1d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_1d";
    }
    else if(abbreviation.endsWith("2d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_2d";
    }
    else if(abbreviation.endsWith("3d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_3d";
    }
    else if(abbreviation.endsWith("4d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_4d";
    }
    else if(abbreviation.endsWith("5d") && this.containsAbbreviation(
            abbreviation.substring(0, abbreviation.length()-2))) {
      baseForm = abbreviation.substring(0, abbreviation.length()-2);
      suffix = "_5d";
    }

    if(baseForm == null) {
      return null;
    }
    else {
      List<String> pair = new ArrayList<>();
      pair.add(baseForm);
      pair.add(suffix);
      return pair;
    }

  }

  public List<String> getFiltered(String abbreviation, String type) {
    // use base form abbreviation then add back in


    // TODO: handle plurals here
    List<String> pairOfValues = getReversibleBaseForm(abbreviation);


    if(!abbreviation.equals("bbs") && (
            pairOfValues == null || abbreviation.length() == 2)) {

      if(!tree.containsKey(abbreviation)) {
        return null;
      }
      if(!tree.get(abbreviation).containsKey(type)) {
        return getWithoutTypeFiltered(abbreviation, null);
      }

      Map<String, Integer> lastMap = tree.get(abbreviation).get(type);
      return sortedTopFiveList(lastMap, null);
    }
    else {
      String baseForm = pairOfValues.get(0);
      String suffix = pairOfValues.get(1);

      if(!tree.containsKey(baseForm)) {
        if(!tree.containsKey(abbreviation)) {
          return null;
        }
        else if(tree.containsKey(abbreviation) &&
                 !tree.get(abbreviation).containsKey(type)) {
          return getWithoutTypeFiltered(abbreviation, null);
        }
        else {
          Map<String, Integer> lastMap = tree.get(abbreviation).get(type);
          return sortedTopFiveList(lastMap, null);
        }
      }
      if(!tree.get(baseForm).containsKey(type)) {
        return getWithoutTypeFiltered(baseForm, suffix);
      }


      Map<String, Integer> lastMap = tree.get(baseForm).get(type);
      return sortedTopFiveList(lastMap, suffix);
    }


    // try out plural ones and the ones with numbers
  }

  private List<String> internalPart(String abbreviation, String suffix) {

    Map<String, Integer> lastMap = new ConcurrentHashMap<>();

    for(String type : tree.get(abbreviation).keySet()) {
      for (String fullName : tree.get(abbreviation).get(type).keySet()) {

        int popularity = tree.get(abbreviation).get(type).get(fullName);

        if (!lastMap.containsKey(fullName)) {
          lastMap.put(fullName, popularity);
        }
        else {
          lastMap.put(fullName, lastMap.get(fullName) + popularity);
        }
      }
    }
    return sortedTopFiveList(lastMap, suffix);
  }


  private List<String> internalPartCombined(String abbreviation, String suffix, String full) {

    Map<String, Integer> lastMap = new ConcurrentHashMap<>();

    for(String type : tree.get(abbreviation).keySet()) {
      for (String fullName : tree.get(abbreviation).get(type).keySet()) {

        int popularity = tree.get(abbreviation).get(type).get(fullName);

        if (!lastMap.containsKey(fullName)) {
          lastMap.put(fullName, popularity);
        }
        else {
          lastMap.put(fullName, lastMap.get(fullName) + popularity);
        }
      }
    }

    if(full != null && tree.containsKey(full)) {
      for (String type : tree.get(full).keySet()) {
        for (String fullName : tree.get(full).get(type).keySet()) {

          int popularity = tree.get(full).get(type).get(fullName);

          if (!lastMap.containsKey(fullName)) {
            lastMap.put(fullName, popularity);
          } else {
            lastMap.put(fullName, lastMap.get(fullName) + popularity);
          }
        }
      }
    }
    return sortedTopFiveList(lastMap, suffix);
  }



  public List<String> getWithoutTypeFiltered(String abbreviation, String existingSuffix) {
    List<String> pairOfValues = getReversibleBaseForm(abbreviation);

    if(existingSuffix != null) {
      if(!tree.containsKey(abbreviation)) {
        return null;
      }
      return internalPart(abbreviation, existingSuffix);
    }
    else if(!abbreviation.equals("bbs") && (
            pairOfValues == null || abbreviation.length() == 2)) {
      if(!tree.containsKey(abbreviation)) {
        return null;
      }
      return internalPart(abbreviation, null);
    }
    else {
      String baseForm = pairOfValues.get(0);
      String suffix = pairOfValues.get(1);

      if(!tree.containsKey(baseForm)) {
        if (!tree.containsKey(abbreviation)) {
          return null;
        }
        else {
          return internalPart(abbreviation, null);
        }
      }
      return internalPart(baseForm, suffix);
//      return internalPartCombined(baseForm, suffix, abbreviation);
    }
  }

  public Map<String, Integer> get(String abbreviation, String type) {
    if(!tree.containsKey(abbreviation)) {
      return null;
    }
    if(!tree.get(abbreviation).containsKey(type)) {
      return getWithoutType(abbreviation);
    }

    return tree.get(abbreviation).get(type);
//    return sortedTopFiveList(lastMap);
  }

  public Map<String, Integer> getWithoutType(String abbreviation) {
    if(!tree.containsKey(abbreviation)) {
      return null;
    }

    Map<String, Integer> lastMap = new ConcurrentHashMap<>();

    for(String type : tree.get(abbreviation).keySet()) {
      Map<String, Integer> currentMap = tree.get(abbreviation).get(type);
      for (String fullName : currentMap.keySet()) {
        int popularity = currentMap.get(fullName);

        if (!lastMap.containsKey(fullName)) {
          lastMap.put(fullName, popularity);
        } else {
          lastMap.put(fullName, lastMap.get(fullName) + popularity);
        }
      }
    }

    return lastMap;
//    return sortedTopFiveList(lastMap);
  }


  /**
   * This will ignore the type of the abbreviation
   *
   * @param abbreviation abbreviation
   */
  public void consolidate(String abbreviation) {

    if(tree.containsKey(abbreviation)) {
      List<RankingItem> list = new ArrayList<>();
      for(String type : tree.get(abbreviation).keySet())     {
        for(String fullName : tree.get(abbreviation).get(type).keySet()) {
          RankingItem rankingItem = new RankingItem(fullName,
                  tree.get(abbreviation).get(type).get(fullName));
          list.add(rankingItem);
        }

      }

      tree.put(abbreviation, new ConcurrentHashMap<>());
      tree.get(abbreviation).put("{OTHER}", new ConcurrentHashMap<>());
      for(RankingItem rankingItem : list) {
        String word = rankingItem.string;
        tree.get(abbreviation).get("{OTHER}").put(word, rankingItem.popularity);
      }
    }

  }

  public boolean containsAbbreviation(String abbreviation) {
    return tree.containsKey(abbreviation);
  }


  public int getNumberOfDifferentWords(String abbreviation) {
    if(tree.containsKey(abbreviation)) {
      int count = 0;
      for(String type : tree.get(abbreviation).keySet())     {
        for(String ignored : tree.get(abbreviation).get(type).keySet()) {
          count++;
        }
      }
      return count;
    }
    else return 0;
  }


  // line looks like this:
  // idx;int;get_index;index;100

  public void importTree(String importFromThatFilename) {
    String fileAsString = Utility.readFile(importFromThatFilename);
    if(fileAsString == null) {
      return;
    }

    Scanner scanner = new Scanner(fileAsString);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);
      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);
      String fullName = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);
      String popularityString = line;
      int popularity = Integer.parseInt(popularityString);

      insertFull(abbreviation, type, fullName, popularity);
    }
  }

  private int getNumberOfEntriesPerAbbreviation(String abbreviation) {
    if(tree.containsKey(abbreviation)) {
      int count = 0;
      for(String type : tree.get(abbreviation).keySet()) {
        for(String fullWord : tree.get(abbreviation).get(type).keySet()) {
          count = count + tree.get(abbreviation).get(type).get(fullWord);
        }
      }
      return count;
    }
    else return 0;
  }

  private int getNumberOfEntriesPerType(String abbreviation, String type) {
    if(tree.containsKey(abbreviation)) {
      if(tree.get(abbreviation).containsKey(type)) {
        int count = 0;
        for (String fullWord : tree.get(abbreviation).get(type).keySet()) {
          count = count + tree.get(abbreviation).get(type).get(fullWord);
        }
        return count;
      }
      else return 0;
    }
    else return 0;
  }


  /**
   * Exports to a text file
   *
   * @param exportToThisFilename location of text file
   */
  public void exportTree(String exportToThisFilename) {

    StringBuilder stringBuilder = new StringBuilder();

    Set<String> usedAbbreviations = new HashSet<>();
    for(int i = 0; i < tree.size(); i++) {
      int popularityAbbreviation = -1;
      String mostPopularAbbreviation = null;

      for (String abbreviation : tree.keySet()) {
        int number = getNumberOfEntriesPerAbbreviation(abbreviation);
        if (number > popularityAbbreviation && !usedAbbreviations.contains(abbreviation)) {
          popularityAbbreviation = number;
          mostPopularAbbreviation = abbreviation;
        }
      }
      usedAbbreviations.add(mostPopularAbbreviation);
      Set<String> usedTypes = new HashSet<>();

      if(popularityAbbreviation != -1 && mostPopularAbbreviation != null) {
        for (int j = 0; j < tree.get(mostPopularAbbreviation).size(); j++) {
          // format
          int popularityType = -1;
          String mostPopularType = null;

          for (String type : tree.get(mostPopularAbbreviation).keySet()) {
            int number = getNumberOfEntriesPerType(mostPopularAbbreviation, type);
            if (number > popularityType && !usedTypes.contains(type)) {
              popularityType = number;
              mostPopularType = type;
            }
          }
          usedTypes.add(mostPopularType);

          if (popularityType != -1 && mostPopularType != null) {
            stringBuilder.append(toStringWithExact(mostPopularAbbreviation, mostPopularType));
          }

        }
      }
    }



    Utility.writeFile(exportToThisFilename, stringBuilder.toString());
  }

  private String toStringWithExact(String abbreviation, String type) {
    StringBuilder stringBuilder = new StringBuilder();

    for(String fullName : tree.get(abbreviation).get(type).keySet()) {
      int popularity = tree.get(abbreviation).get(type).get(fullName);

      stringBuilder.append(abbreviation).append(";")
              .append(type).append(";")
              .append(fullName).append(";")
              .append(popularity).append("\n");
    }



    return stringBuilder.toString();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbbreviationTree that = (AbbreviationTree) o;
    return Objects.equals(tree, that.tree);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tree);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    for(String abbreviation : tree.keySet()) {
      for(String type : tree.get(abbreviation).keySet()) {
          for(String fullName : tree.get(abbreviation).get(type).keySet()) {
            int popularity = tree.get(abbreviation).get(type).get(fullName);

            stringBuilder.append(abbreviation).append(";")
                    .append(type).append(";")
                    .append(fullName).append(";")
                    .append(popularity).append("\n");
          }

      }
    }

    return stringBuilder.toString();
  }



  // for testing only, this should not be used. Adds some entries to a dummy tree
  public static void main(String[] args) {
    AbbreviationTree abbreviationTree = new AbbreviationTree();
    abbreviationTree.insertFull("idx",
            "int",
            "index",
            90);
    abbreviationTree.insertFull("idx",
            "int",
            "indexer",
            1);
    abbreviationTree.insert("idx",
            "int",
            "index");
    abbreviationTree.insert("idx",
            "int",
            "index");
    abbreviationTree.insert("idx",
            "int",
            "index");
    abbreviationTree.exportTree("outoutoutout.txt");

    abbreviationTree = new AbbreviationTree();
    abbreviationTree.importTree("outoutoutout.txt");
    System.out.println(abbreviationTree);

  }


}
