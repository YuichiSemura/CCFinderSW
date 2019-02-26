package aleesa;

import common.FileAndString;
import common.JudgeCharset;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentsRemoverUsingRegex {

    public static void main(String[] args) {
        new CommentsRemoverUsingRegex().runInstance(args);
    }

    private void runInstance(String[] args) {
        String target = "C:\\Users\\y-semura\\Desktop\\Test.java";
        String output = "C:\\Users\\y-semura\\Desktop\\TestSkipped.java";
        String comRegex = "/\\*[\\s\\S]*?\\*/|//((?![\\r\\n])[\\s\\S])*";
        String strRegex = "\\\"((?!\\\")[\\s\\S])*?\\\"";

        String source = "";
        try {
            source = JudgeCharset.readAll(target, "UTF-8");
        } catch (IOException e) {
            System.out.println(e);
        }
        String str = getCommentRemover(source, "UTF-8", comRegex, strRegex);
        FileAndString.writeAll(output, str);
    }

    //通常
    public static String getCommentRemover(String path, String charset, String COM, String STR) {
        String source = "";
        try {
            source = JudgeCharset.readAll(path, charset);
        } catch (IOException e) {
            System.out.println(e);
        }
        return STR.equals("") ? removeComment(source, COM)
                : removeCommentAvoidString(source, COM, STR);
    }

    private static String removeComment(String source, String COM) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(COM).matcher(source);
        while (m.find()) {
            m.appendReplacement(sb, "");
            sb.append(m.group().replaceAll("\\S", " "));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String removeCommentAvoidString(String source, String COM, String STR) {
        String groupName = "str";
        STR = "(?<" + groupName + ">" + STR + ")";
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(COM + "|" + STR).matcher(source);
        while (m.find()) {
            m.appendReplacement(sb, "");
            sb.append(m.group(groupName) == null
                    ? m.group().replaceAll("\\S", " ")
                    : m.group());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    //recursive
    public static String getCommentRemoverRecursive(String path, String charset, String COM, String STR) {
        String source = "";
        try {
            source = JudgeCharset.readAll(path, charset);
        } catch (IOException e) {
            System.out.println(e);
        }
        return STR.equals("") ? removeCommentRecursive(source, COM)
                : removeCommentAvoidStringRecursive(source, COM, STR);
    }

    private static String removeCommentRecursive(String source, String COM) {
        String beforeStr = "";
        Pattern p = Pattern.compile(COM);
        while (!beforeStr.equals(source)) {
            StringBuffer sb = new StringBuffer();
            beforeStr = source;
            Matcher m = p.matcher(source);
            while (m.find()) {
                m.appendReplacement(sb, "");
                sb.append(m.group().replaceAll("\\S", " "));
            }
            m.appendTail(sb);
            source = sb.toString();
        }
        return source;
    }

    private static String removeCommentAvoidStringRecursive(String source, String COM, String STR) {
        String beforeStr = "";
        String groupName = "str";
        STR = "(?<" + groupName + ">" + STR + ")";
        Pattern p = Pattern.compile(COM + "|" + STR);
        while (!beforeStr.equals(source)) {
            StringBuffer sb = new StringBuffer();
            beforeStr = source;
            Matcher m = p.matcher(source);
            while (m.find()) {
                m.appendReplacement(sb, "");
                sb.append(m.group(groupName) == null
                        ? m.group().replaceAll("\\S", " ")
                        : m.group());
            }
            m.appendTail(sb);
            source = sb.toString();
        }
        return source;
    }
}
