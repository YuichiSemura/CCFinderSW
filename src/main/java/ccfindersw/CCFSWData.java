package ccfindersw;

public class CCFSWData {
    private static final String Name = "CCFinderSW";
    private static final String Version = "1.0";

    public static String getVersion() {
        return Version;
    }

    public static String getName() {
        return Name;
    }

    public static String getToolName() {
        return Name + " " + Version;
    }
}
