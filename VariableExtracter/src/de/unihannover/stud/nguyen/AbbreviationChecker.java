package de.unihannover.stud.nguyen;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.unihannover.stud.nguyen.Global.commentsEnabled;
import static de.unihannover.stud.nguyen.StringOperations.*;
import static de.unihannover.stud.nguyen.Utility.readFile;
import static de.unihannover.stud.nguyen.Utility.tooManyLines;
import static de.unihannover.stud.nguyen.VariableExtractor.*;


/**
 * Class that resolves abbreviations to full words, at least as far as possible
 */
class AbbreviationChecker {



  public static int threadcounter = 0;
  static int maxThreads = 2;
  static int portNum = 5001;

  /**
   * Get the variable out of the single line
   * Output is saved to a stringLongMap
   *
   * @param line current line
   * @return true if it is a multiline statement otherwise false
   */
  static Set<String> getVariables(String line, Set<String> abbreviationSet) {

    if(checkInvalidAssignment(line)) {
      return null;
    }

    if(line.contains("#") && checkIfNotInComment(line)) {
      line = line.substring(0,line.indexOf("#"));
    }

    // arguments in function definition
    Scanner dScanner = new Scanner(line);
//        if(line.startsWith("def ") || multiline) {
    if(dScanner.hasNext()) {
      String word = dScanner.next();
      if(word.equals("async") || word.equals("def") || word.equals("for") ||
              word.equals("if") || word.equals("elif") || word.equals("while"))  {
        dScanner.close();
        return null;
      }
    }
    dScanner.close();

    // non for variables
    LinkedList<String> list = new LinkedList<>();
    LinkedList<String> out  = new LinkedList<>();

    String simplifiedLine = removeInsideBraces(line);
    
    
    simplifiedLine = getLeftHandSideVariableDeclaration(simplifiedLine);
    if(simplifiedLine == null) {
      return null;
    }

//    assert simplifiedLine != null;
    if(simplifiedLine.contains(":")) {
      simplifiedLine = simplifiedLine.substring(0, simplifiedLine.indexOf(":"));
    }
    if(simplifiedLine.equals(")") || simplifiedLine.equals("]")) {
      return null;
    }
    if(simplifiedLine.contains("[")) {
      simplifiedLine = simplifiedLine.substring(0, simplifiedLine.indexOf("["));
    }

    Scanner scanner = new Scanner(simplifiedLine);
//        System.out.println(simplifiedLine.substring(0,simplifiedLine.indexOf("=")));
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
    Set<String> set = new HashSet<>();
    if(b && out.size() > 0) {

      for(String key : out) {
        if(isASCIIBasic(key) && !key.contains("`") && !key.contains(".") && !key.contains("\\")) {
          if(checkIfItIsNotAKeyword(key)) {
            if(abbreviationSet.contains(key)) {
              set.add(key);
            }
            else if(key.endsWith("ies") &&
                    abbreviationSet.contains(key.substring(0, key.length()-3) + "y")) {
              set.add(key);
            }
            else if(key.endsWith("oes") && abbreviationSet.contains(key.substring(0, key.length()-2))) {
              set.add(key);
            }
            else if(key.endsWith("ses") && abbreviationSet.contains(key.substring(0, key.length()-2))) {
              set.add(key);
            }
            else if(key.endsWith("shes") && abbreviationSet.contains(key.substring(0, key.length()-2))) {
              set.add(key);
            }
            else if(key.endsWith("ches") && abbreviationSet.contains(key.substring(0, key.length()-2))) {
              set.add(key);
            }
            else if(key.endsWith("xes") && abbreviationSet.contains(key.substring(0, key.length()-2))) {
              set.add(key);
            }
            else if(key.endsWith("s") && !key.endsWith("ss") &&
                    abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("1") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("2") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("3") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("4") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("5") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("6") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("7") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("8") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("9") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
            else if(key.endsWith("0") && abbreviationSet.contains(key.substring(0, key.length()-1))) {
              set.add(key);
            }
          }
        }
      }
    }
    scanner.close();
    return set;
  }


  /**
   * Check if the full word has every character from the abbreviation, in order
   * Full word must start with the first character of the abbreviation.
   * Every next character must be there too, in order.
   *
   * @param abbreviation abbreviation
   * @param string       potential word
   * @return true if word is the full word of the abbreviation, false otherwise
   */
  private static boolean hasEveryChar(String abbreviation, String string) {
    if (string.length() == 0) {
      return false;
    }

    if (abbreviation.charAt(0) != string.charAt(0)) {
      return false;
    }

    int charIndex = 0;
    for (int i = 0; i < abbreviation.length(); i++) {
      boolean hasChar = false;
      for (int j = charIndex; j < string.length(); j++) {
        if (abbreviation.charAt(i) == string.charAt(j)) {
          hasChar = true;
          charIndex = j + 1;
          break;
        }
      }
      if (!hasChar) {
        return false;
      }
    }
    return true;
  }


  /**
   * Return value
   *
   * @param word         unregulated full word
   * @param abbreviation abbreviation
   * @param plural       true if abbreviation was a plural
   * @return full word of the abbreviation
   */
  private static String returnValue(String word, String abbreviation, boolean plural, int number) {
    if (word.equals(abbreviation)) {
      return null;
    }
    if (word.startsWith(abbreviation + "_")) {
      return null;
    }
    if (word.endsWith("\\")) {
      word = word.substring(0, word.indexOf("\\"));
    }
    if (word.contains(":")) {
      word = word.substring(0, word.indexOf(":"));
    }
    if (word.contains("@")) {
      word = word.substring(0, word.indexOf("@"));
    }
    if (word.contains("!")) {
      word = word.substring(0, word.indexOf("!"));
    }
    while (word.contains("\"")) {
      word = word.substring(0, word.indexOf("\""));
    }
    while (word.contains("'")) {
      word = word.substring(0, word.indexOf("'"));
    }
    while (word.endsWith("0") || word.endsWith("1") || word.endsWith("2") ||
            word.endsWith("4") || word.endsWith("5") || word.endsWith("6") ||
            word.endsWith("7") || word.endsWith("8") || word.endsWith("9") || word.endsWith("3")) {
      word = word.substring(0, word.length() - 1);
    }


    if(number == 1111111) {
      return word + "_1d";
    }
    if(number == 2222222) {
      return word + "_2d";
    }
    else if(number == 3333333) {
      return word + "_3d";
    }
    else if(number == 4444444) {
      return word + "_4d";
    }
    else if(number == 9999990) {
      return word + "_height";
    }
    else if(number == 9999991) {
//      System.out.println(word);
      return word + "_width";
    }

    if (word.endsWith("s") && !word.endsWith("ss") && !plural) {
      word = word.substring(0, word.length() - 1);
      if(number <= -1) {
        return word;
      }
      else {
        return word + "_" + number;
      }
    }

    // already in plural,
    if (word.endsWith("s")) {
      if(number <= -1) {
        return word;
      }
      else {
        return word + "_" + number;
      }
    }

    // in which case add the s/es back
    if (plural) {
      if (word.endsWith("y")) {
        word = word + "ies";
      } else if (word.endsWith("f") || word.endsWith("fe")) {
        word = word.substring(0, word.length() - 1) + "ves";
      } else if (word.endsWith("ch") || word.endsWith("s") || word.endsWith("sh") ||
              word.endsWith("x") || word.endsWith("z")) {
        word = word + "es";
      } else {
        word = word + "s";

      }
    }

    if(number <= -1) {
      return word;
    }
    else {
      return word + "_" + number;
    }
  }

  /**
   * Process right hand side of the line.
   *
   * @param line full line
   * @return abridged right side of the line
   */
  private static String processRightSide(String line) {
    String rightSide = line.substring(line.indexOf("=") + 1);
    rightSide = removeInsideBraces(rightSide);
    rightSide = rightSide.replace(" or ", ",");
    rightSide = rightSide.replace(" and ", ",");
    rightSide = rightSide.replace(" not ", ",");
//    rightSide = rightSide.replace(" ", "");
    rightSide = rightSide.replace("\t", " ");
    return rightSide;
  }

  private static String getFullWordFromLine(String abbreviation, String line) {
    String originalAbbreviation = abbreviation;

    boolean plural = false;
    int number = -1;

    // Begin part that checks if it is a plural or ends with a number.
    if(abbreviation.endsWith("2d")) {
      number = 2222222;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    }
    else if(abbreviation.endsWith("1d")) {
      number = 1111111;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    }
    else if(abbreviation.endsWith("3d")) {
      number = 3333333;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    }
    else if(abbreviation.endsWith("4d")) {
      number = 4444444;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    }
    else if (    abbreviation.endsWith("0") || abbreviation.endsWith("1") ||
            abbreviation.endsWith("2") || abbreviation.endsWith("3") ||
            abbreviation.endsWith("4") || abbreviation.endsWith("5") ||
            abbreviation.endsWith("6") || abbreviation.endsWith("7") ||
            abbreviation.endsWith("8") || abbreviation.endsWith("9")) {
      String lastOne = abbreviation.substring(abbreviation.length()-1);
//      System.out.println(lastOne);
      number = Integer.parseInt(lastOne);
      abbreviation = abbreviation.substring(0, abbreviation.length() - 1);
    }

    if(abbreviation.endsWith("_h") && abbreviation.length() > 3) {
      number = 9999990;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    }
    else if(abbreviation.endsWith("_w") && abbreviation.length() > 3) {
      number = 9999991;
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
    } // cookies ->
    else if (abbreviation.endsWith("ies") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 3) + "y";
      plural = true;
    }
    else if (abbreviation.endsWith("oes") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
      plural = true;
    }
    else if (abbreviation.endsWith("ses") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
      plural = true;
    }
    else if (abbreviation.endsWith("shes") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
      plural = true;
    }
    else if (abbreviation.endsWith("ches") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
      plural = true;
    }
    else if (abbreviation.endsWith("xes") && abbreviation.length() > 4) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 2);
      plural = true;
    }
    else if (abbreviation.endsWith("s")&&!abbreviation.endsWith("ss")&&abbreviation.length() > 3) {
      abbreviation = abbreviation.substring(0, abbreviation.length() - 1);
      plural = true;
    }
    else if(abbreviation.equals("bbs")) {
      abbreviation = "bb";
      plural = true;
    }
    else {
      abbreviation = NLPOperations.stemming(abbreviation);
    }
    // End part that checks if it is a plural or not

    boolean valid = checkValidVariableName(abbreviation);
    if(!valid) {
//      System.out.println(abbreviation);
      return null;
    }

    // discard ternary operator
    if (line.contains(" if ")) {
      line = line.substring(0, line.indexOf(" if "));
    }

    if (line.contains(" else ")) {
      line = line.substring(0, line.indexOf(" else "));
    }

    if (!checkAssignmentOperatorWhitespace(line)) {
      return null;
    }

    String leftSide = getLeftHandSideVariableDeclaration(line);
    if (leftSide == null) {
      return null;
    }


    Pattern abbPattern = Pattern.compile("\\b" + originalAbbreviation + "\\b");
    Matcher abbMatcher = abbPattern.matcher(leftSide);
    if (!abbMatcher.find()) {
      return null;
    }
    Pattern antipattern = Pattern.compile("\\b" + originalAbbreviation + ".\\b");
    Matcher antimatcher = antipattern.matcher(leftSide);
    if (antimatcher.find()) {
      return null;
    }

    String rightSide = processRightSide(line);
    Scanner scanner = new Scanner(rightSide);
    scanner.useDelimiter(globalPattern);
    String returnvalue = null;

    while (scanner.hasNext()) {
      String word = scanner.next();
      if (!word.equals("")) {
        word = toSnakeCase(word, true);
        word = removeLeadingAndTrailingUnderscores(word);

        Scanner wordScanner = new Scanner(word);
        wordScanner.useDelimiter("_");

        List<Character> firstLetters = new ArrayList<>();

        while (wordScanner.hasNext()) {
          String sub = wordScanner.next();
          if (sub.length() != 0) {
            firstLetters.add(sub.charAt(0));

            // check if starts with first char then check if every other char is there
            if (sub.startsWith(abbreviation) && !sub.equals(abbreviation)) {
              returnvalue = returnValue(sub, abbreviation, plural, number);
            }

            if (hasEveryChar(abbreviation, sub)) {
              returnvalue = returnValue(sub, abbreviation, plural, number);
            }
          }
        }
        wordScanner.close();

//         does not work right now
        // supposed to be acronyms.
        boolean exactAcronym = false;
        if (firstLetters.size() == abbreviation.length()) {
          exactAcronym = true;
          for (int i = 0; i < firstLetters.size(); i++) {
            if (abbreviation.charAt(i) != firstLetters.get(i)) {
              exactAcronym = false;
              break;
            }
          }
        }
        if (exactAcronym) {
          return returnValue(word, abbreviation, plural, number);
        }

        if (returnvalue != null) {
          return returnvalue;
        }
//         look at full word
        if (word.startsWith(abbreviation) && !word.equals(abbreviation)) {
          return returnValue(word, abbreviation, plural, number);
        }

        if (hasEveryChar(abbreviation, word)) {
          return returnValue(word, abbreviation, plural, number);
        }
      }
    }
    scanner.close();

