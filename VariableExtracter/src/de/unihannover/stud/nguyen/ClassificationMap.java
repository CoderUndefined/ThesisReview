package de.unihannover.stud.nguyen;

import java.util.*;

import static de.unihannover.stud.nguyen.StringOperations.*;

/**
 * Map that classifies variables based on number of English words.
 */
class ClassificationMap {
  Map<VariableEnum, Long> classificationMap;

  Set<String> englishSet;
  Set<String> partialEnglishSet;
  Set<String> weakEnglishSet;
  Set<String> almostEnglishSet;

  Set<String> abrSet;
  Set<String> partialAbrSet;
  Set<String> weakAbrSet;
  Set<String> almostAbrSet;

  Set<String> nonEnglishSet;
  Set<String> nonEnglishPlusOneSet;

  /**
   * Initializes classification map
   */
  public ClassificationMap() {
    classificationMap = new HashMap<>();
    classificationMap.put(VariableEnum.pseudoSingleLetterVariable, 0L);
    classificationMap.put(VariableEnum.singleLetterVariable, 0L);
    classificationMap.put(VariableEnum.twoLetterVariable, 0L);
    classificationMap.put(VariableEnum.threeLetterVariable, 0L);
    classificationMap.put(VariableEnum.fourLetterVariable, 0L);
    classificationMap.put(VariableEnum.fiveLetterVariable, 0L);
    classificationMap.put(VariableEnum.sixLetterVariable, 0L);
    classificationMap.put(VariableEnum.sevenLetterVariable, 0L);
    classificationMap.put(VariableEnum.eightLetterVariable, 0L);
    classificationMap.put(VariableEnum.nineLetterVariable, 0L);
    classificationMap.put(VariableEnum.tenLetterVariable, 0L);
    classificationMap.put(VariableEnum.moreLetterVariable, 0L);

    classificationMap.put(VariableEnum.singleEnglishWord, 0L);
    classificationMap.put(VariableEnum.twoEnglishWords, 0L);
    classificationMap.put(VariableEnum.threeEnglishWords, 0L);
    classificationMap.put(VariableEnum.fourEnglishWords, 0L);
    classificationMap.put(VariableEnum.fiveEnglishWords, 0L);
    classificationMap.put(VariableEnum.moreEnglishWords, 0L);

    classificationMap.put(VariableEnum.singleEnglishWordPlus, 0L);
    classificationMap.put(VariableEnum.twoEnglishWordsPlus, 0L);
    classificationMap.put(VariableEnum.threeEnglishWordsPlus, 0L);
    classificationMap.put(VariableEnum.fourEnglishWordsPlus, 0L);
    classificationMap.put(VariableEnum.fiveEnglishWordsPlus, 0L);
    classificationMap.put(VariableEnum.moreEnglishWordsPlus, 0L);

    classificationMap.put(VariableEnum.singleEnglishWordWeak, 0L);
    classificationMap.put(VariableEnum.twoEnglishWordsWeak, 0L);
    classificationMap.put(VariableEnum.threeEnglishWordsWeak, 0L);
    classificationMap.put(VariableEnum.fourEnglishWordsWeak, 0L);
    classificationMap.put(VariableEnum.fiveEnglishWordsWeak, 0L);
    classificationMap.put(VariableEnum.moreEnglishWordsWeak, 0L);

    classificationMap.put(VariableEnum.singleEnglishWordAlmost, 0L);
    classificationMap.put(VariableEnum.twoEnglishWordsAlmost, 0L);
    classificationMap.put(VariableEnum.threeEnglishWordsAlmost, 0L);
    classificationMap.put(VariableEnum.fourEnglishWordsAlmost, 0L);
    classificationMap.put(VariableEnum.fiveEnglishWordsAlmost, 0L);
    classificationMap.put(VariableEnum.moreEnglishWordsAlmost, 0L);

    classificationMap.put(VariableEnum.singleAbrWord, 0L);
    classificationMap.put(VariableEnum.twoAbrWords, 0L);
    classificationMap.put(VariableEnum.threeAbrWords, 0L);
    classificationMap.put(VariableEnum.fourAbrWords, 0L);
    classificationMap.put(VariableEnum.fiveAbrWords, 0L);
    classificationMap.put(VariableEnum.moreAbrWords, 0L);

    classificationMap.put(VariableEnum.singleAbrWordPlus, 0L);
    classificationMap.put(VariableEnum.twoAbrWordsPlus, 0L);
    classificationMap.put(VariableEnum.threeAbrWordsPlus, 0L);
    classificationMap.put(VariableEnum.fourAbrWordsPlus, 0L);
    classificationMap.put(VariableEnum.fiveAbrWordsPlus, 0L);
    classificationMap.put(VariableEnum.moreAbrWordsPlus, 0L);

    classificationMap.put(VariableEnum.singleAbrWordWeak, 0L);
    classificationMap.put(VariableEnum.twoAbrWordsWeak, 0L);
    classificationMap.put(VariableEnum.threeAbrWordsWeak, 0L);
    classificationMap.put(VariableEnum.fourAbrWordsWeak, 0L);
    classificationMap.put(VariableEnum.fiveAbrWordsWeak, 0L);
    classificationMap.put(VariableEnum.moreAbrWordsWeak, 0L);

    classificationMap.put(VariableEnum.singleAbrWordAlmost, 0L);
    classificationMap.put(VariableEnum.twoAbrWordsAlmost, 0L);
    classificationMap.put(VariableEnum.threeAbrWordsAlmost, 0L);
    classificationMap.put(VariableEnum.fourAbrWordsAlmost, 0L);
    classificationMap.put(VariableEnum.fiveAbrWordsAlmost, 0L);
    classificationMap.put(VariableEnum.moreAbrWordsAlmost, 0L);

    classificationMap.put(VariableEnum.twoNonWords, 0L);
    classificationMap.put(VariableEnum.threeNonWords, 0L);
    classificationMap.put(VariableEnum.fourNonWords, 0L);
    classificationMap.put(VariableEnum.fiveNonWords, 0L);
    classificationMap.put(VariableEnum.moreNonWords, 0L);

    classificationMap.put(VariableEnum.singleUnderscore, 0L);
    classificationMap.put(VariableEnum.doubleUnderscore, 0L);
    classificationMap.put(VariableEnum.abr, 0L);
    classificationMap.put(VariableEnum.other, 0L);

    englishSet = new HashSet<>();
    partialEnglishSet = new HashSet<>();
    weakEnglishSet = new HashSet<>();
    almostEnglishSet = new HashSet<>();

    abrSet = new HashSet<>();
    partialAbrSet = new HashSet<>();
    weakAbrSet = new HashSet<>();
    almostAbrSet = new HashSet<>();
    
    nonEnglishSet = new HashSet<>();
    nonEnglishPlusOneSet = new HashSet<>();
  }

