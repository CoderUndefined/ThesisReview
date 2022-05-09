package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {


    /**
     * Helper function for readFile
     *
     * @param bufferedReader current bufferedReader
     * @return all collected lines from bufferedReader
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

//    static int functions = 0;
    static int parameters = 0;
    static int statements = 0;
    static int complexity = 0;

    static LinkedList<Integer> parameterList;
    static LinkedList<Integer> statementList;
    static LinkedList<Integer> complexityList;


    /**
     * Second step of processing a Pylint results file.
     * Counts the number of parameters and statements per function.
     * Also counts the number of cyclomatic complexity per function.
     *
     * Each entry get added to a list. Mean and median is calculated at the end
     *
     * @param stringList filtered string list of relevant Pylint warnings
     */
    static void processStringList(List<String> stringList) {
        Collections.sort(stringList);
        if(parameterList == null) {
            parameterList = new LinkedList<>();
            statementList = new LinkedList<>();
            complexityList = new LinkedList<>();
        }

        boolean hasComplexity = false;
        int a = 0;
        int b = 0;
//        System.out.println(stringList);

        String currentLine = "-1";
        for(String line : stringList) {
            if(!line.contains(":") ||
                    line.startsWith("Your code has been rated at ")) {
                continue;
            }

//            System.out.println(line);
//            String originalLine = line;
            if(line.contains("R0913")) {
                line = line. substring(line.indexOf("R0913") - 10);
                if(line.startsWith("/")) {
                    line = line.substring(1);
                }

            }
            else if(line.contains("R0915")) {
                line = line.substring(line.indexOf("R0915") - 10);
                if(line.startsWith("/")) {
                    line = line.substring(1);
                }
            }
            else if(line.contains("R1260")) {
                line = line.substring(line.indexOf("R1260") - 10);
                if(line.startsWith("/")) {
                    line = line.substring(1);
                }
            }
            else {
                System.out.println(line);
                System.out.println("warning small");
                continue;
            }

            String lineDigits = (line.substring(
                    line.indexOf(":")+1,line.indexOf(":",line.indexOf(":")+1)));


            if (!currentLine.equals(lineDigits)) {
                currentLine = lineDigits;

            }
            if(line.contains("R0913") && line.contains("Too many arguments (")) {
                int parseInt = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf("/")));
                parameters = parameters + parseInt;
                parameterList.add(parseInt);
                a = parseInt;

            }
            if(line.contains("R0915") && line.contains("Too many statements (")) {
                int parseInt = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf("/")));
                statements = statements + parseInt;
                statementList.add(parseInt);
                b = parseInt;
            }
            if(line.contains("R1260") && line.contains("The McCabe rating is")) {
                String shorterString = line.substring((line.indexOf(" (")-2), line.indexOf(" ("));
                if(shorterString.startsWith(" ")) {
                    shorterString = shorterString.substring(1);
                }
                int parseInt = Integer.parseInt(shorterString);
//                complexityList.add(parseInt);
//                parameters = parameters + a;
//                statements = statements + b;
                complexity = complexity + parseInt;
//                parameterList.add(a);
//                statementList.add(b);
                complexityList.add(parseInt);

            }
        }

        // only happens 5 times
        while (parameterList.size() < statementList.size()) {
            parameterList.add(0);
        }

        // only happens 2 times
        while (parameterList.size() > statementList.size()) {
            statementList.add(1);
        }
    }

    /**
     * First step of processing the string list.
     * Filters out any irrelevant warnings that are not required for this program.
     * Counts the number of arguments and statements per function.
     *
     * @param file Single Pylint result file.
     */
    static void processFile(File file) {
        Scanner scanner = new Scanner(Objects.requireNonNull(readFile(file.getAbsolutePath())));
        List<String> stringList = new LinkedList<>();
        List<String> temporaryList = new LinkedList<>();
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if(line.contains("R0801: Similar lines in 2 files")) {
                break;
            }
            else if(line.startsWith("**********")) {
                stringList.addAll(temporaryList);
                temporaryList = new LinkedList<>();
            }
            else if(line.contains("R0913") && line.contains("Too many arguments")){
                temporaryList.add(line);
            }
            else if(line.contains("R0915") && line.contains("Too many statements")) {
                temporaryList.add(line);
            }
            else if(line.contains("R1260") && line.contains("The McCabe rating is ") &&
                    !line.contains("This 'if' is") &&
                    !line.contains("This 'for' is") &&
                    !line.contains("This 'tryexcept' is") &&
                    !line.contains("This 'while' is")) {
                temporaryList.add(line);
            }
            else if(line.contains("Fatal error")) {
                temporaryList = new LinkedList<>();
            }

        }
        scanner.close();
        stringList.addAll(temporaryList);
        if(!stringList.isEmpty()) {
            Collections.sort(stringList);
            processStringList(stringList);
        }
    }

    /**
     * Recursively traverses a directory until a result file is found
     *
     * @param parent current directory
     */
    static void directoryTraversal(File parent) {
        for(File file : Objects.requireNonNull(parent.listFiles())) {
            if(file.isDirectory()) {
                directoryTraversal(file);
            }
            else if(file.getName().endsWith(".txt")) {
                processFile(file);
            }
        }
    }

    /**
     * Get the median from a list
     *
     * @param list an integer list
     * @return median from an integer list
     */
    static int getMedian(List<Integer> list) {
        int size = list.size();
        int half = size / 2;
        return list.get(half);
    }


    /**
     * Main function
     * Initial function before the pylint result folder is processed
     *
     * Then the median and mean are calculated here and then
     * printed to the console
     *
     *
     * The first argument is --processOutputFolder
     * The second argument is the directory where the Pylint results are located
     *
     * @param args arguments. see above.
     * @throws IOException occurs if wrong directory is specified?
     */
    public static void main(String[] args) throws IOException {
        if(args.length == 2 && args[0].equals("--processOutputFolder")) {
            // write your code here
//        processStringList(new File("12dmodel#deep_motion_mag.txt"));
            directoryTraversal(new File(args[1]));


            Collections.sort(parameterList);
            Collections.sort(statementList);
            Collections.sort(complexityList);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MEAN");
            stringBuilder.append("\n");
            stringBuilder.append("Parameters: ");
            stringBuilder.append((1.0 * parameters) / (1.0 * parameterList.size()));
            stringBuilder.append("\n");
            stringBuilder.append("Statements: ");
            stringBuilder.append((1.0 * statements) / (1.0 * statementList.size()));
            stringBuilder.append("\n");
            stringBuilder.append("Complexity: ");
            stringBuilder.append((1.0 * complexity) / (1.0 * complexityList.size()));
            stringBuilder.append("\n");

            stringBuilder.append("MEDIAN");
            stringBuilder.append("\n");
            stringBuilder.append("Parameters: ");
            stringBuilder.append(getMedian(parameterList));
            stringBuilder.append("\n");
            stringBuilder.append("Statements: ");
            stringBuilder.append(getMedian(statementList));
            stringBuilder.append("\n");
            stringBuilder.append("Complexity: ");
            stringBuilder.append(getMedian(complexityList));
            stringBuilder.append("\n");

//        stringBuilder.append(statementList);
            System.out.println(parameterList.size());


            for (int i = 0; i < 3055; i++) {
                parameterList.removeFirst();
                parameterList.removeLast();

                statementList.removeFirst();
                statementList.removeLast();

                complexityList.removeFirst();
                complexityList.removeLast();
            }
            stringBuilder.append("TRIMMED MEAN");
            stringBuilder.append("\n");
            stringBuilder.append("Parameters: ");
            stringBuilder.append((1.0 * parameters) / (1.0 * parameterList.size()));
            stringBuilder.append("\n");
            stringBuilder.append("Statements: ");
            stringBuilder.append((1.0 * statements) / (1.0 * statementList.size()));
            stringBuilder.append("\n");
            stringBuilder.append("Complexity: ");
            stringBuilder.append((1.0 * complexity) / (1.0 * complexityList.size()));
            stringBuilder.append("\n");

            FileWriter fileWriter = new FileWriter("out.txt");
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();

            System.out.println(stringBuilder.toString());
        }
        else  {
            System.out.println("Available options:");
            System.out.println("--processOutputFolder [PYLINT RESULTS FOLDER FROM PYLINT_ZERO SCAN] ");
            System.out.println("  Processes a output folder that contains Pylint results. ");
        }
    }
}
