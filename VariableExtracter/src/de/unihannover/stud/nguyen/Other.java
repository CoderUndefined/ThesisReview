package de.unihannover.stud.nguyen;

import java.io.File;
import java.util.*;

import static de.unihannover.stud.nguyen.Utility.readFile;

class Other {


  /**
   * Count the number of whitespaces before the first alphanumeric character.
   * This is important for Python, because indentation matters in Python!
   * <p>
   * Whitespace characters including the space character, are \t, \r and \n
   *
   * @param line full String line.
   * @return number of whitespace characters before first alphanumeric character
   */
  private static int whitespaceCounter(String line) {
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
   * Check if the line is not empty. Comments to not count.
   *
   * @param line line to be checked
   * @return true if line is not empty, false otherwise
   */
  private static boolean isLineNotEmpty(String line) {
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
   * Check if that line is a NOT one liner docstring.
   * A one liner docstring starts and ends with three quotes.
   * It would look like this:
   * '''Documentation'''
   *
   * @param line String line to be checked
   * @return true if it is a NOT one liner docstring, otherwise false
   */
  private static boolean isOneLinerDocStringSingleInverted(String line) {
    // like this:         '''docstring'''
    // first check number of "
    int numberOfP = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '\'') {
        numberOfP++;
      }
    }

    Scanner firstWordScanner = new Scanner(line);
    if (firstWordScanner.hasNext()) {
      String word = firstWordScanner.next();
      if (word.startsWith("'''") && line.endsWith("'''") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      } else if (word.startsWith("r'''") && line.endsWith("'''") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      } else {
        firstWordScanner.close();
        return true;
      }
    } else {
      firstWordScanner.close();
      return true;
    }

    // handle single q
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
  private static boolean isOneLinerDocStringInverted(String line) {
    // like this:         """docstring"""
    // first check number of "
    int numberOfP = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '"') {
        numberOfP++;
      }
    }

    Scanner firstWordScanner = new Scanner(line);
    if (firstWordScanner.hasNext()) {
      String word = firstWordScanner.next();
      if (word.startsWith("\"\"\"") && line.endsWith("\"\"\"") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      } else if (word.startsWith("r\"\"\"") && line.endsWith("\"\"\"") &&
              numberOfP == 6) {
        firstWordScanner.close();
        return false;
      } else {
        firstWordScanner.close();
        return true;
      }
    } else {
      firstWordScanner.close();
      return true;
    }

