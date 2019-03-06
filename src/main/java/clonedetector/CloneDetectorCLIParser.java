package clonedetector;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import static common.FileRelation.*;
import static common.TokenName.BAR;

@SuppressWarnings("deprecation")
public class CloneDetectorCLIParser {
    private OptionReader or;
    private boolean ccfinder = false;
    private boolean ccfinderx = false;
    private boolean ccfindersw = false;
    private boolean cloneSet = false;
    private boolean noLexer = false;
    private Options opts;
    private final String d = "d";
    private final String l = "l";
    private final String o = "o";
    private final String t = "t";
    private final String w = "w";
    private final String b = "b";
    private final String g = "g";
    private final String antlr = "antlr";
    private final String nolx = "nolx";
    private final String ccf = "ccf";
    private final String ccfx = "ccfx";
    private final String json = "json";
    private final String ccfsw = "ccfsw";
    private final String charset = "charset";
    private final String help = "help";
    private final String p1 = "p1";
    private final String p2 = "p2";

    public CloneDetectorCLIParser(OptionReader or) {
        this.or = or;
    }

    public void addOptions() {
        opts.addOption(help, "help", false,
                "display this help\n" +
                "Refer to github.com/YuichiSemura/CCFinderSW\n" +
                "Example: -help\n");
        opts.addOption(d, "directory", true,
                "directory of source files\nExample: -d src");
        opts.addOption(l, "language", true,
                "language of analyzed files\n" +
                        "Example: -l cpp\n" +
                        "Please prepare 2 files in same directory with this jar\n" +
                        "comment rule file -> \"cpp_comment.txt\"\n" +
                        "                  reserved word file -> \"cpp_reserved.txt\"");
        opts.addOption(o, "outputFile", true,
                "outputFile of clonePair data\nExample: -o outputFile\n" +
                "-> outputFile.txt or outputFile.ccfxd\n");
        opts.addOption(t, "threshold", true,
                "minimum number of tokens of clone pair\nExample: -t 50");
        opts.addOption(w, "detection range", true,
                "detecting only in-file(1) or only file-to-file(2) clone pair \n Example: -w 1");
        opts.addOption(ccf, "ccf", false,
                "output with format of CCFinder(Gemini) \noutputfile.txt\nExample: -ccf");
        opts.addOption(ccfx, "ccfx", false,
                "output with format of CCFinderX(GemX) \noutputfile.ccfxd\nExample: -ccfx");
        opts.addOption(ccfsw, "ccfsw", true,
                "output with format of CCFinderSW \n choose \"pair\" or \"set\"\nExample: -ccfsw pair");
        opts.addOption(json, "json", true,
                "output with format of Rigel(CCAnalyzer) to \"outputFile_ccfsw.json\" \n" +
                        "choose \"+\" or \"-\", about json formatting\n" +
                        "recommend \"+\"\n" +
                "Example: -json +");
        opts.addOption(charset, "charset", true,
                "charset of tokenization set \"utf8\",\"sjis\"");
        opts.addOption(b, "block", false,
                "recognizes indent block and embeds a zero-width hash\n A width of tab is 4 times a space");
        opts.addOption(antlr, "antlr", true,
                "Take comment and reserved word from grammarsv4\\language\\foo.g4\n" +
                "As this argument, you should set Regular Expression of extensions of target source codes.\n" +
                "Example: -antlr h|hh|hpp|hxx|c|cc|cpp|cxx\n" +
                "Example: -antlr py");
        opts.addOption(g, "group", true,
                "grouping of detection \nExample: -g 10 ");
        opts.addOption(nolx, "noLexer", false,
                "use when in exactly the same condition as last\n" +
                "Example: -nolx ");
    }

