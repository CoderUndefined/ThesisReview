package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.unihannover.stud.nguyen.Utility.removeQuotes;


/**
 * Class that sums up all Pylint error codes and calculates the relative
 * frequency of them, divided by the total number of source lines of code.
 *
 * @author Kim Nguyen
 */
class WarningCodeCounter {

    Map<String, Integer> errorCodeTally;
    Map<String, Double> percentageTally;
    Map<String, Double> functionPercentageTally;

    double numberOfFunctions;
    double lineOfCodes;
    int totalTally;
    String resultFilename;
    String parent;
    String repo;
    String originDirectory;

    boolean countE = false;
    boolean inv = false;

    /**
     * Simpler constructor only containing the parent directory of the repository
     * This is just for summing up the error codes in total. Not the be actually used in other ways
     *
     * @param origin Parent directory of the repository directory
     */
    public WarningCodeCounter(String origin) {
        this.errorCodeTally = new HashMap<>();
        this.percentageTally = new HashMap<>();
        this.functionPercentageTally = new HashMap<>();
        this.numberOfFunctions = 0;
        this.lineOfCodes = 0;
        this.totalTally = 0;
        this.originDirectory = origin;
    }

    /**
     * Constructor method for the WarningCodeCounter
     *
     * @param resultFilename Single results text output from Pylint. Name should be author#repository.txt
     * @param origin Parent directory of the repository directory.
     */
    public WarningCodeCounter(String resultFilename, String origin, boolean notSummedUp,
                               boolean e, boolean i) {
        this.errorCodeTally = new HashMap<>();
        this.percentageTally = new HashMap<>();
        this.functionPercentageTally = new HashMap<>();
        this.numberOfFunctions = 0;
        this.lineOfCodes = 0;
        this.totalTally = 0;
        this.resultFilename = resultFilename;

        this.countE = e;
        this.inv = i;

        if(notSummedUp) {
            this.parent = resultFilename.substring(this.resultFilename.lastIndexOf("/") + 1,
                    this.resultFilename.lastIndexOf("#"));

            this.repo = resultFilename.substring(this.resultFilename.lastIndexOf("#") + 1,
                    this.resultFilename.lastIndexOf("."));


            this.originDirectory = origin + File.separator + parent + File.separator + repo;
        }
        else {
            this.originDirectory = origin;
        }
    }

    private void addToGlobalTally(boolean hasC0103,
                                  String fullPath,
                                  int temporaryTally,
                                  Set<String> filesWithC0103,
                                  Map<String, Integer> temporaryErrorTally) {

        if(hasC0103) {
            if(fullPath != null && !fullPath.equals("")) {
                for(String warningCode : temporaryErrorTally.keySet()) {
                    if (errorCodeTally.containsKey(warningCode)) {
                        errorCodeTally.put(warningCode,errorCodeTally.get(warningCode) +
                                temporaryErrorTally.get(warningCode));
                    }
                    else {
                        errorCodeTally.put(warningCode,
                                temporaryErrorTally.get(warningCode));
                    }
                }


                totalTally = totalTally + temporaryTally;
            }
        }
    }

    /**
     * Process a single result text file
     * Find error codes and add them up in a map.
     * Calculate the relative percentages at the end.
     */
    public void processResults(boolean countEverything, boolean inverted) {
        countEverything = countE;
        inverted = inv; // true without C0103, false with C0103 only

        Scanner scanner = new Scanner(Objects.requireNonNull(Utility.readFile(resultFilename)));

        boolean hasC0103 = false;
        boolean first = true;

        String currentFilePath = null;
        String fullPath = null;
        Set<String> filesWithC0103 = new HashSet<>();
        Map<String, Integer> temporaryErrorTally = new HashMap<>();
        int temporaryTally = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // REMOVE THIS ASAP
            if(line.contains("Similar lines in 2")) {
                break;
            }

            if(line.startsWith("**********") && first)  {
                first = false;
            }
            // also execute at the end
            else if(line.startsWith("**********") && !first) { // end of single file
                if((hasC0103 && !inverted) || countEverything) {
                    if(fullPath != null && !fullPath.equals("")) {
                        filesWithC0103.add(fullPath);
                        addToGlobalTally(true, fullPath, temporaryTally,
                                filesWithC0103, temporaryErrorTally);
                    }
                }
                else if(!hasC0103 && inverted) {
                    if(fullPath != null && !fullPath.equals("")) {
                        addToGlobalTally(true, fullPath, temporaryTally,
                                filesWithC0103, temporaryErrorTally);
                    }
                }
                else if(hasC0103 && inverted) {
                    filesWithC0103.add(fullPath);
                }
                currentFilePath = null;
                fullPath = null;
                temporaryErrorTally = new HashMap<>();
                temporaryTally = 0;
                hasC0103 = false;
            }

            if(line.contains(":")) {
                currentFilePath = line.substring(0, line.indexOf(":"));
                fullPath = originDirectory + File.separator + currentFilePath;
            }

            Pattern pattern = Pattern.compile("[CERFWI][0-9][0-9][0-9][0-9]");
            Matcher matcher = pattern.matcher(line);


            // put on temporary counter. Then add to real counter if C0103 exists
            if(matcher.find()) {

                if(line.contains("C0103")) {
                    hasC0103 = true;
                }

                line = matcher.group();

                if(temporaryErrorTally.containsKey(line)) {
                    temporaryErrorTally.put(line,temporaryErrorTally.get(line)+1);
                }
                else {
                    temporaryErrorTally.put(line,1);
                }
                temporaryTally++;
            }
        }
        scanner.close();

