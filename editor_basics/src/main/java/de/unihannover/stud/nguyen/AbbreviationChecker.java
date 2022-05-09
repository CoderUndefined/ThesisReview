package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.unihannover.stud.nguyen.StringOperations.splitLineBetweenAssignmentOperator;
import static de.unihannover.stud.nguyen.StringOperations.toSnakeCase;

/**
 * Class that resolves abbreviations to full words, at least as far as possible
 */
public class AbbreviationChecker {

  public static String removeQuotes(String fullText) {
    boolean inSingleDocstring = false;
    boolean inDoubleDocstring = false;

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    boolean inSingleComment = false;

    int counter = 0;
    int escapecounter = 0;

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
    return stringBuilder.toString();
  }

  private static String simplify(String line) {
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
   * Check if the full word has every character from the abbreviation, in order
   * Full word must start with the first character of the abbreviation.
   * Every next character must be there too, in order.
   *
   * @param abbreviation abbreviation
   * @param string       potential word
   * @return true if word is the full word of the abbreviation, false otherwise
   */
  private static boolean hasEveryChar(String abbreviation, String string) {
    if (string.length() == 0) {
      return false;
    }

    if (abbreviation.charAt(0) != string.charAt(0)) {
      return false;
    }

    int charIndex = 0;
    for (int i = 0; i < abbreviation.length(); i++) {
      boolean hasChar = false;
      for (int j = charIndex; j < string.length(); j++) {
        if (abbreviation.charAt(i) == string.charAt(j)) {
          hasChar = true;
          charIndex = j + 1;
          break;
        }
      }
      if (!hasChar) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return value
   *
   * @param word         unregulated full word
   * @param abbreviation abbreviation
   * @param plural       true if abbreviation was a plural
   * @return full word of the abbreviation
   */
  private static String returnValue(String word, String abbreviation, boolean plural) {
    if (word.equals(abbreviation)) {
      return null;
    }
    if (word.startsWith(abbreviation + "_")) {
      return null;
    }
    if (word.endsWith("\\")) {
      word = word.substring(0, word.indexOf("\\"));
    }
    if (word.contains(":")) {
      word = word.substring(0, word.indexOf(":"));
    }
    if (word.contains("@")) {
      word = word.substring(0, word.indexOf("@"));
    }
    if (word.contains("!")) {
      word = word.substring(0, word.indexOf("!"));
    }
    while (word.contains("\"")) {
      word = word.substring(0, word.indexOf("\""));
    }
    while (word.contains("'")) {
      word = word.substring(0, word.indexOf("'"));
    }
    while (word.endsWith("0") || word.endsWith("1") || word.endsWith("2") ||
            word.endsWith("4") || word.endsWith("5") || word.endsWith("6") ||
            word.endsWith("7") || word.endsWith("8") || word.endsWith("9") || word.endsWith("3")) {
      word = word.substring(0, word.length() - 1);
    }


    if (word.endsWith("s") && !word.endsWith("ss") && !plural) {
      word = word.substring(0, word.length() - 1);
      return word;
    }

    // already in plural,
    if (word.endsWith("s")) {
      return word;
    }

    // in which case add the s/es back
    if (plural) {
      if (word.endsWith("y")) {
        word = word + "ies";
      } else if (word.endsWith("f") || word.endsWith("fe")) {
        word = word.substring(0, word.length() - 1) + "ves";
      } else if (word.endsWith("ch") || word.endsWith("s") || word.endsWith("sh") ||
              word.endsWith("x") || word.endsWith("z")) {
        word = word + "es";
      } else {
        word = word + "s";

      }
    }

    return word;
  }

  /**
   * Process right hand side of the line.
   *
   * @param line full line
   * @return abridged right side of the line
   */
  private static String processRightSide(String line) {
    String rightSide = line.substring(line.indexOf("=") + 1);
    rightSide = simplify(rightSide);
    rightSide = rightSide.replace(" or ", ",");
    rightSide = rightSide.replace(" and ", ",");
    rightSide = rightSide.replace(" not ", ",");
    rightSide = rightSide.replace(" ", "").replace("\t", "");
    return rightSide;
  }

  static Pattern globalPattern = Pattern.compile("[+\\-*/=%&|^~<>{}();\\[\\],. ]");

  private static String getFullWordFromLine(String abbreviation, String line) {
    if (line.contains(" if ")) {
      line = line.substring(0, line.indexOf(" if "));
    }

    if (line.contains(" else ")) {
      line = line.substring(0, line.indexOf(" else "));
    }

    if (!StringOperations.checkAssignmentOperatorWhitespace(line)) {
      return null;
    }

    String leftSide = Objects.requireNonNull(splitLineBetweenAssignmentOperator(line)).get(0);
    if (leftSide == null) {
      return null;
    }


    boolean plural = false;
    if (abbreviation.endsWith("s") && abbreviation.length() > 3) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 1);
      plural = true;
    }

    Pattern abbPattern = Pattern.compile("\\b" + abbreviation + "\\b");
    Matcher abbMatcher = abbPattern.matcher(leftSide);
    if (!abbMatcher.find()) {
      return null;
    }
    Pattern antipattern = Pattern.compile("\\b" + abbreviation + ".\\b");
    Matcher antimatcher = antipattern.matcher(leftSide);
    if (antimatcher.find()) {
      return null;
    }

    String rightSide = processRightSide(line);

    Scanner rightHandSideScanner = new Scanner(rightSide);
    if(globalPattern == null) {
      globalPattern = Pattern.compile("[+\\-*/=%&|^~<>{}();\\[\\],. ]");
    }

    rightHandSideScanner.useDelimiter(globalPattern);
    String returnvalue = null;


    // TODO: Optimize, turn List into Set
    while (rightHandSideScanner.hasNext()) {
      String word = rightHandSideScanner.next();
      if (!word.equals("")) {
        word = toSnakeCase(word, true);
        word = StringOperations.removeUnderscores(word);
//        System.out.println(word);
        // TODO: Soft word splitter

        Scanner wordScanner = new Scanner(word);
        wordScanner.useDelimiter("_");

        List<Character> firstLetters = new ArrayList<>();

        while (wordScanner.hasNext()) {
          String sub = wordScanner.next();
          if (sub.length() != 0) {
            firstLetters.add(sub.charAt(0));

            // check if starts with first char then check if every other char is there
            if (sub.startsWith(abbreviation) && !sub.equals(abbreviation)) {
              returnvalue = returnValue(sub, abbreviation, plural);
              break;
            }
            else if (hasEveryChar(abbreviation, sub)) {
              returnvalue = returnValue(sub, abbreviation, plural);
              break;
            }
          }
        }
        wordScanner.close();

//         does not work right now
        boolean exactAcronym = false;
        if (firstLetters.size() == abbreviation.length()) {
          exactAcronym = true;
          for (int i = 0; i < firstLetters.size(); i++) {
//            System.out.println(abbreviation.charAt(i) + " ?? " + firstLetters.get(i));
            if (abbreviation.charAt(i) != firstLetters.get(i)) {
              exactAcronym = false;
              break;
            }
          }
        }
        if (exactAcronym) {
          return returnValue(word, abbreviation, plural);
        }

        if (returnvalue != null) {
          return returnvalue;
        }
//         look at full word
        if (word.startsWith(abbreviation) && !word.equals(abbreviation)) {
          return returnValue(word, abbreviation, plural);
        }

        if (hasEveryChar(abbreviation, word)) {
          return returnValue(word, abbreviation, plural);
        }
      }
    }
    rightHandSideScanner.close();
    return null;
  }

  /**
   * Get the line out of a single file to be examined for variables
   *
   * @param file             current file
   * @param abbreviationList abbreviationList
   */
  public static void processLines(File file,
                                  List<String> abbreviationList,
                                  Map<String, Map<String, Integer>> megaMap) {
    String text = IOOperations.readFile(file);
    if (text == null) {
      return;
    }
//    System.out.println(text);


    text = removeQuotes(text); // very expensive operation
//    System.out.println(text);

    // 10 mins for 1 variable * 16 threads

    Scanner scanner = new Scanner(text);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (line.length() > 0) {
        for (String abbreviation : abbreviationList) {

          String word = getFullWordFromLine(abbreviation, line);

          if (!megaMap.containsKey(abbreviation)) {
            megaMap.put(abbreviation, new HashMap<>());
          }


          if (word != null && !word.equals(abbreviation) && word.length() > abbreviation.length()) {
            if (megaMap.get(abbreviation).containsKey(word)) {
              megaMap.get(abbreviation).replace(word, megaMap.get(abbreviation).get(word) + 1);
//              megaMap.replace(word,megaMap.get(word)+1);
            } else {
              megaMap.get(abbreviation).put(word, 1);
//              megaMap.put(abbreviation, (word,1));
            }
          }
        }
      }
    }
    scanner.close();

  }

//
//  private static void processComments(File file,
//                                      String abbreviation,
//                                      Map<String, Integer> stringIntegerMap) {
//    String text = Utility.readFile(file.getAbsolutePath());
//    if(text == null) {
//      return;
//    }
//    text = removeQuotesInverted(text);
//
//
//  }


}
