package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.*;

public class AbbreviationSummer {


  /**
   * Function to merge all files in a single directory
   * Should not be used anymore
   *
   * @param folderName All files in a single directory
   * @param targetFilename New single text file
   */
  static void mergeFiles(String folderName, String targetFilename) {
    StringBuilder stringBuilder = new StringBuilder();
//        File directory = new File("./resultsFolder2FULL");
    for(int i = 0; i < 1000; i++) { // must be in order

      File file = new File(folderName+File.separator+i+".txt");
      if(!file.exists()) {
        break;
      }

      System.out.println(file.getAbsolutePath());
      String entireFile = Utility.readFile(file.getAbsolutePath());
      assert entireFile != null;
      if (!entireFile.equals("\n") && !entireFile.isEmpty()) {
        stringBuilder.append(entireFile);
        stringBuilder.append("\n");
      }
    }
    Utility.writeFile(targetFilename,stringBuilder.toString());
  }

  /**
   * Create a map from a text file
   * used to generate a map of abbreviation and expected result
   *
   * @param filename text file containing list of abbreviations and expected result
   * @return Java map of abbreviation and result
   */
  public static Map<String, List<String>> createMap(String filename) {
    String expectedText = Utility.readFile(filename);
    assert expectedText != null;
    Scanner scanner = new Scanner(expectedText);

    Map<String, List<String>> map = new HashMap<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.contains(";")) {
        String abbreviation = line.substring(0, line.indexOf(";"));
        String expectedFullWord = line.substring(line.indexOf(";") + 1);

        if(expectedFullWord.contains(";")) {
          expectedFullWord = expectedFullWord.substring(0, expectedFullWord.indexOf(";"));
        }

        if (!map.containsKey(abbreviation)) {
          map.put(abbreviation, new ArrayList<>());
        }

        map.get(abbreviation).add(expectedFullWord);
      }
    }


    return map;
  }

  /**
   * Test accuracy of abbreviations using manually set files
   *
   * @param filename abbreviation tree text file
   * @param testFilename text file containing list of abbreviations and expected result
   */
  static void testAbbreviations(String filename, String testFilename) {
    StringOperations.initAbbreviationMap(filename);

    AbbreviationTree abbreviationTree = new AbbreviationTree();
    abbreviationTree.importTree(filename);

    Map<String, List<String>> expectedMap = createMap(testFilename);

    int total = 0;
    int foundPerfect = 0;
    int foundTop2 = 0;
    int foundTop3 = 0;
    int foundTop5 = 0;
    int foundTooFar = 0;
    int foundAtAll = 0;


    for(String expectedAbbreviation : expectedMap.keySet()) {
      List<String> resolveList = abbreviationTree.getWithoutTypeFiltered(expectedAbbreviation, null);
//      System.out.println("expected " + expectedAbbreviation +
//              "=>" + expectedMap.get(expectedAbbreviation));
//      System.out.println("actual: " + resolveList);


      if (resolveList != null) {
        if (resolveList.contains(expectedMap.get(expectedAbbreviation).get(0))) {
          int index = resolveList.indexOf(expectedMap.get(expectedAbbreviation).get(0));
          if (index == 0) {
            foundPerfect++;
            foundTop2++;
            foundTop3++;
            foundTop5++;
            foundTooFar++;
          } else if (index == 1) {
            foundTop2++;
            foundTop3++;
            foundTop5++;
            foundTooFar++;
          } else if (index == 2) {
            foundTop3++;
            foundTop5++;
            foundTooFar++;
          } else if (index == 3 || index == 4) {
            foundTop5++;
            foundTooFar++;
          } else {
            foundTooFar++;
          }
        } else {
//          System.out.println("No match for: " + expectedAbbreviation);
        }
        foundAtAll++;
      } else {
//        System.out.println("ERROR: NOT FOUND: " + expectedAbbreviation);
      }

      total++;
    }
    System.out.println("Top 1 result:  " + foundPerfect + " / 400");
    System.out.println("Top 2 results: " + foundTop2 + " / 400");
    System.out.println("Top 3 results: " + foundTop3 + " / 400");
    System.out.println("Top 5 results: " + foundTop5 + " / 400");
    System.out.println("In Abr. tree:  " + foundAtAll + " / 400");
//    System.out.println(total);
    System.out.println();

  }

  /**
   * Public function to merge all files in a single directory
   * Should not be used anymore
   *
   * @param folderName All files in a single directory
   * @param targetName New single text file
   */
  public static void call(String folderName, String targetName) {
    mergeFiles(folderName, targetName);
  }

  /**
   * Test abbreviation accuracy
   *
   * @param filename Abbreviation tree text file
   */
  public static void testAbbreviations2(String filename) {
    System.out.println("Results DS");
    testAbbreviations(filename, "expectedResultsDS.txt");
    System.out.println("Results Non-DS");
    testAbbreviations(filename, "expectedResultsNODS.txt");
    System.out.println("Results Control");
    testAbbreviations(filename, "expectedResultsControl.txt");

  }


  /**
   * Main function, no longer required
   *
   * @param args Does not accept any arguments
   */
  public static void main(String[] args) {
    System.out.println("Results DS");
    testAbbreviations("seventhRound.txt", "expectedResultsDS.txt");
    System.out.println("Results Non-DS");
    testAbbreviations("seventhRound.txt", "expectedResultsNODS.txt");
    System.out.println("Results Control");
    testAbbreviations("seventhRound.txt", "expectedResultsControl.txt");
  }
}