        if((hasC0103 && !inverted) || countEverything) {
            addToGlobalTally(true, fullPath, temporaryTally,
                    filesWithC0103, temporaryErrorTally);
        }
        else if(!hasC0103 && inverted) {
            addToGlobalTally(true, fullPath, temporaryTally,
                    filesWithC0103, temporaryErrorTally);
        }
        else if(hasC0103 && inverted) {
            filesWithC0103.add(fullPath);
        }

        if(!countEverything) {
            lineCountTraversal(new File(originDirectory), filesWithC0103, inverted); // CHANGE!!!
//            functionCountTraversal(new File(originDirectory), filesWithC0103, inverted);
        }
        else {
            lineCountTraversal(new File(originDirectory), null, false);
//            functionCountTraversal(new File(originDirectory), null, false);
        }
        calculatePercentages();

    }

    private String readHelper(BufferedReader bufferedReader) {
        return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Check if the string has an alphanumeric character
     *
     * @param string String
     * @return true if it has a alphanumeric character otherwise false
     */
    private boolean hasCharacter(String string) {
        for(int i = 0; i < string.length(); i++) {
            if(string.charAt(i) > 32 && string.charAt(i) < 127) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read a text file
     * Loosely based of
     * https://stackoverflow.com/questions/16104616/using-bufferedreader-to-read-text-file
     *
     * @param filename File name of the file
     * @return Text of the file as string
     */
    public  String readFile(String filename) {
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
     * Count the number of souce lines in a single .py file
     * A source line of code is any line of code that is not empty
     * or is not a (block) comment.
     *
     * @param filename File name of the current file
     * @return number of source line of code.
     */
    private int countFunctions(String filename) {
        // remove quotes first
        String s = removeQuotes(readFile(filename));

        //
        Scanner scanner = new Scanner(s);
        int numberOfFunctions = 0;
        boolean blockComment = false;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(hasCharacter(line)) {
                if(line.contains("\"\"\"") && !blockComment) {
                    blockComment = true;
                }
                else if(line.contains("\"\"\"") && blockComment) {
                    blockComment = false;
                }
                else if(!blockComment) {
                    Scanner lineScanner = new Scanner(line);
                    String firstWord = lineScanner.next();
                    if(firstWord.equals("def")) {
                        numberOfFunctions++;
                    }
                    else if(firstWord.equals("async")) {
                        if(lineScanner.hasNext() && lineScanner.next().equals("def")) {
                            numberOfFunctions++;
                        }
                    }
                    lineScanner.close();
                }
            }
        }
        return numberOfFunctions;
    }

//
//    /**
//     * Basic file lineCountTraversal for counting lines.
//     * Recursive call if another directory is found.
//     * If .py file is found, count lines and add them up.
//     *
//     * @param directory current directory.
//     */
//    private void functionCountTraversal(File directory, Set<String> filesWithC0103, boolean inverted) {
////        if(directory.listFiles() == null) {
////        }
////        else {
//            for (File file : Objects.requireNonNull(directory.listFiles())) {
//                if (file.isDirectory()) {
//                    functionCountTraversal(file, filesWithC0103, inverted);
//                }
//                else if (file.isFile() &&
//                        file.getAbsolutePath().endsWith(".py")) {
//                    if(filesWithC0103 != null) {
//                        if(filesWithC0103.contains(file.getAbsolutePath())  && !inverted) {
//                            numberOfFunctions = numberOfFunctions + countFunctions(file.getAbsolutePath());
//                        }
//                        else if(!filesWithC0103.contains(file.getAbsolutePath()) && inverted) {
//                            numberOfFunctions = numberOfFunctions + countFunctions(file.getAbsolutePath());
//                        }
//                    }
//                    else {
//                        numberOfFunctions = numberOfFunctions + countFunctions(file.getAbsolutePath());
//                    }
//                }
//            }
////        }
//    }

    /**
     * Count the number of souce lines in a single .py file
     * A source line of code is any line of code that is not empty
     * or is not a (block) comment.
     *
     * @param filename File name of the current file
     * @return number of source line of code.
     */
    private int countLines(String filename) {
        // remove quotes first
        String s = removeQuotes(readFile(filename));

        //
        Scanner scanner = new Scanner(s);
        int numberOfLines = 0;
        boolean blockComment = false;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(hasCharacter(line)) {
                if(line.contains("\"\"\"") && !blockComment) {
                    blockComment = true;
                }
                else if(line.contains("\"\"\"") && blockComment) {
                    blockComment = false;
                }
                else if(!blockComment) {
                    Scanner lineScanner = new Scanner(line);
                    String firstWord = lineScanner.next();
                    if(!firstWord.contains("#")) {
                        numberOfLines++;
                    }
                    lineScanner.close();
                }
            }
        }
        if(numberOfLines > 10000) {
            return 0;
        }
        return numberOfLines;
    }


    /**
     * Basic file lineCountTraversal for counting lines.
     * Recursive call if another directory is found.
     * If .py file is found, count lines and add them up.
     *
     * @param directory current directory.
     */
    private void lineCountTraversal(File directory, Set<String> filesWithC0103, boolean inverted) {
//        if(directory.listFiles() == null) {
//        }
//        else {
//        if(directory.exists())
//
//        if(directory.exists()) {
            for (File file : (Objects.requireNonNull(directory.listFiles()))) {
                if (file.isDirectory()) {
                  lineCountTraversal(file, filesWithC0103, inverted);
                }
                else if (file.isFile() &&
                      file.getAbsolutePath().endsWith(".py")) {
                  if (filesWithC0103 != null) {
                      if (filesWithC0103.contains(file.getAbsolutePath()) && !inverted) {
                          lineOfCodes = lineOfCodes + countLines(file.getAbsolutePath());
                      } else if (!filesWithC0103.contains(file.getAbsolutePath()) && inverted) {
                          lineOfCodes = lineOfCodes + countLines(file.getAbsolutePath());
                      }
                  } else {
                      lineOfCodes = lineOfCodes + countLines(file.getAbsolutePath());
                  }
                }
            }
//        }
//        }
    }

    /**
     * Caculate the number of errors divided by the total number of source lines of coode
     */
    public void calculatePercentages() {
        for(String errorCode : errorCodeTally.keySet()) {
            double numberOfErrors = errorCodeTally.get(errorCode);
            double percentage = numberOfErrors / lineOfCodes;
            double functionPercentage = numberOfErrors / numberOfFunctions;

            percentageTally.put(errorCode,percentage);
            functionPercentageTally.put(errorCode,functionPercentage);
        }
    }

    /**
     * Returns the number of errors per error code
     *
     * @return number of errors per error code, in absolute numbers
     */
    public Map<String, Integer> getWarningCodeTally() {
        return errorCodeTally;
    }

    /**
     * Returns the relative frequency of the error code
     *
     * @return percentage of how often the error code occurs
     */
    public Map<String, Double> getPercentageTally() {
        return percentageTally;
    }

    public Map<String, Double> getFunctionPercentageTally() {
        return functionPercentageTally;
    }
}


class TraversalThread extends Thread {
    boolean running = true;
    File resultFile;
    File originDirectory;
    List<WarningCodeCounter> list;
    boolean c;
    boolean i;

    public TraversalThread(File resultFile, File originDirectory, List<WarningCodeCounter> list,
                           boolean c, boolean i) {
        this.resultFile = resultFile;
        this.originDirectory = originDirectory;
        this.list = list;
        this.c = c;
        this.i = i;
    }

    public void run() {
        WarningCodeCounter errorCodeCounter =
                new WarningCodeCounter(
                        resultFile.getAbsolutePath(),
                        originDirectory.getAbsolutePath(),
                        true, c, i);
        try {
            errorCodeCounter.processResults(false, false);
            list.add(errorCodeCounter);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(resultFile.getAbsolutePath());
            System.out.println("There is a result file but");
            System.out.println("there is no corresponding directory in the dataset");
            System.out.println("Make sure you have not mixed up the data");
            System.out.println("Delete that result file if you are not sure");
        }
        finally {
            running = false;
        }
    }

    public List<WarningCodeCounter> getList() {
        return list;
    }

    public boolean isRunning() {
        return running;
    }
}

/**
 * Main class.
 */
public class Main {

    static boolean everything = false;
    static boolean invert = false;

    /**
     * Traverse all pylint output files that are contained in a single folder
     *
     * @param directory Directory where all pylint output files are located
     * @param origin Directory where all the repositories are located
     * @return Full list of frequency of error codes
     */
    static List<WarningCodeCounter> textfileTraversal(File directory, File origin) {
        List<WarningCodeCounter> list = new CopyOnWriteArrayList<>();
        List<TraversalThread> traversalThreads = new CopyOnWriteArrayList<>();

        int originalNumberOfThreads = Thread.activeCount();

//        StringBuilder currentText = new StringBuilder();
        for(File resultFile : Objects.requireNonNull(directory.listFiles())) {
            if(resultFile.isFile() && resultFile.getAbsolutePath().endsWith(".txt")) {

//                    WarningCodeCounter errorCodeCounter =
//                            new WarningCodeCounter(resultFile.getAbsolutePath(),
//                                    origin.getAbsolutePath());
//                    errorCodeCounter.processResults();
//                    list.add(errorCodeCounter);


                // DO NOT REMOVE THIS
                List<WarningCodeCounter> invididualList = new ArrayList<>();
                TraversalThread thread = new TraversalThread(resultFile, origin, invididualList, everything, invert);
                traversalThreads.add(thread);
                thread.start();
                // TEMPORARY ONLY


                // combine all files into one and read as one file
//                String fileAsText = Utility.readFile(resultFile.getAbsolutePath());
//                currentText.append("\n").append(fileAsText);
            }
        }
//        String allText = currentText.toString();
//        Utility.writeFile("testout.txt", allText);
//        WarningCodeCounter totalWarningCounter =
//                new WarningCodeCounter(
//                        "testout.txt",
//                        origin.getAbsolutePath(),
//                        false);
//        totalWarningCounter.processResults(false);
//        list.add(totalWarningCounter);

        for (;;) {
            boolean notRunning = true;
            for(TraversalThread thread : traversalThreads) {
                if (thread.isRunning()) {
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

        for(TraversalThread thread : traversalThreads) {
            list.addAll(thread.getList());
        }

        return list;
    }

    // TODO: Read all files as one (dsdataset only!)

    /**
     * Main class. See details below
     *
     * @param args program arguments, unused
     */
    public static void main(String[] args) {
        if(args.length != 5 || !args[0].equals("--generatePylintStats")) {
            System.out.println("options:");
            System.out.println("--generatePylintStats [DIRECTORY OF DATASET] " +
                    "[DIRECTORY OF PYLINT REPORTS] [0/1/2] [OUTPUT FILE]");

            System.out.println("Directory of dataset is the exact location of the dataset");
            System.out.println("Directory of pylint reports is created from PylintChecker.jar");
            System.out.println("0/1/2 are switches.");
            System.out.println("0 will scan everything");
            System.out.println("1 will only scan reports with non compliant names");
            System.out.println("2 will only scan reports without non compliant names");
            System.out.println("The last argument is the output file");
            return;
        }

        String outfile = args[4];
        File origin = new File(args[1]);
        //  Traverse in the folder that contains all the pylint output files.
//        String outfile = "DScombinedREMIXwithoutC0103.txt";
//        File origin = new File("/home/pc/Desktop/gitDSDatasetDepth1");
//        File origin = new File("/media/pc/SecondSSD/noDSFolder");

        // parallel part
//        List<WarningCodeCounter> list = textfileTraversal(new File("stdgitDSDatasetDepth1"),origin);


        switch (args[3]) {
            case "0":
                everything = true;
                invert = false;
                break;
            case "1":
                everything = false;
                invert = false;
                break;
            case "2":
                everything = false;
                invert = true;
                break;
            default:
                return;
        }
        List<WarningCodeCounter> list = textfileTraversal(new File(args[2]),origin);
        // sequential part


        // Total counting of all pylint error codes and frequency, summing it up
        // to get the average across all projects
        WarningCodeCounter totalWarningCounter = new WarningCodeCounter(origin.getAbsolutePath());

        for(WarningCodeCounter warningCodeCounter : list) {
            totalWarningCounter.totalTally += warningCodeCounter.totalTally;
            totalWarningCounter.lineOfCodes += warningCodeCounter.lineOfCodes;
            totalWarningCounter.numberOfFunctions += warningCodeCounter.numberOfFunctions;

            for(String warningCode : warningCodeCounter.getWarningCodeTally().keySet()) {
                if(totalWarningCounter.getWarningCodeTally().containsKey(warningCode)) {

                    // This is an addition
                    totalWarningCounter.getWarningCodeTally().put(
                            warningCode,
                                 warningCodeCounter.getWarningCodeTally().get(warningCode) +
                                    totalWarningCounter.getWarningCodeTally().get(warningCode));
                }
                else {

                    // initializes mapping
                    totalWarningCounter.getWarningCodeTally().put(
                            warningCode,
                                    warningCodeCounter.getWarningCodeTally().get(warningCode));
                }
            }

            totalWarningCounter.calculatePercentages();
        }


        // Calculate the median value for each error code. Sort the list first though
        Map<String, Double> medianMap = new HashMap<>();
        for(String warning : totalWarningCounter.getWarningCodeTally().keySet()) {
            List<Double> percentageList = new ArrayList<>();
            for(WarningCodeCounter ecc : list) {
                percentageList.add(ecc.getPercentageTally().getOrDefault(warning, 0.0));
            }
            Collections.sort(percentageList);

            int size = percentageList.size();

            // Median for even sized lists
            if(size % 2 == 0) {
                // average (0, 1, !2!, !3!, 4, 5)
                double a = percentageList.get((size/2)-1);
                double b = percentageList.get(size/2);

                medianMap.put(warning, (a+b)/2.0);
            }
            else { // Median for odd sized lists
                medianMap.put(warning, percentageList.get((size-1)/2));
            }

        }

        // Begin of printing the summary out.
        List<String> stringList = new ArrayList<>();
        stringList.add("00000,Description                  ,Absol. ,mean                ,median;");
        for(String s : totalWarningCounter.getWarningCodeTally().keySet()) {
            if(s.equals("C0103") || s.equals("C0114") || s.equals("C0115") ||
               s.equals("C0116") || s.equals("C0301") || s.equals("C0303") ||
               s.equals("C0411") || s.equals("R0201") || s.equals("R0902") ||
               s.equals("R0913") || s.equals("R0914") || s.equals("W0311") ||
               s.equals("W0621") || s.equals("W0612") || s.equals("W0613") ||
               s.equals("R0912") || s.equals("R1702") || s.equals("R1260")) {

                String word;
                switch (s) {
                    case "C0103":
                        word = "Non compliant name:          ";
                        break;
                    case "C0114":
                        word = "Missing module docstring:    ";
                        break;
                    case "C0115":
                        word = "Missing class docstring:     ";
                        break;
                    case "C0116":
                        word = "Missing function docstring:  ";
                        break;
                    case "C0301":
                        word = "Line too long:               ";
                        break;
                    case "C0303":
                        word = "Trailing whitespace:         ";
                        break;
                    case "C0411":
                        word = "Wrong import order:          ";
                        break;
                    case "R0201":
                        word = "Method could be function:    ";
                        break;
                    case "R0902":
                        word = "Too many instance attributes:";
                        break;
                    case "R0912":
                        word = "Too many branches:           ";
                        break;
                    case "R0913":
                        word = "Too many arguments:          ";
                        break;
                    case "R0914":
                        word = "Too many local variables:    ";
                        break;
                    case "R1260":
                        word = "Too complex:                 ";
                        break;
                    case "R1702":
                        word = "Too many nested blocks:      ";
                        break;
                    case "W0311":
                        word = "Bad indentation:             ";
                        break;
                    case "W0612":
                        word = "Unused variables:            ";
                        break;
                    case "W0613":
                        word = "Unused argument:             ";
                        break;
                    default:
                        word = "Redefined outer name:        ";
                        break;
                }

                stringList.add(s+","+word+","+totalWarningCounter.getWarningCodeTally().get(s)+
                                 ","+totalWarningCounter.getPercentageTally().get(s)+
                                 ","+medianMap.get(s)+
                                 ";"/*+totalWarningCounter.getFunctionPercentageTally().get(s)*/);
            }
        }
        Collections.sort(stringList);

        // String builder
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : stringList) {
            stringBuilder.append(s).append("\n");
        }
        stringBuilder.append(totalWarningCounter.totalTally).append("\n");
        stringBuilder.append(totalWarningCounter.lineOfCodes);

        // Save the file
        Utility.writeFile(outfile,stringBuilder.toString());
    }
}
