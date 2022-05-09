package de.unihannover.stud.nguyen;

import org.intellij.sdk.editor.Global;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.unihannover.stud.nguyen.StringOperations.removeUnderscores;
import static de.unihannover.stud.nguyen.IOOperations.saveGraphList;
import static de.unihannover.stud.nguyen.StringOperations.*;


// Here be dragons

// 30 mins! just for checking files
/**
 * Application that renames bad variable names into better ones
 *
 * @author Huu Kim Nguyen
 */
public class App {

  private static void internalFunction(int leftIndex, int rightIndex, List<String> rightStrings,
                                       List<String> leftStrings, int whitespaceindex,
                                       HashMap<Integer, SClass> indentMap,
                                       List<Integer> whitespaceList, String hint,
                                       boolean callsItself) {

    String right = rightStrings.get(rightIndex);
    right = removeInsideBraces(right); // TODO: REMOVE THIS IF NECESSARY

    Scanner scanner = new Scanner(right); // split into individual parts
    if(leftStrings == null) {
      scanner.useDelimiter(Pattern.compile("[+\\-*/=%&|^~<>{};\\[\\], ]"));
    }
    else {
      scanner.useDelimiter(Pattern.compile("[+\\-*/=%&|^~<>{};\\[\\]]"));
    }
    Set<String> rightComponents = new HashSet<>();
    while(scanner.hasNext()) {
      String potentialName = scanner.next();
      potentialName = removeUnecessaryStuffFromVariable(potentialName);

      if(isNotPrimitive(potentialName) && !potentialName.isEmpty()) {

        if (!potentialName.equals("self") && !potentialName.startsWith("self.") &&
            !potentialName.equals("cls") && !potentialName.startsWith("cls.") &&
            !potentialName.equals("kwargs")&&!potentialName.equals("*kwargs")&&!potentialName.equals("**kwargs") &&
            !potentialName.equals("args") && !potentialName.equals("*args") && !potentialName.equals("**args") &&
            !potentialName.equals("_") && !potentialName.equals(")") &&
            !potentialName.equals("0)") && !potentialName.equals("1)") &&
            !potentialName.equals("2)") && !potentialName.equals("3)") &&
            !potentialName.equals("4)") && !potentialName.equals("5)") &&
            !potentialName.equals("6)") && !potentialName.equals("7)") &&
            !potentialName.equals("8)") && !potentialName.equals("9)")) {
          StringBuilder stringBuilder = new StringBuilder();
          for(int i = 0; i < potentialName.length(); i++) {
            char ch = potentialName.charAt(i);
  //          System.out.println(ch);
            if(ch == '\\' || ch == '.' || ch == '[' || ch == ']' ||
                ch == '{' || ch == '}' || ch == '(' || ch == ')' ||
                ch == '|' || ch == '^' || ch == '$' || ch == '?' ||
                ch == '*' || ch == '+' || ch == '-' || ch == '/') {
              stringBuilder.append('\\').append(ch);
            }
            else {
              stringBuilder.append(ch);
            }
          }

          String temp = "\\b"+ stringBuilder +"\\b\\(";

          Pattern pattern = Pattern.compile(temp);
          Matcher matcher = pattern.matcher(right);
          if(matcher.matches()) {
            rightComponents.add(potentialName+"()"); // TODO:cannot handle nested functions yet
          }
          else {
            rightComponents.add(potentialName);
          }
        }
      }

      // primitives
      else if(!potentialName.isEmpty()) {
        if(isInteger(potentialName)) {
          rightComponents.add("#Integer#");
        }
        else if(isNumber(potentialName)) {
          rightComponents.add("#Float#");
        }
        else if(potentialName.equals("True") || potentialName.equals("False")) {
          rightComponents.add("#Boolean#");
        }
        else if(potentialName.startsWith("\"") && potentialName.endsWith("\"")) {
          rightComponents.add("#String#");
        }
        else if(potentialName.startsWith("'") && potentialName.endsWith("'")) {
          rightComponents.add("#String#");
        }
      }
    }
    scanner.close();

    int maxWhiteSpaceIndex = whitespaceindex;
    for(String rString : rightComponents) {
      // start the the highest white space index.
      // if not found go lower until it is found. Works until white space index 0

      while (whitespaceindex >= 0) {
        // ONLY WRITE DOWN FUNCTIONS
        // DO NOT WRITE DOWN VARIABLES!!!

        // direct_call of x =>   var1 = x
        // field_access of x =>  var1 = x.field
        // method_call of x =>   var1 = x.method()
        // method call

        // change plan: Try to list all available vars first

        if(checkValidNumberOfBrackets(rString) && rString.contains("(") &&
                rString.contains(")") && !rString.startsWith("(") && rString.endsWith(")")) {

          String withMethodName = rString.substring(0,rString.indexOf("("));
          if(withMethodName.contains(".")) {
            String varName = withMethodName.substring(0,withMethodName.indexOf("."));
            String methodName = withMethodName.substring(withMethodName.indexOf(".")+1);


            if (indentMap.get(whitespaceindex).contains(varName)) {
              indentMap.get(whitespaceindex).add(varName, RelationEnum.METHOD_CALL,
                      varName + "." + methodName+"()");
            }
          }
        }

        // field_access
        if(!rString.contains("(") && !rString.contains(")") && rString.contains(".") &&
                isNotPrimitive(rString)) {
          String varName = rString.substring(0,rString.indexOf("."));
          String fieldName = rString.substring(rString.indexOf(".")+1);
          if(fieldName.contains("[")) {
            fieldName = fieldName.substring(0,fieldName.indexOf("["));
          }
          if (indentMap.get(whitespaceindex).contains(varName)) {
            indentMap.get(whitespaceindex).add(varName, RelationEnum.FIELD_ACCESS,
                    varName + "." + fieldName);
          }
        }

        // direct_call
        if (indentMap.get(whitespaceindex).contains(rString) && leftStrings != null) {
          String left = removeUnecessaryStuffFromVariable(leftStrings.get(leftIndex));
          indentMap.get(whitespaceindex).add(rString, RelationEnum.DIRECT_CALL, left);
        }
        else if(indentMap.get(whitespaceindex).contains(rString) && leftStrings == null) {
          indentMap.get(whitespaceindex).add(rString, RelationEnum.DIRECT_CALL, hint);
        }
        if(whitespaceindex <= 0) {
          break;
        }
        else {
          whitespaceindex = whitespaceList.get(whitespaceList.indexOf(whitespaceindex) - 1);
        }
        if (whitespaceindex < 0) {
          break;
        }
      }
      whitespaceindex = maxWhiteSpaceIndex;
    }

    if(leftStrings == null) {
      return;
    }
    // handle left side then

    String left = leftStrings.get(leftIndex);
    left = removeUnecessaryStuffFromVariable(left);

    boolean stillNotDone = true;

    // TODO: Handle this better. Something that is not the rightSide. Anonymize vars
    // the purpose is to look deeper. ifs and fors do not have function names.
    // TODO: Does not handle primitive vars yet.
//    System.out.println(otherSetOfComponents);
    // note that there can only be one left side var, but multiple right side vars
    // like this: x = a + b + c

    for(String rString : rightComponents) {
      while (indentMap.get(whitespaceindex).functionName == null && whitespaceindex > 0) {

        if (indentMap.get(whitespaceindex).contains(left)) {
          indentMap.get(whitespaceindex).add(left, RelationEnum.ASSIGNMENT, rString);
          stillNotDone = false;

          // ONLY WRITE DOWN FUNCTIONS   DO NOT WRITE DOWN VARIABLES!!!
        }

        whitespaceindex = whitespaceList.get(whitespaceList.indexOf(whitespaceindex) - 1);
        if (whitespaceindex < 0) {
          whitespaceindex = 0;
        }
      }
      if (stillNotDone) {
        whitespaceindex = maxWhiteSpaceIndex;
        indentMap.get(whitespaceindex).add(left, RelationEnum.ASSIGNMENT, rString);
      }
    }
    if(callsItself) {
      indentMap.get(whitespaceindex).add(left, RelationEnum.ASSIGNMENT, left);
      indentMap.get(whitespaceindex).add(left, RelationEnum.DIRECT_CALL, left);
    }
  }

