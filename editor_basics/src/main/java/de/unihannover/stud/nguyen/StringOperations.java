package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.*;

import static de.unihannover.stud.nguyen.IOOperations.readFile;

/**
 * Static class of string operations
 * @author Huu Kim Nguyen
 */
public class StringOperations {

  static Set<String> dictionary;
  static AbbreviationTree abbreviationTree;
  static Map<String, Map<String, Integer>> wordToAbbreviationMap;

  /**
   * Initializes dictionary
   *
   * @param filename Text file name of the dictionary.
   */
  public static boolean initDictionaryAndAbbreviation(String filename, String filenameAbrMap) {
    String text = IOOperations.readFile(new File(filename));
    if(text == null) {
      return false;
    }
    else if(text != null) {
      dictionary = new HashSet<>();

      Scanner scanner = new Scanner(text);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if(!line.startsWith("#!comment:")) {
          dictionary.add(line);
        }
      }
      scanner.close();
    }

    text = IOOperations.readFile(new File(filenameAbrMap));
    if(text == null) {
      return false;
    }
    else if (text != null) {
      abbreviationTree = new AbbreviationTree();
      abbreviationTree.importTree(filenameAbrMap);

//      abbreviationToWordMap = new HashMap<>();
//
//      Scanner scanner = new Scanner(text);
//      while (scanner.hasNextLine()) {
//        String line = scanner.nextLine();
//        if (!line.startsWith("#!") && line.contains(";")) {
//          String abbreviation = line.substring(0, line.indexOf(";"));
//          String fullWord = line.substring(line.indexOf(";") + 1);
//
//          if(!abbreviationToWordMap.containsKey(abbreviation)) {
//            abbreviationToWordMap.put(abbreviation, new ArrayList<>());
//          }
//          if(abbreviationToWordMap.get(abbreviation).size() < 6) {
//            abbreviationToWordMap.get(abbreviation).add(fullWord);
//          }
//        }
//      }
//      scanner.close();
    }
    return true;
  }

  /**
   * Initializes dictionary
   *
   * @param filename Text file name of the dictionary.
   */
  public static void initDictionaryOnly(String filename) {
    String text = IOOperations.readFile(new File(filename));
    if(text != null) {
      dictionary = new HashSet<>();

      Scanner scanner = new Scanner(text);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if(!line.startsWith("#!comment:")) {
          dictionary.add(line);
        }
      }
      scanner.close();
    }
  }
  /**
   * Check if a string is in the dictionary
   *
   * @param string string to be checked
   * @return true if in the dictionary
   */
  public static boolean inDictionary(String string) {
    return dictionary.contains(string);
  }


  public static boolean inAbbreviationMap(String string) {
    return abbreviationTree.containsAbbreviation(string);
  }
