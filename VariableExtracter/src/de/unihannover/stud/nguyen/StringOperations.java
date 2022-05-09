package de.unihannover.stud.nguyen;

import java.util.*;

/**
 * Class with static methods to check strings. Also contains a dictionary.
 */
class StringOperations {

  static List<String> dictionary;
  static Set<String> dictionarySet;
  static AbbreviationTree globalAbbreviationTree;

  /**
   * Initializes dictionary
   *
   * @param filenameDic Text file name of the dictionary.
   * @param filenameAbrMap Text file name of the abbreviation map
   */
  public static void initStringChecker(String filenameDic, String filenameAbrMap) {
    String text = Utility.readFile(filenameDic);
    if (text != null) {
      dictionary = new ArrayList<>();
      dictionarySet = new HashSet<>();

      Scanner scanner = new Scanner(text);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (!line.startsWith("#!")) {
          if(!dictionarySet.contains(line)) {
            dictionary.add(line);
            dictionarySet.add(line);
          }
        }
      }
      scanner.close();
    }
    text = Utility.readFile(filenameAbrMap);
    if (text != null) {
      globalAbbreviationTree = new AbbreviationTree();
      globalAbbreviationTree.importTree(filenameAbrMap);
    }
  }

  public static void initAbbreviationMap(String filenameAbrMap) {
    String text = Utility.readFile(filenameAbrMap);
    if (text != null) {
      globalAbbreviationTree = new AbbreviationTree();
      globalAbbreviationTree.importTree(filenameAbrMap);
    }
  }

  /**
   * Initializes dictionary
   *
   * @param filenameDic Text file name of the dictionary.
   */
  public static void initStringChecker(String filenameDic) {
    String text = Utility.readFile(filenameDic);
    if (text != null) {
      dictionary = new ArrayList<>();
      dictionarySet = new HashSet<>();

      Scanner scanner = new Scanner(text);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (!line.startsWith("#!")) {
          if(!dictionarySet.contains(line)) {
            dictionary.add(line);
            dictionarySet.add(line);
          }
        }
      }
      scanner.close();
    }
    globalAbbreviationTree = new AbbreviationTree();
  }


  static String removeFirstComments(String fullText) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(fullText);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String templine = line.replace(" ","").replace("\t","");
      if (!templine.startsWith("#")) {
        stringBuilder.append(line).append("\n");
      }
    }
    scanner.close();
    return stringBuilder.toString();
  }

  // must be executed AFTER removeQuotes
  static String removeComments(String fullText) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(fullText);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.contains("#")) {
        line = line.substring(0,line.indexOf("#")); // watch out for # in strings!!!
      }
      stringBuilder.append(line).append("\n");
    }
    scanner.close();
    return stringBuilder.toString();
  }

  static String removeQuotesInverted(String fullText) {
    boolean inSingleDocstring = false;
    boolean inDoubleDocstring = false;

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    boolean inSingleComment = false;

    int counter = 0;
    int escapecounter = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < fullText.length(); i++) {

      /*if(fullText.charAt(i) == '\n') {
        if(inSingleComment) {
          inSingleComment = false;
        }
        stringBuilder.append(fullText.charAt(i));
      }
      else*/ if(counter > 0) {
      }
      else if(inDoubleDocstring) {
        if(     i+2 < fullText.length() &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' && fullText.charAt(i-5) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        }
        else if(i+2 < fullText.length() &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        }
        else if(i+2 < fullText.length() &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) != '\\') {


          inDoubleDocstring = false;
          counter = 3;
        }
        else {
          // INVERTED
          stringBuilder.append(fullText.charAt(i));
        }


      }
      else if(inSingleDocstring) {
        if(     i+2 < fullText.length() &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' && fullText.charAt(i-5) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
        else if(i+2 < fullText.length() &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
        else if(i+2 < fullText.length() &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
        else {
          // INVERTED
          stringBuilder.append(fullText.charAt(i));
        }


      }
      else if(inDoubleQuote) {
        // backslash not an escape charcter and said backslash is escaped
        if(fullText.charAt(i) == '"' && i >= 9 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) == '\\' &&
                fullText.charAt(i-8) == '\\' &&
                fullText.charAt(i-9) != '\\') {
          inDoubleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 7 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) != '\\') {
          inDoubleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 5 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) != '\\') {
          inDoubleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 3 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) != '\\') {
          inDoubleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if (fullText.charAt(i) == '"' && fullText.charAt(i-1) != '\\') {
          inDoubleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else {
          // INVERTED
          stringBuilder.append(fullText.charAt(i));
        }
      }


      else if(inSingleQuote) {
        if(fullText.charAt(i) == '\'' && i >= 9 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) == '\\' &&
                fullText.charAt(i-8) == '\\' &&
                fullText.charAt(i-9) != '\\') {
          inSingleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 7 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) != '\\') {
          inSingleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 5 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) != '\\') {
          inSingleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 3 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) != '\\') {
          inSingleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && fullText.charAt(i-1) != '\\') {
          inSingleQuote = false;
//          stringBuilder.append(fullText.charAt(i));
        }
        else {
          // INVERTED
          stringBuilder.append(fullText.charAt(i));
        }
      }
      else if(inSingleComment) {
        if(fullText.charAt(i) == '\n') {
//          stringBuilder.append(fullText.charAt(i));
          inSingleComment = false;
        }
        else {
          // INVERTED
//          stringBuilder.append(fullText.charAt(i));
        }
      }



      else {
        if(i+2 < fullText.length() &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"') {
          inDoubleDocstring = true;
          counter = 3;
        }
        else if(i+2 < fullText.length() &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'') {
          inSingleDocstring = true;
          counter = 3;
        }

        else if(fullText.charAt(i) == '"') {
          inDoubleQuote = true;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'') {
          inSingleQuote = true;
//          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '#') {
          inSingleComment = true;
        }
        else {
//          stringBuilder.append(fullText.charAt(i));
        }
      }


      counter = counter - 1;
      if(counter < 0) {
        counter = 0;
      }
    }
    return stringBuilder.toString();
  }

  static String removeEmptyLines(String fullText) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(fullText);
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.replace(" ", "").replace("\t","");
      if (!line.isEmpty()) {
        stringBuilder.append(line).append("\n");
      }
    }
    return stringBuilder.toString();
  }

  static String removeExcessWhitespace(String fullText) {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(fullText);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      boolean leadingWhitespace = true;
      boolean inWhitespace = false;

      for(int i = 0; i < line.length(); i++) {
        if(leadingWhitespace) {
          if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
            leadingWhitespace = false;
          }
          stringBuilder.append(line.charAt(i));
        }
        else {
          if(inWhitespace) {
            if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {

            }
            else {
              inWhitespace = false;
              stringBuilder.append(line.charAt(i));
            }
          }
          else {
            if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
              inWhitespace = true;
              stringBuilder.append(line.charAt(i));
            }
            else {
              stringBuilder.append(line.charAt(i));
            }
          }
        }
      }
      stringBuilder.append("\n");
    }
    scanner.close();

    return stringBuilder.toString();
  }

  static String turnToSingleLines(String fullText) {

    int squareBracketCount = 0;
    int roundBracketCount = 0;
    int curlyBracketCount = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < fullText.length(); i++) {
      if(fullText.charAt(i) == '(') {
        roundBracketCount++;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == '{') {
        curlyBracketCount++;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == '[') {
        squareBracketCount++;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == ')') {
        roundBracketCount--;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == '}') {
        curlyBracketCount--;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == ']') {
        squareBracketCount--;
        stringBuilder.append(fullText.charAt(i));
      }
      else if(fullText.charAt(i) == '\n'){
        if(i > 0 && fullText.charAt(i-1) == '\\') {

        }
        else if(squareBracketCount > 0 ||
                roundBracketCount > 0 || curlyBracketCount > 0) {
        }
        else  {
          stringBuilder.append(fullText.charAt(i));
        }
      }
      else {
        stringBuilder.append(fullText.charAt(i));
      }
    }
    return (stringBuilder.toString());

  }

  // TODO: Replace? This runs slow on average
  static String removeQuotes(String fullText) {
    boolean inSingleDocstring = false;
    boolean inDoubleDocstring = false;

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    boolean inSingleComment = false;

    int counter = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < fullText.length(); i++) {

      if(counter > 0) {

      }
      else if(inDoubleDocstring) {

        if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' && fullText.charAt(i-5) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        }
        else if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        }
        else if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"' && fullText.charAt(i-1) != '\\') {


          inDoubleDocstring = false;
          counter = 3;
        }
      }
      else if(inSingleDocstring) {

        if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' && fullText.charAt(i-5) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
        else if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' && fullText.charAt(i-3) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
        else if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'' && fullText.charAt(i-1) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
      }
      else if(inDoubleQuote) {
        // backslash not an escape charcter and said backslash is escaped
        if(fullText.charAt(i) == '"' && i >= 9 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) == '\\' &&
                fullText.charAt(i-8) == '\\' &&
                fullText.charAt(i-9) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 7 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 5 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '"' && i >= 3 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if (fullText.charAt(i) == '"' && fullText.charAt(i-1) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
      }
      else if(inSingleQuote) {
        if(fullText.charAt(i) == '\'' && i >= 9 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) == '\\' &&
                fullText.charAt(i-8) == '\\' &&
                fullText.charAt(i-9) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 7 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) == '\\' &&
                fullText.charAt(i-6) == '\\' &&
                fullText.charAt(i-7) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 5 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) == '\\' &&
                fullText.charAt(i-4) == '\\' &&
                fullText.charAt(i-5) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && i >= 3 &&
                fullText.charAt(i-1) == '\\' &&
                fullText.charAt(i-2) == '\\' &&
                fullText.charAt(i-3) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'' && fullText.charAt(i-1) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
      }
      else if(inSingleComment) {
        if(fullText.charAt(i) == '\n') {
          inSingleComment = false;
          stringBuilder.append("\n");
        }
      }
      else {
//        System.out.println(i+": "+(i+2+1));
//        System.out.println("A: "+fullText.length());


        if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '"' &&
                fullText.charAt(i+1) == '"' &&
                fullText.charAt(i+2) == '"') {
          inDoubleDocstring = true;
          counter = 3;
        }
        else if((i+2 < fullText.length()) &&
                fullText.charAt(i) == '\'' &&
                fullText.charAt(i+1) == '\'' &&
                fullText.charAt(i+2) == '\'') {
          inSingleDocstring = true;
          counter = 3;
        }

        else if(fullText.charAt(i) == '"') {
          inDoubleQuote = true;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '\'') {
          inSingleQuote = true;
          stringBuilder.append(fullText.charAt(i));
        }
        else if(fullText.charAt(i) == '#') {
          inSingleComment = true;
        }
        else {
          stringBuilder.append(fullText.charAt(i));

        }
      }


      counter = counter - 1;
      if(counter < 0) {
        counter = 0;
      }
    }
    return turnToSingleLines(stringBuilder.toString());
  }

  public static String toSnakeCase(String var, boolean lowerCase) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(String.valueOf(var.charAt(0)).toLowerCase());
    for(int idx = 1; idx < var.length(); idx++) {
      char previous = var.charAt(idx-1);
      char ch = var.charAt(idx);
      if(ch >= 'A' && ch <= 'Z' &&
              (previous >= 'a' && previous <= 'z')) {
        String s = String.valueOf(ch);
        stringBuilder.append("_").append(s.toLowerCase());
      }
      else if(ch >= 'A' && ch <= 'Z' &&
              (previous >= '0' && previous <= '9')) {
        String s = String.valueOf(ch);
        stringBuilder.append("_").append(s.toLowerCase());
      }
      else if(ch >= 'A' && ch <= 'Z') {
        String s = String.valueOf(ch);
        stringBuilder.append(s.toLowerCase());
      }
      else {
        stringBuilder.append(ch);
      }
    }


    var = stringBuilder.toString();
    if(!lowerCase) {
      var = var.toUpperCase();
    }
    return var;
  }


  public static int getIndexOfDictionary(String string) {
    for(int i = 0; i < dictionary.size(); i++) {
      if(string.equals(dictionary.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Check if a string is not in the dictionary
   *
   * @param string string to be checked
   * @return true if not in the dictionary
   */
  public static boolean notInDictionary(String string) {
    if(string.length() <= 1) {
      return true;
    }
    if (!dictionarySet.contains(string)) {
      return true;
    }
    else if (!dictionarySet.contains(string.toLowerCase())) {
      return true;
    }
    else {
      return false;
    }
  }

  public static boolean notInAbrMap(String string) {
    if(!globalAbbreviationTree.containsAbbreviation(string)) {
      return true;
    }
    else if(!globalAbbreviationTree.containsAbbreviation(string.toLowerCase())) {
      return true;
    }
    else {
      return false;
    }
  }

  public static Map<String, Integer> getAbbreviations(String string, String type) {
    if(type == null) {
      return globalAbbreviationTree.getWithoutType(string);
    }
    else {
      return globalAbbreviationTree.get(string, type);
    }
  }

  /**
   * Check if the string is an assignment operator
   *
   * @param word string, a single word
   * @return true if it is an assignment operator
   */
  public static boolean checkAssignmentOperatorWhitespace(String word) {
    return word.contains(" = ") || word.contains(" += ") || word.contains(" -= ") ||
            word.contains(" *= ") || word.contains(" /= ") || word.contains(" %= ") ||
            word.contains(" &= ") || word.contains(" |= ") || word.contains(" ^= ") ||
            word.contains(" >>= ") || word.contains(" <<= ") ||
            word.contains(" //= ") || word.contains(" &&= ");
  }

  /**
   * Check if the string is an assignment operator
   *
   * @param word string, a single word
   * @return true if it is an assignment operator
   */
  public static boolean checkAssignmentOperator(String word) {
    return word.equals("=") || word.equals("+=") || word.equals("-=") ||
            word.equals("*=") || word.equals("/=") || word.equals("%=") ||
            word.equals("&=") || word.equals("|=") || word.equals("^=") ||
            word.equals(">>=") || word.equals("<<=") ||
            word.equals("//=") || word.equals("&&=");
  }

  /**
   * Check if the word is a valid variable by checking how it starts
   *
   * @param string word, a variable
   * @return true if it starts validly.
   */
  public static boolean checkString(String string) {
    return !string.startsWith("0") && !string.startsWith("1") &&
            !string.startsWith("2") && !string.startsWith("3") &&
            !string.startsWith("4") && !string.startsWith("5") &&
            !string.startsWith("6") && !string.startsWith("7") &&
            !string.startsWith("8") && !string.startsWith("9") && !string.equals("\t") &&
            !string.contains("+") && !string.contains("-") && !string.equals("  ") &&
            !string.contains("*") && !string.contains("/") && !string.equals(" ") &&
            !string.startsWith("=") && !string.startsWith("<") && !string.startsWith(">") &&
            !string.isEmpty() && !string.equals("(") && !string.equals(")") &&
            !string.contains("{") && !string.contains("}") && !string.startsWith("@") &&
            !string.contains("\"") && !string.startsWith("!") &&
            !string.startsWith("#") && !string.startsWith("%") &&
            !string.startsWith("$") && !string.startsWith("&") &&
            !string.contains("'") && !string.startsWith("|");
  }


  /**
   * Splits a line. The split is done at the assignment operator.
   * It returns a list of two sides if there is an assignment operator.
   * The first string is the left side before the assignment operator.
   * The second string is the right side after the assignment operator.
   * <p>
   * If there is a multiline statement then the lines have to be combined into
   * a single line first before applying this function.
   * <p>
   * Returns null if line does not have an assignment operator
   *
   * @param line Line to be split
   * @return A single list of two strings, 1st entry is left side, 2nd is right side
   */
  static String getLeftHandSideVariableDeclaration(String line) {

    String leftSide = null;
    if (line.contains("&&=")) {
      leftSide = line.substring(0, line.indexOf("&&="));
    } else if (line.contains("//=")) {
      leftSide = line.substring(0, line.indexOf("//="));
    } else if (line.contains(">>=")) {
      leftSide = line.substring(0, line.indexOf(">>="));
    } else if (line.contains("<<=")) {
      leftSide = line.substring(0, line.indexOf("<<="));
    } else if (line.contains("+=")) {
      leftSide = line.substring(0, line.indexOf("+="));
    } else if (line.contains("-=")) {
      leftSide = line.substring(0, line.indexOf("-="));
    } else if (line.contains("*=")) {
      leftSide = line.substring(0, line.indexOf("*="));
    } else if (line.contains("/=")) {
      leftSide = line.substring(0, line.indexOf("/="));
    } else if (line.contains("%=")) {
      leftSide = line.substring(0, line.indexOf("%="));
    } else if (line.contains("&=")) {
      leftSide = line.substring(0, line.indexOf("&="));
    } else if (line.contains("|=")) {
      leftSide = line.substring(0, line.indexOf("|="));
    } else if (line.contains("^=")) {
      leftSide = line.substring(0, line.indexOf("^="));
    } else if (line.contains("=")) {
      leftSide = line.substring(0, line.indexOf("="));
    }

    if (leftSide == null) {
//      System.out.println("1234567890 this should not be possible");
//      System.out.println("a");
      return null;
    }

    return leftSide;
  }

  public static String removeInsideBraces(String line) {
    StringBuilder stringBuilder = new StringBuilder();
    int inSquareBraces = 0;
    int inRoundBraces = 0;
    int inCurlyBraces = 0;
    int inDoubleQuote = 0;
    int inSingleQuote = 0;

    for(int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if(inSquareBraces > 0) {
        if(c == '[') {
          inSquareBraces++;
          stringBuilder.append(c);
        }
        if(c == ']') {
          inSquareBraces--;
          stringBuilder.append(c);
        }
      }
      else if(inRoundBraces > 0) {
        if(c == '(') {
          inRoundBraces++;
          stringBuilder.append(c);
        }
        if(c == ')') {
          inRoundBraces--;
          stringBuilder.append(c);
        }
      }
      else if(inCurlyBraces > 0) {
        if(c == '{') {
          inCurlyBraces++;
          stringBuilder.append(c);
        }
        if(c == '}') {
          inCurlyBraces--;
          stringBuilder.append(c);
        }
      }
      else if(inDoubleQuote > 0) {
        if(c == '"' && line.charAt(i-1) != '\\') {
          inDoubleQuote--;
          stringBuilder.append(c);
        }
      }
      else if(inSingleQuote > 0) {
        if(c == '\'' && line.charAt(i-1) != '\\'){
          inSingleQuote--;
          stringBuilder.append(c);
        }
      }
      else {
        if(c == '[') {
          inSquareBraces++;
        }
        else if(c == '(') {
          inRoundBraces++;
        }
        else if(c == '{') {
          inCurlyBraces++;
        }
        else if(c == '"') {
          inDoubleQuote++;
        }
        else if(c == '\'') {
          inSingleQuote++;
        }
        stringBuilder.append(c);
      }
    }
    return stringBuilder.toString();
  }

  // only remove leading and ending underscores
  // leading underscores are used for "private" values
  // do NOT remove underscores in the middle

  /**
   * Remove leading and trailing underscores
   *
   * @param name variable name
   * @return variable name without leading and trailing underscores
   */
  public static String removeLeadingAndTrailingUnderscores(String name) {
    if (name.equals("_")) {
      return name;
    }

    while (name.startsWith("_")) {
      name = name.substring(1);
    }
    while (name.endsWith("_")) {
      name = name.substring(0, name.length() - 1);
    }
    return name;
  }

  /**
   * Removes invalid characters from string
   *
   * @param word         variable name
   * @param inDefinition true if it is an argument in function. false if indep. variable
   * @return String without
   */
  static String removeStuff(String word, boolean inDefinition) {
    while (word.contains(":")) {
      word = word.substring(0, word.lastIndexOf(":"));
    }


    if (word.contains("[") && word.contains("]")) {
      word = word.substring(0, word.indexOf("["));
    }
    if (word.contains("[")) {
      word = word.substring(0, word.indexOf("["));
    }

    if (word.contains("]")) {
      word = word.substring(0, word.indexOf("]"));
    }
    if (word.startsWith("(")) {
      while (word.startsWith("(")) {
        word = word.substring(1);
      }
    } else if (word.endsWith("(")) {
      word = word.substring(0, word.indexOf("("));
    } else if (word.contains("(")) {
      word = word.substring(0, word.indexOf("("));
    }

    if (word.contains(")")) {
      word = word.substring(0, word.indexOf(")"));
    }

    if (word.contains(",")) {
      word = word.substring(0, word.indexOf(","));
    }

    if (!inDefinition) {
      while (word.contains(".")) {
        word = word.substring(word.indexOf(".") + 1);
      }

    }
    return removeLeadingAndTrailingUnderscores(word);
  }

  /**
   * Removes numbers from string
   *
   * @param string variable name
   * @return variable name without numbers
   */
  static String removeNumbers(String string) {
    string = string.replace("0", "");
    string = string.replace("1", "");
    string = string.replace("2", "");
    string = string.replace("3", "");
    string = string.replace("4", "");
    string = string.replace("5", "");
    string = string.replace("6", "");
    string = string.replace("7", "");
    string = string.replace("8", "");
    string = string.replace("9", "");

    // second pass
    string = string.replace("0", "");
    string = string.replace("1", "");
    string = string.replace("2", "");
    string = string.replace("3", "");
    string = string.replace("4", "");
    string = string.replace("5", "");
    string = string.replace("6", "");
    string = string.replace("7", "");
    string = string.replace("8", "");
    string = string.replace("9", "");


    return string;
  }


  /**
   * Checks if certain keywords are used
   * TODO: Not finished
   *
   * @param line single line
   * @return true if it does not start with that otherwise false.
   */
  static boolean checkInvalidAssignment(String line) {
    if (line.startsWith("class")) {
      return true;
    }
    if (line.startsWith("if")) {
      return true;
    }
    return line.startsWith("elif");
  }

  /**
   * Checks if word is not a keyword
   *
   * @param word word, variable name
   * @return true if it is not a keyword
   */
  static boolean checkIfItIsNotAKeyword(String word) {
    return !word.equals("Optional") && !word.equals("None") &&
            !word.equals("Sequence") && !word.equals("Union") &&
            !word.equals("List") && !word.equals("Tuple") &&
            !word.equals("and") && !word.equals("as") &&
            !word.equals("assert") && !word.equals("break") &&
            !word.equals("class") && !word.equals("continue") &&
            !word.equals("def") && !word.equals("del") &&
            !word.equals("elif") && !word.equals("else") &&
            !word.equals("except") && !word.equals("False") &&
            !word.equals("for") && !word.equals("from") &&
            !word.equals("global") && !word.equals("if") &&
            !word.equals("import") && !word.equals("in") &&
            !word.equals("is") && !word.equals("lambda") &&
            !word.equals("nonlocal") && !word.equals("not") &&
            !word.equals("or") && !word.equals("pass") &&
            !word.equals("raise") && !word.equals("return") &&
            !word.equals("True") && !word.equals("try") &&
            !word.equals("while") && !word.equals("with") &&
            !word.equals("yield");
  }

  /**
   * Check if the word is a number. Does support a single decimal point and fractions
   *
   * @param word word, variable name?
   * @return true if the word is a number, otherwise false
   */
  static boolean checkIfNumber(String word) {
    if (word.equals("*")) {
      return false;
    }

    boolean singleDot = false;
    boolean singleFraction = false;
    for (int index = 0; index < word.length(); index++) {
      char character = word.charAt(index);

      if (character == '/' || character == '*' || character == '+' || character == '-') {
        if (singleFraction) {
          return false;
        } else {
          singleFraction = true;
        }
      } else if (character == '.') {
        if (singleDot) {
          return false;
        } else {
          singleDot = true;
        }
      } else if (character < '0' || character > '9') {
        return false;
      }
    }
    return true;
  }

  public static boolean isAlphabetic(String string) {
    for(int i = 0; i < string.length(); i++) {
      if(string.charAt(i) >= 'a' && string.charAt(i) <= 'z') {

      }
      else if(string.charAt(i) >= 'A' && string.charAt(i) <= 'Z') {

      }
      else {
        return false;
      }
    }
    return true;
  }

  public static String wordSeparator(String twoWordsInIt, boolean abrexists) {
    if(twoWordsInIt.length() < 5 || twoWordsInIt.contains("_")) {
      return null;
    }
    int full = twoWordsInIt.length();
    if(full % 2 == 1) {
      full = full + 1;
    }

    int half = full / 2;

    String replacementString = twoWordsInIt;
    int combinedIndex = Integer.MAX_VALUE;

    for(int index = 3; index < twoWordsInIt.length()-2; index++) {
      String left  = twoWordsInIt.substring(0, index);
      String right = twoWordsInIt.substring(index);


      if(!StringOperations.notInDictionary(left) && !StringOperations.notInDictionary(right)) {
//        list.add(left+"_"+right);
        int leftIndex = StringOperations.getIndexOfDictionary(left);
        int rightIndex = StringOperations.getIndexOfDictionary(right);
        if((leftIndex+rightIndex) < combinedIndex) {
          replacementString = left + "_" + right;
          combinedIndex = leftIndex + rightIndex;
        }
      }
      else if(abrexists &&
              !StringOperations.notInAbrMap(left) && !StringOperations.notInDictionary(right)) {
        int leftIndex = 100000 + Math.abs(half - index);
        int rightIndex = StringOperations.getIndexOfDictionary(right);
        if((leftIndex+rightIndex) < combinedIndex) {
          replacementString = left + "_" + right;
          combinedIndex = leftIndex + rightIndex;
        }
      }
      else if(abrexists &&
              !StringOperations.notInDictionary(left) && !StringOperations.notInAbrMap(right)) {
        int leftIndex = StringOperations.getIndexOfDictionary(left);
        int rightIndex = 100000 + Math.abs(half - index);
        if((leftIndex+rightIndex) < combinedIndex) {
          replacementString = left + "_" + right;
          combinedIndex = leftIndex + rightIndex;
        }
      }
//      else if(abrexists &&
//              !StringOperations.notInAbrMap(left) && !StringOperations.notInAbrMap(right)) {
//        int leftIndex = 1000000;
//        int rightIndex = 1000000;
//        if((leftIndex+rightIndex) < combinedIndex) {
//          replacementString = left + "_" + right;
//          combinedIndex = leftIndex + rightIndex;
//        }
//      }
      // chieken egg problem, when trying to use the abbreviation map
      // TODO: Factor in word index
      // TODO: ABR MAP!!!
    }
    // abcdef
    // abc_def
    // abcd_ef
    // ab_cdef
//    for(int index = half-1; index >= 2; index--) {
//      String left  = twoWordsInIt.substring(0, index);
//      String right = twoWordsInIt.substring(index);
//
//      if(!StringOperations.notInDictionary(left) && !StringOperations.notInDictionary(right)) {
////        list.add(left+"_"+right);
//        int leftIndex = StringOperations.getIndexOfDictionary(left);
//        int rightIndex = StringOperations.getIndexOfDictionary(right);
//        if((leftIndex+rightIndex) < combinedIndex) {
//          replacementString = left + "_" + right;
//          combinedIndex = leftIndex + rightIndex;
//        }
//      }
//    }
    if(replacementString.equals(twoWordsInIt)) {
      return null;
    }
    else {
      return replacementString;
    }

  }


  public static boolean checkValidVariableName(String word) {
    if(word == null) {
      return false;
    }
    if(word.length() == 0) {
      return false;
    }
    if(word.charAt(0) >= '0' && word.charAt(0) <= '9') {
      return false;
    }

    for(int i = 0; i < word.length(); i++) {
      if(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z' ||
              word.charAt(i) >= 'a' && word.charAt(i) <= 'z') {

      }
      else if(word.charAt(i) >= '0' && word.charAt(i) <= '9') {

      }
      else if(word.charAt(i) == '_'){

      }
      else if(word.charAt(i) > 128) {

      }
      else {
        return false;
      }
    }
    return true;
  }

}
