package de.unihannover.stud.nguyen;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static de.unihannover.stud.nguyen.StringOperations.toSnakeCase;

public class VariableNameAbbreviation {

    public static String getAbbreviation(String fullWord,
                                  Map<String, Map<String, Integer>> wordToAbbreviationMap) {
        if(!wordToAbbreviationMap.containsKey(fullWord)){
            return null;
        }

        if(wordToAbbreviationMap.get(fullWord).isEmpty()) {
            return null;
        }

        if(wordToAbbreviationMap.get(fullWord).size() == 1) {
            Set<String> singleWordSet = wordToAbbreviationMap.get(fullWord).keySet();
            String[] array = singleWordSet.toArray(new String[0]);
            return array[0];
        }


        Set<String> wordSet = wordToAbbreviationMap.get(fullWord).keySet();
        String returnWord = null;
        int currentLength = -999;
        int currentPopularirty = -999;
        for(String word : wordSet) {
            if(word.length() > currentLength) {
                currentLength = word.length();
                returnWord = word;
                currentPopularirty = wordToAbbreviationMap.get(fullWord).get(word);
            }
            else if(word.length() == currentLength) {
                int popularity = wordToAbbreviationMap.get(fullWord).get(word);
                if(popularity > currentPopularirty) {
                    returnWord = word;
                    currentPopularirty = popularity;
                }
            }
        }
        return returnWord;
    }

    public static String multiWordAbbreviation(String multipleWords,
                                        Map<String, Map<String, Integer>> wordToAbbreviationMap) {

        int currentWordLength = multipleWords.length();
        int maximumWordLength = 30;

        multipleWords = toSnakeCase(multipleWords, true);
        if(multipleWords.length() < maximumWordLength) {
            return multipleWords; // already short enough?
        }


        Scanner wordScanner = new Scanner(multipleWords);
        wordScanner.useDelimiter("_");

        StringBuilder stringBuilder = new StringBuilder();
        while(wordScanner.hasNext()) {
            String singleWord = wordScanner.next();

            String abbreviation = getAbbreviation(singleWord, wordToAbbreviationMap);
            if(abbreviation == null) {
                System.out.println("WARNING: No abbreviation found for " + singleWord);
                abbreviation = singleWord;
            }
            if(currentWordLength < maximumWordLength) {
                abbreviation = singleWord;
            }
            else {
                int lengthDifference = singleWord.length() - abbreviation.length();
                currentWordLength = currentWordLength - lengthDifference;

            }


            stringBuilder.append(abbreviation).append("_");
        }
        String returnWord = stringBuilder.toString();
        returnWord = returnWord.substring(0, returnWord.length()-1);
        return returnWord;
    }
}


// if multiple abbreviations exist, take longest one. After that, take most popular one.