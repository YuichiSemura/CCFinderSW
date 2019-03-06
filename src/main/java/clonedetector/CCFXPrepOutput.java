package clonedetector;

import clonedetector.classlist.Pre;
import common.FileAndString;

import java.io.File;
import java.util.ArrayList;

import static common.TokenName.*;

public class CCFXPrepOutput {

    private String directoryName;
	private ArrayList<Pre> preList;
	private String language;

	public CCFXPrepOutput(String dn, ArrayList<Pre> preList, String language){
		directoryName = dn;
		this.preList=preList;
		this.language = language;
	}

    /**
     * 見たらわかる
     */
    private String replaceNewLine(String str) {
        return str.replaceAll("\r", "&r;").replaceAll("\n", "&n;");
    }

    /**
     * ccfxprepファイル出力 c,cpp,java以外
     *
     * @param filename 出力先
     */
    public void outputCCFXPrep(String filename) {
        String dirname = directoryName + File.separator + ".ccfxprepdir";
        filename = dirname + filename.substring(directoryName.length()) + "." + language + ".2_0_0_2.default.ccfxprep";

        File newDir;
        if (!filename.substring(dirname.length() + 1).contains(File.separator)) {
            newDir = new File(dirname);
        } else {
            newDir = new File(dirname + filename.substring(dirname.length(), filename.lastIndexOf(File.separator)));
        }
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        StringBuilder buf = new StringBuilder();
        for (Pre aPreList : preList) {
            String newStr = aPreList.token;
            buf.append(Integer.toHexString(aPreList.lineStart + 1));
            buf.append(".");
            buf.append(Integer.toHexString(aPreList.clmStart + 1));
            buf.append(".");
            buf.append(Integer.toHexString(aPreList.sumStart));
            buf.append("\t");

            boolean rare = false;
            for (int i = 0; i < aPreList.token.length(); i++) {
                char a = aPreList.token.charAt(i);
                if ((int) a > 255) {
                    newStr = newStr.replaceFirst(aPreList.token.substring(i, i + 1), "&#x" + Integer.toHexString(a) + ";");
                    rare = true;
                }
            }

            if (aPreList.lineStart == aPreList.lineEnd && !rare) {
                buf.append("+");
                if (aPreList.type != ZERO) {
                    buf.append(Integer.toHexString(aPreList.token.length()));
                } else {
                    buf.append("0");
                }
            } else {
                buf.append(Integer.toHexString(aPreList.lineEnd + 1));
                buf.append(".");
                buf.append(Integer.toHexString(aPreList.clmEnd + 1));
                buf.append(".");
                buf.append(Integer.toHexString(aPreList.sumEnd));
            }
            buf.append("\t");

            if (aPreList.type == IDENTIFIER) {
                buf.append("id|");
                buf.append(replaceNewLine(newStr));
            } else if (aPreList.type == RESERVE) {
                buf.append("r_");
                buf.append(newStr);
            } else if (aPreList.type == STRING) {
                buf.append("l_string|");
                buf.append(replaceNewLine(newStr));
            } else if (aPreList.type == NUMBER) {
                if (newStr.matches("\\w+")) {
                    buf.append("l_int|");
                } else {
                    buf.append("l_float|");
                }
                buf.append(newStr);
            } else if (aPreList.type == SYMBOL || aPreList.type == OPERATOR) {
                switch (newStr) {
                    case ";":
                        buf.append("suffix:semicolon");
                        break;
                    case "(":
                        buf.append("(paren");
                        break;
                    case ")":
                        buf.append(")paren");
                        break;
                    case "{":
                        buf.append("(brace");
                        break;
                    case "}":
                        buf.append(")brace");
                        break;
                    default:
                        buf.append("s_");
                        buf.append(newStr);
                        break;
                }
            } else if (aPreList.type == ZERO) {
                switch (newStr) {
                    case "{":
                        buf.append("(brace");
                        break;
                    case "}":
                        buf.append(")brace");
                        break;
                    default:
                        buf.append(newStr);
                        break;
                }
            } else {
                buf.append(newStr);
            }
            buf.append("\n");
        }
        FileAndString.writeAll(filename, buf.toString());
    }
}
