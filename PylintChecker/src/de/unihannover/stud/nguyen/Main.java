package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.unihannover.stud.nguyen.Main.firstLevel;
import static de.unihannover.stud.nguyen.Main.runCommand;

class Global {
    public static int threadcounter = 0;
}


/**
 * File thread class. Contains a partial list and starts the multithreaded
 * process on its own.
 *
 * @author Kim Nguyen
 */
class FileThread extends Thread {
    List<File> fileList;
    boolean active = true;

    /**
     * Initialize the list in this thread
     *
     * @param list partial list of directories from the origin directory.
     */
    public FileThread(List<File> list) {
        this.fileList = list;
    }

    /**
     * Run this thread, starting the file traversal process.
     */
    public void run() {
        firstLevel(this.fileList);
        this.active = false;
    }

    /**
     * Interrupt this thread
     */
    @Override
    public void interrupt() {
        this.active = false;
        super.interrupt();
    }

    /**
     * Returns active status of this thread
     *
     * @return active status of this thread, true if active, otherwise false
     */
    public boolean isActive() {
        return this.active;
    }
}

class ExThread extends Thread {
    List<String> arguments;
    String out;
    String err;

    public ExThread(List<String> arguments, String out, String err) {
        this.arguments = arguments;
        this.out = out;
        this.err = err;

    }

    @Override
    public void run() {
        try {
            runCommand(arguments, out, err);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Global.threadcounter--;
        }
    }
}

/**
 * Main class containing all relevant methods. This is a multithreaded program
 * that checks GitHub repositories with the external program Pylint.
 * This program simply coordinates starting this.
 *
 * @author Kim Nguyen
 */
public class Main {



    /**
     * Runs a command in the shell. Exact shell (ex. bash) can be specified
     * WARNING: This function is currently unstable.
     *
     * @param arguments all arguments, Whitespace is not accepted, instead list them .
     * @param out Text file where output is redirected
     * @param err Text file where errors are redirected
     */
    static void runCommand(List<String> arguments, String out, String err) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(arguments);
        processBuilder.redirectOutput(new File(out));
        processBuilder.redirectError(new File(err));

