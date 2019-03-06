package aleesa;

import aleesa.ANTLRv4Parser.ANTLRv4Lexer;
import aleesa.ANTLRv4Parser.ANTLRv4Parser;
import common.FileAndString;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ALEESA (A Lexical Element Extractor from Syntax definitions of ANTLR)
 */

public class Aleesa {
    private LinkedHashSet<String> keywordList = new LinkedHashSet<>();
    private ArrayList<String> fragmentList = new ArrayList<>();
    private LinkedHashMap<String, ArrayList<String>> lexerExpList = new LinkedHashMap<>();
    private StringBuilder builderSkip = new StringBuilder();
    private StringBuilder builderKeyWord = new StringBuilder();
    private LinkedHashMap<String, ArrayList<String>> stringList = new LinkedHashMap<>();
    private StringBuilder builderString = new StringBuilder();
    private ArrayList<String> filePathList = new ArrayList<>();
    private static String variableRegex = "[0-9a-zA-Z_]";
    private String specialRegex =
            "(\\[" + variableRegex + "+]|"
                    + "'" + variableRegex + "+'|"
                    + variableRegex + "|[()|])+";
    // (\[[0-9a-zA-Z_]+]|'[0-9a-zA-Z_]+'|[0-9a-zA-Z_]|[()|])+

    public boolean isRecursive = false;
    public String skipRegex = "";
    public String reservedRegex = "";
    public String strRegex = "";

    private String language = null;

    public void getGrammar(String g4DirectoryPath) {
        generateOneLanguage(new File(g4DirectoryPath));
        //read skip and reserved
        String skip = builderSkip.toString();
        skipRegex = skip.substring(0, Math.max(skip.length() - 1, 0));
        String reserved = builderKeyWord.toString();
        reservedRegex = reserved.substring(0, Math.max(reserved.length() - 1, 0));
        String str = builderString.toString();
        strRegex = str.substring(0, Math.max(str.length() - 1, 0));

        System.out.println("language = " + language);
        System.out.println((isRecursive ? "recursive " : "") + "skip = " + skipRegex);
        System.out.println("reserved = " + reservedRegex);
        System.out.println("string = " + strRegex);
    }

    public void generateOneLanguage(File aFileList) {
        System.out.println("------------" + aFileList + "------------");
        language = aFileList.getName().substring(aFileList.getName().lastIndexOf("\\") + 1);

        ArrayList<String> fileList = FileAndString.searchDirectory(aFileList.getPath());
        for (String x : fileList) {
            File aFile = new File(x);
            if (aFile.isFile()) {
                int last = x.lastIndexOf(".");
                String tmp = x.substring(last + 1);
                if (tmp.equals("g4")) {
                    filePathList.add(x);
                }
            }
        }

        boolean antlrMessage = true;
        for (String x : filePathList) {
            createParser(x);
            if (antlrMessage) {
                System.out.println();
                antlrMessage = false;
            }
            System.out.println("input = " + x);
        }
        buildRegexAndReserved();
    }

