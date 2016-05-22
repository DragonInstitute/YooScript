package Backend.Generator;

public class Util {

    public static final int INPUT = 0, OUTPUT = 1, THIRD = 2;

    public static String getInput(String[] args, String defaultValue) {
        return getArgs(args, INPUT, defaultValue);
    }

    public static String getOutput(String[] args, String defaultValue) {
        return getArgs(args, OUTPUT, defaultValue);
    }

    public static String getArgs(String[] args, int position, String defaultValue) {
        return getArgs(args, position, defaultValue == null, defaultValue);
    }

    public static String getArgs(String[] args, int position, boolean need, String defaultValue) {
        if (args.length >= (position + 1)) {
            return args[position];
        } else {
            if (need) {
                throw new IllegalArgumentException("Too less args!");
            } else {
                return defaultValue;
            }
        }
    }

}
