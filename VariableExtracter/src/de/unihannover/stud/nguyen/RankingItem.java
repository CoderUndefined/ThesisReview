package de.unihannover.stud.nguyen;

public class RankingItem implements Comparable<RankingItem> {
  String string;
  int popularity;

  public RankingItem(String string, int popularity) {
    this.string = string;
    this.popularity = popularity;
  }

  @Override
  public int compareTo(RankingItem rankingItem) {
    return Integer.compare(popularity, rankingItem.popularity);

  }
}
