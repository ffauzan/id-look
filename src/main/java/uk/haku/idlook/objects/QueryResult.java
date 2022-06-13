package uk.haku.idlook.objects;


public class QueryResult implements Comparable<QueryResult> {
    public int Id;
    public String Name;
    public String ItemType;
    public int Score;

    public QueryResult(int id, String Name, String itemType, int score) {
        this.Id = id;
        this.Name = Name;
        this.ItemType = itemType;
        this.Score = score;
    }

    public int getScore() {
        return this.Score;
    }

    @Override public int compareTo(QueryResult compareq) {
        int compareScore = ((QueryResult)compareq).getScore();
        return compareScore - this.Score;
    }
}
