package de.unihannover.stud.nguyen;

import java.util.*;

/**
 * Basic data structure which represents a basic graph consisting of
 * a single starting point that is a "String" variable and multiple "Relation" leaves.
 *
 * @author Huu Kim Nguyen
 */
public class GraphOrigin {
  String variable;
  List<Relation> relationList;

  /**
   * Basic constructor that requires a string as input. Creates an empty list of relations.
   *
   * @param variable variable name
   */
  public GraphOrigin(String variable) {
    this.variable = variable;
    this.relationList = new ArrayList<>();
  }

  /**
   * Constructor with a GraphOrigin as parameter. Use this to copy any GraphOrigin.
   * Copies both variable name and relation list.
   *
   * @param copy Another graphOrigin to be copied from
   */
  public GraphOrigin(GraphOrigin copy) {
    this.variable = copy.variable;
    this.relationList = new ArrayList<>();
    this.relationList.addAll(copy.relationList);
  }

  @Override
  public String toString() {
    return "GraphOrigin{" +
            "variable='" + variable + '\'' +
            ", relationList=" + relationList +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GraphOrigin that = (GraphOrigin) o;
    return Objects.equals(variable, that.variable) &&
            Objects.equals(relationList, that.relationList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variable, relationList);
  }

  // return value is based off how dissimilar it is
  // 0 is the best. Higher is worse.
  // for now only the same size is compared
  // order is
  // 1. initial assignment
  // 2. other
  // 3. other
  // ...
  // n-1: FUNCTION_NAME: function name or global variable?
  // n-0: IN_SAME_FUNCTION: Other variables in same function (no additional graphs for now)
  public int compare(GraphOrigin otherGraphOrigin) {
    GraphOrigin workingCopy = new GraphOrigin(otherGraphOrigin); // cloning?
    int score = 0;

    if(this.relationList.size() == workingCopy.relationList.size()) {
      // initial assignment
      Relation leftRel0 = this.relationList.get(0);
      Relation rightRel0 = workingCopy.relationList.get(0);

      if(leftRel0.relationEnum != rightRel0.relationEnum) {
        score++;
      }
      if(!leftRel0.destination.equals(rightRel0.destination)) {
        score++;
      }

      for(int i = 1; i < this.relationList.size(); i++) {
        boolean foundMatch = false;
        int jIndex = 0;

        for(int j = 1; j < workingCopy.relationList.size(); j++) {
          if (this.relationList.get(i).equals(workingCopy.relationList.get(j))) {
            foundMatch = true;
            jIndex = j;
            break;
          }

        }
        if(foundMatch) {
          workingCopy.relationList.remove(jIndex);
        }
        else {
          score = score + 2;
        }
      }

      // function name (either second to last or last)

      // in_same_function (not guaranteed)



      return score;
    }


    return 0;
  }

  /**
   * Return the function name from where this variable originated
   *
   * @return function name where this variable originated
   */
  public String getFunctionName() {
    for(int i = relationList.size()-1; i >= 0; i--) {
      Relation relation = relationList.get(i);
      if(relation.relationEnum == RelationEnum.FUNCTION_NAME) {
        return relation.destination;
      }
    }
    System.out.println("FUNCTION NAME NOT FOUND");
    return null;
  }

  private int calculateVarSize(Relation left, Relation right) {

    int leftSize = 0;
    for(int i = 0; i < left.destination.length(); i++) {
      if(left.destination.charAt(i) == '#') {
        leftSize++;
      }
    }

    int rightSize = 0;
    for(int i = 0; i < right.destination.length(); i++) {
      if(right.destination.charAt(i) == '#') {
        rightSize++;
      }
    }

    int number = Math.max(leftSize, rightSize);
    if(rightSize > leftSize) {
      number = -number;
    }
    return number;

  }