  /**
   * Use this for line statements that do not have a left side and therefore
   * do not have an assignment operator(ex. =, +=,..)
   *
   * @param line full line, possibly including multiple lines
   * @param indentMap ????
   * @param whitespaceList list of whitespaces
   * @param hint if used in an operator, has a hint
   * @param depthList currently used indentation depths
   */
  static void rightHandFunctionOnly(String line, HashMap<Integer, SClass> indentMap,
                                    List<Integer> whitespaceList, String hint,
                                    List<Integer> depthList) {

    int whitespaceindex = depthList.get(depthList.size()-1);

    line = removeThingsInQuotes(line);

    String rightSide = line;
    rightSide = handleInnerBracers(rightSide);
    rightSide = removeWord(rightSide,"if");
    rightSide = removeWord(rightSide,"elif");
    rightSide = removeWord(rightSide,"for");
    rightSide = removeWord(rightSide,"while");
    rightSide = removeWord(rightSide,"return");

    rightSide = removeWord(rightSide,"is not");
    rightSide = removeWord(rightSide,"is");

    rightSide = removeWord(rightSide,"not in");
    rightSide = removeWord(rightSide,"in");

    rightSide = removeWord(rightSide,"and");
    rightSide = removeWord(rightSide,"or");
    rightSide = removeWord(rightSide,"not");

    rightSide = removeWord(rightSide,"with");

    if(rightSide.endsWith(":")) {
      rightSide = rightSide.substring(0,rightSide.lastIndexOf(":"));
    }

    List<String> rightStrings = new ArrayList<>();

//    Scanner rightScanner = new Scanner(rightSide);
//    rightScanner.useDelimiter(",");
//    while(rightScanner.hasNext()) {
//      rightStrings.add(rightScanner.next()); // TODO: Remove ASAP
//    }
//    rightScanner.close();


    // this is a full line that may contain multiple variables
    rightStrings.add(rightSide);

    for(int i = 0; i < rightStrings.size(); i++) {
      internalFunction(0,i,rightStrings,null,whitespaceindex,indentMap,
              whitespaceList,hint,false);
    }
  }


  static void extractVarParameters(String line, HashMap<Integer, SClass> indentMap,
                                   List<Integer> whitespaceList,
                                   List<Integer> depthList, boolean callsItself) {

    int whitespaceindex = depthList.get(depthList.size()-1);

    List<String> splitStrings = splitLineBetweenAssignmentOperator(line);

    assert splitStrings != null;
    String leftSide = splitStrings.get(0);
    String rightSide = splitStrings.get(1);

    leftSide = removeBraces(leftSide);
//    rightSide = removeBraces(rightSide);
    leftSide = handleInnerBracers(leftSide);
    rightSide = handleInnerBracers(rightSide);


    leftSide = removeWhitespace(leftSide);

    if(rightSide.contains(" if ")) {
      rightSide = rightSide.substring(0,rightSide.indexOf(" if "));
    }

    rightSide = removeWhitespace(rightSide);

    List<String> leftSideStrings = new ArrayList<>();
    List<String> rightSideStrings = new ArrayList<>();

    // multiple full lines that may contain multiple variables
    Scanner leftScanner = new Scanner(leftSide);
    leftScanner.useDelimiter(",");
    while(leftScanner.hasNext()) {
      leftSideStrings.add(leftScanner.next());
    }
    leftScanner.close();

    // multiple full lines that may contain multiple variables
    Scanner rightScanner = new Scanner(rightSide);
    rightScanner.useDelimiter(",");
    while(rightScanner.hasNext()) {
      rightSideStrings.add(rightScanner.next());
    }
    rightScanner.close();

    if(leftSideStrings.size() == 1 && rightSideStrings.size() >= 2) {
      for(int i = 0; i < rightSideStrings.size(); i++) {
        internalFunction(0,i,rightSideStrings,leftSideStrings,whitespaceindex,indentMap,
                whitespaceList,null,false);
      }

    }
    else if(leftSideStrings.size() >= 2 && rightSideStrings.size() == 1) {
      for(int i = 0; i < leftSideStrings.size(); i++) {
        internalFunction(i,0,rightSideStrings,leftSideStrings,whitespaceindex,indentMap,
                whitespaceList,null,false);
      }

    }
    else if(leftSideStrings.size() == rightSideStrings.size()) {
      // handle right side first
      if(leftSideStrings.size() == 1) {
        for(int i = 0; i < leftSideStrings.size(); i++) {
          internalFunction(i,i,rightSideStrings,leftSideStrings,whitespaceindex,indentMap,
                  whitespaceList,null,callsItself);
        }
      }
      else {
        for(int i = 0; i < leftSideStrings.size(); i++) {
          internalFunction(i,i,rightSideStrings,leftSideStrings,whitespaceindex,indentMap,
                  whitespaceList,null,false);
        }

      }
        // TODO: For the time being,
          /*
          multiple setups!!!
          // 1. Variables are NOT anonymous, on both sides
          // 2. left side vars not anonymous, but right side vars anonymous
          // 3. left side vars anonymous, but right side vars NOT anonymous
           */
    }
    // TODO: Watch out for ternary operators!! (x = 0.5 if condition is True else 1.0)
  }

  static int extractDefParameters(String line, HashMap<Integer, SClass> indentMap,
                                  List<Integer> whitespaceList) {
    // get def name
    String defName = extractDefName(line);

    // note that def is offset like this and must be accounted for
    // def def_name(a,b,c):
    //     print()  # do your stuff here
    int whitespaceindex = whitespaceCounter(line); // add 4
    int index = whitespaceList.indexOf(whitespaceindex);
//    System.out.println(whitespaceList);
    whitespaceindex = whitespaceList.get(index+1);

    line = line.substring(line.indexOf("(")+1, line.lastIndexOf(")"));
    line = removeWhitespace(line);
    if(line.contains("return")) {
      line = line.substring(0,line.indexOf("return"));
    }

    line = removeBraces(line);
    indentMap.get(whitespaceindex).setFunctionName(defName);

    if(line.isEmpty()) {
      return whitespaceindex;
    }

    Scanner variableScanner = new Scanner(line);
    variableScanner.useDelimiter(",");

    while(variableScanner.hasNext()) {
      String variable = variableScanner.next();
      variable = removeUnecessaryStuffFromVariable(variable);
      if (!variable.equals("self") && !variable.startsWith("self.") &&
              !variable.equals("cls") && !variable.startsWith("cls.") &&
              !variable.equals("kwargs") && !variable.equals("*kwargs") && !variable.equals("**kwargs") &&
              !variable.equals("args") && !variable.equals("*args") && !variable.equals("**args") &&
              !variable.equals("_") && !variable.equals(")") &&
              !variable.equals("0)") && !variable.equals("1)") &&
              !variable.equals("2)") && !variable.equals("3)") &&
              !variable.equals("4)") && !variable.equals("5)") &&
              !variable.equals("6)") && !variable.equals("7)") &&
              !variable.equals("8)") && !variable.equals("9)")) {
        // assign things here
        indentMap.get(whitespaceindex).add(variable,RelationEnum.ASSIGNMENT, "param");
//      indentMap.get(whitespaceindex).add(variable,RelationEnum.FUNCTION_NAME,
//              "[DEF]: "+defName);

      }

    }
    variableScanner.close();
    return whitespaceindex;
  }