  /**
   * Classify a list of variables depending on how many English words they have.
   *
   * @param sortedMappingList Sorted mapping list containing all variables and occurences
   */
  void classify(SortedMappingList sortedMappingList, boolean original) {

    SortedMappingList englishMappingList = new SortedMappingList();
    SortedMappingList partialMappingList = new SortedMappingList();
    SortedMappingList abrMappingList = new SortedMappingList();
    SortedMappingList partialAbrMappingList = new SortedMappingList();
    SortedMappingList nonMappingList = new SortedMappingList();

    for (Mapping mapping : sortedMappingList.getMappingList()) {
      String variableName = mapping.getString();
      String originalName = variableName;
      Long amount = mapping.getNumber();

      variableName = toSnakeCase(variableName,true);


      // first check if this is an english word
      // but also check if the word consists of multiple words
      List<String> wordsInVariable = new ArrayList<>();
      Scanner scanner = new Scanner(variableName);

      scanner.useDelimiter("_");
      while (scanner.hasNext()) {
        wordsInVariable.add(scanner.next());
      }
      wordsInVariable.removeIf(String::isEmpty);

      boolean isEnglish = true; // Assume all words are in English
      boolean isPartiallyEnglish = (wordsInVariable.size() > 1); // All but 1 word
      boolean isWeakEnglish = (wordsInVariable.size() > 2);      // All but 2 words
      boolean isAlmostEnglish = (wordsInVariable.size() > 3);    // All but 3 words

      boolean isAbr = false; // Assume all words are in Abr
      boolean isPartiallyAbr = false; // All but 1 word
      boolean isWeakAbr = false;      // All but 2 words
      boolean isAlmostAbr = false;   // All but 3 words


      boolean isAbbreviation = false;
      
      for (String word : wordsInVariable) {
        word = removeNumbers(word);
        word = word.toLowerCase();

        String word2;
        if (word.endsWith("ves") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 3) + "f";
        }
        else if (word.endsWith("ies") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 3) + "y";
        }
        else if (word.endsWith("oes") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("ses") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("shes") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("ches") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("xes") && word.length() > 4) {
          word2 = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("s") && !word.endsWith("ss") && !word.equals("s")) {
          word2 = word.substring(0, word.length() - 1);
        }
        else if(NLPOperations.stemming(word) != null) {
          word2 = NLPOperations.stemming(word);
        }
        else {
          word2 = word;
        }


        boolean notInDic = notInDictionary(word);
        boolean notInDic2 = notInDictionary(word2);

        if (notInDic && notInDic2 && isEnglish) {
          if (!StringOperations.notInAbrMap(word) || !StringOperations.notInAbrMap(word2)) {
            isAbbreviation = true;
            isEnglish = false;
            isPartiallyEnglish = false;
            isWeakEnglish = false;
            isAlmostEnglish = false;
            break;
          }
          else {
            isEnglish = false;
          }
        }
        else if (notInDic && notInDic2 && isPartiallyEnglish && wordsInVariable.size() > 1) {
          if (!StringOperations.notInAbrMap(word) || !StringOperations.notInAbrMap(word2)) {
            isAbbreviation = true;
            isEnglish = false;
            isPartiallyEnglish = false;
            isWeakEnglish = false;
            isAlmostEnglish = false;
            break;
          }
          else {
            isPartiallyEnglish = false;
          }
        }
        else if (notInDic && notInDic2 && isWeakEnglish && wordsInVariable.size() > 2) {
          if (!StringOperations.notInAbrMap(word) || !StringOperations.notInAbrMap(word2)) {
            isAbbreviation = true;
            isEnglish = false;
            isPartiallyEnglish = false;
            isWeakEnglish = false;
            isAlmostEnglish = false;
            break;
          }
          else {
            isWeakEnglish = false;
          }
        }
        else if (notInDic && notInDic2 && isAlmostEnglish &&  wordsInVariable.size() > 3) {
          if (!StringOperations.notInAbrMap(word) || !StringOperations.notInAbrMap(word2)) {
            isAbbreviation = true;
            isEnglish = false;
            isPartiallyEnglish = false;
            isWeakEnglish = false;
            isAlmostEnglish = false;
            break;
          }
          else {
            isAlmostEnglish = false;
            break;
          }
        }
      }
      
