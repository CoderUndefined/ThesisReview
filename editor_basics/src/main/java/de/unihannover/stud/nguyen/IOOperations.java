package de.unihannover.stud.nguyen;

import org.intellij.sdk.editor.Global;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static de.unihannover.stud.nguyen.IOOperations.loadGraphList;

/**
 * Class containing all functions that require reading and writing from the hard drive
 */
public class IOOperations {


  final static int SIZE = 10000;

  private static String readHelper(BufferedReader bufferedReader) {
    return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Read a file from the hard drive
   * Loosely based of
   * https://stackoverflow.com/questions/16104616/using-bufferedreader-to-read-text-file
   *
   * @param file File to be read from the hard drive
   * @return String representation of the file
   */
  public static String readFile(File file) {

    // TODO: Optimize
//    try {
//      StringBuilder stringBuilder = new StringBuilder();
//      Scanner scanner = new Scanner(new FileReader(file.getAbsolutePath()));
//      while(scanner.hasNextLine()) {
//        stringBuilder.append(scanner.nextLine()).append("\n");
//      }
//      scanner.close();
//      return stringBuilder.toString();
//    }
//    catch (FileNotFoundException e) {
//      e.printStackTrace();
//      return null;
//    }

    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
              new FileInputStream(file.getAbsolutePath()), StandardCharsets.UTF_8));

