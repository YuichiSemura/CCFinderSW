package common;

public class TokenName{
    /**
     * TOKEN Type
     * symbol:1
     * string:2
     * number:3
     * value:4
     * reserve:5
     */
    public final static int ZERO=0;
    public final static int SYMBOL=1;
    public final static int OPERATOR=2;
    public final static int STRING=3;
    public final static int NUMBER=4;
    public final static int IDENTIFIER = 5;
    public final static int RESERVE=6;

    /**
     * use in normalization
     */
    public final static String SHADOW="$";
    public final static String DARK="&";
    public final static String REAPER="=";
    public final static String BOOKER="#";

    /**
     * comment type
     */
    public final static int PRIOR=0;
    public final static int START=1;
    public final static int START_END=2;
    public final static int LINE_START=3;
    public final static int LINE_START_END=4;

    public final static String BAR = "----------------------------------------------------------------------";

    public static int getSHADOWHash(){
        return SHADOW.hashCode();
    }

    public static int getDARKHash(){
        return DARK.hashCode();
    }

    public static int getREAPERHash(){
        return REAPER.hashCode();
    }

    public static int getBOOKERHash(){
        return BOOKER.hashCode();
    }
}