      if(isAbbreviation) {
        isAbr = true; // Assume all words are in Abr
        isPartiallyAbr = (wordsInVariable.size() > 1); // All but 1 word
        isWeakAbr = (wordsInVariable.size() > 2);      // All but 2 words
        isAlmostAbr = (wordsInVariable.size() > 3);    // All but 3 words

        for (String word : wordsInVariable) {
          word = removeNumbers(word);
          word = word.toLowerCase();
          
          String word2;
          if (word.endsWith("ves") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 3) + "f";
          }
          else if (word.endsWith("ies") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 3) + "y";
          }
          else if (word.endsWith("oes") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 2);
          }
          else if (word.endsWith("ses") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 2);
          }
          else if (word.endsWith("shes") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 2);
          }
          else if (word.endsWith("ches") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 2);
          }
          else if (word.endsWith("xes") && word.length() > 4) {
            word2 = word.substring(0, word.length() - 2);
          }
          else if (word.endsWith("s") && !word.equals("s")) {
            word2 = word.substring(0, word.length() - 1);
          }
          else if (word.endsWith("s") && !word.endsWith("ss") && !word.equals("s")) {
            word2 = word.substring(0, word.length() - 1);
          }
          else if(NLPOperations.stemming(word) != null) {
            word2 = NLPOperations.stemming(word);
          }
          else {
            word2 = word;
          }
          boolean notInDic = notInDictionary(word);
          boolean notInDic2 = notInDictionary(word2);

          boolean notInAbr = notInAbrMap(word);
          boolean notInAbr2 = notInAbrMap(word2);

          if (notInDic && notInDic2 && notInAbr && notInAbr2 && isAbr) {
            isAbr = false;
          }
          else if (notInDic && notInDic2 &&
                   notInAbr && notInAbr2 && isPartiallyAbr && wordsInVariable.size() > 1) {
            isPartiallyAbr = false;
          }
          else if (notInDic && notInDic2 &&
                   notInAbr && notInAbr2 && isWeakAbr && wordsInVariable.size() > 2) {
            isWeakAbr = false;
          }
          else if (notInDic && notInDic2 &&
                   notInAbr && notInAbr2 && isAlmostAbr && wordsInVariable.size() > 3) {
            isAlmostAbr = false;
            break;
          }
        }
      }

      if(original) {
        variableName = originalName;
      }
      int numberOfStrings = wordsInVariable.size();

        if (variableName.length() == 1) {
          nonEnglishSet.add(variableName);
          classificationMap.put(VariableEnum.singleLetterVariable,
                  classificationMap.get(VariableEnum.singleLetterVariable) + amount);
        }

        else if (isEnglish) {
          englishSet.add(variableName);
          englishMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 1) {
            classificationMap.put(VariableEnum.singleEnglishWord,
                    classificationMap.get(VariableEnum.singleEnglishWord) + amount);
          }
          else if (numberOfStrings == 2) {
            classificationMap.put(VariableEnum.twoEnglishWords,
                    classificationMap.get(VariableEnum.twoEnglishWords) + amount);
          }
          else if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeEnglishWords,
                    classificationMap.get(VariableEnum.threeEnglishWords) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourEnglishWords,
                    classificationMap.get(VariableEnum.fourEnglishWords) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveEnglishWords,
                    classificationMap.get(VariableEnum.fiveEnglishWords) + amount);
          }
          else if (numberOfStrings > 5) {
            classificationMap.put(VariableEnum.moreEnglishWords,
                    classificationMap.get(VariableEnum.moreEnglishWords) + amount);
          }
        }
        else if (isPartiallyEnglish) {
          partialEnglishSet.add(variableName);
          partialMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 2) {
            classificationMap.put(VariableEnum.twoEnglishWordsPlus,
                    classificationMap.get(VariableEnum.twoEnglishWordsPlus) + amount);
          }
          else if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeEnglishWordsPlus,
                    classificationMap.get(VariableEnum.threeEnglishWordsPlus) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourEnglishWordsPlus,
                    classificationMap.get(VariableEnum.fourEnglishWordsPlus) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveEnglishWordsPlus,
                    classificationMap.get(VariableEnum.fiveEnglishWordsPlus) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreEnglishWordsPlus,
                    classificationMap.get(VariableEnum.moreEnglishWordsPlus) + amount);
          }
        }
        else if (isWeakEnglish) {
          weakEnglishSet.add(variableName);
          partialMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeEnglishWordsWeak,
                    classificationMap.get(VariableEnum.threeEnglishWordsWeak) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourEnglishWordsWeak,
                    classificationMap.get(VariableEnum.fourEnglishWordsWeak) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveEnglishWordsWeak,
                    classificationMap.get(VariableEnum.fiveEnglishWordsWeak) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreEnglishWordsWeak,
                    classificationMap.get(VariableEnum.moreEnglishWordsWeak) + amount);
          }
        }
        else if (isAlmostEnglish) {
          almostEnglishSet.add(variableName);
          partialMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourEnglishWordsAlmost,
                    classificationMap.get(VariableEnum.fourEnglishWordsAlmost) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveEnglishWordsAlmost,
                    classificationMap.get(VariableEnum.fiveEnglishWordsAlmost) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreEnglishWordsAlmost,
                    classificationMap.get(VariableEnum.moreEnglishWordsAlmost) + amount);
          }
        }


        else if (isAbr) {
          abrSet.add(variableName);
          abrMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 1) {
            classificationMap.put(VariableEnum.singleAbrWord,
                    classificationMap.get(VariableEnum.singleAbrWord) + amount);
          }
          else if (numberOfStrings == 2) {
            classificationMap.put(VariableEnum.twoAbrWords,
                    classificationMap.get(VariableEnum.twoAbrWords) + amount);
          }
          else if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeAbrWords,
                    classificationMap.get(VariableEnum.threeAbrWords) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourAbrWords,
                    classificationMap.get(VariableEnum.fourAbrWords) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveAbrWords,
                    classificationMap.get(VariableEnum.fiveAbrWords) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreAbrWords,
                    classificationMap.get(VariableEnum.moreAbrWords) + amount);
          }
        }
        else if (isPartiallyAbr) {
          partialAbrSet.add(variableName);
          partialAbrMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 2) {
            classificationMap.put(VariableEnum.twoAbrWordsPlus,
                    classificationMap.get(VariableEnum.twoAbrWordsPlus) + amount);
          }
          else if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeAbrWordsPlus,
                    classificationMap.get(VariableEnum.threeAbrWordsPlus) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourAbrWordsPlus,
                    classificationMap.get(VariableEnum.fourAbrWordsPlus) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveAbrWordsPlus,
                    classificationMap.get(VariableEnum.fiveAbrWordsPlus) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreAbrWordsPlus,
                    classificationMap.get(VariableEnum.moreAbrWordsPlus) + amount);
          }
        }
        else if (isWeakAbr) {
          weakAbrSet.add(variableName);
          partialAbrMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 3) {
            classificationMap.put(VariableEnum.threeAbrWordsWeak,
                    classificationMap.get(VariableEnum.threeAbrWordsWeak) + amount);
          }
          else if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourAbrWordsWeak,
                    classificationMap.get(VariableEnum.fourAbrWordsWeak) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveAbrWordsWeak,
                    classificationMap.get(VariableEnum.fiveAbrWordsWeak) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreAbrWordsWeak,
                    classificationMap.get(VariableEnum.moreAbrWordsWeak) + amount);
          }
        }
        else if (isAlmostAbr) {
          almostAbrSet.add(variableName);
          partialAbrMappingList.getMappingList().add(mapping);
          if (numberOfStrings == 4) {
            classificationMap.put(VariableEnum.fourAbrWordsAlmost,
                    classificationMap.get(VariableEnum.fourAbrWordsAlmost) + amount);
          }
          else if (numberOfStrings == 5) {
            classificationMap.put(VariableEnum.fiveAbrWordsAlmost,
                    classificationMap.get(VariableEnum.fiveAbrWordsAlmost) + amount);
          }
          else {
            classificationMap.put(VariableEnum.moreAbrWordsAlmost,
                    classificationMap.get(VariableEnum.moreAbrWordsAlmost) + amount);
          }
        }

        else if (variableName.length() == 2) {
          nonEnglishSet.add(variableName);


          if (variableName.charAt(0) >= 'a' && variableName.charAt(0) <= 'z' &&
                  variableName.charAt(1) >= '0' && variableName.charAt(1) <= '9') {

            classificationMap.put(VariableEnum.pseudoSingleLetterVariable,
                    classificationMap.get(VariableEnum.pseudoSingleLetterVariable) + amount);
          }
          else if (variableName.charAt(0) >= 'A' && variableName.charAt(0) <= 'Z' &&
                  variableName.charAt(1) >= '0' && variableName.charAt(1) <= '9') {

            classificationMap.put(VariableEnum.pseudoSingleLetterVariable,
                    classificationMap.get(VariableEnum.pseudoSingleLetterVariable) + amount);
          }
          else {
            nonEnglishPlusOneSet.add(variableName);
            classificationMap.put(VariableEnum.twoLetterVariable,
                    classificationMap.get(VariableEnum.twoLetterVariable) + amount);
          }
        }

        else {
          nonEnglishSet.add(variableName);
          nonEnglishPlusOneSet.add(variableName);
          nonMappingList.getMappingList().add(mapping);
          int length = variableName.length();
          if (length == 3) {
            classificationMap.put(VariableEnum.threeLetterVariable,
                    classificationMap.get(VariableEnum.threeLetterVariable) + amount);
          }
          else if (length == 4) {
            classificationMap.put(VariableEnum.fourLetterVariable,
                    classificationMap.get(VariableEnum.fourLetterVariable) + amount);
          }
          else if (length == 5) {
            classificationMap.put(VariableEnum.fiveLetterVariable,
                    classificationMap.get(VariableEnum.fiveLetterVariable) + amount);
          }
          else if (length == 6) {
            classificationMap.put(VariableEnum.sixLetterVariable,
                    classificationMap.get(VariableEnum.sixLetterVariable) + amount);
          }
          else if (length == 7) {
            classificationMap.put(VariableEnum.sevenLetterVariable,
                    classificationMap.get(VariableEnum.sevenLetterVariable) + amount);
          }
          else if (length == 8) {
            classificationMap.put(VariableEnum.eightLetterVariable,
                    classificationMap.get(VariableEnum.eightLetterVariable) + amount);
          }
          else if (length == 9) {
            classificationMap.put(VariableEnum.nineLetterVariable,
                    classificationMap.get(VariableEnum.nineLetterVariable) + amount);
          }
          else if (length == 10) {
            classificationMap.put(VariableEnum.tenLetterVariable,
                    classificationMap.get(VariableEnum.tenLetterVariable) + amount);
          }
          else if (length >= 11) {
            classificationMap.put(VariableEnum.moreLetterVariable,
                    classificationMap.get(VariableEnum.moreLetterVariable) + amount);
          }
        }