    private void createParser(String someString) {
        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(someString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ANTLRv4Lexer lexer = new ANTLRv4Lexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        ANTLRv4Parser.GrammarSpecContext a = parser.grammarSpec();
        explore(a, 0, false);
    }

    private void buildRegexAndReserved() {
        for (Map.Entry<String, ArrayList<String>> entry : lexerExpList.entrySet()) {
            printValueToRegexList(entry.getKey(), entry.getValue());
        }

        combineRegexToSkip();

        for (String x : keywordList) {
            if (x.matches(specialRegex) && x.length() > 1) {
                builderKeyWord.append(x).append("|");
            }
        }

        checkString();
    }

    private void checkString() {
        ArrayList<String> newKeyList = new ArrayList<>();
        for (String x : stringList.keySet()) {
            for (String y : stringList.get(x)) {
                if (lexerExpList.keySet().contains(y)) {
                    newKeyList.add(y);
                }
            }
        }

        for (String x : stringList.keySet()) {
            if (newKeyList.contains(x)) {
                continue;
            }
            StringBuilder buf = new StringBuilder();
            ArrayList<String> keyList = new ArrayList<>();
            keyList.add(x);
            buildExpressionToSkip(keyList, x, stringList.get(x), buf);
            builderString.append(buf.toString());
            builderString.append("|");
        }
    }

    private void explore(RuleContext ctx, int indentation, boolean isFragment) {
        String ruleName = ANTLRv4Parser.ruleNames[ctx.getRuleIndex()];
        if (!(ctx instanceof ANTLRv4Parser.GrammarSpecContext)) {
            //general
        } else {
            ANTLRv4Parser.GrammarSpecContext gtc = (ANTLRv4Parser.GrammarSpecContext) ctx;
            indentation--;
        }

        String str = ctx.getText();
        if (isSingleQuotation(str) && !isFragment) {
            keywordList.add(doEscape(str).substring(1, str.length() - 1));
        }

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof ANTLRv4Parser.LexerRuleSpecContext) {
                ANTLRv4Parser.LexerRuleSpecContext x = (ANTLRv4Parser.LexerRuleSpecContext) element;
                if (x.lexerRuleBlock() == null) {
                    continue;
                }
                lexerExpList.put(x.TOKEN_REF().getText(), x.lexerRuleBlock().getRegexList());
                if (x.FRAGMENT() != null) {
                    fragmentList.add(x.TOKEN_REF().getText());
                }
                explore((RuleContext) element, indentation + 1, isFragment || x.FRAGMENT() != null);
            } else if (element instanceof ANTLRv4Parser.ParserRuleSpecContext) {
                ANTLRv4Parser.ParserRuleSpecContext x = (ANTLRv4Parser.ParserRuleSpecContext) element;
                explore((RuleContext) element, indentation + 1, isFragment);
            } else {
                if (element instanceof RuleContext) {
                    explore((RuleContext) element, indentation + 1, isFragment);
                }
            }
        }
    }

    private String doEscape(String str) {
        StringBuilder strBuilder = new StringBuilder();
        Pattern p = Pattern.compile("[-\\\\*+.?{}()^$|\\[\\]]");
        Matcher m = p.matcher(str);
        int start = 0;
        int end = 0;
        while (m.find()) {
            end = m.start();
            strBuilder.append(str, start, end);
            strBuilder.append("\\");
            strBuilder.append(m.group());
            start = m.end();
        }
        strBuilder.append(str.substring(start));
        return strBuilder.toString();
    }

    private boolean isCommentInArrayList(ArrayList<String> list) {
        for (String x : list) {
            if (!x.startsWith("->") && x.toLowerCase().contains("comment")) {
                return true;
            }
        }
        return false;
    }

    private String printValueRecursiveToRegexList(ArrayList<String> keyList, ArrayList<String> list) {
        StringBuilder buf = new StringBuilder();
        for (String str : list) {
            if (lexerExpList.containsKey(str) && !lexerExpList.get(str).contains(str) && !keyList.contains(str)) {
                keyList.add(str);
                buf.append(printValueRecursiveToRegexList(keyList, lexerExpList.get(str)));
                keyList.remove(keyList.size() - 1);
                continue;
            }
            if (str.matches("'.*?'")) {
                str = str.substring(1, str.length() - 1);
            }
            buf.append(str);
        }
        return buf.toString();
    }

    private void printValueToRegexList(String key, ArrayList<String> list) {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(key);
        String recursive = printValueRecursiveToRegexList(keyList, list);
        if (recursive.matches(specialRegex) && !fragmentList.contains(key)) {
            keywordList.add(recursive);
        }
    }

    private void combineRegexToSkip() {
        /* combine */
        Pattern p = Pattern.compile("-> *channel\\(.*?(COMMENT).*?\\)");
        Pattern p2 = Pattern.compile(".*[sS][tT][rR][iI][nN][gG].*");
        Pattern p3 = Pattern.compile("(\\[[0-9a-zA-Z_]+]|'[0-9a-zA-Z_]+'|[0-9a-zA-Z_]|[()|])+");
        for (Map.Entry<String, ArrayList<String>> entry : lexerExpList.entrySet()) {
            boolean skip = false;
            if (entry.getValue().get(entry.getValue().size() - 1).equals("->skip")
                    || entry.getValue().get(entry.getValue().size() - 1).equals("->channel(HIDDEN)")
                    || ((entry.getKey().toLowerCase().contains("comment")
                    || p.matcher(entry.getValue().get(entry.getValue().size() - 1)).find()) &&
                    !isCommentInArrayList(entry.getValue()))
                    ) {
                skip = true;
            }

            for (int i = 0; i < entry.getValue().size(); i++) {
                if (entry.getValue().get(i).matches("->.*")) {
                    entry.getValue().remove(i);
                    i--;
                }
            }

            StringBuilder buf = new StringBuilder();
            ArrayList<String> keyList = new ArrayList<>();
            keyList.add(entry.getKey());
            buildExpressionToSkip(keyList, entry.getKey(), entry.getValue(), buf);
            if (skip) {
                String str = buf.toString();
                if (!str.equals(".")) {
                    str = str.replaceAll("\\.", "[\\\\s\\\\S]");
                    builderSkip.append(str);
                    builderSkip.append("|");
                }
            }

            if (p2.matcher(entry.getKey()).find()) {
                if (!p3.matcher(buf.toString()).matches()) {
                    stringList.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void buildExpressionToSkip(ArrayList<String> keyList, String key, ArrayList<String> list, StringBuilder buf) {
        ArrayList<String> newList = new ArrayList<String>();

        /*comment nest*/
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("(")
                    && i + 1 < list.size() && list.get(i + 1).equals("(")
                    && i + 2 < list.size() && key.equals(list.get(i + 2))
                    && i + 3 < list.size() && list.get(i + 3).equals(")")
                    && i + 4 < list.size() && list.get(i + 4).equals("|")
                    && i + 5 < list.size() && list.get(i + 5).equals(".")
                    && i + 6 < list.size() && list.get(i + 6).equals(")")
                    && i + 7 < list.size() && list.get(i + 7).equals("*?")) {
                newList.add("(");
                newList.add("(?!");
                for (int j = 0; j < i; j++) {
                    newList.add(list.get(j));
                }
                newList.add(")[\\s\\S])");
                newList.add("*?");
                i += 7;
                isRecursive = true;
            } else {
                newList.add(list.get(i));
            }
        }

        for (String str : newList) {
            if (str.equals("->skip")) {
                continue;
            }

            if (lexerExpList.containsKey(str) && !lexerExpList.get(str).contains(str) && !keyList.contains(str)) {
                keyList.add(str);
                buildExpressionToSkip(keyList, str, lexerExpList.get(str), buf);
                keyList.remove(keyList.size() - 1);
                continue;
            }
            if (str.matches("'.*?'")) {
                String tmp = str.substring(1, str.length() - 1);
                buf.append(tmp);
                continue;
            }
            if (str.matches("->.*")) {
                break;
            }
            buf.append(str);
        }
    }

    private boolean isSingleQuotation(String str) {
        if (str == null || str.length() == 0 || str.charAt(0) != '\'') {
            return false;
        }
        int i = 1;
        while (i < str.length()) {
            if (str.charAt(i) == '\'') {
                return i == str.length() - 1;
            } else if (str.charAt(i) == '\\') {
                i++;
            }
            i++;
        }
        return false;
    }

}
