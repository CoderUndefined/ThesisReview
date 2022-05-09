package de.unihannover.stud.nguyen;

import java.util.Objects;

/**
 * Basic class that represents the last two things in a triple.
 * The first part is the variable which is already in graphOrigin
 */
public class Relation {
  RelationEnum relationEnum;
  String destination;

  /**
   * Basic constructor, assigning variables
   * 
   * @param relationEnum a RelationEnum ENUM that is the middle of the triple
   * @param destination a String that represents the last piece of the triple
   */
  public Relation(RelationEnum relationEnum, String destination) {
    this.relationEnum = relationEnum;
    this.destination = destination;
  }

  /**
   * Basic toString() function. 
   *
   * @return a stringified representation of this Relation class
   */
  @Override
  public String toString() {
    return "Relation{" +
            "relationEnum=" + relationEnum +
            ", destination='" + destination + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Relation relation = (Relation) o;
    return relationEnum == relation.relationEnum && Objects.equals(destination, relation.destination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relationEnum, destination);
  }
}
