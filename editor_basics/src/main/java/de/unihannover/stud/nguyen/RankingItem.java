package de.unihannover.stud.nguyen;

import java.util.Comparator;
import java.util.Objects;

/**
 * Basic class which ranks a string. It implements the Comparable interface and can
 * be placed inside a PriorityQueue.
 * Contains the string and an integer used for ranking
 */
class RankingItem implements Comparable<RankingItem> {
  String string;
  int popularity;

  public RankingItem(String string, int popularity) {
    this.string = string;
    this.popularity = popularity;
  }

  /**
   * Compares this with another RankingItem
   * The primary criteria is the integer, a smaller integer is "smaller".
   * If both are equal then the String itself is used as tiebreaker.
   *
   * @return -1 if integer is smaller, 1 if integer is larger.
   */
  @Override
  public int compareTo(RankingItem rankingItem) {
    if (this.popularity < rankingItem.popularity) {
      return -1;
    } else if (this.popularity == rankingItem.popularity) {
      return Objects.compare(this.string, rankingItem.string, Comparator.naturalOrder());
    } else {
      return 1;
    }
  }
}
