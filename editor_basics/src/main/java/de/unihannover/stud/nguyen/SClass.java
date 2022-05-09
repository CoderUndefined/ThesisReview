package de.unihannover.stud.nguyen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class SClass {
  String functionName = null;
  String filename;
  HashMap<String, List<Relation>> stringListHashMap;

  public SClass(String filename) {
    this.filename = filename;
    this.stringListHashMap = new HashMap<>();
  }

  public void add(String origin, RelationEnum relationEnum,
                  String destination) {
    if (!stringListHashMap.containsKey(origin)) {
      stringListHashMap.put(origin, new ArrayList<>());
    }
    stringListHashMap.get(origin).add(new Relation(relationEnum, destination));
  }

  public boolean contains(String string) {
    return stringListHashMap.containsKey(string);
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("def_name: ").append(functionName).append("  rest:\n");
    for (String key : stringListHashMap.keySet()) {
      List<Relation> g = stringListHashMap.get(key);
      stringBuilder.append(key).append("\n");
      stringBuilder.append(g).append("\n");
    }
    return stringBuilder.toString();
  }

  public boolean isEmpty() {
    return stringListHashMap.isEmpty();
  }

  public void setFunctionName(String functionName) {
    this.functionName = functionName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SClass sClass = (SClass) o;
    return Objects.equals(functionName, sClass.functionName) &&
            Objects.equals(filename, sClass.filename) &&
            Objects.equals(stringListHashMap, sClass.stringListHashMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(functionName, filename, stringListHashMap);
  }

  public int temporary() {
    int number = 0;
    for(String key : stringListHashMap.keySet()) {
      List<Relation> relationList = stringListHashMap.get(key);
      number = number + relationList.size();
    }
    return number;
  }
}
