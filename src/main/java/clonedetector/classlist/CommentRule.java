package clonedetector.classlist;

public class CommentRule {
    /**
     * comment rule type
     * in TokenName.java
     */
    public int type;
    /**
     * start symbol
     */
    public String start = null;
    /**
     * end symbol
     */
    public String end = null;// nullもあり
    /**
     * boolean allow nest
     */
    public boolean nest;

    public CommentRule(int type, String start, String end, boolean nest) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.nest = nest;
    }

    public CommentRule(int type, String start, String end) {
        this(type,start,end,false);
    }

    public CommentRule(int type, String start) {
        this(type,start,null,false);
    }

    @Override
    public String toString() {
        return "CommentRule [type=" + type + ", start=" + start + ", end=" + end + ", nest=" + nest + "]";
    }
}