package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Basic utility methods
 */
class Utility {


  private static String readHelper(BufferedReader bufferedReader) {
    return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Read a text file
   * Loosely based of
   * https://stackoverflow.com/questions/16104616/using-bufferedreader-to-read-text-file
   *
   * @param filename File name of the file
   * @return Text of the file as string
   */
  public static String readFile(String filename) {
    File file = new File(filename);
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
              new FileInputStream(file.getAbsolutePath()), StandardCharsets.UTF_8));

      String str = readHelper(bufferedReader);
      bufferedReader.close();
      return str;
    }
    catch (IOException e) {
      e.printStackTrace();
      return  null;
    }
  }

  /**
   * Write text to a new file. If the file already exists, overwrite it
   *
   * @param filename File name of the new file
   * @param text     Text to be written to the file
   */
  static void writeFile(String filename, String text) {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      fileWriter.write(text);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  static String removeQuotes(String fullText) {
    boolean inSingleDocstring = false;
    boolean inDoubleDocstring = false;

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    boolean inSingleComment = false;

    int counter = 0;
    int escapecounter = 0;

    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < fullText.length(); i++) {

      if (counter > 0) {

      } else if (inDoubleDocstring) {

        if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i + 1) == '"' &&
                fullText.charAt(i + 2) == '"' && fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' && fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' && fullText.charAt(i - 5) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        } else if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i + 1) == '"' &&
                fullText.charAt(i + 2) == '"' && fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' && fullText.charAt(i - 3) != '\\') {
          inDoubleDocstring = false;
          counter = 3;
        } else if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '"' && fullText.charAt(i + 1) == '"' &&
                fullText.charAt(i + 2) == '"' && fullText.charAt(i - 1) != '\\') {


          inDoubleDocstring = false;
          counter = 3;
        }
      } else if (inSingleDocstring) {

        if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i + 1) == '\'' &&
                fullText.charAt(i + 2) == '\'' && fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' && fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' && fullText.charAt(i - 5) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        } else if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i + 1) == '\'' &&
                fullText.charAt(i + 2) == '\'' && fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' && fullText.charAt(i - 3) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        } else if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '\'' && fullText.charAt(i + 1) == '\'' &&
                fullText.charAt(i + 2) == '\'' && fullText.charAt(i - 1) != '\\') {
          inSingleDocstring = false;
          counter = 3;
        }
      } else if (inDoubleQuote) {
        // backslash not an escape charcter and said backslash is escaped
        if (fullText.charAt(i) == '"' && i >= 9 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) == '\\' &&
                fullText.charAt(i - 6) == '\\' &&
                fullText.charAt(i - 7) == '\\' &&
                fullText.charAt(i - 8) == '\\' &&
                fullText.charAt(i - 9) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '"' && i >= 7 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) == '\\' &&
                fullText.charAt(i - 6) == '\\' &&
                fullText.charAt(i - 7) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '"' && i >= 5 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '"' && i >= 3 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '"' && fullText.charAt(i - 1) != '\\') {
          inDoubleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
      } else if (inSingleQuote) {
        if (fullText.charAt(i) == '\'' && i >= 9 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) == '\\' &&
                fullText.charAt(i - 6) == '\\' &&
                fullText.charAt(i - 7) == '\\' &&
                fullText.charAt(i - 8) == '\\' &&
                fullText.charAt(i - 9) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '\'' && i >= 7 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) == '\\' &&
                fullText.charAt(i - 6) == '\\' &&
                fullText.charAt(i - 7) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '\'' && i >= 5 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) == '\\' &&
                fullText.charAt(i - 4) == '\\' &&
                fullText.charAt(i - 5) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '\'' && i >= 3 &&
                fullText.charAt(i - 1) == '\\' &&
                fullText.charAt(i - 2) == '\\' &&
                fullText.charAt(i - 3) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '\'' && fullText.charAt(i - 1) != '\\') {
          inSingleQuote = false;
          stringBuilder.append(fullText.charAt(i));
        }
      } else if (inSingleComment) {
        if (fullText.charAt(i) == '\n') {
          inSingleComment = false;
        }
      } else {
//        System.out.println(i+": "+(i+2+1));
//        System.out.println("A: "+fullText.length());


        if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '"' &&
                fullText.charAt(i + 1) == '"' &&
                fullText.charAt(i + 2) == '"') {
          inDoubleDocstring = true;
          counter = 3;
        } else if ((i + 2 < fullText.length()) &&
                fullText.charAt(i) == '\'' &&
                fullText.charAt(i + 1) == '\'' &&
                fullText.charAt(i + 2) == '\'') {
          inSingleDocstring = true;
          counter = 3;
        } else if (fullText.charAt(i) == '"') {
          inDoubleQuote = true;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '\'') {
          inSingleQuote = true;
          stringBuilder.append(fullText.charAt(i));
        } else if (fullText.charAt(i) == '#') {
          inSingleComment = true;
        } else {
          stringBuilder.append(fullText.charAt(i));

        }
      }


      counter = counter - 1;
      if (counter < 0) {
        counter = 0;
      }
    }
    return stringBuilder.toString();
  }

}
