package clonedetector.classlist;

import java.util.ArrayList;

/**
 * ある言語における設定を保持するファイル
 * （複数言語を同時に扱う可能性を考えて）
 */
public class LangRuleConstructor {
    public boolean comment = true;
    public boolean reserved = true;
    public boolean doEscape = true;
    public String lineContinue = null;
    public boolean doNest = false;
    public String commentFilePath = null;
    public String reservedFilePath = null;
    public ArrayList<String> reservedWordList;
    public ArrayList<CommentRule> commentRuleList;
    public ArrayList<CommentRule> commentRuleList_Line;
    public ArrayList<CommentRule> literalRuleList;
    public ArrayList<String> extensionList;

    public LangRuleConstructor() {
        reservedWordList = new ArrayList<>();
        commentRuleList = new ArrayList<>();
        commentRuleList_Line = new ArrayList<>();
        literalRuleList = new ArrayList<>();
        extensionList = new ArrayList<>();
    }
}
