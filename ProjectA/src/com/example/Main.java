package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static HashMap<Category, Double> tooShortMap;
    static HashMap<Category, Double> tooLongMap;
    static HashMap<Category, Double> otherMap;
    static HashMap<Category, Double> totalMap;


    /**
     * Handle variables. Increments "Too short name" or "Too long name" or "Other reason" map
     * depending on the reason why the name is bad
     *
     * @param line Line of the variable
     * @param category Enumeration of the category to be incremented
     */
    static void handleVars(String line, Category category) {
        String var = line.substring(line.indexOf("\"")+1);
        var = var.substring(0,var.indexOf("\""));



        if(var.length() <= 2) {
            tooShortMap.replace(category,tooShortMap.get(category)+1.0);
        }
        else if(var.length() > 30) {
            tooLongMap.replace(category,tooLongMap.get(category)+1.0);
        }
        else {
            otherMap.replace(category,otherMap.get(category)+1.0);
        }
        totalMap.replace(category,totalMap.get(category)+1.0);
//        totalMap.replace(Category.OTHER,totalMap.get(category)+1.0);
    }


    /**
     * Processes a single Pylint result file.
     * Maps are updated accordingly depending on the results
     *
     * @param file File of the Pylint result file.
     */
    static void processFile(File file) {
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                Pattern pattern = Pattern.compile("[CERFWI][0-9][0-9][0-9][0-9]");
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()) {
                    if(line.contains("C0103")) {
                        line = line.substring(line.indexOf(":") + 1);
                        line = line.substring(line.indexOf(":") + 1);
                        line = line.substring(line.indexOf(":") + 2);

                        if (line.contains("Variable name ")) {
                            if (line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.VARIABLE);
                            }
                        }
                        else if (line.contains("Method name ")){
                            if(line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.OTHER);
                            }
                        }
                        else if(line.contains("Function name ")) {
                            if(line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.OTHER);
                            }
                        }
                        else if (line.contains("Argument name ")) {
                            if (line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.ARGUMENT);
                            }
                        }
                        else if (line.contains("Constant name ")) {
                            if (line.contains("conform to UPPER_CASE naming style")) {
                                handleVars(line, Category.CONSTANT);
                            }
                        }
                        else if (line.contains("Attribute name ")) {
                            if (line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.ATTRIBUTE);
                            }
                        }
                        else if (line.contains("Module name ")) {
                            if(line.contains("conform to snake_case naming style")) {
                                handleVars(line, Category.OTHER);
                            }
                        }
                        else if(line.contains("Class constant name ")) {
                            if(line.contains("conform to UPPER_CASE naming style")) {
                                handleVars(line, Category.OTHER);
                            }
                        }
                        else if(line.contains("Class name ")) {
                            if(line.contains("conform to PascalCase naming style")) {
//                                toPascalCase(line);
                                handleVars(line, Category.OTHER);
                            }
                        }
//                        else if(line.contains("Class attribute name ")) {
//                            // custom??
//                            if(line.contains("conform to '([A-Za-z_][A-Za-z0-9_]{2,30}|(__.*__))$' pattern")) {
//
//                            }
//                        }
                    }
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Traverse the Pylint result directory
     * @param directory Pylint result directory
     */
    static void fileTraversal(File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            processFile(file);
        }
    }

    /**
     * Main function. Creates maps and scans Pylint result files to compile C0103 warning breakdown
     *
     * First argument must be "--processPylintResultDirectory"
     * Second argument is the directory in which all Pylint results are located for that dataset
     *
     * @param args Arguments. First argument must be "--processPylintResultDirectory"
     */
    public static void main(String[] args) {

        if(args.length == 2 && args[0].equals("--processPylintResultDirectory")) {

            tooShortMap = new HashMap<>();
            tooLongMap = new HashMap<>();
            otherMap = new HashMap<>();
            totalMap = new HashMap<>();

            for (Category t : Category.values()) {
                tooShortMap.put(t, 0.0);
                tooLongMap.put(t, 0.0);
                otherMap.put(t, 0.0);
                totalMap.put(t, 0.0);
            }

            fileTraversal(new File(args[1]));

            double totaltotal = 0.0;
            for (Category t : Category.values()) {
                totaltotal = totaltotal + totalMap.get(t);
            }

            for (Category t : Category.values()) {
                if(t.equals(Category.VARIABLE) || t.equals(Category.CONSTANT) ||
                        t.equals(Category.ARGUMENT) || t.equals(Category.ATTRIBUTE)) {
                    System.out.println(t);
                    double shortPercentage = tooShortMap.get(t) / totaltotal;
                    double longPercentage = tooLongMap.get(t) / totaltotal;
                    double otherPercentage = otherMap.get(t) / totaltotal;
                    double allPercentage = ((tooShortMap.get(t) + tooLongMap.get(t) + otherMap.get(t)) / totaltotal);

                    System.out.println();
                    System.out.println("Name too short: " + shortPercentage);
                    System.out.println("Name too long:  " + longPercentage);
                    System.out.println("Other reason:   " + otherPercentage);
                    System.out.println("Total of all %: " + allPercentage);
                    System.out.println();
                }
            }

            double sp = 0.0;
            double lp = 0.0;
            double op = 0.0;
            double ap = 0.0;
            for (Category t : Category.values()) {
                sp = sp + tooShortMap.get(t);
                lp = lp + tooLongMap.get(t);
                op = op + otherMap.get(t);
                ap = ap + (tooShortMap.get(t) + tooLongMap.get(t) + otherMap.get(t));
            }

            double shortPercentage = sp / totaltotal;
            double longPercentage = lp / totaltotal;
            double otherPercentage = op / totaltotal;
            double allPercentage = ap / totaltotal;
//            System.out.println(shortPercentage);
//            System.out.println(longPercentage);
//            System.out.println(otherPercentage);
//            System.out.println(allPercentage);
//            System.out.println();
        }
        else {
            System.out.println("Options:");
            System.out.println("--processPylintResultDirectory [PYLINT RESULT DIRECTORY]");
            System.out.println("Processes all Pylint results in that directory. Output is on console");

        }


    }
}

enum Category {
    VARIABLE, FUNCTION, ARGUMENT, CONSTANT, ATTRIBUTE, MODULE, CLASS_CONSTANT, CLASS, METHOD, OTHER
}