    // handle single q
  }

  /**
   * Remove any Python comments that are in a single line of Python code
   * <p>
   * Example
   * Before: x = 0  # This is a comment
   * After:  x = 0
   *
   * @param line String line that might contain a single Python comment
   * @return String line without Python comment
   */
  private static String removeSingleComment(String line) {

    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;
    boolean singleComment = false;
    int index = 0;

    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);

      if (inDoubleQuote) {
        if (ch == '"' && line.charAt(i - 1) != '\\') {
          inDoubleQuote = false;
        }
      } else if (inSingleQuote) {
        if (ch == '\'' && line.charAt(i - 1) != '\\') {
          inSingleQuote = false;
        }
      } else {
        if (ch == '"') {
          inDoubleQuote = true;
        } else if (ch == '\'') {
          inSingleQuote = true;
        } else if (ch == '#') {
          singleComment = true;
          index = i;
          break;
        }
      }
    }

    if (singleComment) {
      return line.substring(0, index);
    } else {
      return line;
    }
  }

  /**
   * Check if the line is in an implicit multiline statement, by looking at the number of
   * brackets in a line. If the number of left brackets is greater than the number of right brackets,
   * then it is in an implicit multiline statements
   *
   * @param line            current line
   * @param existingCounter current number of left brackets that are not done yet
   * @return number of unfinished left brackets. If greater than 0, then it is in a multiline statement
   */
  private static int checkStartOrEndMultilineStatement(String line, int existingCounter) {

    int numberOfLeftBracket = 0;
    int numberOfRightBracket = 0;
    int numberOfLeftLessBracket = 0;
    int numberOfRightLessBracket = 0;


    boolean inSingleQuote = false; // '
    boolean inDoubleQuote = false; // "

    // TODO: Maybe remove quotes alltogether? this is problematic with huge files right now
    // WARNING: TRIPLE """ must be handled separately!
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (inSingleQuote) {
        if (c == '\'') {
          inSingleQuote = false;
        }
      } else if (inDoubleQuote) {
        if (c == '"') {
          inDoubleQuote = false;
        }
      } else {
        if (c == '\'') {
          inSingleQuote = true;
        } else if (c == '"') {
          inDoubleQuote = true;
        }

        if (c == '(') {
          numberOfLeftBracket++;
        } else if (c == '{') {
          numberOfLeftBracket++;
        } else if (c == '[') {
          numberOfLeftBracket++;
        }
        if (c == ')') {
          numberOfRightBracket++;
        } else if (c == '}') {
          numberOfRightBracket++;
        } else if (c == ']') {
          numberOfRightBracket++;
        }
      }
    }

    int result = existingCounter + numberOfLeftBracket + numberOfLeftLessBracket -
            numberOfRightBracket - numberOfRightLessBracket;
    if (result < 0) {
      result = 0;
    }

    return result;
  }

  /**
   * Check if the line is an explicit multiline statement.
   * Such statements are always indicated by a backslash at the end of the line
   *
   * @param line   Line to be checked
   * @param invert if true, invert the result
   *               return true if the line ends with a backlash, therefore being an explicit multiline statement
   */
  private static boolean isExplicitMultilineStatement(String line, boolean invert) {
    //TODO: Optimize

    line = line.replace(" ", "").replace("\t", "");
    if (!invert) {
      return line.endsWith("\\");
    } else {
      return !line.endsWith("\\");
    }
  }


  public static List<Integer> getWhitespaces(File file) {
    int explicitMultiline = 999999;
    String text = readFile(file.getAbsolutePath());
    if (text == null) {
      System.out.println("WARNING: Error reading file");
      return null;
    }

    Scanner scanner = new Scanner(text);
    Set<Integer> set = new HashSet<>();

    boolean blockCommentSingle = false;
    boolean blockComment = false;
//    boolean multiline = false;
    int multilinecounter = 0;

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = removeSingleComment(line);


      if (blockCommentSingle) {
        if (line.contains("'''")) {
          blockCommentSingle = false;
        }
      } else if (blockComment) {
        if (line.contains("\"\"\"")) {
          blockComment = false;
        }
      } else {
        if (multilinecounter == explicitMultiline) {
          if (isExplicitMultilineStatement(line, true)) {
            multilinecounter = 0;
          }

        } else if (multilinecounter > 0) {
          multilinecounter = checkStartOrEndMultilineStatement(line, multilinecounter);
        } else {

          if (isOneLinerDocStringInverted(line) && isOneLinerDocStringSingleInverted(line)) {
            if (line.contains("'''") || line.contains("r'''")) {
              blockCommentSingle = true;
            } else if (line.contains("\"\"\"") || line.contains("r\"\"\"")) {
              blockComment = true;
            } else {
              if (isExplicitMultilineStatement(line, false)) {
                multilinecounter = explicitMultiline;
              }
              if (multilinecounter != explicitMultiline) {
                multilinecounter = checkStartOrEndMultilineStatement(line, 0);
                if (isLineNotEmpty(line)) {
                  set.add(whitespaceCounter(line));
                }
              }
            }
          }

        }
      }

    }
    scanner.close();

    set.add(0);
    for (int i : set) {
      if (i < 0) {
        set.remove(i);
      }
    }

    List<Integer> list = new ArrayList<>(set);
    list.sort(Comparator.comparing(Integer::valueOf));
    if (list.size() >= 2) {
      // generate additional, just in case
      int diff = list.get(list.size() - 1) - list.get(list.size() - 2);
      list.add(list.get(list.size() - 1) + diff);
    }

    if (list.size() == 1) {
      list.add(4);
    }

    if (list.size() == 0) {
      list.add(0);
      list.add(4);
    }
    // manually rectify list, initially assume steps of 4

    list.sort(Comparator.comparing(Integer::valueOf));

    return list;
  }
}