        try {
            Process process = processBuilder.start();
            int x = process.waitFor();
            if(x != 0) {
//                System.out.println(arguments);
//                System.out.println(out);
//                System.out.println(x);
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Third level of directory traversal, this time it is the actual repository
     * A shell command can be started here.
     *
     * @param authorName author of the repository
     * @param repositoryName name of the repository
     */
    static void thirdLevel(String authorName, String repositoryName) {

        String rcfilename;
        File rcfile;
        if(!zero) {
            rcfilename = ".pylintrc";
            rcfile = new File(rcfilename);
        }
        else {
            rcfilename = ".pylintrcZero";
            rcfile = new File(rcfilename);
        }

        String second = repositoryName.substring(repositoryName.lastIndexOf("/")+1);
        List<String> command = new ArrayList<>();
        command.add("bash");
        command.add("-c");
        command.add("cd "+repositoryName+"; shopt -s globstar ;" +
                "pylint **/*.py --rcfile " + rcfile.getAbsolutePath() +
                " --load-plugins=pylint.extensions.mccabe;");

        ExThread thread = new ExThread(command,
                "./"+stdOut+"/"+authorName+"#"+second+".txt",
                "./"+errOut+"/"+authorName+"#"+second+"#err.txt");
        while (Global.threadcounter >= (Runtime.getRuntime().availableProcessors() / 2)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        thread.start();
        Global.threadcounter++;
//        runCommand(command,
//                "./"+stdOut+"/"+authorName+"#"+second+".txt",
//                "./"+errOut+"/"+authorName+"#"+second+"#err.txt");
    }


    /**
     * Second level of directory traversal. One GitHub author may have
     * multiple repositories.
     *
     * 1st/2ndLevel(authors)/3rd(repositories)
     *
     * @param authorDirectory directory of the GitHub author of the repository
     */
    static void secondLevel(File authorDirectory, String first) {
        for(File file : Objects.requireNonNull(authorDirectory.listFiles())) {
            if(file.isDirectory()) {
                String path = file.getAbsolutePath();
                thirdLevel(first,path);
            }
        }
    }


    static int counter = 0;
    /**
     * First level of directory traversal. Due to multithreading,
     * instead of parsing the entire directory a list containing a part of
     * the directory is parsed instead. The size of the list is 1/n threads.
     *
     * 1stLevel/2ndLevel(authors)/3rdLevel(repositories)
     *
     * @param files a partial list of directories in the first level
     */
    public static void firstLevel(List<File> files) {
        for(File file : files) {
            if(file.isDirectory()) {
                secondLevel(file,file.getAbsolutePath().substring(
                        file.getAbsolutePath().lastIndexOf("/")+1));
            }
            counter++;
            System.out.println(counter + "/" + files.size());
        }
    }

    /**
     * Logical "zeroth" level. The main purpose of this method is to handle
     * multithreading to increase performance. Depending on the number
     * of CPU threads the full list of the origin directory is split into
     * n CPU threads. Then n program threads with each partial list will be
     * started. This method will not finish until all threads are finished.
     *
     * @param originDirectory directory containing all authors and repositories
     */
    static void zeroLevel(File originDirectory) {
        int cpuThreads = 1;
//        int cpuThreads = Runtime.getRuntime().availableProcessors()- 1;
        List<List<File>> multiList = new ArrayList<>();
        FileThread[] fileThreads = new FileThread[cpuThreads];
        for(int i = 0; i < cpuThreads; i++) {
            multiList.add(new ArrayList<>());
        }
        int index = 0;
        for(File file : Objects.requireNonNull(originDirectory.listFiles())) {
            if(file.isDirectory()) {
                multiList.get(index).add(file);
            }
        }

        for(int i = 0; i < fileThreads.length; i++) {
            fileThreads[i] = new FileThread(multiList.get(i));
            fileThreads[i].start();
        }

        while(true) {
            boolean finished = true;
            for (FileThread fileThread : fileThreads) {
                if (!fileThread.isActive()) {
                    finished = false;
                    break;
                }
            }

            if(finished) {
                break;
            }
            else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static boolean zero = false;
    static String folder;
    static String stdOut;
    static String errOut;

    static boolean checkFile() {
        if(new File(".pylintrc").exists() &&
                new File(".pylintrcZero").exists()) {
            return true;
        }
        else {
            System.out.println(".pylintrc or .pylintrcZero not found");
            return false;
        }
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
     * Check if jshint command exists in bash.
     * Otherwise the program will stop running.
     * So please install it if it is not installed yet.
     *
     * @return true if that command exists in bash
     */
    static boolean checkIfPylintExists() {
        List<String> command = new LinkedList<>();
        command.add("bash");
        command.add("-c");
        command.add("command -v pylint");
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
     * Main method, which starts the intial method.
     * This will parse Pylint on every Python file under that directory
     * which is the second argument
     *
     * Requires the first argument --standardPylint
     * which uses the standard Pylint settings
     * or the other first argument --zeroPylint
     * which will always flag at every function by setting thresholds to 0
     *
     * @param args Program arguments, currently unused.
     */
    public static void main(String[] args) {


        if(args.length == 2 && args[0].equals("--standardPylint")) {
            if(!checkIfPylintExists()){
                System.out.println("Pylint is not installed. " +
                        "Install it first with sudo pip install pylint. " +
                        "The sudo is important here, it must be installed globally");
                return;
            }

            if(!checkFile()) {
                System.out.println("No .pylintrc or .pylintrcZero file found" +
                        "Please run this from the original directory." +
                        "These files are hidden");
                return;
            }

            File directory = new File(args[1]);
            if(directory.exists() && directory.isDirectory()) {
                folder = directory.getName();
                stdOut = "std1" + folder;
                errOut = "err1" + folder;
                zero = false;

                File stdoutfolder = new File(stdOut);
                stdoutfolder.mkdir();

                File erroutfolder = new File(errOut);
                erroutfolder.mkdir();

                zeroLevel((directory));

            }
        }
        else if(args.length == 2 && args[0].equals("--zeroPylint")) {
            if(!checkIfPylintExists()){
                System.out.println("Pylint is not installed. " +
                        "Install it first with sudo pip install pylint. " +
                        "The sudo is important here, it must be installed globally");
                return;
            }

            if(!checkFile()) {
                System.out.println("No .pylintrc or .pylintrcZero file found" +
                        "Please run this from the original directory." +
                        "These files are hidden");
                return;
            }

            File directory = new File(args[1]);
            if(directory.exists() && directory.isDirectory()) {
                folder = directory.getName();
                stdOut = "stdZERO" + folder;
                errOut = "errZERO" + folder;
                zero = true;

                File stdoutfolder = new File(stdOut);
                stdoutfolder.mkdir();

                File erroutfolder = new File(errOut);
                erroutfolder.mkdir();

                zeroLevel((directory));

            }
        }
        else {
            System.out.println("Options:");
            System.out.println("--standardPylint [DATASET DIRECTORY]");
            System.out.println("Run pylint on every Python file in that directory");
            System.out.println("Output will be saved in a new directory named ");
            System.out.println("std1[DATASET DIRECTORY NAME]");
            System.out.println("which is required for later processing in PylintWarningCounter");
            System.out.println();
            System.out.println("--zeroPylint [DATASET DIRECTORY]");
            System.out.println("Run pylint on every Python file in that directory");
            System.out.println("All threshold are set to 0 so it will always report");
            System.out.println("Output will be saved in a new directory named ");
            System.out.println("stdZERO[DATASET DIRECTORY NAME]");
            System.out.println("which is required for later processing in PylintFunctionCounter");
            System.out.println();
            System.out.println("Both .pylintrc and .pylintrcZero files must be present");
            System.out.println("on the same directory as this jar");
        }
//        zeroLevel(new File("/media/pc/SecondSSD/"+folder));

//        ProcessBuilder processBuilder = new ProcessBuilder();
//        List<String> command = new ArrayList<>();
//        command.add("bash");
//        command.add("-c");
//        command.add("pylint /home/pc/Desktop/gitDepth1/TheAlgorithms/Python/**/*.py");
//        runCommand(command,"TheAlgorithms#Python.txt","err.txt");
    }
}