      String str = readHelper(bufferedReader);
      bufferedReader.close();
      return str;
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
//
//    try {
//      // TODO: MalformedInputException, maybe some kind of UTF-8 thingy/
//      return Files.readString(Path.of(file.getAbsolutePath()));
//    } catch (IOException e) {
//      e.printStackTrace();
//      return null;
//    }
  }

  /**
   * Write a single file to the hard drive
   *
   * @param filename Target destination of the file
   * @param text Text to be saved in the file.
   */
  public static void writeFile(String filename, String text) {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      fileWriter.write(text);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a single dir
   *
   * @param dirName name of directory to be created
   */
  public static void createDir(String dirName) {
    File dir = new File(dirName);
    if (!dir.exists()) {
      boolean b = dir.mkdir();
//      if (!b) {
//        System.out.println("ERROR IN CREATING DIR");
//      }
    }
  }

  public static void saveGraphList(List<GraphOrigin> graphOriginList,
                                   String filename, boolean caching) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    for (GraphOrigin graphOrigin : graphOriginList) {
      if(caching) {
        String shorterName = graphOrigin.getFunctionName();
        shorterName = StringOperations.toSnakeCase(shorterName,true);
        shorterName = StringOperations.trimName(shorterName);

        if (!shorterName.equals("init") &&
                !shorterName.equals("[global]") &&
                !shorterName.equals("test") &&
                !shorterName.equals("get") &&
                !shorterName.equals("set")) {
                  continue;
        }
      }


      stringBuilder.append("[VAR]: ").append(graphOrigin.variable).append("\n");
      for (Relation relation : graphOrigin.relationList) {
        stringBuilder.append(relation.relationEnum);
        stringBuilder.append(";");
        stringBuilder.append(relation.destination).append("\n");
      }
      if(i % 100000 == 0 && i > 0) {
        int j = i / 100000;
        writeFile(filename+j+".txt",stringBuilder.toString());
        stringBuilder = new StringBuilder();
      }
      else if(i == graphOriginList.size()-1) {
        int k = i % 100000;
        k = 100000 - k;
        int tempIndex = i + k;
        tempIndex = tempIndex / 100000;
        writeFile(filename+tempIndex+".txt",stringBuilder.toString());
        break;
      }
      i++;
    }
  }

  // possibly change this into this?
  // Map<Integer, Map<String, List<GraphOrigin>>>
  //     GraphSize, FunctionName, (graphs)



  private static void insertToMap(GraphOrigin graphOrigin, boolean hasInSameFunction,
                                  Map<Integer, Map<String, List<GraphOrigin>>> ultraMap,
                                  String function) {
    function = StringOperations.toSnakeCase(function, true);
    function = StringOperations.trimName(function);
    if(function.length() == 0) {
      return;
    }


    int size = graphOrigin.relationList.size();
    if(!hasInSameFunction) {
      size = -size;
    }

    if (!ultraMap.containsKey(size)) {
      ultraMap.put(size, new HashMap<>());
    }
    if(!ultraMap.get(size).containsKey(function)) {
      ultraMap.get(size).put(function, new ArrayList<>());
    }
    ultraMap.get(size).get(function).add(graphOrigin);

    // split function name up into multiple names
    // convert into snake_case if necessary

    Scanner wordScanner = new Scanner(function);
    wordScanner.useDelimiter("_");
    while (wordScanner.hasNext()) {
      String part = wordScanner.next();
      if (!ultraMap.containsKey(size)) {
        ultraMap.put(size, new HashMap<>());
      }
      if(!ultraMap.get(size).containsKey(part)) {
        ultraMap.get(size).put(part, new ArrayList<>());
      }
      ultraMap.get(size).get(part).add(graphOrigin);
    }
    wordScanner.close();

  }
  
  /**
   * Load 
   *
   * If transfer is true then it also saves that output. (SHOULD BE DISABLED RIGHT NOW) 
   *
   * Can be either run on its own or be multithreaded via LoaderThread and loaderFunction
   */
  public static Map<Integer, Map<String, List<GraphOrigin>>> loadGraphList(String filename,
                                                                           int start,
                                                                           int interval) {

    GraphOrigin graphOrigin = null;
    boolean hasInSameFunction = false;
    String function = "ABCDEFGHJ";
    Map<Integer, Map<String, List<GraphOrigin>>> ultraMap = new HashMap<>();

    for(int i = start;;i = i + interval) {
//      System.out.println(i);
      File file = new File(filename+i+".txt");
      if(!file.exists()) {
        if(i > 3) {
          System.out.println(filename);
          System.out.println("file does not exist. exit here: " + i);
        }
        break;
      }

      String text = readFile(file);
      if(text == null) {
        return null;
      }


      Scanner scanner = new Scanner(text);

      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if(line.startsWith("[VAR]: ")) {
          // put in
          if(graphOrigin != null) {
            insertToMap(graphOrigin,hasInSameFunction,ultraMap,function);
          }

          // start new here
          graphOrigin = new GraphOrigin(line.substring(7));
          hasInSameFunction = false;
          function = "ABCDEFGHJ";
        }
        else {
          RelationEnum relationEnum;
          String relationString = line.substring(0,line.indexOf(";"));
          switch (relationString) {
            case "ASSIGNMENT":
              relationEnum = RelationEnum.ASSIGNMENT;
              break;
            case "DIRECT_CALL":
              relationEnum = RelationEnum.DIRECT_CALL;
              break;
            case "METHOD_CALL":
              relationEnum = RelationEnum.METHOD_CALL;
              break;
            case "FIELD_ACCESS":
              relationEnum = RelationEnum.FIELD_ACCESS;
              break;
            case "FUNCTION_NAME":
              relationEnum = RelationEnum.FUNCTION_NAME;
              break;
            case "FILE_NAME":
              relationEnum = RelationEnum.FILE_NAME;
              break;
            case "IN_SAME_FUNCTION":
              relationEnum = RelationEnum.IN_SAME_FUNCTION;
              hasInSameFunction = true;
              break;
            default:
              relationEnum = null; // this should never happen
              break;
          }
          if(relationEnum == null) {
            continue;
          }

          String destination = line.substring(line.indexOf(";")+1);

          if(destination.equals("null")) {
            destination = null;
          }

          if(relationString.equals("FUNCTION_NAME")) {
            if(destination == null) {
              function = "null";
//              System.out.println(line);
            }
            else {
              function = destination;
            }
          }


          assert graphOrigin != null;
          graphOrigin.relationList.add(new Relation(relationEnum,destination));

        }
      }
      scanner.close();
    }

    assert graphOrigin != null;
    insertToMap(graphOrigin,hasInSameFunction,ultraMap,function);

    return ultraMap;
  }

  public static void export(Map<Integer,Map<String,List<GraphOrigin>>> ultraMap, File destination) {
    // Directories must be generated before this method is called!
    writeFile(destination.getAbsolutePath() + File.separator + "integrity.txt",
            Global.integrityString);


    for(Integer integer : ultraMap.keySet()) {
      File integerDir = new File(destination.getAbsolutePath() + File.separator + integer);
      if(!integerDir.exists()) {
        boolean mkdir = integerDir.mkdir();
      }

      for(String string : ultraMap.get(integer).keySet()) {
        if(string.length() >= 1 && string.length() < 60) {
          String firstChar;

          if(string.length() == 1) {
            firstChar = string.substring(0,1);
          }
          else if(string.length() == 2) {
            firstChar = string.substring(0,2);
          }
          else {
            firstChar = string.substring(0,3);
          }

          File partialDir = new File(destination.getAbsolutePath()+
                  File.separator+integer+File.separator+firstChar);
          if(!partialDir.exists()) {
            boolean mkdir = partialDir.mkdir();
          }

          File fullDir = new File(destination.getAbsolutePath()+
                  File.separator+integer+File.separator+firstChar+File.separator+string);
          if(!fullDir.exists()) {
            boolean mkdir = fullDir.mkdir();
          }
          saveGraphList(ultraMap.get(integer).get(string),
                  fullDir.getAbsolutePath() + File.separator+ "sc",false);
        }
      }
    }
  }

  // multithreaded implementation
  public static Map<Integer, Map<String, List<GraphOrigin>>> loaderFunction(String filename) {
    String parentDirString = filename.substring(0, filename.lastIndexOf(File.separator));
    System.out.println(parentDirString);
    File file = new File(parentDirString);
    int num = Objects.requireNonNull(file.listFiles()).length;

    int threads = Math.min(num, Runtime.getRuntime().availableProcessors()/2); // 2+ mins for 8; 5 mins for 1 thread

    int originalNumberOfThreads = Thread.activeCount();

    LoaderThread[] loaderThreads = new LoaderThread[threads];
    for(int i = 0; i < threads; i++) {
      loaderThreads[i] = new LoaderThread(filename, i+1, threads);
      loaderThreads[i].start();
    }

    for(;;) {
      boolean notRunning = true;
      for(int i = 0; i < threads; i++) {
        if (loaderThreads[i].isRunning()) {
          notRunning = false;
          break;
        }
      }

      int currentCount = Thread.activeCount();

      if(notRunning) {
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

    Map<Integer, Map<String, List<GraphOrigin>>> ultraMap = new HashMap<>();
    for(int numOfLeaves = -SIZE; numOfLeaves <= SIZE; numOfLeaves++) {
      ultraMap.put(numOfLeaves,new HashMap<>());
    }

    for(int threadIndex = 0; threadIndex < threads; threadIndex++) {
      for(int numOfLeaves = -SIZE; numOfLeaves <= SIZE; numOfLeaves++) {
        if(loaderThreads[threadIndex].getUltraMap().containsKey(numOfLeaves)) {
          for(String key : loaderThreads[threadIndex].getUltraMap().get(numOfLeaves).keySet()) {
            if(!ultraMap.get(numOfLeaves).containsKey(key)) {
              ultraMap.get(numOfLeaves).put(key,new ArrayList<>());
            }
            ultraMap.get(numOfLeaves).get(key).addAll(
                    loaderThreads[threadIndex].getUltraMap().get(numOfLeaves).get(key));
          }


//          integerListMap.get(numOfLeaves).addAll(
//                  loaderThreads[threadIndex].getUltraMap().get(numOfLeaves));
        }
      }
    }

    List<Integer> integerList = new ArrayList<>();
    for(int numOfLeaves : ultraMap.keySet()) {
      if(ultraMap.get(numOfLeaves).isEmpty()) {
        integerList.add(numOfLeaves);
      }
    }

    for(int i : integerList) {
      ultraMap.remove(i);
    }

    return ultraMap;
  }
}

/**
 * 
 */
class LoaderThread extends Thread {
  Map<Integer, Map<String, List<GraphOrigin>>> ultraMap;
  String filename;
  int start;
  int interval;
  boolean running;

  public LoaderThread(String filename, int start, int interval) {
    this.ultraMap = new HashMap<>();
    this.filename = filename;
    this.start = start;
    this.interval = interval;
    this.running = true;
  }

  public void run() {
    ultraMap = loadGraphList(filename, start, interval);
    running = false;
  }

  public Map<Integer, Map<String, List<GraphOrigin>>> getUltraMap() {
    return ultraMap;
  }

  public boolean isRunning() {
    return running;
  }
}
















