package de.unihannover.stud.nguyen;

import java.util.*;

public class RecursiveResolver {

  private static String[] internalResolve(String fullWord, String type) {
    String firstOutput = null;
    String secondOutput = null;
    String thirdOutput = null;
    while(!StringOperations.notInAbrMap(fullWord)) {
      String firstString = null;
      String secondString = null;
      String thirdString = null;
      int firstPopularity = -1;
      int secondPopularity = -2;
      int thirdPopularity = -3;

      Map<String, Integer> map = StringOperations.getAbbreviations(fullWord, null);
      for (String string : map.keySet()) {
        if (map.get(string) > firstPopularity) {
          thirdString = secondString;
          thirdPopularity = secondPopularity;

          secondString = firstString;
          secondPopularity = firstPopularity;

          firstString = string;
          firstPopularity = map.get(string);
        }
      }
      fullWord = firstString;
      firstOutput = firstString;
      secondOutput = secondString;
      thirdOutput = thirdString;
    }
    String[] strings = new String[3];
    strings[0] = firstOutput;
    strings[1] = secondOutput;
    strings[2] = thirdOutput;

    return strings;
  }

  public static void resolve(String textfile) {
    if(StringOperations.globalAbbreviationTree == null) {
      System.out.println("ERROR, No abbreviation map");
      return;
    }

    String fileAsString = Utility.readFile(textfile);
    assert fileAsString != null;
    Scanner scanner = new Scanner(fileAsString);

    StringBuilder stringBuilder = new StringBuilder();
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String originalLine = line;


      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      String fullWord = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      int popularity = Integer.parseInt(line);

      if(!fullWord.contains("_")) {
        String[] array = internalResolve(fullWord, type);
        if(array[0] == null && array[1] == null && array[2] == null) {
            stringBuilder.append(originalLine).append("\n");
        }
        else {
          for(String output : array) {
            if (output != null) {
              String outputString = abbreviation + ";"+type+ ";"+ output + ";" + popularity + "\n";
              stringBuilder.append(outputString);
            }
            else {
//            stringBuilder.append(line).append("\n");
              break;
            }
          }
        }

      }
      else {
        Scanner wordScanner = new Scanner(fullWord);
        wordScanner.useDelimiter("_");

        StringBuilder outputString = new StringBuilder();
        while(wordScanner.hasNext()) {
          String singleWord = wordScanner.next();
          if(singleWord.isEmpty()) {
            break;
          }

          String[] array = internalResolve(singleWord, type);
          if(array[0] != null) {
            outputString.append(array[0]).append("_");
          }
          else {
            outputString.append(singleWord).append("_");
          }
        }
        wordScanner.close();
        String out = outputString.toString();
        out = out.substring(0,out.length()-1);
        stringBuilder.append(abbreviation).append(";").append(type).append(";")
                .append(out).append(";").append(popularity).append("\n");
      }
    }
    scanner.close();
    Utility.writeFile("thirdRound.txt",stringBuilder.toString());
  }