//  /**
//   * Checks if word is not a keyword
//   *
//   * @param word word, variable name
//   * @return true if it is not a keyword
//   */
//  static boolean doesNotUseKeywords(String word) {
//    return !word.equals("Optional") && !word.equals("None") &&
//            !word.equals("Sequence") && !word.equals("Union") &&
//            !word.equals("List") && !word.equals("Tuple") &&
//            !word.equals("and") && !word.equals("as") &&
//            !word.equals("assert") && !word.equals("break") &&
//            !word.equals("class") && !word.equals("continue") &&
//            !word.equals("def") && !word.equals("del") &&
//            !word.equals("elif") && !word.equals("else") &&
//            !word.equals("except") && !word.equals("False") &&
//            !word.equals("for") && !word.equals("from") &&
//            !word.equals("global") && !word.equals("if") &&
//            !word.equals("import") && !word.equals("in") &&
//            !word.equals("is") && !word.equals("lambda") &&
//            !word.equals("nonlocal") && !word.equals("not") &&
//            !word.equals("or") && !word.equals("pass") &&
//            !word.equals("raise") && !word.equals("return") &&
//            !word.equals("True") && !word.equals("try") &&
//            !word.equals("while") && !word.equals("with") &&
//            !word.equals("yield") && !word.equals(">>>")  && !word.equals("#");
//  }


  public static Map<String, Map<String, Integer>> reverseAbbreviationMap(String filenameAbrMap) {
    Map<String, Map<String, Integer>> map = new HashMap<>();

    String fileAsString = readFile(new File(filenameAbrMap));



    if (fileAsString != null) {
      Scanner scanner = new Scanner(fileAsString);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        String abbreviation = line.substring(0, line.indexOf(";"));
        line = line.substring(line.indexOf(";")+1);

        String type = line.substring(0, line.indexOf(";"));
        line = line.substring(line.indexOf(";")+1);

        String fullWord = line.substring(line.indexOf(";")+1);
        line = line.substring(line.indexOf(";")+1);

        int popularity = Integer.parseInt(line);

        if(!map.containsKey(fullWord)) {
          map.put(fullWord, new HashMap<>());
          map.get(fullWord).put(abbreviation, popularity);
        }
        else {
          if(map.get(fullWord).containsKey(abbreviation)) {
            map.get(fullWord).put(abbreviation, map.get(fullWord).get(abbreviation)+popularity);
          }
          else {
            map.get(fullWord).put(abbreviation,popularity);
          }
        }
      }
    }

    wordToAbbreviationMap = map;
    return map;
  }

  /**
   * Remove any Python comments that are in a single line of Python code
   *
   * Example
   * Before: x = 0  # This is a comment
   * After:  x = 0
   *
   * @param line String line that might contain a single Python comment
   * @return String line without Python comment
   */
  static String removeSingleComment(String line) {

    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;
    boolean singleComment = false;
    int index = 0;

    for(int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);

      if(inDoubleQuote) {
        if(ch == '"' && line.charAt(i-1) != '\\') {
          inDoubleQuote = false;
        }
      }
      else if(inSingleQuote) {
        if(ch == '\''&& line.charAt(i-1) != '\\') {
          inSingleQuote = false;
        }
      }
      else {
        if(ch == '"') {
          inDoubleQuote = true;
        }
        else if(ch == '\'') {
          inSingleQuote = true;
        }
        else if(ch == '#') {
          singleComment = true;
          index = i;
          break;
        }
      }
    }

    if(singleComment) {
      return line.substring(0,index);
    }
    else {
      return line;
    }
  }

  /**
   * Check if a single char is not inside a quote (either " or ')
   *
   * @param fullLine full string line
   * @param charToBeChecked char that is to be checked if it is inside a quote
   * @return true if that char is inside a quote, otherwise false
   */
  public static boolean checkIfNotInQuote(String fullLine, char charToBeChecked) {

    int braceScore = 0;
    int quoteScore = 0;
    for (int i = 0; i < fullLine.length(); i++) {
      char ch = fullLine.charAt(i);

      if (ch == charToBeChecked && braceScore >= 1) {
        return false;
      } else if (ch == charToBeChecked && (quoteScore % 2) == 1) {
        return false;
      } else if (ch == '(' || ch == '{' || ch == '[') {
        braceScore++;
      } else if (ch == ')' || ch == '}' || ch == ']') {
        braceScore--;
      } else if (ch == '"' || ch == '\'') {
        quoteScore++;
      }
    }
    return true;
  }

  public static String trimName(String function) {
    while (function.endsWith("0") || function.endsWith("1") ||
            function.endsWith("2") || function.endsWith("3") ||
            function.endsWith("4") || function.endsWith("5") ||
            function.endsWith("6") || function.endsWith("7") ||
            function.endsWith("8") || function.endsWith("9") ||
            function.endsWith("_")) {
      function = function.substring(0,function.length()-1);
    }
    while (function.startsWith("_")) {
      function = function.substring(1);
    }
    return function;
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

  /**
   * Check if the string is an assignment operator
   *
   * @param line string, a single line
   * @return true if it is an assignment operator
   */
  public static boolean hasAlternativeAssignmentOperatorWithWhitespace(String line) {
    if (line.contains(" += ") || line.contains(" -= ") ||
            line.contains(" *= ") || line.contains(" /= ") || line.contains(" %= ") ||
            line.contains(" &= ") || line.contains(" |= ") || line.contains(" ^= ") ||
            line.contains(" >>= ") || line.contains(" <<= ") ||
            line.contains(" //= ") || line.contains(" &&= ")) {
      return checkIfNotInQuote(line, '=');
    } else {
      return false;
    }
  }

  /**
   * Check if the string is an assignment operator
   *
   * @param line string, a single line
   * @return true if it is an assignment operator
   */
  public static boolean hasAssignmentOperatorWithWhitespace(String line) {
    if (line.contains(" = ") || line.contains(" += ") || line.contains(" -= ") ||
            line.contains(" *= ") || line.contains(" /= ") || line.contains(" %= ") ||
            line.contains(" &= ") || line.contains(" |= ") || line.contains(" ^= ") ||
            line.contains(" >>= ") || line.contains(" <<= ") ||
            line.contains(" //= ") || line.contains(" &&= ")) {
      return checkIfNotInQuote(line, '=');
    } else {
      return false;
    }
  }


  /**
   * Check if the line is not empty. Comments to not count.
   *
   * @param line line to be checked
   * @return true if line is not empty, false otherwise
   */
  public static boolean isLineNotEmpty(String line) {
    if (line.contains("#")) {
      line = removeSingleComment(line);
    }
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) >= 33 && line.charAt(i) <= 126) {
        return true;
      }
    }
    return false;
  }


  /**
   * Splits a line. The split is done at the assignment operator.
   * It returns a list of two sides if there is an assignment operator.
   * The first string is the left side before the assignment operator.
   * The second string is the right side after the assignment operator.
   *
   * If there is a multiline statement then the lines have to be combined into
   * a single line first before applying this function.
   *
   * Returns null if line does not have an assignment operator
   *
   *
   * @param line Line to be split
   * @return A single list of two strings, 1st entry is left side, 2nd is right side
   */
  static List<String> splitLineBetweenAssignmentOperator(String line) {

    String leftSide = null;
    String rightSide = null;
    if (line.contains("&&=")) {
      leftSide = line.substring(0, line.indexOf("&&="));
      rightSide = line.substring(line.indexOf("&&=") + 3);
    } else if (line.contains("//=")) {
      leftSide = line.substring(0, line.indexOf("//="));
      rightSide = line.substring(line.indexOf("//=") + 3);
    } else if (line.contains(">>=")) {
      leftSide = line.substring(0, line.indexOf(">>="));
      rightSide = line.substring(line.indexOf(">>=") + 3);
    } else if (line.contains("<<=")) {
      leftSide = line.substring(0, line.indexOf("<<="));
      rightSide = line.substring(line.indexOf("<<=") + 3);
    } else if (line.contains("+=")) {
      leftSide = line.substring(0, line.indexOf("+="));
      rightSide = line.substring(line.indexOf("+=") + 2);
    } else if (line.contains("-=")) {
      leftSide = line.substring(0, line.indexOf("-="));
      rightSide = line.substring(line.indexOf("-=") + 2);
    } else if (line.contains("*=")) {
      leftSide = line.substring(0, line.indexOf("*="));
      rightSide = line.substring(line.indexOf("*=") + 2);
    } else if (line.contains("/=")) {
      leftSide = line.substring(0, line.indexOf("/="));
      rightSide = line.substring(line.indexOf("/=") + 2);
    } else if (line.contains("%=")) {
      leftSide = line.substring(0, line.indexOf("%="));
      rightSide = line.substring(line.indexOf("%=") + 2);
    } else if (line.contains("&=")) {
      leftSide = line.substring(0, line.indexOf("&="));
      rightSide = line.substring(line.indexOf("&=") + 2);
    } else if (line.contains("|=")) {
      leftSide = line.substring(0, line.indexOf("|="));
      rightSide = line.substring(line.indexOf("|=") + 2);
    } else if (line.contains("^=")) {
      leftSide = line.substring(0, line.indexOf("^="));
      rightSide = line.substring(line.indexOf("^=") + 2);
    } else if (line.contains("=")) {
      leftSide = line.substring(0, line.indexOf("="));
      rightSide = line.substring(line.indexOf("=") + 1);
    }

    if (leftSide == null) {
      System.out.println("1234567890 this should not be possible");
      return null;
    }

    List<String> list = new ArrayList<>();
    list.add(leftSide);
    list.add(rightSide);
    return list;
  }

  /**
   * Check if the string is an integer
   *
   * @param variable String to be checked
   * @return true if it is an integer, false otherwise
   */
  static boolean isInteger(String variable) {
    for (int i = 0; i < variable.length(); i++) {
      char ch = variable.charAt(i);
      if (ch < '0' || ch > '9') {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the string is a number.
   * Apply isInteger instead, then apply this function
   * to check if the string is a Float.
   *
   * @param variable String to be checked
   * @return true if it is an integer, false otherwise
   */
  static boolean isNumber(String variable) {
    boolean singleDecimalPointUsed = false;
    for (int i = 0; i < variable.length(); i++) {
      char ch = variable.charAt(i);

      if (ch == '.' && singleDecimalPointUsed) {
        return false;
      } else if (ch == '.') {
        singleDecimalPointUsed = true;
      } else {
        if (ch < '0' || ch > '9') {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Check if the string is not a primitive variable.
   * Primitive variables are numbers, strings, True and False.
   *
   * @param variable String to be checked
   * @return true if it NOT a primitive variable, false otherwise.
   */
  static boolean isNotPrimitive(String variable) {
    if (variable.equals("0")) {
      return false;
    }

    if (isNumber(variable)) {
      return false;
    }

    if (variable.startsWith("'") && variable.endsWith("'")) {
      return false;
    }

    if (variable.startsWith("\"") && variable.endsWith("\"")) {
      return false;
    }

    return !variable.equals("True") && !variable.equals("False");
  }

  /**
   * Replace any commas inside braces with semicolons
   *
   * @param string String that might contain any braces
   * @return same string but with commas replaced with semicolons inside braces
   */
  static String handleInnerBracers(String string) {
    int braceScore = 0;

    int quoteScore = 0;
    int quoteScoreSingle = 0;
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);

      if (ch == ',' && braceScore >= 1) {
        stringBuilder.append(';');
      } else if (ch == ',' && (quoteScore % 2) == 1) {
        stringBuilder.append(';');

      } else if (ch == '(') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
        stringBuilder.append("(");
      } else if (ch == '{') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
        stringBuilder.append("{");
      } else if (ch == '[') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
        stringBuilder.append("[");
      } else if (ch == ')') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
        stringBuilder.append(")");
      } else if (ch == '}') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
        stringBuilder.append("}");
      } else if (ch == ']') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
        stringBuilder.append("]");
      } else if (ch == '"') {
        quoteScore++;
        stringBuilder.append("\"");
      } else if (ch == '\'') {
        quoteScoreSingle++;
        stringBuilder.append("'");
      } else {
        stringBuilder.append(ch);
      }
    }
    return stringBuilder.toString();
  }


  /**
   * Extract a Python function name from the line
   *
   * @param line full string line
   * @return Python function name
   */
  static String extractDefName(String line) {
    Scanner scanner = new Scanner(line);
    scanner.next();
    String defName;
    defName = scanner.next();
    if (defName.contains("(")) {
      defName = defName.substring(0, defName.indexOf("("));

    }
    scanner.close();

    return defName;
  }

  /**
   * Check if the number of brackets and braces are valid.
   * To be valid, the number of these have to be even.
   *
   * @param line full String line
   * @return true of the number of brackets and braces are even, false otherwise
   */
  static boolean checkValidNumberOfBrackets(String line) {
    int braceScore = 0;

    int quoteScore = 0;
    int quoteScoreSingle = 0;
    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);

      if (ch == '(') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
      } else if (ch == '{') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
      } else if (ch == '[') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore++;
        }
      } else if (ch == ')') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
      } else if (ch == '}') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
      } else if (ch == ']') {
        if ((quoteScore % 2 == 0) && (quoteScoreSingle % 2 == 0)) {
          braceScore--;
        }
      } else if (ch == '"') {
        quoteScore++;
      } else if (ch == '\'') {
        quoteScoreSingle++;
      }
    }
    return (braceScore == 0);
  }

  // check first if line is empty!

  /**
   * Count the number of whitespaces before the first alphanumeric character.
   * This is important for Python, because indentation matters in Python!
   *
   * Whitespace characters including the space character, are \t, \r and \n
   *
   * @param line full String line.
   * @return number of whitespace characters before first alphanumeric character
   */
  static int whitespaceCounter(String line) {
    int whitespaces = 0;
    for (int i = 0; i < line.length(); i++) {
      char character = line.charAt(i);
      if (character == ' ' || character == '\t' || character == '\r' || character == '\n') {
        whitespaces++;
      } else {
        return whitespaces; // return number if non-whitespace char is reached
      }
    }
    return whitespaces;  // failsafe
  }

  /**
   * Remove whitespace chracters from the line
   * Note that newlines (\n) are not removed, for format reasons
   *
   * @param line Full string line
   * @return String line without any whitespace characters.
   */
  static String removeWhitespace(String line) {
    return line.replace(" ","").replace("\t","");
  }

  /**
   * Remove anthing after a : or after a =
   *
   * @param variable String
   * @return String without anything after a : or a =
   */
  static String removeUnecessaryStuffFromVariable(String variable) {
    if (variable.contains(":")) {
      variable = variable.substring(0, variable.indexOf(":"));
    }
    if (variable.contains("=")) {
      variable = variable.substring(0, variable.indexOf("="));
    }
    return variable;
  }


  /**
   * Remove any content within the [ and ] characters (braces)
   * Does not remove the actual [ and ] characters
   *
   * @param line input String line
   * @return String but with any content between [ and ] removed
   */
  static String removeBraces(String line) {

    int leftBracket = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (ch == '[') {
        leftBracket++;
      } else if (ch == ']') {
        leftBracket--;
      } else if (leftBracket == 0) {
        stringBuilder.append(ch);
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Remove any content within the " and " characters and
   * remove any content within the ' and ' characters 
   * Does not remove the actual " or " characters
   *
   * @return String but with any content between any " and between any ' removed
   */
  static String removeThingsInQuotes(String string) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;

    for(int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);

      if(inDoubleQuote) {
        if(ch == '"' && string.charAt(i-1) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(ch);
        }
      }
      else if(inSingleQuote) {
        if(ch == '\''&& string.charAt(i-1) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(ch);
        }
      }
      else {
        if(ch == '"') {
          inDoubleQuote = true;
          stringBuilder.append(ch);
        }
        else if(ch == '\'') {
          inSingleQuote = true;
          stringBuilder.append(ch);
        }
        else {
          stringBuilder.append(ch);
        }
      }
    }


    return stringBuilder.toString();
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
  /**
   * Count the number of that character in a given string
   *
   * @param string full string to be looked at
   * @param toBeCompared the character to be counted
   * @return the number of occurences of that character in that given string
   */
  static int countChars(String string, char toBeCompared) {
    int counter = 0;
    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;

    for(int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);


      if(inDoubleQuote) {
        if(ch == '"') {
          inDoubleQuote = false;
          if(ch == toBeCompared) {
            counter++;
          }
        }
      }
      else if(inSingleQuote) {
        if(ch == '\'') {
          inSingleQuote = false;
          if(ch == toBeCompared) {
            counter++;
          }
        }
      }
      else {
        if(ch == '"') {
          inDoubleQuote = true;
          if(ch == toBeCompared) {
            counter++;
          }
        }
        else if(ch == '\'') {
          inSingleQuote = true;
          if(ch == toBeCompared) {
            counter++;
          }
        }
        else {
          if(ch == toBeCompared) {
            counter++;
          }
        }
      }

    }
    return counter;
  }

  /**
   * Check if that line is NOT a one liner docstring.
   * A one liner docstring starts and ends with three quotes.
   * It would look like this:
   * """Documentation"""
   *
   * @param line String line to be checked
   * @return true if it is NOT a one liner docstring, otherwise false
   */
  static boolean isOneLinerDocStringInverted(String line) {
    // like this:         """docstring"""
    // first check number of "
    int numberOfP = 0;
    for(int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if(c == '"') {
        numberOfP++;
      }
    }

    Scanner firstWordScanner = new Scanner(line);
    if(firstWordScanner.hasNext()) {
      String word = firstWordScanner.next();
      if(word.startsWith("\"\"\"") && line.endsWith("\"\"\"") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      }
      else if(word.startsWith("r\"\"\"") && line.endsWith("\"\"\"") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      }
      else {
        firstWordScanner.close();
        return true;
      }
    }
    else {
      firstWordScanner.close();
      return true;
    }

    // handle single q
  }

  /**
   * Check if that line is a NOT one liner docstring.
   * A one liner docstring starts and ends with three quotes.
   * It would look like this:
   * '''Documentation'''
   *
   * @param line String line to be checked
   * @return true if it is a NOT one liner docstring, otherwise false
   */
  static boolean isOneLinerDocStringSingleInverted(String line) {
    // like this:         '''docstring'''
    // first check number of "
    int numberOfP = 0;
    for(int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if(c == '\'') {
        numberOfP++;
      }
    }

    Scanner firstWordScanner = new Scanner(line);
    if(firstWordScanner.hasNext()) {
      String word = firstWordScanner.next();
      if(word.startsWith("'''") && line.endsWith("'''") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      }
      else if(word.startsWith("r'''") && line.endsWith("'''") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      }
      else {
        firstWordScanner.close();
        return true;
      }
    }
    else {
      firstWordScanner.close();
      return true;
    }

    // handle single q
  }

  /**
   * Check if the line is an explicit multiline statement.
   * Such statements are always indicated by a backslash at the end of the line
   *
   * @param line Line to be checked
   * @param invert if true, invert the result
   * return true if the line ends with a backlash, therefore being an explicit multiline statement
   */
  static boolean isExplicitMultilineStatement(String line, boolean invert) {
    //TODO: Optimize

    line = line.replace(" ","").replace("\t","");
    if(!invert) {
      return line.endsWith("\\");
    }
    else {
      return !line.endsWith("\\");
    }
  }

  /**
   * Check if the line is in an implicit multiline statement, by looking at the number of
   * brackets in a line. If the number of left brackets is greater than the number of right brackets,
   * then it is in an implicit multiline statements 
   * 
   * @param line current line
   * @param existingCounter current number of left brackets that are not done yet
   * @return number of unfinished left brackets. If greater than 0, then it is in a multiline statement
   */
  static int checkStartOrEndMultilineStatement(String line, int existingCounter) {

    int numberOfLeftBracket = 0;
    int numberOfRightBracket = 0;
    int numberOfLeftLessBracket = 0;
    int numberOfRightLessBracket = 0;


    boolean inSingleQuote = false; // '
    boolean inDoubleQuote = false; // "

    // TODO: Maybe remove quotes alltogether? this is problematic with huge files right now
    // WARNING: TRIPLE """ must be handled separately!
    for(int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if(inSingleQuote) {
        if(c == '\'') {
          inSingleQuote = false;
        }
      }
      else if(inDoubleQuote) {
        if(c == '"') {
          inDoubleQuote = false;
        }
      }
      else {
        if(c == '\'') {
          inSingleQuote = true;
        }
        else if(c == '"') {
          inDoubleQuote = true;
        }

        if     (c == '(') {
          numberOfLeftBracket++;
        }
        else if(c == '{' ) {
          numberOfLeftBracket++;
        }
        else if(c == '[' ) {
          numberOfLeftBracket++;
        }
        if     (c == ')' ) {
          numberOfRightBracket++;
        }
        else if(c == '}' ) {
          numberOfRightBracket++;
        }
        else if(c == ']' ) {
          numberOfRightBracket++;
        }
      }
    }

    int result = existingCounter + numberOfLeftBracket + numberOfLeftLessBracket -
            numberOfRightBracket - numberOfRightLessBracket;
    if(result < 0) {
      result = 0;
    }

    return result;
  }

  /**
   * Remove a word from a single line
   *
   * @param line full String line
   * @param word word to be removed
   * @return string line without that word
   */
  static String removeWord(String line, String word) {
    if(line.startsWith(word+" ")) {
      line = line.substring(word.length());
    }
    if(line.endsWith(" "+word)) {
      line = line.substring(0,line.lastIndexOf(word));
    }
    if(line.contains("\t"+word+" ")) {
      line = line.replace("\t"+word+" "," ");
    }
    if(line.contains(" "+word+" ")) {
      line = line.replace(" "+word+" "," ");
    }
    return line;
  }

  /**
   * Combine multiple lines into one single line
   * Note that no further processing is done, whitespaces have to be removed manually
   *
   * @param multilineList List of Strings that have lines
   * @return single large String line.
   */
  static String combineLines(List<String> multilineList) {
    StringBuilder stringBuilder = new StringBuilder();
    for(String string : multilineList) {
      stringBuilder.append(string);
    }
    return stringBuilder.toString();
  }

  /**
   * Remove leading and trailing underscores
   *
   * @param name variable name
   * @return variable name without leading and trailing underscores
   */
  public static String removeUnderscores(String name) {
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
   * Check if the string is an assignment operator
   *
   * @param word string, a single word
   * @return true if it is an assignment operator
   */
  static boolean checkAssignmentOperatorWhitespace(String word) {
    return word.contains(" = ") || word.contains(" += ") || word.contains(" -= ") ||
            word.contains(" *= ") || word.contains(" /= ") || word.contains(" %= ") ||
            word.contains(" &= ") || word.contains(" |= ") || word.contains(" ^= ") ||
            word.contains(" >>= ") || word.contains(" <<= ") ||
            word.contains(" //= ") || word.contains(" &&= ");
  }

  public static void swapArray(String[] array, String highestPriorityString) {
    for(int i = 0; i < array.length; i++) {
      if(array[i].equals(highestPriorityString)) {
        // swap array position
        String helper = array[0];
        array[0] = array[i];
        array[i] = helper;
      }
    }
  }
}
