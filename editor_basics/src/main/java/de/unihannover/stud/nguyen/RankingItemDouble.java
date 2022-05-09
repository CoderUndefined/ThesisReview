package de.unihannover.stud.nguyen;

import java.util.Comparator;
import java.util.Objects;

public class RankingItemDouble implements Comparable<RankingItemDouble>{
  String string;
  double ranking;
  int originalRanking;

  public RankingItemDouble(String string, double ranking, int originalRanking) {
    this.string = string;
    this.ranking = ranking;
    this.originalRanking = originalRanking;
  }

  /**
   * Compares this with another RankingItem
   * The primary criteria is the integer, a smaller integer is "smaller".
   * If both are equal then the String itself is used as tiebreaker.
   *
   * @return -1 if ranking is lower, 1 if ranking is higher
   */
  @Override
  public int compareTo(RankingItemDouble rankingItem) {
    if (this.ranking < rankingItem.ranking) {
      return 1;
    }
    else if (this.ranking == rankingItem.ranking) {
      if(this.originalRanking < rankingItem.originalRanking) {
        return -1;
      }
      else if(this.originalRanking == rankingItem.originalRanking) {
        return Objects.compare(this.string, rankingItem.string, Comparator.naturalOrder());
      }
      else {
        return 1;
      }

    }
    else {
      return -1;
    }
  }
}