    public void commandline(String[] args) {
        opts = new Options();
        addOptions();
        BasicParser parser = new BasicParser();
        CommandLine cl;
        HelpFormatter helpFormatter = new HelpFormatter();
        boolean spaceIndent = false;

        try {
            cl = parser.parse(opts, args);

            //error
            if (cl.hasOption(help)) {
                System.out.println("Hello Help Health mode!!");
                throw new ParseException("");
            }

            String directory = cl.getOptionValue(d);
            if (directory == null) {
                System.out.println("No target directory");
                throw new ParseException("");
            }

            String language = cl.getOptionValue(l);
            if (language == null) {
                System.out.println("No language");
                throw new ParseException("");
            }

            String output = cl.getOptionValue(o);
            if (output == null) {
                System.out.println("No output file");
                throw new ParseException("");
            }

            int detectionRange = ALL_RELATION;
            if (cl.getOptionValue(w) != null) {
                detectionRange = Integer.parseInt(cl.getOptionValue(w));
                if (detectionRange < ALL_RELATION || detectionRange > FILE_TO_FILE) {
                    System.out.println("Range mode Error: 0 <= range <= 2");
                    throw new ParseException("");
                }
            }

            int threshold;
            if (cl.getOptionValue(t) != null) {
                threshold = Integer.parseInt(cl.getOptionValue(t));
                if (threshold < 10) {
                    throw new ParseException("threshold < 10");
                }
            } else {
                threshold = 50;
            }

            int group;
            if (cl.getOptionValue(g) != null) {
                group = Integer.parseInt(cl.getOptionValue(g));
                if (group < 1) {
                    throw new ParseException("group < 1");
                }
                System.out.println("group :\"" + group + "\"");
            } else {
                group = 1;
            }

            //standard
            System.out.println("---Options---");
            System.out.println("directory :\"" + directory + "\"");
            System.out.println("language :\"" + language + "\"");
            System.out.println("threshold :\"" + threshold + "\"");
            System.out.println("output file:\"" + output + "\"");

            //output
            if (cl.hasOption(ccf)) {
                System.out.println("CCFinder mode");
                ccfinder = true;
            }

            if (cl.hasOption(ccfx)) {
                System.out.println("CCFX mode");
                ccfinderx = true;
            }

            if (cl.hasOption(ccfsw)) {
                System.out.println("CCFSW mode");
                String tmp = cl.getOptionValue(ccfsw);
                ccfindersw = true;
                switch (tmp) {
                    case "pair":
                        break;
                    case "set":
                    case "class":
                        cloneSet = true;
                        break;
                    default:
                        System.out.println("The arg of -ccfw is incorrect");
                        throw new ParseException("");
                }
            }

            boolean isJson = false;
            if (cl.hasOption(json)) {
                switch (cl.getOptionValue(json)) {
                    case "+":
                        or.setJsonIndent(true);
                        break;
                    case "-":
                        or.setJsonIndent(false);
                        break;
                    default:
                        System.out.println("The arg of -json is incorrect");
                        throw new ParseException("");
                }
                System.out.println("JSON mode");
                isJson = true;
            }

            if (!ccfinder && !ccfinderx && !ccfindersw && !isJson) {
                System.out.println("Gemini mode");
                ccfinder = true;
            }

            if (detectionRange != ALL_RELATION) {
                String x;
                if (detectionRange == INSIDE_FILE) {
                    x = "INSIDE_FILE";
                } else {
                    x = "FILE_TO_FILE";
                }
                System.out.println("Range:" + x);
            }

            if (cl.hasOption(b)) {
                spaceIndent = true;
                System.out.println("recognizes code blocks by indents");
            }

            if (cl.hasOption(charset)) {
                if (cl.getOptionValue(charset) == null) {
                    System.out.println("charset is invalid");
                    throw new ParseException("");
                }
                if (cl.getOptionValue(charset).toLowerCase().equals("sjis")
                        || cl.getOptionValue("charset").toLowerCase().equals("shift-jis")) {
                    System.out.println("charset: \"sjis\"");
                    or.setCharset("Shift-JIS");
                } else if (cl.getOptionValue(charset).toLowerCase().equals("utf8")
                        || cl.getOptionValue("charset").toLowerCase().equals("utf-8")) {
                    System.out.println("charset: \"utf8\"");
                    or.setCharset("UTF-8");
                } else if (cl.getOptionValue(charset).toLowerCase().equals("euc-jp")
                        || cl.getOptionValue("charset").toLowerCase().equals("euc")) {
                    System.out.println("charset: \"euc\"");
                    or.setCharset("EUC-JP");
                } else if (cl.getOptionValue(charset).toLowerCase().equals("auto")) {
                    System.out.println("charset: \"auto\"");
                    or.setCharset("AUTO");
                } else {
                    System.out.println("charset is neither \"utf8\", \"sjis\", \"euc\" nor \"auto\" ");
                    throw new ParseException("");
                }
            }

            if (cl.hasOption(nolx)) {
                System.out.println("no Lexer");
                noLexer = true;
            }

            if (cl.hasOption(antlr)) {
                if (cl.getOptionValue(antlr) == null) {
                    System.out.println("set extension as an argument.\n");
                    throw new ParseException("");
                }
                String extensionRegex = cl.getOptionValue(antlr);
                System.out.println("ANTLR mode");
                or.setANTLRMode(true);
                or.setExtensionRegex(extensionRegex);
            }

            or.setDirectory(directory);
            or.setLanguage(language);
            or.setOutput(output);
            or.setThreshold(threshold);
            or.setDetectionRange(detectionRange);
            or.setCcfinder(ccfinder);
            or.setCcfinderx(ccfinderx);
            or.setCcfindersw(ccfindersw);
            or.setJson(isJson);
            or.setSpaceIndent(spaceIndent);
            or.setGroup(group);
            or.setNoLexer(noLexer);
            or.setCloneSet(cloneSet);

            if (ccfinderx) {
                or.setN(threshold - 2);
            } else {
                or.setN(Math.min(threshold - 2, 20));//
            }

            //TODO Group
            //or.setNoLexer(noLexer);
            //or.setNoLexer(false);
            //or.setGroup(group);
            or.setGroup(1);

        } catch (ParseException e) {
            helpFormatter.setOptionComparator(null);
            StringWriter buf = new StringWriter();
            PrintWriter pw = new PrintWriter(buf);
            helpFormatter.printHelp(pw, helpFormatter.getWidth(), "[D]" + "\n" + BAR, null, opts
                    , helpFormatter.getLeftPadding(), helpFormatter.getDescPadding(), null, false);
            System.out.print(buf.toString());
            System.out.println(BAR);
            System.exit(1);
        }
    }
}
