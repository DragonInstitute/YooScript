package Backend.Generator;

public class Util {
    public static String getInput(String[] args, String defaultValue) {
        if (args.length < 1) {
            if (defaultValue == null) {
                throw new IllegalArgumentException("No input file!");
            } else {
                return defaultValue;
            }
        } else if (args.length >= 1) {
            return args[0];
        }
        return defaultValue;
    }

    public static String getOutput(String[] args, String defaultValue) {
        if (args.length == 2) {
            return args[1];
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("No input file!");
        } else {
            return defaultValue;
        }
    }

}
