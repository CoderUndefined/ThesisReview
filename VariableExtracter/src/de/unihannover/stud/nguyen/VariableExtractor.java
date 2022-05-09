package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.unihannover.stud.nguyen.StringOperations.*;
import static de.unihannover.stud.nguyen.Utility.writeFile;


/**
 * Main class.
 * Used to get variable names (python only) out of the files
 */
public class VariableExtractor {

  static boolean checkIfNotInComment(String line) {
    boolean inSingle = false;
    boolean inDouble = false;

    for(int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if(inDouble){
        if(ch == '"') {
          inDouble = false;
        }
        else if(ch == '#') {
          return false;
        }
      }
      else if(inSingle) {
        if(ch == '\'') {
          inSingle = false;
        }
        else if(ch == '#') {
          return false;
        }
      }
      else {
        if(ch == '"') {
          inDouble = true;
        }
        else if(ch == '\'') {
          inSingle = true;
        }
      }
    }
    return true;
  }

  // clf -> word must have these three things. first letter always matches
  // word splitter: wordthing. use 2 words first. simply try to split and see if it works


  /**
   * Get the variable out of the single line
   * Output is saved to a stringLongMap
   *
   * @param line current line
   * @param stringLongMap string map where to put it on with the number of occurences.
   * @param multiline true if it is a multiline statement
   * @return true if it is a multiline statement otherwise false
   */
  static boolean getVariable(String line, Map<String, Long> stringLongMap,
                             boolean multiline, String filename) {

    if(checkInvalidAssignment(line)) {
      return false;
    }

    if(line.contains("#") && checkIfNotInComment(line)) {
      line = line.substring(0,line.indexOf("#"));
    }

    // arguments in function definition
    Scanner dScanner = new Scanner(line);
    if(dScanner.hasNext()) {
      String first = dScanner.next();
      if((first.equals("def")) || multiline) {
        if(!multiline) {
          line = line.substring(line.indexOf("(")+1);
        }

        Scanner defScanner = new Scanner(line);
        defScanner.useDelimiter(",");
        while(defScanner.hasNext()) {
          String s = removeStuff(defScanner.next(),true);
          while(s.contains(" ")) {
            s = s.replace(" ","");
          }
          while(s.contains("\t")) {
            s = s.replace("\t","");
          }

          if(s.contains("=")) {
            s = s.substring(0,s.indexOf("="));
          }
          if(s.length() > 0 && checkIfItIsNotAKeyword(s) && !checkIfNumber(s)) {
            if(isASCII(s, filename) && StringOperations.checkValidVariableName(s) &&
                    !s.contains("`")&&!s.contains(".")&&!s.contains("\\")) {
              if(stringLongMap.containsKey(s)) {
                stringLongMap.put(s,stringLongMap.get(s)+1L);
              }
              else {
                stringLongMap.put(s,1L);
              }
            }
          }
        }
        defScanner.close();
        dScanner.close();
        line = line.replace(" ","");
        line = line.replace("\t","");

        return !line.endsWith(":");
      }
      else if(first.equals("async") && dScanner.hasNext()) {
        String secondString = dScanner.next();
        if(secondString.equals("with")) {
          return false;
        }
        else if(secondString.equals("def")) {

          //                System.out.println("before: "+line);
          line = line.substring(line.indexOf("(")+1);
//                System.out.println("after:  "+line);
          Scanner defScanner = new Scanner(line);
          defScanner.useDelimiter(",");
          while (defScanner.hasNext()) {
            String s = removeStuff(defScanner.next(), true);
            while (s.contains(" ")) {
              s = s.replace(" ", "");
            }
            while (s.contains("\t")) {
              s = s.replace("\t", "");
            }

            if (s.contains("=")) {
              s = s.substring(0, s.indexOf("="));
            }
            if (s.length() > 0 && checkIfItIsNotAKeyword(s) && !checkIfNumber(s)) {
              if (isASCII(s, filename) && StringOperations.checkValidVariableName(s) &&
                      !s.contains("`") && !s.contains(".") && !s.contains("\\")) {

                if (stringLongMap.containsKey(s)) {
                  stringLongMap.put(s, stringLongMap.get(s) + 1L);
                } else {
                  stringLongMap.put(s, 1L);
                }
              }
            }
          }
          defScanner.close();
          dScanner.close();
          line = line.replace(" ", "");
          line = line.replace("\t", "");

          return !line.endsWith(":");
        }
      }
    }

    dScanner.close();

    // guard condition 3...loop variables in for loop
    Scanner guardScanner = new Scanner(line);
    boolean condition = false;
    boolean inForLoop = false;
    String forVariable = "";
    for (int idx = 0; guardScanner.hasNext(); idx++) {
      String word = guardScanner.next();
      if(idx == 0 && word.equals("for")) {
        inForLoop = true;
      }
      if(idx == 1 && inForLoop) {
        forVariable = word;
      }
      if(idx == 2 && inForLoop && word.equals("in")) {
        String s = removeStuff(forVariable, false);
        if(isASCII(s, filename) && StringOperations.checkValidVariableName(s) &&
                !s.contains("`") && !s.contains(".") && !s.contains("\\")) {

          if(stringLongMap.containsKey(s)) {
            stringLongMap.put(s,stringLongMap.get(s)+1L);
          }
          else {
            stringLongMap.put(s,1L);
          }
        }
        guardScanner.close();
        return false;
      }

      if (checkAssignmentOperator(word)) {
        condition = true;
        break;
      }
    }
    guardScanner.close();

    if(!condition) {
      return false;
    }


    // non for variables
    LinkedList<String> list = new LinkedList<>();
    LinkedList<String> out  = new LinkedList<>();

    line = getLeftHandSideVariableDeclaration(line);
    assert line != null;
    if(line.contains(":")) {
      line = line.substring(0, line.indexOf(":"));
    }
    if(line.equals(")") || line.equals("]")) {
      return false;
    }
    if(line.contains("[")) {
      line = line.substring(0, line.indexOf("["));
    }

    Scanner scanner = new Scanner(line);
    boolean b = false;
    while(scanner.hasNext()) {
      // ignore >>>
      list.add(scanner.next());
      if(list.getLast().equals(">>>")) {
        b = false;
      }
      else if(list.getLast().equals("assert")) {
        b = false;
        break; // stop right there
      }
      else if(list.getLast().contains("[") && list.getLast().contains(",")) {
        b = true;
        out.add(removeStuff(list.getLast(),false));
        break;
      }
      else if(checkAssignmentOperator(list.getLast())) {
        b = true;
        break;
      }
      else if(list.getLast().endsWith(",")) {
        b = true;
        out.add(removeStuff(list.getLast(),false));
      }
      else if(list.getLast().endsWith(":")) {
        b = true;
        out.add(removeStuff(list.getLast(),false));
        break;
      }
      else {
        b = true;
        out.add(removeStuff(list.getLast(),false));
      }

    }
    if(b && out.size() > 0) {

      for(String key : out) {
        if(isASCII(key, filename) &&  StringOperations.checkValidVariableName(key)
                && !key.contains("`")
                && !key.contains(".") && !key.contains("\\")) {

          if(checkIfItIsNotAKeyword(key)) {

            if(stringLongMap.containsKey(key)) {
              stringLongMap.put(key,stringLongMap.get(key)+1L);
            }
            else {
              stringLongMap.put(key,1L);
            }
          }
        }
      }
    }

    scanner.close();
    return false;
  }