//      }
      scanner.close();
    }
  }


  /**
   * Print classification map
   *
   * @param total Total number of source lines of codes, used to get relative percentages
   */
  void printClassificationMap(long total) {
    double word1percentage = 0.0;
    double word2percentage = 0.0;
    double word3percentage = 0.0;
    double word4percentage = 0.0;
    double word5percentage = 0.0;
    double word6percentage = 0.0;

    double word2PlusPercentage = 0.0;
    double word3PlusPercentage = 0.0;
    double word4PlusPercentage = 0.0;
    double word5PlusPercentage = 0.0;
    double word6PlusPercentage = 0.0;

    double word3WeakPercentage = 0.0;
    double word4WeakPercentage = 0.0;
    double word5WeakPercentage = 0.0;
    double word6WeakPercentage = 0.0;

    double word4AlmostPercentage = 0.0;
    double word5AlmostPercentage = 0.0;
    double word6AlmostPercentage = 0.0;
    
    double abr1percentage = 0.0;
    double abr2percentage = 0.0;
    double abr3percentage = 0.0;
    double abr4percentage = 0.0;
    double abr5percentage = 0.0;
    double abr6percentage = 0.0;

    double abr2PlusPercentage = 0.0;
    double abr3PlusPercentage = 0.0;
    double abr4PlusPercentage = 0.0;
    double abr5PlusPercentage = 0.0;
    double abr6PlusPercentage = 0.0;

    double abr3WeakPercentage = 0.0;
    double abr4WeakPercentage = 0.0;
    double abr5WeakPercentage = 0.0;
    double abr6WeakPercentage = 0.0;

    double abr4AlmostPercentage = 0.0;
    double abr5AlmostPercentage = 0.0;
    double abr6AlmostPercentage = 0.0;
    
    double word2NonPercentage = 0.0;
    double word3NonPercentage = 0.0;
    double word4NonPercentage = 0.0;
    double word5NonPercentage = 0.0;
    double word6NonPercentage = 0.0;

    double pseudoPercentage = 0.0;
    double char1Percentage = 0.0;
    double char2Percentage = 0.0;
    double char3Percentage = 0.0;
    double char4Percentage = 0.0;
    double char5Percentage = 0.0;
    double char6Percentage = 0.0;
    double char7Percentage = 0.0;
    double char8Percentage = 0.0;
    double char9Percentage = 0.0;
    double char10Percentage = 0.0;
    double char11Percentage = 0.0;


    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.singleEnglishWord)) {
        word1percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("1 Word  =>" + classificationMap.get(e) + " " + word1percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.twoEnglishWords)) {
        word2percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("2 Words =>" + classificationMap.get(e) + " " + word2percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeEnglishWords)) {
        word3percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 Words =>" + classificationMap.get(e) + " " + word3percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourEnglishWords)) {
        word4percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 Words =>" + classificationMap.get(e) + " " + word4percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveEnglishWords)) {
        word5percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 Words =>" + classificationMap.get(e) + " " + word5percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreEnglishWords)) {
        word6percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+Words =>" + classificationMap.get(e) + " " + word6percentage);
      }
    }


    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.twoEnglishWordsPlus)) {
        word2PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("2 Words+r =>" + classificationMap.get(e) + " " + word2PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeEnglishWordsPlus)) {
        word3PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 Words+r =>" + classificationMap.get(e) + " " + word3PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourEnglishWordsPlus)) {
        word4PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 Words+r =>" + classificationMap.get(e) + " " + word4PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveEnglishWordsPlus)) {
        word5PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 Words+r =>" + classificationMap.get(e) + " " + word5PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreEnglishWordsPlus)) {
        word6PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+Words+r =>" + classificationMap.get(e) + " " + word6PlusPercentage);
      }
    }


    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeEnglishWordsWeak)) {
        word3WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 Words+w =>" + classificationMap.get(e) + " " + word3WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourEnglishWordsWeak)) {
        word4WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 Words+w =>" + classificationMap.get(e) + " " + word4WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveEnglishWordsWeak)) {
        word5WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 Words+w =>" + classificationMap.get(e) + " " + word5WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreEnglishWordsWeak)) {
        word6WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+Words+w =>" + classificationMap.get(e) + " " + word6WeakPercentage);
      }
    }


    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourEnglishWordsAlmost)) {
        word4AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 Words+a =>" + classificationMap.get(e) + " " + word4AlmostPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveEnglishWordsAlmost)) {
        word5AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 Words+a =>" + classificationMap.get(e) + " " + word5AlmostPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreEnglishWordsAlmost)) {
        word6AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+Words+a =>" + classificationMap.get(e) + " " + word6AlmostPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.singleAbrWord)) {
        abr1percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("1 WordAbr  =>" + classificationMap.get(e) + " " + abr1percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.twoAbrWords)) {
        abr2percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("2 WordsAbr =>" + classificationMap.get(e) + " " + abr2percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeAbrWords)) {
        abr3percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 WordsAbr =>" + classificationMap.get(e) + " " + abr3percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourAbrWords)) {
        abr4percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 WordsAbr =>" + classificationMap.get(e) + " " + abr4percentage);
      }

    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveAbrWords)) {
        abr5percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 WordsAbr =>" + classificationMap.get(e) + " " + abr5percentage);
      }

    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreAbrWords)) {
        abr6percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+WordsAbr =>" + classificationMap.get(e) + " " + abr6percentage);
      }
      
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.twoAbrWordsPlus)) {
        abr2PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("2 WordsAbr+r =>" + classificationMap.get(e) + " " + abr2PlusPercentage);
      }

    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeAbrWordsPlus)) {
        abr3PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 WordsAbr+r =>" + classificationMap.get(e) + " " + abr3PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourAbrWordsPlus)) {
        abr4PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 WordsAbr+r =>" + classificationMap.get(e) + " " + abr4PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveAbrWordsPlus)) {
        abr5PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 WordsAbr+r =>" + classificationMap.get(e) + " " + abr5PlusPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreAbrWordsPlus)) {
        abr6PlusPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+WordsAbr+r =>" + classificationMap.get(e) + " " + abr6PlusPercentage);
      }


    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeAbrWordsWeak)) {
        abr3WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 WordsAbr+w =>" + classificationMap.get(e) + " " + abr3WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourAbrWordsWeak)) {
        abr4WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 WordsAbr+w =>" + classificationMap.get(e) + " " + abr4WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveAbrWordsWeak)) {
        abr5WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 WordsAbr+w =>" + classificationMap.get(e) + " " + abr5WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreAbrWordsWeak)) {
        abr6WeakPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+WordsAbr+w =>" + classificationMap.get(e) + " " + abr6WeakPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourAbrWordsAlmost)) {
        abr4AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 WordsAbr+a =>" + classificationMap.get(e) + " "+abr4AlmostPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveAbrWordsAlmost)) {
        abr5AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 WordsAbr+a =>" + classificationMap.get(e) + " "+abr5AlmostPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreAbrWordsAlmost)) {
        abr6AlmostPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6+WordsAbr+a =>" + classificationMap.get(e) + " "+abr6AlmostPercentage);
      }
    }
