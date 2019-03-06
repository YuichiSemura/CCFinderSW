package clonedetector;

import clonedetector.classlist.LangRuleConstructor;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 予約語ファイルを読み込むクラス
 */
public class ReservedWordFileReader {

    private OptionReader pr;
    private String filename = "";

    public ReservedWordFileReader(String language, OptionReader or) {
        this.filename = "reserved" + File.separator + language + "_reserved.txt";
        this.pr = or;
    }

    public void run(String dirPath, LangRuleConstructor rule) {
        readFile(dirPath + File.separator + filename, rule);
        rule.reservedFilePath = dirPath + File.separator + filename;
    }

    private void readFile(String inputFileName, LangRuleConstructor rule) {
        File file = new File(inputFileName);
        if (!file.exists()) {
            file = new File(inputFileName);
            if (!file.exists()) {
                System.out.println("No ReservedWord List");
                rule.reserved = false;
                return;
            }
        }
        System.out.println("Load ReservedWordList: " + inputFileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
            String str;
            while ((str = br.readLine()) != null) { // 改行は含まれない
                rule.reservedWordList.add(str);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