//    for(String comment : set) {
//      if (comment.length() != 0) {
//        // check if starts with first char then check if every other char is there
//        if (comment.startsWith(abbreviation) && !comment.equals(abbreviation)) {
//          return returnValue(comment, abbreviation, plural, number);
//        }
//
//        if (hasEveryChar(abbreviation, comment)) {
//          return returnValue(comment, abbreviation, plural, number);
//        }
//      }
//    }


    return null;
  }

  // only when def is found
  private static int countWhitespaces(String line) {
    int whitespaceCounter = 0;
    for(int i = 0; i < line.length(); i++) {
      if(line.charAt(i) == ' ' || line.charAt(i) == '\t') {
        whitespaceCounter++;
      }
      else {
        return whitespaceCounter;
      }
    }
    return whitespaceCounter;
  }



  private static String getFunctionName(String actualLine, List<String> textList, int index) {
    int currentWhitespaces = countWhitespaces(actualLine);

    // proceed until lines match
    if(actualLine.startsWith(" ") || actualLine.startsWith("\t")) {
      String functionName;


      for(int i = index - 1; i >= 0; i--) {

        String currentLine = textList.get(i);
//        System.out.println(currentLine);

        if(currentLine.startsWith("class") || currentLine.startsWith("while") ||
                currentLine.startsWith("if") || currentLine.startsWith("elif") ||
                currentLine.startsWith("for")) {
          return null;
        }

        Scanner wordScanner = new Scanner(currentLine);
        if(wordScanner.hasNext()) {
          String defWord = wordScanner.next();
          if(defWord.equals("def") && wordScanner.hasNext() &&
                  currentWhitespaces > countWhitespaces(currentLine)) {
            functionName = wordScanner.next();
            if(functionName.contains("(")) {
              functionName = functionName.substring(0, functionName.indexOf("("));
            }
            return functionName;

          }
          else if(defWord.equals("async") && wordScanner.hasNext() &&
                  currentWhitespaces > countWhitespaces(currentLine)) {
            String afterAsync = wordScanner.next();
            if(afterAsync.equals("def") && wordScanner.hasNext() &&
                    currentWhitespaces > countWhitespaces(currentLine)) {
              functionName = wordScanner.next();
              if(functionName.contains("(")) {
                functionName = functionName.substring(0, functionName.indexOf("("));
              }
              return functionName;
            }
          }
          else if(defWord.equals("class") && currentWhitespaces > countWhitespaces(currentLine)) {
            return null;
          }
        }
        wordScanner.close();
        if(currentLine.startsWith("if __name__ == ")) {
          return null;
        }

        int previous = currentWhitespaces;
        currentWhitespaces = countWhitespaces(currentLine);
        if(currentWhitespaces == 0) {
          currentWhitespaces = previous;
        }
        if(currentWhitespaces > previous) {
          currentWhitespaces = previous;
        }
        String stringWithoutWhitespace =
                currentLine.replace("\t","").replace(" ","");
        if(stringWithoutWhitespace.length() == 0) {
          currentWhitespaces = previous;
        }

//        System.out.println(currentWhitespaces);
      }
      // this should not happen
      return null;
    }
    else {
      return null;
    }
  }

  private static List<String> textfileAsList(String text) {
    List<String> list = new ArrayList<>();
    Scanner scanner = new Scanner(text);
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      list.add(line);
    }
    scanner.close();
    return list;
  }


  private static String convertToConform(String filename) {
    return filename.replace("/","#");
  }

  static class Cache {

    Map<String, String> cache;
    List<String> usedList;
    int maxSize = 100000;

    public Cache() {
      cache = new ConcurrentHashMap<>();
      usedList = new CopyOnWriteArrayList<>();
    }

    public void put(String key, String value) {
      if(value == null) {
        cache.put(key, "{\n" +
                "  \"error\": \"Could not predict types for the given source file!\"\n" +
                "}");
        usedList.add(key);
      }
      else {

        cache.put(key, value);
        usedList.add(key);

        if (cache.size() > maxSize) {
          String oldestKey = usedList.get(0);
          cache.remove(oldestKey);
          usedList.remove(0);
        }
      }
    }

    public String get(String key) {
      if(!cache.containsKey(key)) {
        return null;
      }
      else if(cache.get(key) == null) {
        return null;
      }
      else {
        return cache.get(key);
      }
    }
  }

  static Cache cache;

  private static String getJSON(File file) {
    char lastCharacter = file.getAbsolutePath().charAt(file.getAbsolutePath().length()-4);

    // x.py

    File cacheFolder = new File("cache");
    if(!cacheFolder.exists()) {
      boolean result = cacheFolder.mkdir();
    }
    if(cache == null) {
      cache = new Cache();
    }
    try {
      if(cache.get(file.getAbsolutePath()) != null) {
        return cache.get(file.getAbsolutePath());
      }

      File existingFile = new File("cache/"+lastCharacter+"/"+
              convertToConform(file.getAbsolutePath()));
      if(existingFile.exists()) {
        String line = Utility.readFile(existingFile.getAbsolutePath());
        cache.put(file.getAbsolutePath(), line);
        return line;
      }

      HttpClient httpClient = HttpClients.createDefault();
      HttpPost httpPost = new HttpPost("http://127.0.0.1:"+portNum+"/api/predict?tc=0");

      httpPost.setEntity(new FileEntity(file));

      org.apache.http.HttpResponse httpResponse = httpClient.execute(httpPost);
      HttpEntity entity = httpResponse.getEntity();
      if(entity != null) {
        InputStream inputStream = entity.getContent();
        Scanner scanner = new Scanner(inputStream);
        if(scanner.hasNextLine()) {
          String line = scanner.nextLine();
          scanner.close();

          // refactor this
          File subFolder = new File("cache/"+lastCharacter);
          if(!subFolder.exists()){
            boolean result = subFolder.mkdir();
          }

          Utility.writeFile("cache/"+lastCharacter+"/"+
                  convertToConform(file.getAbsolutePath()), line);
          cache.put(file.getAbsolutePath(), line);
          // refactor this end
          return line;
        }
        scanner.close();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      System.out.println("affected file:");
      System.out.println(file.getAbsolutePath());
      cache.put(file.getAbsolutePath(), null);
      return null;
    }
    return null;
  }

  private static String inferType(String abbreviation, String functionName, File file) {
    String line = getJSON(file);
    if(line == null) {
      return null;
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String json = gson.toJson(JsonParser.parseString(line));
    try {
//    System.out.println(json);
      JsonElement jsonElement = JsonParser.parseString(json);
      JsonObject root = jsonElement.getAsJsonObject();
      JsonObject response = root.getAsJsonObject("response");
      JsonArray classes = response.getAsJsonArray("classes");

      String returnValue = null;

      if (functionName == null) {

        for (int i = 0; i < classes.size(); i++) {
          JsonObject classesObject = classes.get(i).getAsJsonObject();
          JsonObject variables_p = classesObject.getAsJsonObject("variables_p");
          if (variables_p.has(abbreviation) &&
                  variables_p.getAsJsonArray(abbreviation).size() > 0) {
            JsonArray temporary = variables_p.getAsJsonArray(abbreviation).get(0).getAsJsonArray();
            returnValue = temporary.get(0).getAsString();
          }

          // condition if there is a "global" variable name.
        }

        JsonObject variables_p = response.getAsJsonObject("variables_p");
        if (variables_p.has(abbreviation) &&
                variables_p.getAsJsonArray(abbreviation).size() > 0) {
          JsonArray temporary = variables_p.getAsJsonArray(abbreviation).get(0).getAsJsonArray();
          if(returnValue != null) {
            return returnValue;
          }
          returnValue = temporary.get(0).getAsString();
          return returnValue;
        }
      }
      else {

        for (int i = 0; i < classes.size(); i++) {
          JsonObject classesObject = classes.get(i).getAsJsonObject();
          JsonArray funcs = classesObject.getAsJsonArray("funcs");

          for (int j = 0; j < funcs.size(); j++) {
            JsonObject variables_p = funcs.get(j).getAsJsonObject().getAsJsonObject("variables_p");
            if (variables_p.has(abbreviation) &&
                    variables_p.getAsJsonArray(abbreviation).size() > 0 &&
                    funcs.get(j).getAsJsonObject().get("name").getAsString().equals(functionName)) {
              JsonArray temporary = variables_p.getAsJsonArray(abbreviation).get(0).getAsJsonArray();
              returnValue = temporary.get(0).getAsString();
//            return temporary.get(0).getAsString();
            }
          }
          // condition if there is a "global" variable name.
        }

        JsonArray funcs = response.getAsJsonArray("funcs");
        for (int j = 0; j < funcs.size(); j++) {
          JsonObject variables_p = funcs.get(j).getAsJsonObject().getAsJsonObject("variables_p");
          if (variables_p.has(abbreviation) &&
                  variables_p.getAsJsonArray(abbreviation).size() > 0 &&
                  funcs.get(j).getAsJsonObject().get("name").getAsString().equals(functionName)) {
            JsonArray temporary = variables_p.getAsJsonArray(abbreviation).get(0).getAsJsonArray();
            if (returnValue != null) {
              return returnValue;
            }

            returnValue = temporary.get(0).getAsString();
          }
        }
      }
      return returnValue;
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(json);
      System.out.println(file.getAbsolutePath());
      return null;
    }
  }

  private static Set<String> turnCommentsToSet(String comments) {
    Set<String> set = new HashSet<>();
    Scanner scanner = new Scanner(comments);
    while (scanner.hasNext()) {
      String word = scanner.next();
      if(word.length() > 2 && !notInDictionary(word)) {
        set.add(word);
      }
    }
    return set;
  }

  /**
   * Get the line out of a single file to be examined for variables
   *
   * @param file             current file
   * @param abbreviationSet abbreviationSet
   */
  private static void processLines(File file,
                                   Set<String> abbreviationSet,
                                   AbbreviationTree abbreviationTree) {
    String raw = readFile(file.getAbsolutePath());
    if (raw == null) {
      return;
    }

    String text = removeQuotes(raw); // very expensive operation

    List<String> textList = textfileAsList(text);

    Scanner scanner = new Scanner(text);

    for(int i = 0; i < textList.size(); i++) {
      String line = textList.get(i);
      if (line.length() > 0) {

        Set<String> set = getVariables(line, abbreviationSet); // TODO: Get function name and type
        if (set != null) {
//          System.out.println(set);
          for (String abbreviation : set) {

            String copyString = abbreviation;

            String fullWord = getFullWordFromLine(abbreviation, line); // ??????
            abbreviation = copyString; // in event abbreviation was altered

            // Replace MegaMap with new datatype
            if (fullWord != null && !fullWord.equals(abbreviation) &&
                    fullWord.length() > abbreviation.length()) {

              String functionName = getFunctionName(line, textList, i);
              String type = inferType(abbreviation, functionName, file);
//              String type = null;

//              if(type == null) {
//                System.out.println(file.getAbsolutePath());
//                System.out.println("Type is null at " + abbreviation + " ; " + functionName);
//              }
              if(type == null) {
                type = "[NULL]";
              }

              if(type.contains("[") && !type.equals("[NULL]")) {
                type = type.substring(0, type.indexOf("["));
              }
              type = type.toLowerCase();

              if(type.equals("int") || type.equals("float")) {
                type = "[NUMBER]";
              }

              abbreviationTree.insert(abbreviation, type, fullWord);
            }
          }
        }
      }
    }
    scanner.close();

    if(commentsEnabled) {
      String comments = removeQuotesInverted(raw);
      Set<String> wordSet = turnCommentsToSet(comments);
      for (String word : wordSet) {
        for (String abbreviation : abbreviationSet) {
          String fullWord = getFullWordFromLine(abbreviation, word);
          if(fullWord != null) {
            abbreviationTree.insert(abbreviation, "[NULL]", fullWord);
          }
        }
      }
    }

  }

  /**
   * Recursive function to traverse directories
   * If directory is found, call this again
   * If .py file is found examine its lines for variables
   * Returns stringLong map
   *
   * @param directory current directory
   * @param abbreviationTree  megaMap. Map with variables and number of occurences
   */
  public static void traversal(List<File> directory,
                               Set<String> abbreviationSet,
                               AbbreviationTree abbreviationTree) {
    for (File file : directory) {
      if (file.isDirectory()) {
        if (!file.getName().equals("venv") &&
                !file.getName().equals(".git") &&
                !file.getName().equals(".github") &&
                !file.getName().equals("__pycache__") &&
                Objects.requireNonNull(file.listFiles()).length > 0 &&
                file.canRead() &&
                !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {
          traversal(Arrays.asList(Objects.requireNonNull(file.listFiles())),
                  abbreviationSet, abbreviationTree);
        }
      }
      else if (file.isFile() && file.getAbsolutePath().endsWith(".py")) {
//        if(file.is) {
          processLines(file, abbreviationSet, abbreviationTree);
//        }
      }
    }
  }

  static int c_count = 0;

  /**
   * Recursive function to traverse directories
   * If directory is found, call this again
   * If .py file is found examine its lines for variables
   * Returns stringLong map
   *
   * @param directory current directory
   * @param abbreviationTree   megaMap. Map with variables and number of occurences
   */
  public static void firstTraversal(List<File> directory,
                                    Set<String> abbreviationSet,
                                    AbbreviationTree abbreviationTree) {
//    NLPOperations.initialize();
    for (File file : directory) {

//      System.out.println(file.getAbsolutePath());
      if (file.isDirectory()) {
        if (!file.getName().equals("venv") &&
                !file.getName().equals(".git") &&
                !file.getName().equals(".github") &&
                !file.getName().equals("__pycache__") &&
                file.canRead() &&
//                Objects.requireNonNull(file.listFiles()).length > 0 &&
                !Files.isSymbolicLink(Path.of(file.getAbsolutePath()))) {

          // try single threaded
          if(commentsEnabled) {
            BasicThread basicThread = new BasicThread(abbreviationSet, abbreviationTree);
            basicThread.getPartialFileList().add(file);
            basicThread.start();
          }
          else {
            traversal(Arrays.asList(Objects.requireNonNull(file.listFiles())),
                    abbreviationSet, abbreviationTree);
          }

          while (threadcounter >= Runtime.getRuntime().availableProcessors() * maxThreads) {
            try {
              Thread.sleep(20);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
      System.out.println(c_count);
      c_count++;
    }


    try {
      Thread.sleep(10);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  static Pattern globalPattern = null;

  /**
   * Function for starting multiple AbbreviationThreads.
   *
   * @param originDirectory base directory of the entire Python corpora
   */
  public static void metafunction(File originDirectory,
                                  String nonEnglishWordsFilename,
                                  String saveLocation) {
    globalPattern = Pattern.compile("[+\\-*/=%&|^~<>{}();\\[\\],. ]");

    String fullTable = readFile(nonEnglishWordsFilename);
    if (fullTable == null) {
      return;
    }

    Scanner scanner = new Scanner(fullTable);

    int number = 0;
    List<String> abbreviationList = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String abbreviation = line.substring(0, line.indexOf(" "));

      if (abbreviation.length() >= 3) {
        abbreviationList.add(abbreviation);
      }
      else if (abbreviation.length() == 2 && !abbreviation.startsWith("x") &&
              !abbreviation.startsWith("y") && !abbreviation.startsWith("z") &&
              !abbreviation.endsWith("0") && !abbreviation.endsWith("1") &&
              !abbreviation.endsWith("2") && !abbreviation.endsWith("3") &&
              !abbreviation.endsWith("4") && !abbreviation.endsWith("5") &&
              !abbreviation.endsWith("6") && !abbreviation.endsWith("7") &&
              !abbreviation.endsWith("8") && !abbreviation.endsWith("9")) {
        abbreviationList.add(abbreviation);
      }
    }
    scanner.close();

    Set<String> abbreviationSet = new HashSet<>(abbreviationList);
    AbbreviationTree abbreviationTree = new AbbreviationTree();

    // split up the originDirectory list into multiple ones
    int originalThreadCount = java.lang.Thread.activeCount();

    firstTraversal(Arrays.asList(Objects.requireNonNull(originDirectory.listFiles())),
            abbreviationSet, abbreviationTree);

    while (Thread.activeCount() > originalThreadCount) {
      try {
        Thread.sleep(10000);
        System.out.println(Thread.activeCount() + " > " + originalThreadCount);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // load abbreviation map first

    boolean abrexists = false;
    File mapFile = new File(Global.globalAbrPath);
    if(mapFile.exists()) {
      StringOperations.initStringChecker(Global.globalDictionaryPath, Global.globalAbrPath);
      abrexists = true;
    }

    for(String abbreviation : abbreviationSet) {
      if(!abbreviationTree.containsAbbreviation(abbreviation) ||
          abbreviationTree.getNumberOfDifferentWords(abbreviation) <= 5) {

        String word1 = wordSeparator(abbreviation, abrexists);
        if(word1 != null) {
          List<String> separatedWords = new ArrayList<>();
          separatedWords.add(word1);
          for(String word : separatedWords) {
            abbreviationTree.insert(abbreviation,"{OTHER}", word);
          }
        }
        abbreviationTree.consolidate(abbreviation);
      }
    }

    abbreviationTree.exportTree(saveLocation + File.separator + number + ".txt");
  }
}


