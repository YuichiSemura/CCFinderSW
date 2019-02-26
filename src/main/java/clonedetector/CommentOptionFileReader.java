package clonedetector;

import clonedetector.classlist.CommentRule;
import clonedetector.classlist.LangRuleConstructor;

import java.io.*;
import java.util.ArrayList;

import static common.TokenName.*;

/**
 * コメントファイルを読み込むクラス
 */
public class CommentOptionFileReader {

    String language;
    private String filename = "";
    OptionReader or;

    public CommentOptionFileReader(String language, OptionReader or) {
        this.language = language;
        this.filename = "comment" + File.separator + language + "_comment.txt";
        this.or = or;
    }

    public void run(String dirPath, LangRuleConstructor rule) {
        rule.commentFilePath = dirPath + File.separator + filename;
        readFile(dirPath + File.separator + filename, rule);
    }

    public void readFile(String inputFileName, LangRuleConstructor rule) {
        File file = new File(inputFileName);
        //System.out.println(inputFileName);
        if (!file.exists()) {
            System.out.println("No CommentRuleList");
            rule.comment = false;
            return;
        } else {
            System.out.println("Load CommentRuleList: " + inputFileName);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String str;
            while ((str = br.readLine()) != null) { // 改行は含まれない
                boolean notCommentOut = true;
                if (str.substring(0, 1).equals("#") || str.substring(0, 1).equals("%")) {
                    if (str.substring(0, 1).equals("%")) {
                        notCommentOut = false;
                    }

                    if (str.substring(1, 6).equals("start") && str.length() < 7) {
                        CommentRule x = new CommentRule(START,
                                br.readLine());
                        if (notCommentOut) rule.commentRuleList.add(x);
                    } else if (str.substring(1, 6).equals("prior")
                            || (str.substring(1, 8).equals("literal") && str.length() < 9)) {
                        CommentRule x = new CommentRule(PRIOR,
                                br.readLine(),
                                br.readLine());
                        if (notCommentOut) rule.literalRuleList.add(x);
                    } else if (str.substring(1, 9).equals("startend") && str.length() < 10) {
                        CommentRule x = new CommentRule(START_END,
                                br.readLine(),
                                br.readLine());
                        if (notCommentOut) rule.commentRuleList.add(x);
                    } else if (str.substring(1, 10).equals("extension")) {
                        if (notCommentOut) {
                            String x = br.readLine();
                            or.extensionList.add(x);
                            ArrayList<String> y = new ArrayList<>();
                            y.add(language);
                            y.add(x);
                            or.extensionMap.add(y);
                        }
                    } else if (str.substring(1, 10).equals("linestart") && str.length() < 11) {
                        CommentRule x = new CommentRule(LINE_START,
                                br.readLine());
                        if (notCommentOut) rule.commentRuleList_Line.add(x);
                    } else if (str.substring(1, 13).equals("startendnest")) {
                        CommentRule x = new CommentRule(START_END,
                                br.readLine(),
                                br.readLine(),
                                true);
                        rule.doNest = true;
                        if (notCommentOut) rule.commentRuleList.add(x);
                    } else if (str.substring(1, 13).equals("linestartend")) {
                        CommentRule x = new CommentRule(LINE_START_END,
                                br.readLine(),
                                br.readLine());
                        if (notCommentOut) rule.commentRuleList_Line.add(x);
                    } else if (str.substring(1, 13).equals("linecontinue")) {
                        String x = br.readLine();
                        if (notCommentOut) rule.lineContinue = x;
                    } else if (str.substring(1, 14).equals("variableregex")) {
                        String x = br.readLine();
                        if (notCommentOut) or.setVariableRegex(x);
                    } else if (str.substring(1, 16).equals("literalverbatim")) {
                        CommentRule x = new CommentRule(PRIOR,
                                br.readLine(),
                                br.readLine(),
                                true);
                        if (notCommentOut) rule.literalRuleList.add(x);
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException | IOException e) {
            e.printStackTrace();
            System.err.println("Syntax error in commentfile");
            System.exit(1);
        }
    }

}
