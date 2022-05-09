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
            int i = 0;
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("/* jshint ") && line.contains("*/")) {
                    line = line.substring(0, line.indexOf("/* jshint")) +
                            line.substring(line.indexOf("*/") + 2);
                    changed = true;
                }
                stringBuilder.append(line).append("\n");
                i++;

                if(i > 10000) {
                    return;
                }
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
     */
    static void fileTraversal(File parent) {
        for(File file : Objects.requireNonNull(parent.listFiles())) {
            if (file.isDirectory()) {
                if (!file.getName().equals("venv") &&
                        !file.getName().equals(".git") &&
                        !file.getName().equals(".github") &&
                        !file.getName().equals("__pycache__") &&
                        !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {
                    fileTraversal(file);
                }
            }
            else if(file.getName().endsWith(".js") && file.exists()) {
                replaceFile(file);
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
            fileTraversal(new File(args[0]));
        }
        else {
            System.out.println("Usage");
            System.out.println("RemoveJSHintComment.jar [DATASET DIRECTORY]");
            System.out.println("Removes all JSHint comments from code files");
        }
//        replaceFile(new File("test_git.py"));
        // write your code here
    }
}
