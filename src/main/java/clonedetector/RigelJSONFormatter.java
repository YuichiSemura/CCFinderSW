package clonedetector;

import ccfindersw.CCFSWData;
import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import clonedetector.classlist.TokenData;
import common.PrintProgress;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class RigelJSONFormatter {

    private FileData fd;
    private NGramFinder nf;
    private String filename;
    private String directoryPath;
    private String language;
    private boolean jsonIndent;
    private int threshold;
    private ClonePairData cpd;

    public RigelJSONFormatter(FileData fd, NGramFinder nf, OptionReader or, ClonePairData cpd) {
        this.fd = fd;
        this.nf = nf;
        this.filename = or.getOutput();
        this.threshold = or.getThreshold();
        this.language = or.getLanguage();
        this.jsonIndent = or.isJsonIndent();
        this.directoryPath = or.getDirectory();
        this.cpd = cpd;
    }

    public void outputRigelJSONFormatter() {
        String filename = this.filename + "_ccfsw.json";
        System.out.println("json file = " + filename);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8), 1048576)) {
            bw.write("{");

            int indentCount = 0;
            //environment and file
            optionRuleFile(bw, jsonIndent, indentCount + 1);

            // クローンペア部
            clonePair(bw, jsonIndent, indentCount + 1);
            if (jsonIndent) {
                bw.write("\n");
            }
            bw.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void optionRuleFile(BufferedWriter bw, boolean indent, int indentCount) throws IOException {
        // オプション部
		/*
		"environment" : {
		 	"clone_detector" : {
				"name" : "",
				"version" : "",
				"parameters" : {
		 			"threshold" : "50",
		 			"language" : "cpp"
				}
			},
			"target" : {
				"project_root" : "path"
			}
		},
		*/
        StringBuilder buf = new StringBuilder();
        //environment
        //indent1
        indentAppend(buf, indent, indentCount);
        buf.append("\"environment\":").append("{");
        ++indentCount;
        //indent2
        indentAppend(buf, indent, indentCount);
        buf.append("\"clone_detector\":").append("{");
        ++indentCount;
        //indent3
        indentAppend(buf, indent, indentCount);
        buf.append("\"name\":").append("\"").append(CCFSWData.getName()).append("\"").append(",");
        indentAppend(buf, indent, indentCount);
        buf.append("\"version\":").append("\"").append(CCFSWData.getVersion()).append("\"").append(",");
        indentAppend(buf, indent, indentCount);
        buf.append("\"parameters\":").append("{");
        ++indentCount;
        //indent4
        indentAppend(buf, indent, indentCount);
        buf.append("\"threshold\":").append(threshold).append(",");
        indentAppend(buf, indent, indentCount);
        buf.append("\"language\":").append("\"").append(language).append("\"");
        --indentCount;
        //indent3
        indentAppend(buf, indent, indentCount);
        buf.append("}");
        --indentCount;
        //indent2
        indentAppend(buf, indent, indentCount);
        buf.append("},");
        indentAppend(buf, indent, indentCount);
        buf.append("\"target\":").append("{");
        ++indentCount;
        //indent3
        indentAppend(buf, indent, indentCount);
        buf.append("\"project_root\":").append("\"").append(directoryPath.replaceAll("\\\\", "\\\\\\\\")).append("\"");
        --indentCount;
        //indent2
        indentAppend(buf, indent, indentCount);
        buf.append("}");
        --indentCount;
        //indent1
        indentAppend(buf, indent, indentCount);
        buf.append("},");

		/*
		"file_table" : [
			{
				"id" : 0,
				"path" : "path"
			},
			{
				"id" : 1,
				"path" : "path"
			}
		],
		*/
        //indent1
        indentAppend(buf, indent, indentCount);
        buf.append("\"file_table\":").append("[");
        ++indentCount;
        for (int j = 0; j < fd.filePathList.size(); j++) {
            //indent2
            indentAppend(buf, indent, indentCount);
            buf.append("{");
            ++indentCount;
            //indent3
            indentAppend(buf, indent, indentCount);
            buf.append("\"id\":").append(j).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"path\":").append("\"").append(fd.fileNameList.get(j).replaceAll("\\\\", "\\\\\\\\")).append("\"");
            --indentCount;
            //indent2
            indentAppend(buf, indent, indentCount);
            buf.append("}");
            if (j != fd.filePathList.size() - 1) {
                buf.append(",");
            }
        }
        --indentCount;
        //indent1
        indentAppend(buf, indent, indentCount);
        buf.append("],");
        bw.write(buf.toString());
    }

    private void clonePair(BufferedWriter bw, boolean indent, int indentCount) throws IOException {
	    /*
        "clone_pairs" : [
            {
                "similarity" : 100,
                "fragment1" : {
                    "file_id" : 0,
                    "begin" : 0,
                    "end" : 5
                },
                "fragment2" : {
                    "file_id" : 1,
                    "begin" : 1,
                    "end" : 5
                }
            },
            {
                "similarity" : 100,
                "fragment1" : {
                    "file_id" : 0,
                    "begin" : 0,
                    "end" : 5
                },
                "fragment2" : {
                    "file_id" : 0,
                    "begin" : 6,
                    "end" : 10
                }
            }
        ]
        */
        PrintProgress ps = new PrintProgress(2);
        //indent1
        if (indent) {
            bw.write("\n");
            for (int i = 0; i < indentCount; i++) bw.write("\t");
        }
        bw.write("\"clone_pairs\":[");
        ++indentCount;
        int index = 0;
        for (int[] x : cpd.pairListTrue) {
            StringBuilder buf = new StringBuilder();
            int distance = x[2];
            //fragment1
            TokenData forward = nf.tokenList[x[0] - distance + 1];
            int fileNum1 = forward.file;
            int lineNum11 = forward.lineStart;
            TokenData backward = nf.tokenList[x[0]];
            int lineNum12 = backward.lineEnd;

            //fragment2
            TokenData forward2 = nf.tokenList[x[1] - distance + 1];
            int fileNum2 = forward2.file;
            int lineNum21 = forward2.lineStart;
            TokenData backward2 = nf.tokenList[x[1]];
            int lineNum22 = backward2.lineEnd;
            //indent2
            indentAppend(buf, indent, indentCount);
            buf.append("{");
            indentCount++;
            //indent3
            indentAppend(buf, indent, indentCount);
            buf.append("\"similarity\":").append("100").append(",");
//            indentAppend(buf, indent, indentCount);
//            buf.append("\"clone_ID\":").append(x[3]).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"fragment1\":{");
            indentCount++;
            //indent4
            indentAppend(buf, indent, indentCount);
            buf.append("\"file_id\":").append(fileNum1).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"begin\":").append(lineNum11).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"end\":").append(lineNum12);
            --indentCount;
            //indent3
            indentAppend(buf, indent, indentCount);
            buf.append("},");
            indentAppend(buf, indent, indentCount);
            buf.append("\"fragment2\":{");
            indentCount++;
            //indent4
            indentAppend(buf, indent, indentCount);
            buf.append("\"file_id\":").append(fileNum2).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"begin\":").append(lineNum21).append(",");
            indentAppend(buf, indent, indentCount);
            buf.append("\"end\":").append(lineNum22);
            --indentCount;
            //indent3
            indentAppend(buf, indent, indentCount);
            buf.append("}");
            --indentCount;
            //indent2
            indentAppend(buf, indent, indentCount);
            buf.append("}");
            if (index++ != cpd.pairListTrue.length - 1) {
                buf.append(",");
            }
            ps.plusProgress(cpd.pairListTrue.length);
            bw.write(buf.toString());
        }
        --indentCount;
        if (indent) {
            bw.write("\n");
            for (int i = 0; i < indentCount; i++) bw.write("\t");
        }
        bw.write("]");
    }

    private void indentAppend(StringBuilder buf, boolean indent, int indentCount) {
        if (!indent) return;
        buf.append("\n");
        for (int i = 0; i < indentCount; i++) {
            buf.append("\t");
        }
    }
}