//
//    for (VariableEnum e : classificationMap.keySet()) {
//      if (e.equals(VariableEnum.twoNonWords)) {
//        word2NonPercentage = ((double) classificationMap.get(e) / (double) total);
//        System.out.println("2 NonWords =>" + classificationMap.get(e) + " " + word2NonPercentage);
//      }
//    }
//
//    for (VariableEnum e : classificationMap.keySet()) {
//      if (e.equals(VariableEnum.threeNonWords)) {
//        word3NonPercentage = ((double) classificationMap.get(e) / (double) total);
//        System.out.println("3 NonWords =>" + classificationMap.get(e) + " " + word3NonPercentage);
//      }
//    }
//
//    for (VariableEnum e : classificationMap.keySet()) {
//      if (e.equals(VariableEnum.fourNonWords)) {
//        word4NonPercentage = ((double) classificationMap.get(e) / (double) total);
//        System.out.println("4 NonWords =>" + classificationMap.get(e) + " " + word4NonPercentage);
//      }
//    }
//
//    for (VariableEnum e : classificationMap.keySet()) {
//      if (e.equals(VariableEnum.fiveNonWords)) {
//        word5NonPercentage = ((double) classificationMap.get(e) / (double) total);
//        System.out.println("5 NonWords =>" + classificationMap.get(e) + " " + word5NonPercentage);
//      }
//    }
//
//    for (VariableEnum e : classificationMap.keySet()) {
//      if (e.equals(VariableEnum.moreNonWords)) {
//        word6NonPercentage = ((double) classificationMap.get(e) / (double) total);
//        System.out.println("6+NonWords =>" + classificationMap.get(e) + " " + word6NonPercentage);
//      }
//    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.singleLetterVariable)) {
        char1Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("1 =>" + classificationMap.get(e) + " " + char1Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.pseudoSingleLetterVariable)) {
        pseudoPercentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("1P=>" + classificationMap.get(e) + " " + pseudoPercentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.twoLetterVariable)) {
        char2Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("2 =>" + classificationMap.get(e) + " " + char2Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.threeLetterVariable)) {
        char3Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("3 =>" + classificationMap.get(e) + " " + char3Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fourLetterVariable)) {
        char4Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("4 =>" + classificationMap.get(e) + " " + char4Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.fiveLetterVariable)) {
        char5Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("5 =>" + classificationMap.get(e) + " " + char5Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.sixLetterVariable)) {
        char6Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("6 =>" + classificationMap.get(e) + " " + char6Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.sevenLetterVariable)) {
        char7Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("7 =>" + classificationMap.get(e) + " " + char7Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.eightLetterVariable)) {
        char8Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("8 =>" + classificationMap.get(e) + " " + char8Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.nineLetterVariable)) {
        char9Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("9 =>" + classificationMap.get(e) + " " + char9Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.tenLetterVariable)) {
        char10Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("10=>" + classificationMap.get(e) + " " + char10Percentage);
      }
    }

    for (VariableEnum e : classificationMap.keySet()) {
      if (e.equals(VariableEnum.moreLetterVariable)) {
        char11Percentage = ((double) classificationMap.get(e) / (double) total);
        System.out.println("++=>" + classificationMap.get(e) + " " + char11Percentage);
      }
    }

    double enlishPercentage = word1percentage + word2percentage + word3percentage +
            word4percentage + word5percentage + word6percentage;

    double partPercentage = word2PlusPercentage + word3PlusPercentage + word4PlusPercentage +
            word5PlusPercentage + word6PlusPercentage;

    double weakPercentage = word3WeakPercentage + word4WeakPercentage +
            word5WeakPercentage + word6WeakPercentage;

    double almostPercentage = word4AlmostPercentage +
            word5AlmostPercentage + word6AlmostPercentage;


    double enlishAbrPercentage = abr1percentage + abr2percentage + abr3percentage +
            abr4percentage + abr5percentage + abr6percentage;

    double partAbrPercentage = abr2PlusPercentage + abr3PlusPercentage + abr4PlusPercentage +
            abr5PlusPercentage + abr6PlusPercentage;

    double weakAbrPercentage = abr3WeakPercentage + abr4WeakPercentage +
            abr5WeakPercentage + abr6WeakPercentage;

    double almostAbrPercentage = abr4AlmostPercentage +
            abr5AlmostPercentage + abr6AlmostPercentage;



    double otherPercentage = word2NonPercentage + word3NonPercentage + word4NonPercentage +
            word5NonPercentage + word6NonPercentage + pseudoPercentage +
            char1Percentage + char2Percentage + char3Percentage +
            char4Percentage + char5Percentage + char6Percentage +
            char7Percentage + char8Percentage + char9Percentage +
            char10Percentage + char11Percentage;


//    System.out.println("english:    " + enlishPercentage);
//    System.out.println("partial:    " + partPercentage);
//    System.out.println("weak:       " + weakPercentage);
//    System.out.println("almost:     " + almostPercentage);
//
//    System.out.println("englishABR: " + enlishAbrPercentage);
//    System.out.println("partialABR: " + partAbrPercentage);
//    System.out.println("weakABR:    " + weakAbrPercentage);
//    System.out.println("almostABR:  " + almostAbrPercentage);
//
//    System.out.println("other:      " + otherPercentage);
//    System.out.println("total:      " + total);

    System.out.println("Actual statistics");
    System.out.println("english:    " + enlishPercentage);
    System.out.println("partial:    " + (partPercentage+weakPercentage+almostPercentage));
    System.out.println("englishABR: " + enlishAbrPercentage);
    System.out.println("partialABR: " + (partAbrPercentage+weakAbrPercentage+almostAbrPercentage));
    System.out.println("(Pseudo)SLV:" + (char1Percentage+pseudoPercentage));
    System.out.println("other:      " + (otherPercentage-char1Percentage-pseudoPercentage));

  }
}
