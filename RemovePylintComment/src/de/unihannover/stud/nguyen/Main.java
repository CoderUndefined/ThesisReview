package de.unihannover.stud.nguyen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    /**
     * Removes all Pylint comments inside a Python file if there are any
     *
     * @param file Python file, maybe with Pylint comments
     */
    static void replaceFile(File file) {
        try {
            boolean changed = false;
            Scanner scanner = new Scanner(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("# pylint:")) {
                    line = line.substring(0, line.indexOf("# pylint:"));
                    changed = true;
                }
                else if(line.contains("#pylint:")) {
                    line = line.substring(0, line.indexOf("#pylint:"));
                    changed = true;
                }
                else if(line.contains("# pragma pylint:")) {
                    line = line.substring(0, line.indexOf("# pragma pylint:"));
                    changed = true;

                }
                stringBuilder.append(line).append("\n");

            }
            scanner.close();

            if(changed) {
                FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
                fileWriter.write(stringBuilder.toString());
                fileWriter.close();
                System.out.println(file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Directory traversal
     *
     * @param parent parent directory
     * @param first first boolean flag. Root function will display current directory on console
     */
    static void fileTraversal(File parent, boolean first) {
        for(File file : Objects.requireNonNull(parent.listFiles())) {
            if (file.isDirectory()) {
                if (!file.getName().equals("venv") &&
                        !file.getName().equals(".git") &&
                        !file.getName().equals(".github") &&
                        !file.getName().equals("__pycache__") &&
                        !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {
                    fileTraversal(file, false);
                }
            }
            else if(file.getName().endsWith(".py") && file.exists() && file.isFile()) {
                replaceFile(file);
            }
            if(first) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    /**
     * Main function of this program
     *
     * @param args One argument, which is the directory of the dataset.
     */
    public static void main(String[] args) {
        if(args.length == 1) {
            fileTraversal(new File(args[0]), true);
        }
        else {
            System.out.println("Usage");
            System.out.println("RemovePylintComment.jar [DATASET DIRECTORY]");
            System.out.println("Removes all Pylint comments in all Python files " +
                    "under that directory. Used to these things dont block Pylint");
        }
//        replaceFile(new File("test_git.py"));
	// write your code here
    }
}
