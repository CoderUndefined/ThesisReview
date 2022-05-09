package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

class Global {
    public static double threadcounter = 0;
}

class ExThread extends Thread {
    ProcessBuilder processBuilder;

    public ExThread(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    @Override
    public void run() {
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Global.threadcounter -= 1.0;
        }
    }
}

public class Main {

    /**
     * Remove all quotes from a given text
     * 
     * @param fullText original text
     * @return same text but with quotes removed
     */
    static String removeQuotes(String fullText) {

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        boolean inSingleComment = false;

        int counter = 0;

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < fullText.length(); i++) {

            if(counter > 0) {

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
            else {

                if(fullText.charAt(i) == '"') {
                    inDoubleQuote = true;
                    stringBuilder.append(fullText.charAt(i));
                }
                else if(fullText.charAt(i) == '\'') {
                    inSingleQuote = true;
                    stringBuilder.append(fullText.charAt(i));
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
        return (stringBuilder.toString());
    }

    /**
     * Converts a string into a valid file name
     * 
     * @param name original name
     * @return valid file name
     */
    static String convertToValidName(String name) {
        return name.replace("/","#");
    }

    static long counter = 0;

    /**
     * Runs a command in the shell. Exact shell (ex. bash) can be specified
     * WARNING: This function is currently unstable.
     *
     * @param arguments all arguments, Whitespace is not accepted, instead list them .
     * @param out Text file where output is redirected
     */
    static void runCommand(List<String> arguments, String out) {
        char lastLetter = out.charAt(out.length()-4);

        File dir1 = new File("outputF");
        if(!dir1.exists()) {
            dir1.mkdir();
        }
        File dir11 = new File("errF");
        if(!dir11.exists()) {
            dir11.mkdir();
        }
        File dir = new File("./outputF/"+lastLetter);
        if(!dir.exists()) {
           dir.mkdir();
        }
        File dir2 = new File("./errF/"+lastLetter);
        if(!dir2.exists()) {
            dir2.mkdir();
        }
        String outname = out;

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(arguments);
        processBuilder.redirectOutput(new File("./outputF/"+lastLetter+"/"+outname+".txt"));
        processBuilder.redirectError(new File("./errF/"+lastLetter+"/"+outname+".txt"));
        while(Global.threadcounter >= Runtime.getRuntime().availableProcessors()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ExThread exThread = new ExThread(processBuilder);
        Global.threadcounter += 1;
        exThread.start();
    }


    /**
     * ReadHelper to convert lines to a single string
     * @param bufferedReader original BufferedReader
     * @return Single string of text.
     */
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
            return null;
        }
    }

    /**
     * Checks if it has an alphabetic character or a bracket
     * 
     * @param line String line
     * @return true if it has one alphabetic character.
     */
    public static boolean containsAlphabetic(String line) {
        for(long i = 0; i < line.length(); i++) {
            char character = line.charAt((int) i);
            if(Character.isAlphabetic(character)) {
               return true;
            }
            else if(Character.isDigit(character)) {
                return true;
            }
            else if(character == '(' || character == ')' ||
                    character == '[' || character == ']' ||
                    character == '-' || character == '+') {
                return true;
            }
        }
        return false;
    }


    static int dirCounter = 0;

    /**
     * Recursive function
     * If a directory is found, recursively call this
     * 
     * Runs JSHint on a .js file using Bash.
     * 
     * @param parent current directory
     * @param accumulator accumulator for counting source lines of code
     * @param first Only for initial function call. Used to display current "progress bar"
     * @return accumulator 
     */
    public static long runJSHintOnFolder(File parent, long accumulator, boolean first) {
        for (File file : Objects.requireNonNull(parent.listFiles())) {
            if(file.isDirectory() &&
                    !file.getName().equals("node_modules") &&
                    !file.getName().equals(".git") &&
                    !file.getName().equals("github") &&
                    !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {
                accumulator = runJSHintOnFolder(file, accumulator, false);
//                globalCount = accumulator;

            }
            else if(file.getName().endsWith(".js") && file.exists()) {
                long linesOfCode = countSLOC(file);
                if(linesOfCode > 0) {
                    accumulator = accumulator + linesOfCode;
                    List<String> command = new LinkedList<>();
                    command.add("bash");
                    command.add("-c");
                    command.add("jshint " + file.getAbsolutePath() +
                            " -c " + jshintFile.getAbsolutePath());
                    runCommand(command, convertToValidName(file.getAbsolutePath()));
                }
            }
            if(first) {
                dirCounter++;
                System.out.println(dirCounter+"/"+Objects.requireNonNull(parent.listFiles()).length);
            }
        }
        return accumulator;
    }

    static int functions = 0;
    static int parameters = 0;
    static int statements = 0;
    static int complexity = 0;

    static LinkedList<Integer> parameterList;
    static LinkedList<Integer> statementList;
    static LinkedList<Integer> complexityList;

    static String previousString  = null;
    static String previousName =null;


    /**
     * 
     * @param sortedFileAsString
     * @param file
     */
    static void processSortedString(String sortedFileAsString, File file) {
        Scanner scanner = new Scanner(sortedFileAsString);


        if(parameterList == null) {
            parameterList = new LinkedList<>();
            statementList = new LinkedList<>();
            complexityList = new LinkedList<>();
        }

        LinkedList<Integer> tempparameterList = new LinkedList<>();
        LinkedList<Integer> tempstatementList = new LinkedList<>();
        LinkedList<Integer> tempcomplexityList = new LinkedList<>();

        String currentLine = "-1";
        String currentColumn = "-1";
        boolean hasParameter = false;
        boolean hasStatement = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String lineDigits = line.substring(5, line.indexOf(","));
            String columnDigits = line.substring(line.indexOf(",")+5, line.lastIndexOf(","));

            if (!currentLine.equals(lineDigits) || !currentColumn.equals(columnDigits)) {
                if(!hasParameter && !currentLine.equals("-1")) {
                    tempparameterList.add(0);
                }
                if(!hasStatement && !currentLine.equals("-1")) {
                    tempstatementList.add(0);
                }
                currentLine = lineDigits;
                currentColumn = columnDigits;

                hasParameter = false;
                hasStatement = false;
            }
            int parseInt = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
            if(line.contains("parameters")) {
                parameters = parameters + parseInt;
                tempparameterList.add(parseInt);
                hasParameter = true;

            }
            if(line.contains("statements")) {
                statements = statements + parseInt;
                tempstatementList.add(parseInt);
                hasStatement = true;
            }
            if(line.contains("cyclomatic complexity")) {
                complexity = complexity + parseInt;
                tempcomplexityList.add(parseInt);
            }
            if(line.contains("Unrecoverable syntax error")) {
                return;
            }
        }
        scanner.close();

        // many functions have 0 parameters which are not logged and have to be dealt with
        if(!hasParameter && !currentLine.equals("-1")) {
            tempparameterList.add(0);
        }
        // only happens rarely. In case there is nothing, nothing will be recorded, so 0 is added
        if(!hasStatement && !currentLine.equals("-1")) {
            tempstatementList.add(0);
        }

        statementList.addAll(tempstatementList);
        parameterList.addAll(tempparameterList);
        complexityList.addAll(tempcomplexityList);

        previousString = sortedFileAsString;
        previousName = file.getAbsolutePath();
    }

    /**
     * First step in processing a file.
     * Filters out irrelevant warnings. 
     * Then passed on processSortedString.
     * 
     * @param file Raw text file
     * @return Filtered warnings.
     */
    static String processFile(File file) {
        String s = readFile(file.getAbsolutePath());

        PriorityQueue<String> priorityQueue = new PriorityQueue<>();
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            priorityQueue.add(scanner.nextLine());
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (!priorityQueue.isEmpty()) {
            String temp = (priorityQueue.poll());
            if (temp.contains(":") && temp.contains("This function has too many")) {
                temp = temp.substring(temp.indexOf(":") + 1);
                stringBuilder.append(temp).append("\n");
                //                System.out.println(temp);
            }
            if (temp.contains(":") && temp.contains("This function's cyclomatic")) {
                temp = temp.substring(temp.indexOf(":") + 1);
                stringBuilder.append(temp).append("\n");
                //                System.out.println(temp);
            }
            if (temp.contains(":") && temp.contains("Unrecoverable syntax error")) {
                return null;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Recursive function for directory traversal
     * Calls itself if directory is found
     * If jshint result text file is found, then call processSortedString
     * 
     * @param parent current directory
     */
    static void countToTally(File parent) {
        for(File file : Objects.requireNonNull(parent.listFiles())) {
            if(file.isDirectory()) {
                countToTally(file);
            }
            else if(file.getName().endsWith(".txt")) {
                String s = processFile(file);
                if(s != null) {
                    processSortedString(s, file);
                }
            }
        }
    }

    /**
     * Get median value
     * 
     * @param list integer list
     * @return median from integer list
     */
    static int getMedian(List<Integer> list) {
        if(list.size() % 2 == 1) {
            int size = list.size();
            int half = size / 2;
            return list.get(half);
        }
        else {
            double size = list.size();
            double half = size / 2.0;
            double firstHalf = half - 0.5;
            double secondHalf = half + 0.5;
            int first = (int) firstHalf;
            int second = (int) secondHalf;
            return (list.get(first) + list.get(second)) / 2;
        }
    }


    static long sloc = 0;

    /**
     * Check if the line is longer than 1000 characters
     * 
     * @param file current file
     * @return true if line is longer than 1000 characters.
     */
    static boolean isLineTooLing(File file) {
        String fileAsString = readFile(file.getAbsolutePath());
        if(fileAsString == null) {
            return true;
        }

        Scanner scanner = new Scanner(fileAsString);
        long num = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.length() > 1000) {
                scanner.close();
                return true;
            }
            num++;
        }
        scanner.close();
        return num < 3;
    }

    /**
     * count number of source lines of code
     * A source line of code must not be empty and must not just consist of a comment
     * 
     * @param file current JS code file
     * @return number of source lines of code
     */
    static long countSLOC(File file) {
        if(isLineTooLing(file)) {
            return 0;
        }

        String fileAsString = readFile(file.getAbsolutePath());
        assert fileAsString != null;

        fileAsString = removeQuotes(fileAsString);

        Scanner scanner = new Scanner(fileAsString);
        long numberOfLines = 0;
        boolean insideBlockComment = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if(line.contains("/*")) {
                insideBlockComment = true;
            }
            if(line.contains("*/")) {
                insideBlockComment = false;
            }

            if(!insideBlockComment) {
                if (line.contains("//")) {
                    line = line.substring(0, line.indexOf("//"));
                }
                if (containsAlphabetic(line)) {
                    numberOfLines++;
                }
            }
        }
        scanner.close();
        if(numberOfLines > 10000) {
            return 0;
        }
        return numberOfLines;
    }

    /**
     * Print out all statistics to the console
     * Also writes the results to Output.txt
     * 
     * @throws IOException occurs if hard drive is full or cannot be written
     */
    static void printStatistics() throws IOException {


        //
        Collections.sort(parameterList);
        Collections.sort(statementList);
        Collections.sort(complexityList);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("MEAN").append("\n");
        stringBuilder.append("Parameters:").append((1.0*parameters)/(1.0*parameterList.size()));
        stringBuilder.append("\n");
        stringBuilder.append("Statements:").append((1.0*statements)/(1.0*statementList.size()));
        stringBuilder.append("\n");
        stringBuilder.append("Complexity:").append((1.0*complexity)/(1.0*complexityList.size()));
        stringBuilder.append("\n");

        stringBuilder.append("MEDIAN").append("\n");
        stringBuilder.append("Parameters:").append(getMedian(parameterList));
        stringBuilder.append("\n");
        stringBuilder.append("Statements:").append(getMedian(statementList));
        stringBuilder.append("\n");
        stringBuilder.append("Complexity:").append(getMedian(complexityList));
        stringBuilder.append("\n");

        for (int i = 0; i < 7650; i++) {
            parameterList.removeFirst();
            parameterList.removeLast();

            statementList.removeFirst();
            statementList.removeLast();

            complexityList.removeFirst();
            complexityList.removeLast();
        }
        stringBuilder.append("TRIMMED MEAN").append("\n");
        stringBuilder.append("Parameters:").append((1.0*parameters)/(1.0*parameterList.size()));
        stringBuilder.append("\n");
        stringBuilder.append("Statements:").append((1.0*statements)/(1.0*statementList.size()));
        stringBuilder.append("\n");
        stringBuilder.append("Complexity:").append((1.0*complexity)/(1.0*complexityList.size()));
        stringBuilder.append("\n");


        FileWriter fileWriter = new FileWriter("Output.txt");
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();

        System.out.println(statementList.size());

        System.out.println(stringBuilder);

    }

    static File jshintFile;
    static File projectDirectory;

    /**
     * Check if .jshintrc exists
     *
     * @return true if that file exists
     */
    static boolean checkJSHINTfile() {
        File file = new File(".jshintrc");
        return file.exists();
    }


    /**
     * Check if jshint command exists in bash.
     * Otherwise the program will stop running.
     * So please install it if it is not installed yet.
     *
     * @return true if that command exists in bash
     */
    static boolean checkIfJSHintExists() {
        List<String> command = new LinkedList<>();
        command.add("bash");
        command.add("-c");
        command.add("command -v jshint");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectOutput(new File("outtest.txt"));
        processBuilder.command(command);
        try {
            Process process = processBuilder.start();
            int x = process.waitFor();
            String s = readFile("outtest.txt");
            if(s.isEmpty()) {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Initial function call
     * Use this utility to process a JavaScript dataset for statistics:
     * Requires JSHint to be installed via npm install --global jshint.
     * Therefore npm must be installed
     * In the dataset, all files that contain jshint in the name must be deleted
     * These files would override the settings
     * 16 thread CPU strongly recommended
     * Available options:
     * --processJSDataset [JAVASCRIPT DATASET DIRECTORY]
     *   Processes a set of JavaScript .
     * --processOutputFolder
     *   Processes the local JSHint output folder.
     * Requires the first command to be run at least once
     * 
     * @param args see above
     * @throws IOException should not happen under a healthy hard drive
     */
    public static void main(String[] args) throws IOException {
        //
        if(args.length == 2 && args[0].equals("--processJSDataset")) {
            boolean check = checkIfJSHintExists();
            if(!check) {
                System.out.println("JSHint is not installed. " +
                        "Install it first with sudo npm i -g jshint");
                return;
            }

            if(!checkJSHINTfile()) {
                System.out.println("No .jshint file found" +
                        "Please run this from the original directory." +
                        "These files are hidden");
                return;
            }
            jshintFile = new File(".jshintrc");

            projectDirectory = new File(System.getProperty("user.dir"));
            runJSHintOnFolder(new File(args[1]), 0, true);
            countToTally(new File("outputF"));
            printStatistics();
        }
        //
        else if(args.length == 1 && args[0].equals("--processOutputFolder")) {
            projectDirectory = new File(System.getProperty("user.dir"));
            countToTally(new File("outputF"));
            printStatistics();
        }
        // show help
        else {
            System.out.println("Use this utility to process a JavaScript dataset for statistics:");
            System.out.println("Requires JSHint to be installed via npm install --global jshint.");
            System.out.println("Therefore npm must be installed");
            System.out.println("In the dataset, all files that contain jshint in the name must be deleted");
            System.out.println("These files would override the settings");
            System.out.println("16 thread CPU strongly recommended");
            System.out.println("Available options:");
            System.out.println("--processJSDataset [JAVASCRIPT DATASET DIRECTORY]");
            System.out.println("  Processes a set of JavaScript .");
            System.out.println("--processOutputFolder");
            System.out.println("  Processes the local JSHint output folder. " +
                    "Requires the first command to be run at least once");
        }


    }
}
