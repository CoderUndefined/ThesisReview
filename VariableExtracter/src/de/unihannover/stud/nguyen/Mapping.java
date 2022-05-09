package de.unihannover.stud.nguyen;

import java.util.Objects;

/**
 * Comparable mapping that has a String and a long number.
 * Usually the variable name and how often the varible occurs
 */
class Mapping implements Comparable<Mapping> {
  String string;
  Long number;

  /**
   * Constructor of the Mapping
   *
   * @param string String, usually the variable name
   * @param number long number, usually the number of occurences of that variable
   */
  public Mapping(String string, Long number) {
    this.string = string;
    this.number = number;
  }

  /**
   * Compares a Mapping with another Mapping, based on the long number
   * Note that all results are descending.
   *
   * @param m the other Mapping
   * @return 1 if this mappings number is smaller, 0 if equal otherwise -1
   */
  public int compareTo(Mapping m) {
    if (this.number < m.number) {
      return 1;
    } else if (this.number.equals(m.number)) {
      return this.string.compareTo(m.string);
    } else return -1;
//        return this.string.compareTo(m.string);
  }


  /**
   * Stringifies this class
   *
   * @return "string => number" (ex. x => 14214)
   */
  @Override
  public String toString() {
    return string + "=>" + number;
  }

  /**
   * Basic equals function
   *
   * @param o the other Mapping
   * @return true if equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Mapping mapping = (Mapping) o;
    return Objects.equals(string, mapping.string) && Objects.equals(number, mapping.number);
  }

  /**
   * Basic hashCode function
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(string, number);
  }


  /**
   * Returns the string
   *
   * @return String, usually variable name
   */
  public String getString() {
    return string;
  }

  /**
   * Returns the number
   *
   * @return long number, usually number of occurences of variable
   */
  public Long getNumber() {
    return number;
  }

}
