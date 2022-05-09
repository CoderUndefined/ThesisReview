package de.unihannover.stud.nguyen;

import java.io.*;
import java.util.Scanner;

/**
 * Calculates percentages, which usually is x out of the total number.
 */
public class PercentageCalculator {

  static long calculateTotalNumber(File file) {
    try {
      Scanner scanner = new Scanner(new FileReader(file));
      long count = 0;
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line = line.substring(line.indexOf(" => ")+4);
        long variableFrequency = Long.parseLong(line);
        count = count + variableFrequency;
      }
      scanner.close();
      return count;
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return -1;
    }
  }

  static String calculatePercentages(File file, long totalNumberLong) {
    try {
      double total = totalNumberLong;
      Scanner scanner = new Scanner(new FileReader(file));
      StringBuilder stringBuilder = new StringBuilder();
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String varName = line.substring(0, line.indexOf(" =>"));

        line = line.substring(line.indexOf(" => ")+4);

        double variableFrequency = Double.parseDouble(line);
        double percentage = variableFrequency / total;

        stringBuilder.append(varName).append(";").append(percentage).append("\n");
      }
      scanner.close();
      return stringBuilder.toString();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  static void writeFile(String filename, String text) {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      fileWriter.write(text);
      fileWriter.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  static boolean checkAlphabetic(String word) {
    int counter = 3;
    for(int i = 0; i < word.length(); i++) {
      char character = word.charAt(i);
      String charAsString = "" + character;

      if(StringOperations.isAlphabetic(charAsString)) {
        counter--;
      }
      if(counter == 0) {
        return true;
      }
    }
    return false;
  }

  static String filter(String string) {
    Scanner scanner = new Scanner(string);
    StringBuilder stringBuilder = new StringBuilder();


    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.substring(0, line.indexOf(" =>"));

      if(line.length() >= 3) {
        if(checkAlphabetic(line)) {
          stringBuilder.append(line).append("\n");
        }
      }
    }

    scanner.close();
    return stringBuilder.toString();
  }

  public static void call(String[] args) {

    File file = new File(args[0]);
    long totalNumberOfVariables = calculateTotalNumber(file);
    String outputString = calculatePercentages(file, totalNumberOfVariables);

    if(outputString != null) {
      writeFile(args[1], outputString);
    }

    String noSingleLetterVars = Utility.readFile(file.getAbsolutePath().substring(
            0,
            file.getAbsolutePath().lastIndexOf("/"))+File.separator+"NoSingleLetterVars.txt");
    writeFile(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("/"))
                    +File.separator+"ThreeOnly.txt" ,
            filter(noSingleLetterVars));
  }
//
//  public static void main(String[] args) {
//    if(args.length == 3 && args[0].equals("--calculatePercentages")) {
//      File file = new File(args[1]);
//      long totalNumberOfVariables = calculateTotalNumber(file);
//      String outputString = calculatePercentages(file, totalNumberOfVariables);
//
//      if(outputString != null) {
//        writeFile(args[2], outputString);
//      }
//    }
//    else {
//      System.out.println("Options:");
//      System.out.println("--caculatePercentages [INPUT FILE] [OUTPUT FILE]");
//    }
//    // write your code here
//  }
}
