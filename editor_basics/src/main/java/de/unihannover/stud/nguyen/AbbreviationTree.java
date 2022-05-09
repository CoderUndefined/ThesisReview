package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbbreviationTree {
  // abbreviation -> type -> popularity -> fullName

  private final Map<String, Map<String, Map<String, Integer>>> tree;

  public AbbreviationTree() {
    this.tree = new ConcurrentHashMap<>();
  }

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

  private List<String> lastPart(Map<String, Integer> lastMap) {
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
      outputList.add(firstString);
      return outputList;
    }
    else if(thirdString == null) {
      outputList.add(firstString);
      outputList.add(secondString);
      return outputList;
    }
    else if(fourthString == null) {
      outputList.add(firstString);
      outputList.add(secondString);
      outputList.add(thirdString);
      return outputList;
    }
    else if(fifthString == null) {
      outputList.add(firstString);
      outputList.add(secondString);
      outputList.add(thirdString);
      outputList.add(fourthString);
      return outputList;
    }
    else {
      outputList.add(firstString);
      outputList.add(secondString);
      outputList.add(thirdString);
      outputList.add(fourthString);
      outputList.add(fifthString);
      return outputList;
    }

  }

  // this assumes all three are known

  public List<String> getFiltered(String abbreviation, String type) {
    if(!tree.containsKey(abbreviation)) {
      return null;
    }
    if(!tree.get(abbreviation).containsKey(type)) {
      return getWithoutTypeFiltered(abbreviation);
    }

    Map<String, Integer> lastMap = tree.get(abbreviation).get(type);
    return lastPart(lastMap);
  }

  public List<String> getWithoutTypeFiltered(String abbreviation) {
    if(!tree.containsKey(abbreviation)) {
      return null;
    }

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
    return lastPart(lastMap);
  }

  public Map<String, Integer> get(String abbreviation, String type) {
    if(!tree.containsKey(abbreviation)) {
      return null;
    }
    if(!tree.get(abbreviation).containsKey(type)) {
      return getWithoutType(abbreviation);
    }

    Map<String, Integer> lastMap = tree.get(abbreviation).get(type);
    return lastMap;
//    return lastPart(lastMap);
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
//    return lastPart(lastMap);
  }


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
    String fileAsString = IOOperations.readFile(new File(importFromThatFilename));
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



    IOOperations.writeFile(exportToThisFilename, stringBuilder.toString());
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



  // for testing only
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
