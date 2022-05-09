package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Simple utility class for utility functions
 */
class Utility {
  /**
   * Merge two files to one string
   *
   * @param filename1 first filename
   * @param filename2 second filename
   * @return combined string
   */
  public static String mergeFiles(String filename1, String filename2) {
    String s1 = readFile(filename1);
    String s2 = readFile(filename2);

    return s1 + "\n" + s2;
  }

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

  public static boolean tooManyLines(File file) {
    try {
      String s = readFile(file.getAbsolutePath());

      int count = 0;
      for (int i = 0; i < Objects.requireNonNull(s).length(); i++) {
        if (s.charAt(i) == '\n') {
          count++;
          if (count > 10000) {
            return true;
          }
        }
      }
      return false;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Write a text file
   *
   * @param filename File name of the new file
   * @param string   String to be written to the new file
   */
  public static void writeFile(String filename, String string) {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      fileWriter.write(string);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Calculates the string distance between source and target strings using
   * the Damerau-Levenshtein algorithm. The distance is case-sensitive.
   *
   * @param source The source String.
   * @param target The target String.
   * @return The distance between source and target strings.
   * @throws IllegalArgumentException If either source or target is null.
   * @author crwohlfeil
   */
  public static int calculateDistance(CharSequence source, CharSequence target) {
    if (source == null || target == null) {
      throw new IllegalArgumentException("Parameter must not be null");
    }
    int sourceLength = source.length();
    int targetLength = target.length();
    if (sourceLength == 0) return targetLength;
    if (targetLength == 0) return sourceLength;
    int[][] dist = new int[sourceLength + 1][targetLength + 1];
    for (int i = 0; i < sourceLength + 1; i++) {
      dist[i][0] = i;
    }
    for (int j = 0; j < targetLength + 1; j++) {
      dist[0][j] = j;
    }
    for (int i = 1; i < sourceLength + 1; i++) {
      for (int j = 1; j < targetLength + 1; j++) {
        int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
        dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1),
                dist[i - 1][j - 1] + cost);
        if (i > 1 &&
                j > 1 &&
                source.charAt(i - 1) == target.charAt(j - 2) &&
                source.charAt(i - 2) == target.charAt(j - 1)) {
          dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
        }
      }
    }
    return dist[sourceLength][targetLength];
  }
}
