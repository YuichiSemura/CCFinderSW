package clonedetector;

import clonedetector.classlist.Pre;
import clonedetector.classlist.Token;

import java.util.ArrayList;

import static common.TokenName.IDENTIFIER;
import static common.TokenName.ZERO;

public class ParserEase {

    public ArrayList<Pre> removeTokensC(ArrayList<Pre> tokenList) {

        //関数やらなんやらの削除
        //関数の出入りをindentCountで管理
        //短い関数の削除（セミコロン1個以下1）
        int i = 0;
        int indentCount = 0;
        int semicolonCount = 0;
        int lastStart = 0;
        while (i < tokenList.size()) {
            int line = tokenList.get(i).lineStart + 1;
            switch (tokenList.get(i).token) {
                case "{":
                    indentCount++;
                    i++;
                    break;
                case "}":
                    indentCount--;
                    if (indentCount == 0) {
                        if (i + 1 < tokenList.size() && tokenList.get(i + 1).token.equals(";")) {
                            i++;
                        } else if (semicolonCount < 2) {
                            tokenList.subList(lastStart, i + 1).clear();
                            i = lastStart;
                        } else {
                            semicolonCount = 0;
                            i++;
                            lastStart = i;
                        }
                    } else {
                        i++;
                    }
                    break;
                case ";":
                    //System.out.println(tokenList.get(lastStart).hash + " " + semicolonCount);
                    if (indentCount == 0) {
                        if (semicolonCount < 2) {
                            tokenList.subList(lastStart, i + 1).clear();
                            i = lastStart;
                        }
                        semicolonCount = 0;
                    } else {
                        semicolonCount++;
                        i++;
                    }
                    break;
                default:
                    i++;
                    break;
            }
        }

        i = 0;
        indentCount = 0;
        lastStart = -1;
        while (i < tokenList.size()) {
            switch (tokenList.get(i).token) {
                case "[":
                    if (i + 1 < tokenList.size() && tokenList.get(i + 1).token.equals("]")
                            && i + 2 < tokenList.size() && tokenList.get(i + 2).token.equals("=")) {
                        i += 3;
                        lastStart = i;
                    } else {
                        i++;
                    }
                case "{":
                    if (lastStart != -1) {
                        indentCount++;
                    }
                    i++;
                    break;
                case "}":
                    if (lastStart != -1) {
                        indentCount--;
                        if (indentCount == 0) {
                            tokenList.subList(lastStart, i + 1).clear();
                            i = lastStart;
                            lastStart = -1;
                        }
                    }
                    i++;
                    break;
                default:
                    i++;
                    break;
            }
        }

        //case文のブロック入れる
        i = 0;
        ArrayList<Pre> addList = new ArrayList<>();
        while (i < tokenList.size()) {
            if (tokenList.get(i).token.equals("switch")) {
                addList.add(tokenList.get(i));
                i = caseCheck(tokenList, i + 1, addList);
            } else {
                addList.add(tokenList.get(i++));
            }
        }
        tokenList = addList;

        //assert breakの削除
        lastStart = -1;
        for (i = 0; i < tokenList.size(); i++) {
            switch (tokenList.get(i).token) {
                case "assert":
                    lastStart = i;
                    break;
                case ";":
                    if (lastStart != -1) {
                        tokenList.subList(lastStart, i + 1).clear();
                        i = lastStart;
                    }
                    lastStart = -1;
                    break;
                default:
                    break;
            }
        }

        //特定トークンの削除
        tokenList.removeIf(t -> t.token.equals("struct") || t.token.equals("static") || t.token.equals("inline") || t.token.equals("union"));

        //トークンの結合
        addList = new ArrayList<>();
        i = 0;
        while (i < tokenList.size()) {
            if (tokenList.get(i).type == IDENTIFIER) {
                addList.add(tokenList.get(i));
                Pre prev = addList.get(addList.size() - 1);
                StringBuilder buf = new StringBuilder();
                buf.append(prev.token);
                i++;
                while (i < tokenList.size()
                        && tokenList.get(i).token.equals("-")
                        && tokenList.get(i + 1).token.equals(">")
                        && tokenList.get(i + 2).type == IDENTIFIER) {
                    buf.append(tokenList.get(i).token).append(tokenList.get(i + 1).token).append(tokenList.get(i + 2).token);
                    i += 3;
                }
                prev.lineEnd = tokenList.get(i - 1).lineEnd;
                prev.clmEnd = tokenList.get(i - 1).clmEnd;
                prev.sumEnd = tokenList.get(i - 1).sumEnd;
                prev.token = buf.toString();
            } else {
                addList.add(tokenList.get(i++));
            }
        }
        tokenList = addList;

        return tokenList;
    }

    // switch   hoge{   case   fuga   :   herohero
    //                  case   fuga2  :   herohero
    //                  default       :   herohero
    //              }

    public int caseCheck(ArrayList<Pre> tokenList, int i, ArrayList<Pre> addList) {
        //hoge
        while (i < tokenList.size() && !tokenList.get(i).token.equals("case") && !tokenList.get(i).token.equals("default")) {
            addList.add(tokenList.get(i++));
        }

        //case and default
        while (i < tokenList.size() && (tokenList.get(i).token.equals("case") || tokenList.get(i).token.equals("default"))) {
            addList.add(tokenList.get(i++));

            //fuga
            while (i < tokenList.size() && !tokenList.get(i).token.equals(":")) {
                addList.add(tokenList.get(i++));
            }
            if (i >= tokenList.size()) break;

            addList.add(tokenList.get(i++));

            //block start
            Pre back2 = tokenList.get(i);
            addList.add(new Pre("{", back2.lineStart, back2.clmStart, back2.lineStart, back2.clmStart, ZERO, back2.sumStart, back2.sumStart));
            back2 = null;

            //herohero (switch)
            while (i < tokenList.size()) {
                if (tokenList.get(i).token.equals("case") || tokenList.get(i).token.equals("default") || tokenList.get(i).token.equals("}")) {
                    break;
                }

                if (tokenList.get(i).token.equals("switch")) {
                    addList.add(tokenList.get(i));
                    i = caseCheck(tokenList, i + 1, addList);
                } else if (i + 1 < tokenList.size() && tokenList.get(i).token.equals("break") && tokenList.get(i + 1).token.equals(";")) {
                    back2 = tokenList.get(i);
                    i += 2;
                } else {
                    addList.add(tokenList.get(i++));
                }
            }

            if (i >= tokenList.size()) break;

            //block end
            back2 = back2 == null ? addList.get(addList.size() - 1) : back2;
            addList.add(new Pre("}", back2.lineStart, back2.clmStart, back2.lineStart, back2.clmStart, ZERO, back2.sumStart, back2.sumStart));
        }
        return i;
    }

    public ArrayList<Token> convertPreToken(ArrayList<Pre> preList) {
        ArrayList<Token> newList = new ArrayList<>();
        preList.forEach(p -> newList.add(new Token(p)));
        return newList;
    }
}
