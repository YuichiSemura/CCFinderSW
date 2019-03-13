package clonedetector;

import aleesa.Aleesa;
import ccfindersw.CCFSWData;
import clonedetector.classlist.LangRuleConstructor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;

public class OptionReader {

    private static final String toolName = CCFSWData.getToolName();
    private String charset = "UTF-8";
    private String variableRegex
            = "[0-9a-zA-Z_]+";// [0-9a-zA-Z\\u3040-\\u30FF一-龠_]+
    private final String relativePath = "..";
    private int threshold = 50;
    private int N = threshold;
    private String output = null;
    private boolean ccfinder = false;
    private boolean ccfinderx = false;
    private boolean ccfindersw = false;
    private boolean isJson = false;
    private boolean jsonIndent = true;
    private boolean cloneSet = true;
    private int detectionRange = 0;
    private boolean spaceIndent = false;
    private boolean noLexer = false;
    private String directory = null;
    private String language = null;
    private int group = 1;
    private boolean ANTLRMode = false;
    private String extensionRegex;
    private int tks = 0;
    private float rnr = 0;
    public Aleesa als = new Aleesa();

    public HashMap<String, LangRuleConstructor> languageRuleMap = new HashMap<>();
    public ArrayList<String> extensionList = new ArrayList<>();
    public ArrayList<ArrayList<String>> extensionMap = new ArrayList<>();
    public HashMap<String, String> extensionMapTrueEnd = new HashMap<>();

    public void readCommentReservedFiles() {
        Path path = null;
        try {
            path = getApplicationPath(CloneDetector.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String optionFileDirPath = searchCommentReservedDirectory(path.getParent().getParent().toString());
        System.out.println("---List---");
        if (language.toLowerCase().equals("auto")) {
            searchCommentOption(optionFileDirPath);
        } else {
            corwLauncher(optionFileDirPath, language);
        }

        extensionMap.sort((s, t) -> Integer.compare(s.get(1).compareTo(t.get(1)), 0));
        for (int i = extensionMap.size() - 1; i >= 0; i--) {
            extensionMapTrueEnd.put(extensionMap.get(i).get(1), extensionMap.get(i).get(0));
        }
    }

    public String g4Read() {
        Path path = null;
        try {
            path = getApplicationPath(CloneDetector.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return searchCommentReservedDirectory(path.getParent().getParent().toString());
    }

    public static Path getApplicationPath(Class<?> cls) throws URISyntaxException {
        ProtectionDomain pd = cls.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL location = cs.getLocation();
        URI uri = location.toURI();
        return Paths.get(uri);
    }

    public String searchCommentReservedDirectory(String parentDirPath) {
        File cDirectory = new File(parentDirPath);
        File[] fileList = cDirectory.listFiles();
        boolean comment = false, reserved = false;
        try {
            Paths.get(relativePath).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            assert fileList != null;
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    if (aFileList.getName().equals("comment")) {
                        comment = true;
                    } else if (aFileList.getName().equals("reserved")) {
                        reserved = true;
                    }
                }
            }
            if (comment && reserved) {
                return parentDirPath;
            }
            for (File aFileList : fileList) {
                if (aFileList.isDirectory() &&
                        !aFileList.toString().substring(aFileList.toString().lastIndexOf(File.separator) + 1).startsWith(".")) {
                    if (!aFileList.getName().equals("comment") && !aFileList.getName().equals("reserved")) {
                        String thisDirectoryPath = parentDirPath + File.separator + aFileList.getName();
                        String x = searchCommentReservedDirectory(thisDirectoryPath);
                        if (x != null) {
                            return x;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("out");
        }
        return null;
    }

    public void searchCommentOption(String dirPath) {
        File cDirectory = new File(dirPath + File.separator + "comment");
        if (!cDirectory.exists()) {
            System.out.println("failed to find directory");
            System.exit(1);
        }
        File[] fileList = cDirectory.listFiles();
        try {
            Paths.get(relativePath).toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            assert fileList != null;
            for (File aFileList : fileList) {
                if (aFileList.isFile()) {
                    int last = aFileList.getName().lastIndexOf(".");
                    String language = aFileList.toString().substring(8, last);
                    corwLauncher(dirPath, language);
                }
            }
        } catch (IOException e) {
            System.out.println("out");
        }
    }

    public void corwLauncher(String dirPath, String language) {
        LangRuleConstructor nowConstructor = new LangRuleConstructor();
        ReservedWordFileReader rw = new ReservedWordFileReader(language, this);
        CommentOptionFileReader co = new CommentOptionFileReader(language, this);
        rw.run(dirPath, nowConstructor);
        co.run(dirPath, nowConstructor);
        languageRuleMap.put(language, nowConstructor);
    }

    public void ANTLRInitializer() {
        extensionMapTrueEnd.put(getExtensionRegex(), getLanguage());
        LangRuleConstructor lrc = new LangRuleConstructor();
        lrc.reserved = true;
        languageRuleMap.put(getLanguage(), lrc);
        String g4DirectoryPath = g4Read() + File.separator + "grammarsv4" + File.separator + getLanguage();
        als = new Aleesa();
        als.getGrammar(g4DirectoryPath);
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isCcfinderx() {
        return ccfinderx;
    }

    public void setCcfinderx(boolean ccfinderx) {
        this.ccfinderx = ccfinderx;
    }

    public int getDetectionRange() {
        return detectionRange;
    }

    public void setDetectionRange(int detectionRange) {
        this.detectionRange = detectionRange;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getToolname() {
        return toolName;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getVariableRegex() {
        return variableRegex;
    }

    public void setVariableRegex(String regex) {
        this.variableRegex = regex;
    }

    public boolean isCcfindersw() {
        return ccfindersw;
    }

    public void setCcfindersw(boolean ccfindersw) {
        this.ccfindersw = ccfindersw;
    }

    public boolean isCloneSet() {
        return cloneSet;
    }

    public void setCloneSet(boolean cloneSet) {
        this.cloneSet = cloneSet;
    }

    public boolean isSpaceIndent() {
        return spaceIndent;
    }

    public void setSpaceIndent(boolean spaceIndent) {
        this.spaceIndent = spaceIndent;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getExtensionRegex() {
        return extensionRegex;
    }

    public void setExtensionRegex(String extensionRegex) {
        this.extensionRegex = extensionRegex;
    }

    public boolean isNoLexer() {
        return noLexer;
    }

    public void setNoLexer(boolean noLexer) {
        this.noLexer = noLexer;
    }

    public void setANTLRMode(boolean b) {
        this.ANTLRMode = b;
    }

    public boolean isANTLRMode() {
        return ANTLRMode;
    }

    public boolean isCcfinder() {
        return ccfinder;
    }

    public void setCcfinder(boolean ccfinder) {
        this.ccfinder = ccfinder;
    }

    public boolean isJsonIndent() {
        return jsonIndent;
    }

    public void setJsonIndent(boolean jsonIndent) {
        this.jsonIndent = jsonIndent;
    }

    public int getTKS() {
        return tks;
    }

    public void setTKS(int tks) {
        this.tks = tks;
    }

    public float getRNR() {
        return rnr;
    }

    public void setRNR(float rnr) {
        this.rnr = rnr;
    }

}