package ccfindersw;

import clonedetector.CloneDetector;

import java.util.Arrays;

public class CCFSWLauncher {

    /**
     * CCFSW mode
     * <null>, D, d -> CloneDetector.java
     * P, p -> //future works PrettyPrinter(etc. CCFXD to CCFSW or JSON)
     * S, s -> //future works FilteringCloneData
     */

    //black30 red31 green32 yellow33 blue34 magenta35 cyan36 white37
    public static String YELLOW = (char) 27 + "[33m";
    public static String RESET = (char) 27 + "[0m";

    public static final String commandHelp = "CCFinderSW mode\n" +
            "     D, d -> CloneDetector\n" +
            "     P, p -> //future works PrettyPrinter(etc. CCFXD to CCFSW or JSON)\n" +
            "     S, s -> //future works FilteringCloneData\n";

    public static final String caution = YELLOW + "Please input mode option.\n"
            + "\"java -jar CCFinderSW_version.jar D -hoge huga...\"" + RESET;

    public static void main(String[] args) {
        new CCFSWLauncher().run(args);
    }

    public void run(String[] args) {
        System.out.println(CCFSWData.getToolName());
        if (args.length == 0 ||
                (args[0].matches("[a-zA-Z&&[^DdPpSsHh]]") && args[0].length() == 1)) {
            System.out.println(caution);
            System.out.println(commandHelp);
        } else if (args[0].matches("[Dd]")) {
            System.out.println("[D] CloneDetector");
            new CloneDetector().start(Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].matches("[Pp]")) {
            System.out.println("[P] Sorry, this mode isn't available.");
        } else if (args[0].matches("[Ss]")) {
            System.out.println("[S] Sorry, this mode isn't available.");
        } else if (args[0].matches("[Hh]")) {
            System.out.println("[H] Usage Help of CCFinderSW");
            System.out.println(commandHelp);
        } else {
            System.out.println(caution);
            System.out.println("[D] CloneDetector");
            new CloneDetector().start(args);
        }
    }
}