  static Set<String> nonASCIIFiles = new HashSet<>();

  static boolean isASCII(String word, String filename) {
    if(nonASCIIFiles == null) {
      nonASCIIFiles = new HashSet<>();
    }


    for(int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);
      if(ch >= 128) {
        int originalLength = ORIGIN_DIRECTORY.length();
        filename = filename.substring(originalLength);
        if(filename.startsWith(File.separator)) {
          filename = filename.substring(1);
        }
        filename = filename.substring(0,filename.indexOf(File.separator,filename.indexOf(File.separator)+1));

        nonASCIIFiles.add(filename);
        return false;
      }
    }
    return true;
  }

  static boolean isASCIIBasic(String word) {
    for(int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);
      if(ch >= 128) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get the line out of a single file to be examined for variables
   *
   * @param file current file
   * @param stringLongMap stringLongMap. Map with variables and number of occurences
   */
  static void getLine(File file, Map<String, Long> stringLongMap) {
    String text = Utility.readFile(file.getAbsolutePath());
    if(text == null) {
      return;
    }

    text = removeQuotes(text);
    Scanner scanner = new Scanner(text);

    boolean multiline = false;

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.length() > 0) {
        Scanner lineScanner = new Scanner(line);
        if(lineScanner.hasNext()) {
          String firstWord = lineScanner.next();
          if(!firstWord.contains("#")) {
            multiline = getVariable(line,stringLongMap,multiline,file.getAbsolutePath());
          }
        }
        lineScanner.close();
      }
    }
    scanner.close();
  }

  /**
   * Recursive function to traverse directories
   * If directory is found, call this again
   * If .py file is found examine its lines for variables
   * Returns stringLong map
   *
   * @param directory current directory
   * @param stringLongMap stringLongMap. Map with variables and number of occurences
   */
  public static void directoryTraversal(List<File> directory, Map<String, Long> stringLongMap) {
    for(File file : directory) {
      if(file.isDirectory()) {
        if(!file.getName().equals("venv") &&
                !file.getName().equals(".git") &&
                !file.getName().equals(".github") &&
                !file.getName().equals("__pycache__") &&
                Objects.requireNonNull(file.listFiles()).length > 0 &&
                file.canRead() &&
                !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {
//          System.out.println(file.getAbsolutePath());
          directoryTraversal(
                  Arrays.asList(Objects.requireNonNull(file.listFiles())), stringLongMap);
        }
      }
      else if(file.isFile() && file.getAbsolutePath().endsWith(".py")) {
//                System.out.println(file.getAbsolutePath());
//        if(!Utility.tooManyLines(file)) {
          getLine(file, stringLongMap);
//        }
      }
    }
  }


  /**
   * Function to start parallel directory traversal
   *
   * @param origin starting directory.
   * @param bigMap bigMap. Map with variables and number of occurences
   */
  static void zeroLevel(File origin, Map<String, Long> bigMap) {
    int cpuThreads = Runtime.getRuntime().availableProcessors()-1;
    if(cpuThreads <= 0) {
      cpuThreads = 1; // someone needs help if they still have a single thread CPU
    }
//    cpuThreads = 1;


    List<List<File>> multiDimList = new ArrayList<>();
    for(int i = 0; i < cpuThreads; i++) {
      multiDimList.add(new ArrayList<>());
    }

    int cpuThreadIndex = 0;
    for(File file : Objects.requireNonNull(origin.listFiles())) {
      if(file.canRead()) {
        multiDimList.get(cpuThreadIndex).add(file);

        if (cpuThreadIndex == cpuThreads - 1) {
          cpuThreadIndex = 0;
        } else {
          cpuThreadIndex++;
        }
      }
    }

    // parallel start
    int originalNumberOfThreads = Thread.activeCount();

    List<TraversalThread> traversalThreadList = new ArrayList<>();
    for(int i = 0; i < cpuThreads; i++) {
      Map<String, Long> map = new HashMap<>();
      TraversalThread thread = new TraversalThread(multiDimList.get(i),map);
      traversalThreadList.add(thread);
      thread.start();
    }

    // checking if it is running
    for(;;) {
      boolean isNotRunning = true;

      for(TraversalThread thread : traversalThreadList) {
        if (thread.isRunning()) {
          isNotRunning = false;
          break;
        }
      }

      int currentCount = Thread.activeCount();

      if(isNotRunning) {
        break;
      }
      else if(currentCount <= originalNumberOfThreads) {
        break;
      }
      else {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    for(TraversalThread thread : traversalThreadList) {
      for (String string : thread.stringLongMap.keySet()) {
        if(!bigMap.containsKey(string)) {
          bigMap.put(string,thread.stringLongMap.get(string));
        }
        else {
          bigMap.put(string,
                  thread.stringLongMap.get(string)
                          +bigMap.get(string));
        }
      }
    }
  }

  /**
   * Count the number off all variables in the stringLongMap
   *
   * @param stringLongMap stringLongMap. Map with variables and number of occurences
   * @return total number of variables.
   */
  static long countVariables(Map<String, Long> stringLongMap) {
    long count = 0;
    for(String string : stringLongMap.keySet()) {
      count = count + stringLongMap.get(string);
    }
    return count;
  }

  static String ORIGIN_DIRECTORY;

  // full generation from python corpus
  // python corpus =>

  /**
   * Main function. Initialization and input/output only.
   *
   * @param args Program arguments, unused.
   */
  public static void main(String[] args) {

    if (args.length == 4 &&
            (args[0].equals("--generateAbbreviationMap") ||
                    args[0].equals("--generateAbbreviationMapHalf") ||
                    args[0].equals("--generateAbbreviationMapQuarter") ||
                    args[0].equals("--generateAbbreviationMapEighth") ||
                    args[0].equals("--generateAbbreviationMapWithDocs"))) {
      String dictionaryFilePath = args[1];
      String originDirectory = args[2];
      String outputAbbreviationMapFilename = args[3];
      if(args[0].equals("--generateAbbreviationMapWithDocs")) {
        Global.commentsEnabled = true;
        DirectoryMarker.markAsReadable(new File(originDirectory));
      }
      else if(args[0].equals("--generateAbbreviationMapHalf")) {
        DirectoryMarker.markSomeAsUnreadable(new File(originDirectory),2);
      }
      else if(args[0].equals("--generateAbbreviationMapQuarter")) {
        DirectoryMarker.markSomeAsUnreadable(new File(originDirectory),4);
      }
      else if(args[0].equals("--generateAbbreviationMapEighth")) {
        DirectoryMarker.markSomeAsUnreadable(new File(originDirectory),8);
      }
      else {
        DirectoryMarker.markAsReadable(new File(originDirectory));
      }




      Global.globalDictionaryPath = dictionaryFilePath;
      Global.globalAbrPath = outputAbbreviationMapFilename;

      ORIGIN_DIRECTORY = originDirectory;

      File origin = new File(originDirectory);
      if(!origin.isDirectory()) {
        System.out.println("Repositories are not a directory");
        return;
      }

      File tempDirectory = new File(new File("").getAbsolutePath() +
              File.separator + "temp");
      if (!tempDirectory.exists()) {
        boolean check = tempDirectory.mkdir();
        if (!check) {
          System.out.println("Directory generation failed");
          return;
        }
      }

      File tempFolderDirectory = new File(new File("").getAbsolutePath() +
              File.separator + "tempResults");
      if (!tempFolderDirectory.exists()) {
        boolean check = tempFolderDirectory.mkdir();
        if (!check) {
          System.out.println("Directory generation failed");
          return;
        }
      }

      StringOperations.initStringChecker(dictionaryFilePath);

      HashMap<String, Long> stringLongMap = new HashMap<>();
      zeroLevel(new File(originDirectory), stringLongMap);
      SortedMappingList unclassifiedMappingList = new SortedMappingList(stringLongMap);

      // no abbreviations!
      ClassificationMap classificationMap = new ClassificationMap();
      classificationMap.classify(unclassifiedMappingList, false);
      classificationMap.printClassificationMap(countVariables(stringLongMap));

      // this file is important!
      String tempFilePath = tempDirectory.getAbsolutePath() + File.separator + "temp.txt";

      writeFile(tempFilePath,
              unclassifiedMappingList.toString(classificationMap,
                      false, false, true, false));

      writeFile(tempFilePath + "A",
              unclassifiedMappingList.toString());
      writeFile(tempFilePath + "ANonEnglish",
              unclassifiedMappingList.toString(classificationMap, true, false, false, false));
      writeFile(tempFilePath + "APartial",
              unclassifiedMappingList.toString(classificationMap, false, true, false, false));
      writeFile(tempFilePath + "AWeak",
              unclassifiedMappingList.toString(classificationMap, false, false, false, false));
      writeFile(tempFilePath + "ANoSingleLetterVars",
              unclassifiedMappingList.toString(classificationMap, false, false, true, false));


      // first round
      AbbreviationChecker.metafunction(new File(originDirectory),
              tempFilePath,
              tempFolderDirectory.getAbsolutePath());

      globalAbbreviationTree = new AbbreviationTree();
      AbbreviationSummer.call(tempFolderDirectory.getAbsolutePath(), "firstRound.txt");


      ORIGIN_DIRECTORY = originDirectory;

      StringOperations.initStringChecker(dictionaryFilePath, "firstRound.txt");
      RecursiveResolver.sortByEnglishWords("firstRound.txt");
      StringOperations.initStringChecker(dictionaryFilePath, "secondRound.txt");
      RecursiveResolver.resolve("secondRound.txt");
      RecursiveResolver.resolve("thirdRound.txt");
      RecursiveResolver.removeRepeatWords("thirdRound.txt");

      RecursiveResolver.sortTheOutputFile("thirdRound.txt");

      StringOperations.dictionary = null;
      StringOperations.dictionarySet = null;
      globalAbbreviationTree = null;
      StringOperations.initStringChecker(dictionaryFilePath, "fourthRound.txt");

      RecursiveResolver.resolveMultiWordNonEnglishWords("fourthRound.txt", "temp/temp.txt");
      RecursiveResolver.checkSuffix("fifthRound.txt");
      RecursiveResolver.combineLines("fifthRound.txt");
      RecursiveResolver.removeExcessStuff("sixthRound.txt");

      String sortedMapAsString = Utility.readFile("seventhRound.txt");
      Utility.writeFile(outputAbbreviationMapFilename, sortedMapAsString);


      DirectoryMarker.markAsReadable(new File(originDirectory));
    }

    else if (args.length == 3 && args[0].equals("--firstStepOnly")) {
      String dictionaryFilePath = args[1];
      String originDirectory = args[2];

      DirectoryMarker.markAsReadable(new File(originDirectory));
      Global.globalDictionaryPath = dictionaryFilePath;

      ORIGIN_DIRECTORY = originDirectory;
      File origin = new File(originDirectory);
      if(!origin.isDirectory()) {
        System.out.println("Repositories are not a directory");
        return;
      }

      File tempDirectory = new File(new File("").getAbsolutePath() +
              File.separator + origin.getName());
      if (!tempDirectory.exists()) {
        boolean check = tempDirectory.mkdir();
        if (!check) {
          System.out.println("Directory generation failed");
          return;
        }
      }

      File tempFolderDirectory = new File(new File("").getAbsolutePath() +
              File.separator + "tempResults");
      if (!tempFolderDirectory.exists()) {
        boolean check = tempFolderDirectory.mkdir();
        if (!check) {
          System.out.println("Directory generation failed");
          return;
        }
      }

      StringOperations.initStringChecker(dictionaryFilePath);

      HashMap<String, Long> stringLongMap = new HashMap<>();
      zeroLevel(new File(originDirectory), stringLongMap);
      SortedMappingList unclassifiedMappingList = new SortedMappingList(stringLongMap);

      // no abbreviations!
      ClassificationMap classificationMap = new ClassificationMap();
      classificationMap.classify(unclassifiedMappingList, false);
      classificationMap.printClassificationMap(countVariables(stringLongMap));

      // this file is important!
      String tempFilePath = tempDirectory.getAbsolutePath() + File.separator + "temp.txt";

      writeFile(tempFilePath,
              unclassifiedMappingList.toString());

      writeFile(tempDirectory.getAbsolutePath()+File.separator+"NoSingleLetterVars.txt",
              unclassifiedMappingList.toString(classificationMap,false,false,true,false));
      String[] strings =
              {tempFilePath,
                      tempFilePath.substring(0,tempFilePath.length()-8)+"percentages.txt"};
      PercentageCalculator.call(strings);
      DirectoryMarker.markAsReadable(new File(originDirectory));
   }

    else if(args.length == 4 && args[0].equals("--generateOriginalFiles")) {


      String dictionaryFilePath = args[1];
      String originDirectory = args[2];
      String abbreviationMapFilename = args[3];

      DirectoryMarker.markAsReadable(new File(originDirectory));
      ORIGIN_DIRECTORY = originDirectory;

      File tempDirectory = new File(new File("").getAbsolutePath() +
              File.separator+"original"+originDirectory.substring(originDirectory.lastIndexOf(File.separator)+1));
      if (!tempDirectory.exists()) {
        boolean check = tempDirectory.mkdir();
        if(!check) {
          System.out.println("Directory generation failed");
          return;
        }
      }

      StringOperations.initStringChecker(dictionaryFilePath, abbreviationMapFilename);

      HashMap<String, Long> stringLongMap = new HashMap<>();
      zeroLevel(new File(originDirectory), stringLongMap);
      SortedMappingList unclassifiedMappingList = new SortedMappingList(stringLongMap);

      ClassificationMap classificationMap = new ClassificationMap();
      classificationMap.classify(unclassifiedMappingList, true);
      classificationMap.printClassificationMap(countVariables(stringLongMap));

      writeFile(tempDirectory.getAbsolutePath()+File.separator+"A.txt",
              unclassifiedMappingList.toString());
      writeFile(tempDirectory.getAbsolutePath()+File.separator+"ANonEnglish.txt",
              unclassifiedMappingList.toString(classificationMap,true,false,false,false));
      writeFile(tempDirectory.getAbsolutePath()+File.separator+"APartial.txt",
              unclassifiedMappingList.toString(classificationMap,false,true,false,false));
      writeFile(tempDirectory.getAbsolutePath()+File.separator+"AWeak.txt",
              unclassifiedMappingList.toString(classificationMap,false,false,false,false));
      writeFile(tempDirectory.getAbsolutePath()+File.separator+"ANoSingleLetterVars.txt",
              unclassifiedMappingList.toString(classificationMap,false,false,true,false));


      DirectoryMarker.markAsReadable(new File(originDirectory));

    }


    // do not use
    else if(args.length == 4 && args[0].equals("--experimental")) {
      String dictionaryFilePath = args[1];
      String originDirectory = args[2];
      String outputAbbreviationMapFilename = args[3];


      Global.globalDictionaryPath = dictionaryFilePath;
      Global.globalAbrPath = outputAbbreviationMapFilename;

      ORIGIN_DIRECTORY = originDirectory;

      StringOperations.initStringChecker(dictionaryFilePath, outputAbbreviationMapFilename);
      RecursiveResolver.sortByEnglishWords(outputAbbreviationMapFilename);
      StringOperations.initStringChecker(dictionaryFilePath, "secondRound.txt");

      RecursiveResolver.resolve("secondRound.txt");
      RecursiveResolver.resolve("thirdRound.txt");
      RecursiveResolver.removeRepeatWords("thirdRound.txt");
      RecursiveResolver.sortTheOutputFile("thirdRound.txt");

      StringOperations.dictionary = null;
      StringOperations.dictionarySet = null;
      globalAbbreviationTree = null;
      StringOperations.initStringChecker(dictionaryFilePath, "fourthRound.txt");

      RecursiveResolver.resolveMultiWordNonEnglishWords("fourthRound.txt","temp/temp.txt");
      RecursiveResolver.checkSuffix("fifthRound.txt") ;
      RecursiveResolver.combineLines("fifthRound.txt");
      RecursiveResolver.removeExcessStuff("sixthRound.txt");
      String sortedMapAsString = Utility.readFile("seventhRound.txt");
      Utility.writeFile("Fin_File.txt", sortedMapAsString);
    }

    

    else if(args.length == 2 && args[0].equals("--testAccuracy")) {
//      AbbreviationSummer.main(null);
      AbbreviationSummer.testAbbreviations2(args[1]);
    }


    else {
      System.out.println("Argument options: ");
      System.out.println("--generateAbbreviationMap " +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[OUTPUT ABBREVIATION FILE LOCATION] \n " +
              "Generate abbreviation map using the full dataset");

      System.out.println();
      System.out.println("--generateAbbreviationMapHalf " +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[OUTPUT ABBREVIATION FILE LOCATION] \n " +
              "Generate abbreviation map using 1/2 of the dataset size");

      System.out.println();
      System.out.println("--generateAbbreviationMapQuarter " +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[OUTPUT ABBREVIATION FILE LOCATION] \n " +
              "Generate abbreviation map using 1/4 of the dataset size");

      System.out.println();
      System.out.println("--generateAbbreviationMapEighth " +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[OUTPUT ABBREVIATION FILE LOCATION] \n " +
              "Generate abbreviation map using 1/8 of the dataset size");

      System.out.println();
      System.out.println("--generateAbbreviationMapWithDocs " +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[OUTPUT ABBREVIATION FILE LOCATION] \n " +
              "Generate abbreviation map using the full dataset with comments");

      System.out.println();
      System.out.println("--generateOriginalFiles" +
              "[DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] " +
              "[ABBREVIATION FILE LOCATION] \n " +
              "Generate original files for evaluation for this thesis");

      System.out.println();
      System.out.println("--firstStepOnly [DICTIONARY FILE LOCATION] [PYTHON DATASET LOCATION] \n" +
              "Generate evaluation files without using abbreviation tree");

      System.out.println();
      System.out.println("--testAccuracy [ABBREVIATION FILE]");
      System.out.println("Test the accuracy of this abbreviation file");

    }
  }
}