//
//  static void writeFile() {
//    if(StringOperations.globalAbbreviationTree  == null) {
//      System.out.println("ERROR, No abbreviation map");
//      return;
//    }
//
//
//    for(String abbreviation : StringOperations.globalAbbreviationMap.keySet()) {
//      for(String fullWord : StringOperations.globalAbbreviationMap.get(abbreviation).keySet()) {
//        // full tokens
//
//        String firstString = null;
//        int currentPopularity = -1;
//
//        while(!StringOperations.notInAbrMap(fullWord)) {
//          Map<String, Integer> map = StringOperations.getAbbreviations(fullWord);
//          for(String string : map.keySet()) {
//            if(map.get(string) > currentPopularity) {
//              firstString = string;
//              currentPopularity = map.get(string);
//            }
//          }
//
//          fullWord = firstString;
//
//          // partial tokens
//        }
//        if(firstString != null) {
//          StringOperations.globalAbbreviationMap.get(abbreviation).remove(firstString);
//          StringOperations.globalAbbreviationMap.get(abbreviation).put(firstString,currentPopularity);
//        }
//
//      }
//    }
//
//  }


  public static void removeRepeatWords(String textfile) {

    String fileAsString = Utility.readFile(textfile);


    assert fileAsString != null;
    Scanner scanner = new Scanner(fileAsString);

    StringBuilder stringBuilder = new StringBuilder();

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String abbreviation = line.substring(0, line.indexOf(";"));

      line = line.substring(line.indexOf(";")+1);

      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      String fullWord = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      String popularity = line;

      Scanner wordScanner = new Scanner(fullWord);


      String previousWord = null;
      String currentWord = null;
      wordScanner.useDelimiter("_");

      StringBuilder formulatedWord = new StringBuilder();
      boolean first = true;
      while(wordScanner.hasNext()) {
        previousWord = currentWord;
        currentWord = wordScanner.next();
        String effectiveCurrentWord = currentWord;

        if(!wordScanner.hasNext() && first) {
          formulatedWord.append(effectiveCurrentWord);
          break;
        }
        else {
          first = false;
        }




        if(currentWord.endsWith("1d")) {
          currentWord = "1d";
        }
        else if(currentWord.endsWith("2d")) {
          currentWord = "2d";
        }
        else if(currentWord.endsWith("3d")) {
          currentWord = "3d";
        }
        else if(currentWord.endsWith("4d")) {
          currentWord = "4d";
        }
        else if(currentWord.endsWith("5d")) {
          currentWord = "5d";
        }

        // first word
        if(previousWord == null) {
          formulatedWord.append(effectiveCurrentWord);
        }
        else {
          boolean duplicateWord = currentWord.equals(previousWord);

          if(!duplicateWord) {
            formulatedWord.append("_").append(effectiveCurrentWord);
          }
        }
      }

      if(!abbreviation.equals(formulatedWord.toString())) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(formulatedWord).append(";")
                .append(popularity).append("\n");
      }
    }
    scanner.close();


    Utility.writeFile("thirdRound.txt", stringBuilder.toString());
  }




  public static void sortByEnglishWords(String textfile) {

    if(StringOperations.globalAbbreviationTree  == null) {
      System.out.println("ERROR, No abbreviation map");
      return;
    }

    String fileAsString = Utility.readFile(textfile);
    assert fileAsString != null;
    Scanner scanner = new Scanner(fileAsString);
//    System.out.println(fileAsString);

    StringBuilder stringBuilder = new StringBuilder();
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
//      System.out.println(line);
      String originalLine = line;

//      System.out.println(line);
      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String fullWord = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      int popularity = Integer.parseInt(line);

      boolean isEnglish = true;
      boolean isPartiallyEnglish = fullWord.contains("_");


      Scanner wordScanner = new Scanner(fullWord);
      wordScanner.useDelimiter("_");

      boolean isVerb = false;
      while(wordScanner.hasNext()) {
        String singleWord = wordScanner.next();
        if(singleWord.isEmpty()) {
          isEnglish = false;
          isPartiallyEnglish = false;
          break;
        }
        String pluralBaseForm = singleWord;


        if (singleWord.endsWith("ves") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 3) + "f";
        }
        else if (singleWord.endsWith("ies") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 3) + "y";
        }
        else if (singleWord.endsWith("oes") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 2);
        }
        else if (singleWord.endsWith("ses") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 2);
        }
        else if (singleWord.endsWith("shes") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 2);
        }
        else if (singleWord.endsWith("ches") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 2);
        }
        else if (singleWord.endsWith("xes") && singleWord.length() > 4) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 2);
        }
        else if (singleWord.endsWith("s") && !singleWord.endsWith("ss") && !singleWord.equals("s")) {
          pluralBaseForm = singleWord.substring(0, singleWord.length() - 1);
        }
        else if(NLPOperations.stemming(singleWord) != null) {
          pluralBaseForm = NLPOperations.stemming(singleWord);
        }


        if(StringOperations.notInDictionary(singleWord) &&
           StringOperations.notInDictionary(pluralBaseForm)) {
          isEnglish = false;
        }
        else if(StringOperations.notInDictionary(singleWord) &&
                StringOperations.notInDictionary(pluralBaseForm) &&
                !isEnglish) {
          isPartiallyEnglish = false;
          break;
        }

