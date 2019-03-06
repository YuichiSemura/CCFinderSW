package common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class JudgeCharset {

    public static String readAll(final String path, String charset) throws IOException {
        if (charset.equals("AUTO")) {
            return readAllAuto(path);
        } else {
            return new String(Files.readAllBytes(Paths.get(path)), Charset.forName(charset));
        }
    }

    public static String readAllAuto(final String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        if (isUTF8(bytes)) {
            return new String(bytes, StandardCharsets.UTF_8);
        } else if (isSJIS(bytes)) {
            return new String(bytes, "Shift_JIS");
        } else if (isEUC(bytes)) {
            return new String(bytes, "EUC_JP");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static boolean isUTF8(byte[] src) {
        return Arrays.equals(new String(src, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8), src);
    }

    public static boolean isSJIS(byte[] src) {
        try {
            return Arrays.equals(new String(src, "Shift_JIS").getBytes("Shift_JIS"), src);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static boolean isEUC(byte[] src) {
        try {
            return Arrays.equals(new String(src, "EUC_JP").getBytes("EUC_JP"), src);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