  static void nameMainFunction(HashMap<Integer, SClass> indentMap, List<Integer> whitespaceList) {
    int whitespaceindex = 0;
    int index = whitespaceList.indexOf(whitespaceindex);
    whitespaceindex = whitespaceList.get(index+1);
    indentMap.get(whitespaceindex).setFunctionName("[global]");
  }


  static boolean logging = false;

  /**
   * Process a single file.
   * This will look for variables and process them further.
   * At the end, create the variable relation lists
   *
   * @param file The file being processed
   * @param whitespaceList List of whitespaces.
   * @return
   */
  public static List<GraphOrigin> fileProcessing(File file, List<Integer> whitespaceList) {
    String fullText = IOOperations.readFile(file);
    if(fullText == null) {
      return new ArrayList<>(); // return empty list
    }
    fullText = AbbreviationChecker.removeQuotes(fullText);

    Scanner scanner = new Scanner(fullText);

//    if(Math.random() < 0.001) {
//      logging = true;
//      System.out.println(file.getAbsolutePath());
//      System.out.println(fullText);
//    }
//    else {
//      logging = false;
//    }

    List<Integer> defDepthList = new ArrayList<>();
    defDepthList.add(0);

    int multilinecounter = 0;

    boolean blockComment = false;
    boolean blockCommentSingle = false;
    //
    HashMap<Integer, SClass> indentMap = new HashMap<>();
    for(int i : whitespaceList) {
      String path = file.getPath();
      path = path.substring(path.indexOf(File.separator)+1);
      path = path.substring(path.indexOf(File.separator)+1);
      path = path.substring(path.indexOf(File.separator)+1);
      path = path.substring(path.indexOf(File.separator)+1);
      path = path.substring(path.indexOf(File.separator)+1);
      indentMap.put(i,new SClass(path));
    }


    List<SClass> savedSClass = new ArrayList<>(); // save them here before clearing 1 layer

    int explicitMultiline = 999999;


    boolean inMultilineDef = false;
    boolean inMultilineVarAssignment = false;
    boolean inMultilineOther = false;
    boolean callsItself = false;
    String hint = null;

    List<String> multilineList = new ArrayList<>();

    indentMap.get(0).setFunctionName("[global]");
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = removeSingleComment(line);


      if(blockCommentSingle) {
        if( line.contains("'''")) {
          blockCommentSingle = false;
        }
      }
      else if(blockComment) {
        if (line.contains("\"\"\"")) {
          blockComment = false;
        }
      }
      else {
        // in multiline statement
        if(multilinecounter == explicitMultiline) {
//          if(inMultilineDef || inMultilineVarAssignment) {
//            multilineList.add(line);
//             extract values as needed
//          }
          if(isExplicitMultilineStatement(line, true)) {
            multilinecounter = 0;

            // TODO: Handle explicit multilines
            if(inMultilineDef) {
              inMultilineDef = false;

            }
            else if(inMultilineVarAssignment) {
              inMultilineVarAssignment = false;
              callsItself = false;
            }
            else if(inMultilineOther) {
              inMultilineOther = false;
            }
          }
        }
        else if (multilinecounter > 0) {
          if(inMultilineDef || inMultilineVarAssignment || inMultilineOther) {
            multilineList.add(line);
            // extract values as needed
          }

          multilinecounter = checkStartOrEndMultilineStatement(line, multilinecounter);
          if(multilinecounter == 0) {
            if(inMultilineDef) {
              inMultilineDef = false;
              // TODO: Things
              int x = extractDefParameters(combineLines(multilineList),indentMap,whitespaceList);// !!!!!!!!!!!!!!!!!!!!
              defDepthList.add(x);
              multilineList = new ArrayList<>();
            }
            else if(inMultilineVarAssignment) {
              inMultilineVarAssignment = false;
              extractVarParameters(combineLines( // !!!!!!!!!!!!!!!!!!!!
                      multilineList),indentMap,whitespaceList,defDepthList,callsItself);
              multilineList = new ArrayList<>();
              callsItself = false;
            }
            else if(inMultilineOther) {
              inMultilineOther = false;
              rightHandFunctionOnly(combineLines(// !!!!!!!!!!!!!!!!!!!!
                      multilineList),indentMap,whitespaceList,hint, defDepthList);
              multilineList = new ArrayList<>();
              hint = null;

            }
          }
        }

        // not in multiline statement
        else { // not a  multiline statement
          if (isOneLinerDocStringInverted(line) && isOneLinerDocStringSingleInverted(line)) {
            if (line.contains("'''") || line.contains("r'''")){
              blockCommentSingle = true;
            }
            else if (line.contains("\"\"\"") || line.contains("r\"\"\"")){
              blockComment = true;
            }
            else {
              if(isLineNotEmpty(line)) {
                boolean hasMain = false;
                boolean hasDef = false;
                boolean varAssignment = false; // mutually exclusive to hasDef
                boolean rightSideOnly = false;

                // Goal is ensure that scope stays within a single def and is not violated
                int spaces = whitespaceCounter(line);
                expungeSClass(indentMap,savedSClass,spaces,file,defDepthList);

                // check for DEF
                Scanner wordScanner = new Scanner(line);
                if(line.startsWith("if __name__ == '__main__'")) {
                  hasMain = true;
                }
                else if(line.startsWith("if __name__ == \"__main__\"")) {
                  hasMain = true;
                }
                else if(wordScanner.hasNext()) {
                  String word = wordScanner.next();
                  if(word.equals("def") && wordScanner.hasNext() && line.contains("(")) {
                    hasDef = true;
                  }
                }
                wordScanner.close();

                if(!hasDef && !hasMain) {
                  // not starting the function!
                  if(hasAlternativeAssignmentOperatorWithWhitespace(line)) {
                    varAssignment = true;
                    callsItself = true;
                  }
                  else if(hasAssignmentOperatorWithWhitespace(line)) {
                    varAssignment = true;
                  }
                  else {
                    if(!line.contains(" raise ") && !line.contains("\traise ")) {
                      if(line.startsWith("if __name__ == '__main__'")) {

                      }
                      else if (line.contains(" if ") || line.contains("\tif ") ||
                              line.startsWith("if ")) {
                        hint = "if";
                        rightSideOnly = true;
                      }
                      else if (line.contains(" elif ") || line.contains("\telif ") ||
                              line.startsWith("elif ")) {
                        hint = "elif";
                        rightSideOnly = true;
                      }
                      else if (line.contains(" return ") || line.contains("\treturn ") ||
                              line.startsWith("return ")) {
                        hint = "return";
                        rightSideOnly = true;
                      }
                      else if (line.contains(" for ") || line.contains("\tfor ") ||
                              line.startsWith("for ")) {
                        hint = "for";
                        rightSideOnly = true;
                        // TODO: Special case for for loops, loop variable?
                      }
                      else if (line.contains(" while ") || line.contains("\twhile ") ||
                              line.startsWith("while ")) {
                        hint = "while";
                        rightSideOnly = true;
                      }
                      else if (line.contains("(") && !line.contains("@")) {
                        hint = "nohint";
                        rightSideOnly = true;

                      }
                    }
                  }
                }

                if(hasMain && !varAssignment && !rightSideOnly && !hasDef) {
                  nameMainFunction(indentMap,whitespaceList);
                }
                else if(hasDef && !varAssignment && !rightSideOnly && !hasMain) {
                  if(isExplicitMultilineStatement(line,false)) {
                    multilinecounter = explicitMultiline; // multiline defs do not exist!
                  }
                  if(multilinecounter != explicitMultiline) {
                    multilinecounter = checkStartOrEndMultilineStatement(line,0);

                    if(multilinecounter > 0) {

                      inMultilineDef = true;
                      inMultilineVarAssignment = false;
                      inMultilineOther = false;
                      multilineList = new ArrayList<>();
                      multilineList.add(line);
                    }
                    else {
                      int x = extractDefParameters(line,indentMap,whitespaceList);  // !!!!!!!!!!!!!!!!!!!!
                      defDepthList.add(x);
                    }
                  }
                }
                else if(varAssignment && !hasMain && !rightSideOnly && !hasDef) {
                  if(isExplicitMultilineStatement(line,false)) {
                    multilinecounter = explicitMultiline;
                    callsItself = false;
                  }
                  if(multilinecounter != explicitMultiline) {
                    multilinecounter = checkStartOrEndMultilineStatement(line,0);
                    if(multilinecounter > 0) {
                      inMultilineDef = false;
                      inMultilineVarAssignment = true;
                      inMultilineOther = false;
                      multilineList = new ArrayList<>();
                      multilineList.add(line);
  //                    System.out.println(line);
                    }
                    else {
                      extractVarParameters(line,indentMap,whitespaceList,defDepthList,callsItself);// !!!!!!!!!!!!!!!!!!!!
                      callsItself = false;
                    }
                  }
                  // TODO: WARNING: DOES NOT HANDLE PURE FUNCTION CALLS
                }
                else if(rightSideOnly && !hasMain && !varAssignment && !hasDef) {

                  if(isExplicitMultilineStatement(line,false)) {
                    multilinecounter = explicitMultiline;
                  }
                  if(multilinecounter != explicitMultiline) {
                    multilinecounter = checkStartOrEndMultilineStatement(line,0);
                    if(multilinecounter > 0) {
                      inMultilineDef = false;
                      inMultilineVarAssignment = false;
                      inMultilineOther = true;
                      multilineList = new ArrayList<>();
                      multilineList.add(line);
                    }
                    else {
                      rightHandFunctionOnly(line,indentMap,whitespaceList,hint, defDepthList);// !!!!!!!!!!!!!!!!!!!!
                      hint = null;
                    }
                  }
                }

                else {
                  if(isExplicitMultilineStatement(line,false)) {
                    multilinecounter = explicitMultiline;
                  }
                  if(multilinecounter != explicitMultiline) {
                    multilinecounter = checkStartOrEndMultilineStatement(line,0);
                  }
                }
              }
            }
          }
        }
      }
    }
    scanner.close();