//        NLPOperations.initialize(posFilePath, lemmatizerFilePath);
//        isVerb = NLPOperations.checkIfVerb(pluralBaseForm);
//        NLPOperations.checkIfVerb("to "+pluralBaseForm);


      }
      wordScanner.close();
      if(isEnglish) {
        popularity = popularity * 1000;
      }
//      else if(isEnglish && isVerb) {
//        popularity = popularity * 50;
//      }
      else if(isPartiallyEnglish) {
        popularity = popularity * 10;
      }


      stringBuilder.append(abbreviation).append(";").append(type).append(";")
              .append(fullWord).append(";").append(popularity).append("\n");

    }
    scanner.close();
    Utility.writeFile("secondRound.txt",stringBuilder.toString());
  }

  public static void sortTheOutputFile(String textfile) {

    if(StringOperations.globalAbbreviationTree  == null) {
      System.out.println("ERROR, No abbreviation map");
      return;
    }

    String fileAsString = Utility.readFile(textfile);
    assert fileAsString != null;
    Scanner scanner = new Scanner(fileAsString);

    StringBuilder stringBuilder = new StringBuilder();
    String previousAbbreviation = null;
    String previousType = null;

    PriorityQueue<RankingItem> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String originalLine = line;

//      System.out.println(line);
      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String fullWord = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      int popularity = Integer.parseInt(line);

      if(previousAbbreviation != null && !previousAbbreviation.equals(abbreviation) &&
         previousType != null && !previousType.equals(type)) {
        // unleash the inner string builder
        while(!priorityQueue.isEmpty()) {
          RankingItem item = priorityQueue.poll();
          stringBuilder.append(item.string).append("\n");
        }
      }
      previousAbbreviation = abbreviation;
      previousType = type;

      RankingItem rankingItem = new RankingItem(originalLine, popularity);
      priorityQueue.add(rankingItem);
    }
    scanner.close();
    // unleash again

    while(!priorityQueue.isEmpty()) {
      RankingItem item = priorityQueue.poll();
      stringBuilder.append(item.string).append("\n");
    }
    Utility.writeFile("fourthRound.txt",stringBuilder.toString());
  }


  public static void resolveMultiWordNonEnglishWords(String textfile, String tempfile) {
    // the temp file must be obtained first!! Either by "GenerateOriginalFiles",
    // then get "temp.txt" from the temp folder
    Scanner scanner = new Scanner(Objects.requireNonNull(Utility.readFile(tempfile)));
    StringBuilder stringBuilder = new StringBuilder();
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String abbreviation = line.substring(0, line.indexOf(" =>"));
      if(line.contains("_") && !line.startsWith("_")&& StringOperations.notInAbrMap(abbreviation)) {
        stringBuilder.append(line).append("\n");
      }
    }
    scanner.close();
    String output = stringBuilder.toString();
    Utility.writeFile("temporaryOutput.txt",output);


    StringBuilder partialStringBuilder = new StringBuilder();
    Scanner reScanner = new Scanner(output);
    while(reScanner.hasNextLine()) {
      String line = reScanner.nextLine();
      String abbreviation = line.substring(0, line.indexOf(" =>"));
      Scanner wordScanner = new Scanner(abbreviation);
      wordScanner.useDelimiter("_");


      StringBuilder innerStringBuilder = new StringBuilder();
      while (wordScanner.hasNext()) {
        String word = wordScanner.next();
        if(word.isEmpty()) {
          break;
        }

        if(!StringOperations.notInAbrMap(word)) {
          System.out.println(word);
          String[] array = internalResolve(word, null);
          if(array[0] != null) {
            innerStringBuilder.append(array[0]).append("_");
          }
          else {
            innerStringBuilder.append(word).append("_");
          }
        }
        else {
          innerStringBuilder.append(word).append("_");
        }
      }
      wordScanner.close();
      String out = innerStringBuilder.toString();
      if(out.length() > 1) {
        out = out.substring(0, out.length() - 1);

        if(!out.equals(abbreviation)) {
          partialStringBuilder.append(abbreviation).append(";").append("[OTHER]")
                  .append(";").append(out).append(";").append(1).append("\n");

        }
      }
    }
    reScanner.close();

    String o = partialStringBuilder.toString();
    Utility.writeFile("testOut.txt", o);

    Utility.writeFile("fifthRound.txt",
            Utility.mergeFiles(textfile,"testOut.txt"));
  }

  public static void combineLines(String textfile) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(Objects.requireNonNull(Utility.readFile(textfile)));
    Map<String, Integer> usedLines = new HashMap<>();
    List<String> order = new ArrayList<>();

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String substring = line.substring(0, line.lastIndexOf(";"));
      int popularity = Integer.parseInt(line.substring(line.lastIndexOf(";")+1));

      if(!usedLines.containsKey(substring)) {
        usedLines.put(substring, popularity);
        order.add(substring);
      }
      else {
        usedLines.put(substring, usedLines.get(substring) + popularity);
//        order.add(substring);
      }
    }

    for(String substring : order) {
      stringBuilder.append(substring).append(";").append(usedLines.get(substring)).append("\n");
    }

    Utility.writeFile("sixthRound.txt", stringBuilder.toString());

  }


  public static void removeExcessStuff(String textfile) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(Objects.requireNonNull(Utility.readFile(textfile)));

    Map<String, Integer> counts = new HashMap<>();

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String originalLine = line;

      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      String type = line.substring(0, line.indexOf(";"));

      String combined = abbreviation + ";" + type;

      if(!counts.containsKey(combined)) {
        counts.put(combined, 1);
        stringBuilder.append(originalLine).append("\n");
      }
      else if (counts.get(combined) < 5) {
        counts.put(combined, counts.get(combined) + 1);
        stringBuilder.append(originalLine).append("\n");
      }

    }
    Utility.writeFile("seventhRound.txt", stringBuilder.toString());

  }

  public static void checkSuffix(String textfile) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(Objects.requireNonNull(Utility.readFile(textfile)));

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String originalLine = line;

