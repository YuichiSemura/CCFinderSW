package aleesa;

import clonedetector.classlist.Pre;
import clonedetector.classlist.Token;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static common.TokenName.*;

public class PreProcessEase {

    public int nowLine;// 行数
    private int lastNewLine = 0;
    public ArrayList<Token> tokenList = new ArrayList<>();
    public ArrayList<Pre> preList = new ArrayList<>();
    private String variableRegex = "[0-9a-zA-Z_]+";
    private String reservedRegex;
    private Pattern p = null;

    public PreProcessEase(String reservedRegex, String strRegex) {
        this.reservedRegex = reservedRegex;
        if (strRegex != null && strRegex.length() != 0) {
            p = Pattern.compile(strRegex);
        }
    }

    public void tokenizeANTLRAvoidStr(String str) {
        int i = 0;
        int tmpIndex;
        int startLine;
        int startClm;
        int startIndex;

        while (i < str.length()) {
            char c = str.charAt(i);
            startLine = nowLine;
            startIndex = i;
            startClm = i - lastNewLine;
            // 空白タブ文字無視
            if (c == '\u0020' || c == '\t' || c == '\u00A0' || c == '\u3000') {
                i++;
            }

            // 改行文字
            else if (i != (tmpIndex = isNewLine(str, i))) {
                i = tmpIndex;
            }

            // 英数字
            else if (str.substring(i, i + 1).matches(variableRegex)) {
                i++;
                while (i < str.length()) {
                    if (str.substring(i, i + 1).matches(variableRegex)) {
                        i++;
                    } else {
                        break;
                    }
                }
                tokenRegister(str.substring(startIndex, i), startLine, startClm, nowLine, i - lastNewLine, startIndex, i);
            }

            //変数と記号
            else {
                int index = str.indexOf("\n", i);
                index = index == -1 ? str.length() : index;
                Matcher m = p.matcher(str.substring(i, index));
                if (m.find() && m.start() == 0) {
                    i += m.end();
                    tokenRegister(m.group(), startLine, startClm, nowLine, i - lastNewLine, startIndex, i + m.end(), STRING);
                } else {
                    if ((int) c != 65279) {
                        tokenRegister(str.substring(startIndex, startIndex + 1), startLine, startClm, nowLine, i - lastNewLine, startIndex, i + 1);
                    }
                    i++;
                }
            }
        }
        zeroTokenAdd("eof", nowLine, str.length() - lastNewLine, str.length());
    }

    private int isNewLine(String str, int i) {
        if (i >= str.length()) {
            return i;
        }
        if (str.charAt(i) == '\n') {
            lastNewLine = i + 1;
            nowLine++;
            return i+1;
        } else if (str.charAt(i) == '\r') {
            if (i + 1 < str.length()) {
                if (str.charAt(i + 1) == '\n') {
                    lastNewLine = i + 2;
                    nowLine++;
                    return i+2;
                }
            }
            lastNewLine = i + 1;
            nowLine++;
            return i + 1;
        }
        return i;
    }

    private void tokenRegister(String str, int lineS, int clmS, int lineE, int clmE, int sumStart, int sumEnd) {
        int type = reType(str);
        tokenRegister(str, lineS, clmS, lineE, clmE, sumStart, sumEnd, type);
    }

    private void tokenRegister(String str, int lineS, int clmS, int lineE, int clmE, int sumStart, int sumEnd, int type) {
        tokenListAdd(new Token(str, lineS, clmS, lineE, clmE, type));
        preListAdd(new Pre(str, lineS, clmS, lineE, clmE, type, sumStart, sumEnd));
    }

    private int reType(String str) {
        if (str.substring(0, 1).matches("[0-9]")) {
            return NUMBER;
        } else if (str.matches(reservedRegex)) {
            return RESERVE;
        } else if (str.substring(0, 1).matches(variableRegex)) {
            return IDENTIFIER;
        } else {
            return SYMBOL;
        }
    }

    private void tokenListAdd(Token tmp) {
        tokenList.add(tmp);
    }

    private void zeroTokenAdd(String str, int lineS, int clmS, int sum) {
        tokenListAdd(new Token(str, lineS, clmS, lineS, clmS, ZERO));
        preListAdd(new Pre(str, lineS, clmS, lineS, clmS, ZERO, sum, sum));
    }
    private void preListAdd(Pre tmp) {
        preList.add(tmp);
    }
}