  private Set<String> createSet(String string) {
    Set<String> smalllerSet = new HashSet<>();
    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < string.length(); i++) {
      if(string.charAt(i) != '#') {
        stringBuilder.append(string.charAt(i));
      }
      else {
        smalllerSet.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
      }
    }
    return smalllerSet;
  }

  /**
   * Calculates a mismatch score between one GraphOrigin and the other.
   * This currently assumes a sorted, ordered list, not a loose set. 
   * The order of the relations matter (for now)s
   *
   * Note that a smaller score is better, because every mismatch increases the score
   *
   * @return a ranking item consisting of that other variable and the score 
   */
  public RankingItem orderedComparison(GraphOrigin other) {
//    GraphOrigin workingCopy = new GraphOrigin(other); // cloning?
    // this assumes size is the same!
    double score = 0;
//    boolean b = Math.random() < 0.005;

    // normalize score to
    if(this.relationList.size() == other.relationList.size()) {
      for (int i = 0; i < this.relationList.size() - 1; i++) {
//        Relation left = this.relationList.get(i);
//        Relation right = other.relationList.get(i);

//        if(left.relationEnum == RelationEnum.TYPE_ASSIGNMENT) {
//          if (left.relationEnum != right.relationEnum) {
//            score++;
//          }
//          if (!left.destination.equals(right.destination)) {
//            score++;
//          }
//        }
//        else {
        boolean exists = false;
        for(int j = 0; j < other.relationList.size(); j++) {
          Relation left = this.relationList.get(i);
          Relation right = other.relationList.get(i);
          if(left.relationEnum == right.relationEnum &&
             left.destination.equals(right.destination)) {
            exists = true;

          }


//        if (left.relationEnum != RelationEnum.IN_SAME_FUNCTION) {
//            if (left.relationEnum != right.relationEnum) {
//              score = score + 1;
//            }
//            if (!left.destination.equals(right.destination)) {
//              score = score + 1;
//            }
        }
        if (!exists) {
          score = score + 1;
        }
//        }
      }
//      score = score * 2000;


//
//      Relation left = this.relationList.get(this.relationList.size()-1);
//      Relation right = other.relationList.get(other.relationList.size()-1);
//      if(left.relationEnum == right.relationEnum) {
//
//        double varScore = 0.0;
//
//        int tempSize = calculateVarSize(left, right);
//        String biggerOne;
//        String smallerOne;
//        if(tempSize < 0) {
//          biggerOne = right.destination;
//          smallerOne = left.destination;
//          tempSize = -tempSize;
//        }
//        else {
//          biggerOne = left.destination;
//          smallerOne = right.destination;
//        }
//
//
//
//        Set<String> biggerSet = createSet(biggerOne);
//        Set<String> smallerSet = createSet(smallerOne);
//
//        for(String s : biggerSet) {
//          if(!smallerSet.contains(s)) {
//            varScore = varScore + 1.0;
//          }
//        }
//
//
//        // normalize to equal relation size.
//        int size = this.relationList.size();
//        varScore = varScore * 1000.0;
//        int intScore = (int) varScore;
//
//        double oldScore = score;
//        score = score + (size * varScore) / ((double)(tempSize));
//        if(varScore != (tempSize*1000)) {
//          System.out.println(left);
//          System.out.println(right);
//          System.out.println(oldScore + "+" + varScore + "\n" +
//                  score + " out of " + this.relationList.size() * 2000);
//          System.out.println((int)(score));
//        }
//      }
//      else {
//        score = score * 3;
//      }

      // handle multi var here
    }
    else {
      int minSize = Math.min(this.relationList.size(), other.relationList.size());
      int sizeDifference = Math.abs(this.relationList.size() - other.relationList.size());

      double theoreticalMaxSize = (minSize + sizeDifference) * 2000;

      for(int i = 0; i < minSize - 1; i++) {
        Relation left = this.relationList.get(i);
        Relation right = other.relationList.get(i);

        if (left.relationEnum != right.relationEnum) {
          score++;
        }
        if (!left.destination.equals(right.destination)) {
          score++;
        }
        score = sizeDifference + score;
        score = score * 1000;


      }


      Relation left = this.relationList.get(this.relationList.size()-1);
      Relation right = this.relationList.get(this.relationList.size()-1);
      if(left.relationEnum == right.relationEnum) {

        double varScore = 0.0;

        int tempSize = calculateVarSize(left, right);
        String biggerOne;
        String smallerOne;
        if(tempSize < 0) {
          biggerOne = right.destination;
          smallerOne = left.destination;
          tempSize = -tempSize;
        }
        else {
          biggerOne = left.destination;
          smallerOne = right.destination;
        }


        Scanner bigListScanner = new Scanner(biggerOne);
        bigListScanner.useDelimiter("#");
        while(bigListScanner.hasNext()) {
          String leftWord = bigListScanner.next();
          Scanner smallListScanner = new Scanner(smallerOne);

          boolean doesNotHaveWord = true;
          while(smallListScanner.hasNext()) {
            String rightWord = smallListScanner.next();
            if(leftWord.equals(rightWord)) {
              doesNotHaveWord = false;
              break;
            }
          }
          if(doesNotHaveWord) {
            varScore = varScore + 1.0;
          }
          smallListScanner.close();
        }
        bigListScanner.close();

        // normalize to equal relation size.
        int size = this.relationList.size();
        varScore = varScore * 1000;
        int intScore = (int) varScore;


        score = score + (size * varScore) / (tempSize);
      }
      else {
        score = score * 2;
      }
      System.out.println(score + " in of " +theoreticalMaxSize*2);

    }

    // lower score is better. Best score is 0, worst score is (size * 2)


    return new RankingItem(other.variable, (int)(score));
  }
}

/*
  currently used enums

  ASSIGNMENT, DIRECT_CALL, METHOD_CALL, FIELD_ACCESS,
  FUNCTION_NAME, IN_SAME_FUNCTION
 */