    // perform this at the end too
    expungeSClass(indentMap,savedSClass,-1,file, defDepthList);
//    System.out.println("final list");
//    System.out.println(savedSClass);


    // not the final list!!

    return finalStep(savedSClass);
  }


  // result is a simpler list of relations

  /**
   * Last step of processing a file.
   * This will convert the loose lists into proper variable relation lists
   *
   * @param savedSClass Current list of variable relation lists
   * @return Saved list of variable relation lists
   */
  static List<GraphOrigin> finalStep(List<SClass> savedSClass) {

//    HashMap<String, List<Relation>> ultraMap = new HashMap<>();
    List<GraphOrigin> graphOriginList = new ArrayList<>();


    for(SClass sClass : savedSClass) {
      boolean notTooManyVars = true;
      if(sClass.stringListHashMap.keySet().size() > 20) {
//        System.out.println("Huge var array at "+sClass.filename);
        notTooManyVars = false;
      }

      for(String key : sClass.stringListHashMap.keySet()) {
        // TODO: Type can be inferred here.

        GraphOrigin graphOrigin = new GraphOrigin(key);
        graphOrigin.relationList = sClass.stringListHashMap.get(key);
        graphOrigin.relationList.add(new Relation(
                RelationEnum.FUNCTION_NAME, toSnakeCase(sClass.functionName,true)));
        graphOriginList.add(graphOrigin);
      }
    }
    return graphOriginList;
  }


  /**
   * Expunge all relation lists that have a greater whitespace count
   * than the current whitespace count
   * Then save these into another temporary list.
   *
   * @param indentMap current indentation map
   * @param savedSClass current loose list of unfinished variable relation lists
   * @param spaces current spaces/tabs
   * @param file current file
   * @param defDepthList full list of whitespaces
   */
  static void expungeSClass(HashMap<Integer, SClass> indentMap,
                            List<SClass> savedSClass, int spaces, File file,
                            List<Integer> defDepthList) {

    for(int numberOfWhitespaces : indentMap.keySet()) {
      if(spaces < numberOfWhitespaces) {
        if (!indentMap.get(numberOfWhitespaces).isEmpty()) {
          savedSClass.add(indentMap.get(numberOfWhitespaces)); // TODO: Handle differently
        }
        String path = file.getPath();
        path = path.substring(path.indexOf(File.separator)+1);
        path = path.substring(path.indexOf(File.separator)+1);
        path = path.substring(path.indexOf(File.separator)+1);
        path = path.substring(path.indexOf(File.separator)+1);
        path = path.substring(path.indexOf(File.separator)+1);
        indentMap.replace(numberOfWhitespaces, new SClass(path));
      }
    }

    while(true) {
      boolean fullRun = true;
      for(int i = 0; i < defDepthList.size() ; i++) {
        Integer number = defDepthList.get(i);
        if(spaces < number) {
          defDepthList.remove(i);
          fullRun = false;
          break;
        }
      }

      if(fullRun) {
        break;
      }
    }

  }

  /**
   * Get all whitespaces from a single file and compile them into a list
   *
   * @param file current code file
   * @return List of all used whitespaces
   */
  static List<Integer> getWhitespaces(File file) {
    int explicitMultiline = 999999;
    String text = IOOperations.readFile(file);
    if(text == null) {
      return null;
    }
    text = AbbreviationChecker.removeQuotes(text);

    Scanner scanner = new Scanner(text);
    Set<Integer> set = new HashSet<>();

    boolean blockCommentSingle = false;
    boolean blockComment = false;
//    boolean multiline = false;
    int multilinecounter = 0;

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = removeSingleComment(line);


      if(blockCommentSingle) {
        if( line.contains("'''")) {
          blockCommentSingle = false;
        }
      }
      else if(blockComment) {
        if (line.contains("\"\"\"")) {
          blockComment = false;
        }
      }
      else {
        if(multilinecounter == explicitMultiline) {
          if(isExplicitMultilineStatement(line, true)) {
            multilinecounter = 0;
          }

        }
        else if (multilinecounter > 0) {
          multilinecounter = checkStartOrEndMultilineStatement(line, multilinecounter);
        }

        else {

          if (isOneLinerDocStringInverted(line) && isOneLinerDocStringSingleInverted(line)) {
            if (line.contains("'''") || line.contains("r'''")){
              blockCommentSingle = true;
            }
            else if (line.contains("\"\"\"") || line.contains("r\"\"\"")){
              blockComment = true;
            }
            else {
              if(isExplicitMultilineStatement(line,false)) {
                multilinecounter = explicitMultiline;
              }
              if(multilinecounter != explicitMultiline) {
                multilinecounter = checkStartOrEndMultilineStatement(line,0);
                if(isLineNotEmpty(line)) {
                  set.add(whitespaceCounter(line));
                }
              }
            }
          }

        }
      }

    }
    scanner.close();

    set.add(0);
    for(int i : set) {
      if(i < 0) {
        set.remove(i);
      }
    }

    List<Integer> list = new ArrayList<>(set);
    list.sort(Comparator.comparing(Integer::valueOf));
    if (list.size() >= 2) {
      // generate additional, just in case
      int diff = list.get(list.size()-1) - list.get(list.size()-2);
      list.add(list.get(list.size()-1)+diff);
    }

    if (list.size() == 1) {
      list.add(4);
    }

    if (list.size() == 0) {
      list.add(0);
      list.add(4);
    }
    // manually rectify list, initially assume steps of 4

    list.sort(Comparator.comparing(Integer::valueOf));

    return list;
  }

  static double correct = 0.0;
  static double allStrings = 0.0;

  /**
   * Get the first three letters of a given string
   *
   * @param part String
   * @return first three letters of a string
   */
  static String getFirstThreeLetters(String part) {
    String partLetters;
    if(part.length() == 1) {
      partLetters = part.substring(0,1);
    }
    else if(part.length() == 2) {
      partLetters = part.substring(0,2);
    }
    else {
      partLetters = part.substring(0,3);
    }
    return partLetters;
  }

  private static Set<String> internal1(GraphOrigin graphOrigin,
                                       File numberDirectory,
                                       int size,
                                       Map<Integer, Map<String, List<GraphOrigin>>> cache) {
    try {

    String functionName = graphOrigin.getFunctionName();
    functionName = toSnakeCase(functionName, true);
    functionName = trimName(functionName);
    if(functionName.length() == 0) {
      return null;
    }

    Set<String> keyset = new HashSet<>();

    String firstLetters = getFirstThreeLetters(functionName);
    File functionDirectory = new File(numberDirectory.getAbsolutePath() +
            File.separator + firstLetters + File.separator + functionName);
    if(cache.containsKey(size) &&
            (functionDirectory.exists() || cache.get(size).containsKey(functionName))) {
      keyset.add(functionName);
    }
    else {
      if(!functionName.contains("_")) {
        System.out.println("WARNING: NO _ in " + functionName);
      }

      Scanner wordScanner = new Scanner(functionName);
      wordScanner.useDelimiter("_");
      boolean hasPart = false;

      while (wordScanner.hasNext()) {
        String part = wordScanner.next();
        if(part.length() > 0) {
          String partLetters = getFirstThreeLetters(part);
          if(partLetters.length() > 0) {
            File partDirectory = new File(numberDirectory.getAbsolutePath() +
                    File.separator + partLetters + File.separator + part);
            if(partDirectory.exists() || cache.get(size).containsKey(part)) {
              keyset.add(part);
              hasPart = true;
            }
          }
        }
      }
      wordScanner.close();

      if(!hasPart) {
        System.out.println("Nothing for "+functionName);
        return null;
      }
    }
    return keyset;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static PriorityQueue<RankingItem> internal2(GraphOrigin graphOrigin,
                                                      File numberDirectory,
                                                      int size,
                                                      Map<Integer, Map<String, List<GraphOrigin>>> cache,
                                                      PriorityQueue<RankingItem> priorityQueue,
                                                      Set<String> keyset) {
    // create ultra map HERE

    for(String string : keyset) {

      String stringLetters = getFirstThreeLetters(string);
      File file = new File(numberDirectory.getAbsolutePath() +
              File.separator + stringLetters + File.separator + string + File.separator + "sc");
  //
  //          System.out.println(file.getAbsolutePath());
      Map<Integer, Map<String, List<GraphOrigin>>> ultraMap;
  //            System.out.println("Load "+string);
      ultraMap = cache;


      if(ultraMap.containsKey(size) && ultraMap.get(size).containsKey(string)) {
        for (GraphOrigin other : ultraMap.get(size).get(string)) { // TODO: check filename. ACTUALLY LOAD HERE
//            String functionName = other
          RankingItem rankingItem = graphOrigin.orderedComparison(other);
          if (priorityQueue.isEmpty()) {
            priorityQueue.add(rankingItem);
          }
          else if (rankingItem.popularity < priorityQueue.peek().popularity) {
            priorityQueue.clear();
            priorityQueue.add(rankingItem);
          }
          else if (rankingItem.popularity == priorityQueue.peek().popularity &&
                  priorityQueue.size() < 10000) {
            priorityQueue.add(rankingItem);
          }
        }
      }
      else
        if(ultraMap.containsKey(size)) {
        for(String s : ultraMap.get(size).keySet()) {
          for (GraphOrigin other : ultraMap.get(size).get(s)) { // TODO: check filename. ACTUALLY LOAD HERE
            RankingItem rankingItem = graphOrigin.orderedComparison(other);
            if (priorityQueue.isEmpty()) {
              priorityQueue.add(rankingItem);
            }
            else if (rankingItem.popularity < priorityQueue.peek().popularity) {
              priorityQueue.clear();
              priorityQueue.add(rankingItem);
            }
            else if (rankingItem.popularity == priorityQueue.peek().popularity &&
                     priorityQueue.size() < 10000) {
              priorityQueue.add(rankingItem);
            }
          }
        }
      }
    }

    return priorityQueue;

  }

  /**
   * Repalce the variable name in a given function
   *
   * @param targetGraphOriginList
   * @param targetFolder
   * @param cache
   * @param searchVariable
   * @param searchFunction
   * @return
   */
  private static String[] replaceVariableName(List<GraphOrigin> targetGraphOriginList,
                                              File targetFolder,
                                              Map<Integer, Map<String, List<GraphOrigin>>> cache,
                                              String searchVariable,
                                              String searchFunction) {
//    searchFunction = toSnakeCase(searchFunction, true);
//    searchFunction = trimName(searchFunction);
    // TODO: Test __init__ function!! (because of leading underscores)

    // check if these even exist
    GraphOrigin graphOrigin = null;
    for (GraphOrigin g : targetGraphOriginList) {
      if(g.variable.equals(searchVariable) &&
         g.getFunctionName().equals(searchFunction)) {
        graphOrigin = g;
        break;
      }
    }

    if(graphOrigin == null) {
      return null;
    }

    int size = graphOrigin.relationList.size();
    if(!graphOrigin.relationList.get(size-1).relationEnum.equals(RelationEnum.IN_SAME_FUNCTION)) {
      size = -size; // anything without an IN_SAME_FUNCTION is negative!
    }

    PriorityQueue<RankingItem> priorityQueue = new PriorityQueue<>();
    Map<String, Integer> popularity = new HashMap<>();

    File numberDirectory = new File(targetFolder.getAbsolutePath() + File.separator + size);

    if(numberDirectory.exists() || cache.containsKey(size)) {
      Set<String> keyset = internal1(graphOrigin, numberDirectory, size, cache);

      if(keyset == null) {
        return null;
      }

      priorityQueue = internal2(graphOrigin, numberDirectory, size, cache, priorityQueue, keyset);

      // popularity: if multiple results with same score, pick most popular one
      // complete rework
      if (priorityQueue.peek() != null) {
        for (RankingItem rankingItem : priorityQueue) {
          if (!popularity.containsKey(rankingItem.string)) {
            popularity.put(rankingItem.string, 1);
          } else {
            popularity.replace(rankingItem.string, popularity.get(rankingItem.string) + 1);
          }
        }
      }

      PriorityQueue<RankingItem> popularItems = new PriorityQueue<>(Comparator.reverseOrder());
      for(String variableName : popularity.keySet()) {
        int frequency = popularity.get(variableName);
        RankingItem rankingItem = new RankingItem(variableName, frequency);
        popularItems.add(rankingItem);
      }

      int maxSize = Math.min(popularItems.size(),5);
      if(maxSize <= 0) {
        return null;
      }

      String[] stringArray = new String[maxSize];
      for(int i = 0; i < maxSize; i++) {
        String str = popularItems.poll().string;
        if (str.contains("*")) {

        }
        else if(str.contains(".")) {
          str = str.substring(str.indexOf(".")+1);
          stringArray[i] = str;
        }
        else {
          stringArray[i] = str;
        }
      }
      return stringArray;
    }
    else {
      return null;
    }
  }

  /**
   * Compare two variable
   *
   * @param targetGraphOriginList
   * @param targetFolder
   * @param cache cache
   */
  static void compareLists(List<GraphOrigin> targetGraphOriginList,
                           File targetFolder,
                           Map<Integer, Map<String, List<GraphOrigin>>> cache) {

//    System.out.println("A");

    for(GraphOrigin graphOrigin : targetGraphOriginList) {
      // Guard condition: If variable name is English, skip it.
      // Do handle partially English strings if possible?
      if(inDictionary(graphOrigin.variable)) {
        continue;
      }

      // also skip keywords
      else if(graphOrigin.variable.equals("self")     || graphOrigin.variable.startsWith("self.") ||
              graphOrigin.variable.equals("cls")      || graphOrigin.variable.startsWith("cls.")  ||
              graphOrigin.variable.equals("kwargs")   || graphOrigin.variable.equals("*kwargs") ||
              graphOrigin.variable.equals("**kwargs") || graphOrigin.variable.equals("_") ||
              graphOrigin.variable.equals("args")     || graphOrigin.variable.equals("*args") ||
              graphOrigin.variable.equals("**args")   || graphOrigin.variable.equals("params")) {
        continue;
      }



      int size = graphOrigin.relationList.size();
      if(!graphOrigin.relationList.get(size-1).relationEnum.equals(RelationEnum.IN_SAME_FUNCTION)) {
        size = -size; // anything without an IN_SAME_FUNCTION is negative!
      }

      PriorityQueue<RankingItem> priorityQueue = new PriorityQueue<>();
      Map<String, Integer> popularity = new HashMap<>();

      File numberDirectory = new File(targetFolder.getAbsolutePath() + File.separator + size);

      if(numberDirectory.exists() || cache.containsKey(size)) {
        Set<String> keyset = internal1(graphOrigin, numberDirectory, size, cache);

        if(keyset == null) {
          continue;
        }
//        System.out.println(graphOrigin.getFunctionName());
//        System.out.println(keyset);

        priorityQueue = internal2(graphOrigin, numberDirectory, size, cache, priorityQueue, keyset);

        // popularity: if multiple results with same score, pick most popular one
        // complete rework
        if (priorityQueue.peek() != null) {
          for (RankingItem rankingItem : priorityQueue) {
            if (!popularity.containsKey(rankingItem.string)) {
              popularity.put(rankingItem.string, 1);
            }
            else {
              popularity.replace(rankingItem.string, popularity.get(rankingItem.string) + 1);
            }
          }

          int best = 0;
          String finalResult = "should Be empty";
          String secondResult = "aaaaaaaaaaaaaaaaaaaa";
          String thirdResult = "bbbbbbbbbbbbbbbbbbb";
          for (String key : popularity.keySet()) {
            if (popularity.get(key) > best) {
              thirdResult = secondResult;
              secondResult = finalResult;

              best = popularity.get(key);
              finalResult = key;
            }
            best = Math.max(popularity.get(key),best);
          }

          // FOR TESTING PURPOSES ONLY!!
          if(graphOrigin.variable.equals(finalResult) ||
                  graphOrigin.variable.equals(secondResult) ||
                  graphOrigin.variable.equals(thirdResult)) {
            correct = correct + 1.0;
          }

          allStrings = allStrings + 1.0;
        }
      }
    }
  }

  static int threadcounter = 0; // increment for every thread started, decrement if finished.

  private static boolean alreadyFullWord(String searchVariable) {
    return inDictionary(searchVariable);
  }

  private static String[] deabbreviation(String searchVariable, String type) {

//    Map<String, List<String>> map = abbreviationToWordMap;
    AbbreviationTree abbreviationTree = StringOperations.abbreviationTree;
    if(abbreviationTree.containsAbbreviation(searchVariable)) {
      List<String> results;
      if(type == null) {
        results = abbreviationTree.getWithoutTypeFiltered(searchVariable); // dont do it here.
      }
      else {
        results = abbreviationTree.getFiltered(searchVariable, type);
      }
      List<String> threeList = new ArrayList<>();
      if(results.size() >= 3) {
        threeList.add(results.get(0));
        threeList.add(results.get(1));
        threeList.add(results.get(2));
      }
      else {
        threeList.addAll(results);
      }
      return threeList.toArray(new String[0]);

    }
    else return null;
  }


  private static double countFullEnglishWords(String variableName) {
    variableName = removeUnderscores(variableName);

    Scanner scanner = new Scanner(variableName);
    scanner.useDelimiter("_");

    double counter = 0;
    while(scanner.hasNext()) {
      String word = scanner.next();
      if(inDictionary(word)) {
        counter = counter + 1.0;
      }
      else if(inAbbreviationMap(word)) {
        counter = counter + 0.5;
      }
    }
    return counter;
  }

  private static double countWords(String variableName) {
    variableName = removeUnderscores(variableName);

    Scanner scanner = new Scanner(variableName);
    scanner.useDelimiter("_");

    double counter = 0;
    while(scanner.hasNext()) {
      scanner.next();
      counter = counter + 1.0;
    }
    return counter;
  }

  private static String[] rankBasedOnEnglishWords(String[] array) {
    PriorityQueue<RankingItemDouble> priorityQueue = new PriorityQueue<>();
    for(int index = 0; index < array.length; index++) {
      String variableName = array[index];
      double englishWordPercentage = countFullEnglishWords(variableName) / countWords(variableName);

      RankingItemDouble item = new RankingItemDouble(variableName, englishWordPercentage, index);
      priorityQueue.add(item);
    }

    String[] sortedArray = new String[array.length];
    for(int index = 0; index < array.length; index++) {
      sortedArray[index] = Objects.requireNonNull(priorityQueue.poll()).string;
    }
    return sortedArray;

  }


  public static String[] reAddPlurals(String[] array, boolean plural, String originalValue) {
    if(array == null) {
      return null;
    }

    array = rankBasedOnEnglishWords(array);

    List<String> list = new ArrayList<>();
    for (String s : array) {
      if (!s.equals(originalValue)) {
        list.add(s);
      }
    }
    array = list.toArray(new String[0]);


    if (plural) {
      for (int i = 0; i < array.length; i++) {
        if (array[i] != null) {
          if (array[i].endsWith("ay") || array[i].endsWith("ey") ||
                  array[i].endsWith("iy") || array[i].endsWith("oy") || array[i].endsWith("uy")) {
            array[i] = array[i] + "s";
          }
          else if (array[i].endsWith("y")) {
            array[i] = array[i].substring(0, array[i].length() - 1) + "ies";
          }
          else if (array[i].endsWith("s") ||
                  array[i].endsWith("sh") || array[i].endsWith("ch") ||
                  array[i].endsWith("x") || array[i].endsWith("z")) {
            array[i] = array[i] + "es";
          }
          else {
            array[i] = array[i] + "s";
          }
        }
      }
    }
    return array;
  }


  private static String turnSingular(String word) {


    if(word.endsWith("ies")) {
      return word.substring(0, word.length()-3) + "y";
    }
    else if(word.endsWith("oes")) {
      return word.substring(0, word.length()-2);
    }
    else if(word.endsWith("ses")) {
      return word.substring(0, word.length()-2);
    }
    else if(word.endsWith("shes")) {
      return word.substring(0, word.length()-2);
    }
    else if(word.endsWith("ches")) {
      return word.substring(0, word.length()-2);
    }
    else if(word.endsWith("xes")) {
      return word.substring(0, word.length()-2);
    }
    else if(word.endsWith("s")) {
      return word.substring(0, word.length()-1);
    }
    else {
      return null;
    }
  }

  private static boolean allUpperCase(String word) {
    for(int i = 0; i < word.length(); i++) {
      if(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') {
      }
      else if(word.charAt(i) >= '0' && word.charAt(i) <= '9') {

      }
      else if(word.charAt(i) == '_') {

      }
      else {
        return false;
      }
    }
    return true;
  }

  private static boolean isLowerCase(String word) {
    for(int i = 0; i < word.length(); i++) {
      if(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') {
        return false;
      }
    }
    return true;
  }

  public static String[] getSuggestedVariableNames(File fullFolder,
                                            Map<Integer, Map<String, List<GraphOrigin>>> cache,
                                            String searchVariable,
                                            String searchFunction,
                                            String searchFileLocation,
                                            String type){
    // Check if search variable is in snake_case, otherwise turn it into it first
    if(!allUpperCase(searchVariable) && !isLowerCase(searchVariable)) {
      String[] singleArray = new String[1];
      singleArray[0] = toSnakeCase(searchVariable, true);
      System.out.println(singleArray[0]);
      return singleArray;
    }


    boolean plural = false;
    String originalVariableName = searchVariable;

    searchVariable = toSnakeCase(searchVariable, true);

    String singularWord = turnSingular(searchVariable);
    String originalPluralName = searchVariable;
    
    
    if(singularWord != null) {
      searchVariable = singularWord;
      plural = true;
    }

    if(searchVariable.length() >= 30) {
      String[] arr = new String[1];
      arr[0] = VariableNameAbbreviation.multiWordAbbreviation(searchVariable,wordToAbbreviationMap);
      return reAddPlurals(arr, plural, originalPluralName);
    }
    
    List<String> twoStringList = new ArrayList<>();
    if(plural) {
      twoStringList.add(searchVariable);
      twoStringList.add(originalPluralName);
    }
    else {
      twoStringList.add(searchVariable);
      twoStringList.add(searchVariable+"s");
    }

    // if full word exists
    if(alreadyFullWord(searchVariable) && searchVariable.length() >= 3) {
      return reAddPlurals(new String[]{Global.ALREADY_EXISTS}, plural, originalPluralName);
    }

    List<String> abbreviationInList = new ArrayList<>();
    abbreviationInList.add(originalPluralName);
    Map<String, Map<String, Integer>> megaMap = new HashMap<>();
    megaMap.put(originalPluralName, new HashMap<>());

    //===========================================================================================
    // check in the actual file if the abbreviation is described in the code

    AbbreviationChecker.processLines(new File(searchFileLocation), abbreviationInList, megaMap);


    // process megaMap. Set priorities.
    String highestPriorityString = null;
    String probableString = null;
    if(megaMap.get(originalPluralName).size() > 0) {
      Map<String, Integer> subMap = megaMap.get(originalPluralName);

      int max = 0;
      for (String key : subMap.keySet()) {
        if (subMap.get(key) > max) {
          max = subMap.get(key);
          probableString = key;
        }
      }
      highestPriorityString = probableString;

//      return megaMap.get(searchVariable).keySet().toArray(new String[0]);
    }


    // try deabbreviation first

    //===========================================================================================
    //
    for(String string : twoStringList) {
      String[] array = deabbreviation(string, type);

      if (array != null) {
        if (highestPriorityString != null) {
          swapArray(array, highestPriorityString);
        }
      }

      if (array != null && array.length > 0) {
        return reAddPlurals(array, plural, originalPluralName);
      } else if (array == null && highestPriorityString != null) {
        return reAddPlurals(new String[]{highestPriorityString}, plural, originalPluralName);
      }

      //===========================================================================================
      // mutli word variables

      if (string.contains("_")) {
        Scanner scanner = new Scanner(string);
        scanner.useDelimiter("_");

        StringBuilder stringBuilder = new StringBuilder();
        int knownLength = 0;
        List<StringBuilder> stringBuilderList = new ArrayList<>();

        boolean reachedNonEnglishWord = false;
        while (scanner.hasNext()) {
          String word = scanner.next();
          if (word == null || word.length() == 0) {

          } else if (word.length() == 1) {
            stringBuilder.append(word).append("_");
          } else if (dictionary.contains(word) || word.endsWith("s") && dictionary.contains(word)) {
            stringBuilder.append(word).append("_");
          } else if (!reachedNonEnglishWord) {
            String[] multipleStrings = deabbreviation(word, type);
            if (multipleStrings != null) {
              reachedNonEnglishWord = true;

              for (int i = 0; i < multipleStrings.length; i++) {
                StringBuilder sb = new StringBuilder(stringBuilder);
                sb.append(multipleStrings[i]).append("_");
                stringBuilderList.add(sb);
              }

              // continue here
              while (scanner.hasNext()) {
                String furtherWord = scanner.next();
                for (StringBuilder sb : stringBuilderList) {
                  sb.append(furtherWord).append("_");
                }
              }
              if (stringBuilderList.size() > 0) {
                String[] stringArray = new String[stringBuilderList.size()];
                for (int i = 0; i < stringBuilderList.size(); i++) {
                  stringArray[i] = stringBuilderList.get(i).toString();
                  stringArray[i] = stringArray[i].substring(0, stringArray[i].length() - 1);
                }
                return reAddPlurals(stringArray, plural, originalPluralName);
              }


              scanner.close();

            } else {
              stringBuilder.append(word).append("_");
            }
          } else {
            stringBuilder.append(word).append("_");
          }
        }
        scanner.close();


        String comparingString = stringBuilder.toString();
        if (comparingString.endsWith("_")) {
          comparingString = comparingString.substring(0, comparingString.length() - 1);
        }
        if (!comparingString.equals(string)) {
          System.out.println("WTF, this is not supposed to happen");
          String[] singleArray = new String[1];
          singleArray[0] = comparingString;
          return reAddPlurals(singleArray, plural, originalPluralName);
        }
      }
    }

    //===========================================================================================


    // if deabbreviation fails
    File file = new File(searchFileLocation);
    List<GraphOrigin> graphOrigins = fileProcessing(file, getWhitespaces(file));
    System.out.println(originalVariableName);
    return reAddPlurals(replaceVariableName(
            graphOrigins, fullFolder, cache, originalVariableName, searchFunction),
            plural, originalPluralName);
  }

  static int count = 0;
  static void directoryTraversal(File directory, List<GraphOrigin> graphOriginList,
                                 boolean renamer, File fullFolder,
                                 Map<Integer, Map<String, List<GraphOrigin>>> cache, boolean first) {

    for(File file : Objects.requireNonNull(directory.listFiles())) {
      if(file.isDirectory() &&
              !Files.isSymbolicLink(Paths.get(file.getAbsolutePath())) &&
              !file.getName().equals("venv")) {
        directoryTraversal(file, graphOriginList, renamer, fullFolder, cache, false);
      }
      else if(file.getName().endsWith(".py") && file.isFile()) {
//        System.out.println(file.getPath().substring(27));

        if(renamer && fullFolder != null && cache != null) {
          List<GraphOrigin> graphOrigins = fileProcessing(file,getWhitespaces(file));

          // TODO: Multithread this.
          while(threadcounter >= 6 /*Runtime.getRuntime().availableProcessors()*/) {
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          threadcounter++;
          ProcessingThread processingThread = new ProcessingThread(graphOrigins,fullFolder,cache);
          processingThread.start();

//          compareLists(graphOrigins, fullFolder, cache);
          if(allStrings > 0.0) {
            double percentage = correct / allStrings;
            System.out.println(percentage + " (" + correct + File.separator + allStrings+ ")");
          }
        }
        else {

          // multithreading feasible, might possibly cause an integrity problem?
          List<GraphOrigin> graphOrigins = fileProcessing(file,getWhitespaces(file));
          graphOriginList.addAll(graphOrigins);
        }
      }
      if(first) {
        count++;
        System.out.println(count);
      }
    }
    if(allStrings > 0.0) {
      double percentage = correct / allStrings;
      System.out.println(percentage + " (" + correct + File.separator + allStrings+ ")");
    }
  }

  public static void main( String[] args ) {
    if(args.length == 0 || args.length == 1 && args[0].equals("--help")) {
      System.out.println("Available options:");
      System.out.println("--createFullFolder [DICTIONARY FILE] [OUTPUT DIRECTORY] [PYTHON DATASET LOCATION]");
      System.out.println("--createCacheFolder [DICTIONARY FILE] [OUTPUT DIRECTORY] [PYTHON DATASET LOCATION] ");
      System.out.println("Dictionary file is the english dictionary file that was provided");
      System.out.println("Output directory is the output");
      System.out.println("Python dataset location is the location of the directory of the Python dataset");
      System.out.println("--export [CACHE FOLDER LOCATION] [OUTPUT DIRECTORY] ");
      System.out.println("Cache folder location is the output from the second option");
      System.out.println("Output directory is the output");
      System.out.println("--experimental [DICTIONARY FILE] [FULL FOLDER from option 1] [PYTHON DATASET LOCATION]");
      System.out.println("test accuracy. Use a different python dataset than the one for creation");
    }
    else if(args.length == 4 &&
            (args[0].equals("--createFullFolder")) || (args[0].equals("--createCacheFolder"))) {
      // createFullFolder should not happen!!

      String dictionaryFile = args[1];
      String cacheOrFullFolder = args[2];
      String pythonCorpusFolder = args[3];


      boolean toCache = args[0].equals("--createCacheFolder");

      initDictionaryOnly(dictionaryFile);
      IOOperations.createDir(cacheOrFullFolder);

      List<GraphOrigin> graphOriginList = new ArrayList<>();
      directoryTraversal(new File(pythonCorpusFolder),
              graphOriginList,false,null,null, true);
      saveGraphList(graphOriginList,cacheOrFullFolder+File.separator + "sclasslist",toCache);


//
//      String folder = "/home/pc/Desktop/cacheFolder/saveFolderZXB";
//      directoryTraversal(new File(pythonCorpusFolder),
//              graphOriginList, true, new File(folder), );

    }
    else if(args.length == 3 && args[0].equals("--export")) {
      String cacheFolder = args[1];
      String fullExportedFolder = args[2];

      IOOperations.createDir(cacheFolder);
      IOOperations.createDir(fullExportedFolder);

      Map<Integer, Map<String, List<GraphOrigin>>> ultraMap = IOOperations.loaderFunction(
              cacheFolder+File.separator + "sclasslist");

      IOOperations.export(ultraMap, new File(fullExportedFolder));
    }
    else if(args.length == 4 && args[0].equals("--experimental")) {

      String dictionaryFile = args[1];
      String cacheOrFullFolder = args[2];
      String pythonCorpusFolder = args[3];

      initDictionaryOnly(dictionaryFile);


      Map<Integer, Map<String, List<GraphOrigin>>> ultraMap = IOOperations.loaderFunction(
              cacheOrFullFolder+File.separator + "sclasslist");

      List<GraphOrigin> graphOriginList = new ArrayList<>();
      directoryTraversal(new File(pythonCorpusFolder),
              graphOriginList,true,new File(cacheOrFullFolder), ultraMap, true);
    }
    else {
      System.out.println("Available options:");
      System.out.println("--createFullFolder [DICTIONARY FILE] [OUTPUT DIRECTORY] [PYTHON DATASET LOCATION]");
      System.out.println("--createCacheFolder [DICTIONARY FILE] [OUTPUT DIRECTORY] [PYTHON DATASET LOCATION] ");
      System.out.println("Dictionary file is the english dictionary file that was provided");
      System.out.println("Output directory is the output");
      System.out.println("Python dataset location is the location of the directory of the Python dataset");
      System.out.println("--export [FULL FOLDER LOCATION] [OUTPUT DIRECTORY] ");
      System.out.println("Full folder location is the output from the first option");
      System.out.println("Output directory is the output");
      System.out.println("--experimental [DICTIONARY FILE] [FULL FOLDER from option 1] [PYTHON DATASET LOCATION]");
      System.out.println("test accuracy. Use a different python dataset than the one for creation");

    }

//    int timeout = 0;
//    while(threadcounter > 0 && timeout < 1800000) {  // 30 mins
//      try {
//        timeout = timeout + 500;
//        Thread.sleep(500);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//    if(allStrings > 0.0) {
//      double percentage = correct / allStrings;
//      System.out.println(percentage + " (" + correct + File.separator + allStrings+ ")");
//    }
  }
}

class ProcessingThread extends Thread {
  List<GraphOrigin> tempList;
  File targetFolder;
  Map<Integer, Map<String, List<GraphOrigin>>> ultraMap;

  public ProcessingThread(List<GraphOrigin> tempList,
                          File targetFolder,
                          Map<Integer, Map<String, List<GraphOrigin>>> ultraMap ) {
    this.tempList = tempList;
    this.targetFolder = targetFolder;
    this.ultraMap = ultraMap;
  }

  // single file
  public void run() {
    App.compareLists(this.tempList, targetFolder, ultraMap);
    App.threadcounter--;
  }

}
// TODO: Redo data structure, at least post processing. At a large size O(n) is not sufficient