//      System.out.println(line);
      String abbreviation = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String type = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

//      System.out.println(line);
      String fullWord = line.substring(0, line.indexOf(";"));
      line = line.substring(line.indexOf(";")+1);

      int popularity = Integer.parseInt(line);

      if(!abbreviation.endsWith("1d") && fullWord.endsWith("_1d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-3).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("2d") && fullWord.endsWith("_2d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-3).append(";")
                .append(popularity).append("\n");
      }

      else if(!abbreviation.endsWith("3d") && fullWord.endsWith("_3d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-3).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("4d") && fullWord.endsWith("_4d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-3).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("5d") && fullWord.endsWith("_5d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-3).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("1d") && fullWord.endsWith("1d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-2).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("2d") && fullWord.endsWith("2d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-2).append(";")
                .append(popularity).append("\n");
      }

      else if(!abbreviation.endsWith("3d") && fullWord.endsWith("3d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-2).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("4d") && fullWord.endsWith("4d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-2).append(";")
                .append(popularity).append("\n");
      }
      else if(!abbreviation.endsWith("5d") && fullWord.endsWith("5d")) {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord, 0, fullWord.length()-2).append(";")
                .append(popularity).append("\n");
      }
      else {
        stringBuilder.append(abbreviation).append(";")
                .append(type).append(";")
                .append(fullWord).append(";")
                .append(popularity).append("\n");

      }
    }

    Utility.writeFile("fifthRound.txt", stringBuilder.toString());
  }
}


