package Backend.Generator;


import java.io.*;
import java.util.*;

class DataHelper {

    /**
     * Generate "binary" for SYSVim from intermediate code
     */

    private final static int A = 200;
    private final static int B = 201;
    private final static int C = 202;
    private final static int D = 203;
    private final static int E = 204;
    private final static int F = 205;
    private final static int I = 206;
    private final static int J = 207;

    private final static int RegType = 0;
    private final static int VarType = 1;
    // int =  {var, Reg}
    // Mapping like this
    //  |------------------------|
    //  |  a   |  b  |  c   | d  |
    //  |------------------------|
    //  | a,R1 | b,0 | c,R2 | R4 |
    //  |------------------------|
    private static HashMap<Integer, int[]> variables = new HashMap<>();
    private static HashMap<Integer, Vector<Integer>> registers = new HashMap<>();

    static {
        registers.put(A, new Vector<>());
        registers.put(B, new Vector<>());
        registers.put(C, new Vector<>());
        registers.put(D, new Vector<>());
        registers.put(E, new Vector<>());
        registers.put(F, new Vector<>());
        registers.put(I, new Vector<>());
        registers.put(J, new Vector<>());
    }

    /**
     * Add var description to reg without any check which ensure that the value of them are equal
     */
    private static void regAddVar(int regId, int varId) {
        Vector<Integer> vars = registers.get(regId);
        if (!vars.contains(varId)) {
            vars.add(varId);
        }
    }

    /**
     * Add var description to reg if the value of them are equal
     */
    private static void regPutVar(int regId, int varId) {

    }

    /**
     * return reg has a var or not
     */
    private static boolean regHasVar(int regId, int varId) {
        if (!registers.containsKey(regId)) {
            return false;
        }
        Vector<Integer> registerDescription = registers.get(regId);
        for (Integer i : registerDescription) {
            if (i == varId) {
                return true;
            }
        }
        return false;
    }

    /**
     * return the reg which the var is store according to the var description
     * return 0 if there is not reg description
     */
    private static int getRegOfVar(int varId) {
        for (Map.Entry<Integer, Vector<Integer>> entry : registers.entrySet()) {
            Vector<Integer> previous = entry.getValue();
            for (Integer val : previous) {
                if (val == varId) {
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    /**
     * return a vector which contains all var in the reg
     * the value of them are equal
     */
    private static Vector<Integer> getVarOfReg(int regId) {
        if (!registers.containsKey(regId)) {
            return null;
        }
        return registers.get(regId);
    }

    /**
     * return a empty reg or return 0 if there is not empty reg
     */
    private static int getEmptyReg() {
        for (Map.Entry<Integer, Vector<Integer>> entry : registers.entrySet()) {
            if (entry.getValue().size() == 0) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * replace or add description of a var
     */
    private static void varAddRegDesc(int varId, int regId) {
        varAddDesc(varId, regId, RegType);
    }

    /**
     * remove reg description of a var
     */
    private static boolean varRmRegDesc(int varId) {
        boolean hasKey = variables.containsKey(varId);
        if (hasKey) {
            int[] previous = variables.get(varId);
            previous[2] = 0;

        }
        return hasKey;
    }

    /**
     * the place in the stack of the var add var description
     * to describe that the value of a var is stored
     */
    private static void stackAddVarDesc(int varId) {
        varAddDesc(varId, 0, VarType);
    }

    /**
     * return if there is a reg which store the var or not
     * TODO: REFACTOR, this function may return false always according to the logic
     */
    private static boolean varSavedExceptReg(int varId, int regId) {
        int reg = getRegOfVar(varId);
        // this var is saved in a reg and is not the reg we want to rewrite
        return reg != regId;
    }

    /**
     * basic function of add description for var
     */
    private static void varAddDesc(int varId, int regId, int type) {
        boolean hasKey = variables.containsKey(varId);
        if (hasKey) {
            int[] previous = variables.get(varId);
            previous[type] = regId;
        } else {
            int[] newContainer = {0, 0};
            variables.put(varId, newContainer);
        }
    }

    /**
     * return if there is reg description of a var
     */
    private static boolean varHasRegDesc(int varId, int regId) {
        return varHasDesc(varId, regId, RegType);
    }

    /**
     * return if the place of var in stack have stored the value of the var
     */
    private static boolean stackHasVarDesc(int varId) {
        return varHasDesc(varId, 0, VarType);
    }

    /**
     * basic function of judge description of var
     */
    private static boolean varHasDesc(int varId, int regId, int type) {
        if (!variables.containsKey(varId)) {
            return false;
        }
        int[] previous = variables.get(varId);
        int val = previous[type];

        return type == VarType ? val == varId : val == regId;
    }

    /**
     * return a valid reg which every value of the vars in it was stored
     */
    private static int candidateReg() {
        boolean breaked = false;
        for (Map.Entry<Integer, Vector<Integer>> entry : registers.entrySet()) {
            Vector<Integer> previous = entry.getValue();
            for (Integer val : previous) {
                if (!stackHasVarDesc(val)) {
                    // make sure that
                    breaked = true;
                    break;
                }
            }
            if (!breaked) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * there is not valid reg, so spill the value in a reg to stack and return it
     */
    private static void spill(int regId) {
        Vector<Integer> vars = getVarOfReg(regId);
        if (vars != null) {
            for (Integer varId : vars) {
                System.out.println("save " + varId + " " + regId);
            }
        }
    }

    /**
     * return a valid reg which can be use directly
     */
    private static int getReg(int ins) {
        int y = getRegOfVar(ins);
        if (y == 0) {
            y = getEmptyReg();
            if (y == 0) {
                y = candidateReg();
                if (y == 0) {
                    spill(A);
                    y = A;
                }
            }
        }
        return y;
    }

    public static void main(String[] args) {
        regAddVar(A, 1);
        regAddVar(A, 3);
        regAddVar(A, 2);
        regAddVar(A, 4);
        regAddVar(B, 5);
        regAddVar(C, 6);
        regAddVar(D, 7);
        regAddVar(E, 8);
        regAddVar(F, 9);
        regAddVar(I, 10);
        regAddVar(J, 11);

//        assert(getRegOfVar(3) == 0);
        System.out.println(getRegOfVar(3));
        System.out.println(getRegOfVar(4));
        System.out.println(getRegOfVar(100));
//        assert getRegOfVar(4) == A;
//        assert getRegOfVar(100) == 0;
        System.out.println(getReg(15));
    }
}

public class IntermediateTranslator {

    // TODO: Pattern failed should push back code
    // HashTable <LabelId, InstructionPosition(line)>
    private Hashtable<Integer, Integer> labels = new Hashtable<>();
    private Scanner scanner;
    private FileWriter writer;
    private int line = 1;
    private String next;
    private static final boolean DEBUG = true;
    private DataHelper dataHelper = new DataHelper();
    private File file;

    private IntermediateTranslator(String input, String output) {
        try {
            file = new File(input);
            scanner = new Scanner(file);
            writer = new FileWriter(output);
        } catch (IOException e) {
            System.out.println("Build translator failed");
            System.out.println(e.toString());
        }
    }

    private void next() {
        next = scanner.next();
    }

    public static void main(String[] args) {
        String input = "test.inter";
        IntermediateTranslator translator = new IntermediateTranslator(input, "out.sysvim");
        translator.buildLabelTable();
        translator.labelEmit();
        translator.labelEmit();
        translator.gotoEmit();
        translator.gotoEmit();
        translator.gotoEmit();
        translator.labelEmit();
        translator.ifEmit();
    }

    private void buildLabelTable() {
        while (scanner.hasNext()) {
            labelEmit();
        }
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }

    private boolean labelEmit() {
        next();
        int label;
        if ((label = readLabel()) != 0) {
            if (next.charAt(next.length() - 1) == ':') {
                labels.put(label, line);
                log("label " + label + " is saved");
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean gotoEmit() {
        next();
        switch (next) {
            case "goto":
                next();
                int label = readLabel();
                emit("jump " + labels.get(label) + " nop");
                line++;
                return true;
            default:
                return false;
        }
    }

    private boolean ifEmit() {
        next();
        if (next.startsWith("if")) {
            if (next.length() == 2) {
                log("if");
            } else if (next.length() == 7 && next.substring(next.length() - 5).equals("false")) {
                log("iffalse");
            } else {
                idEmit();
            }
        }
        return true;
    }

    private boolean conditionEmit(){
        return true;
    }

    private boolean idEmit(){
        return true;
    }

    private int readLabel() {
        if (next.charAt(0) == 'L') {
            int i = 1;
            int label = 0;
            while (next.length() > i && Character.isDigit(next.charAt(i))) {
                label = label * 10 + Integer.valueOf(Character.valueOf(next.charAt(i)).toString());
                i++;
            }
            log("" + label);
            return label;
        } else {
            return 0;
        }
    }

    private void eval() {
        try {
            while (scanner.hasNext()) {
                emit(scanner.next());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void emit(String code) {
        System.out.println(code);
    }

    private void log(String info) {
        if (DEBUG)
            System.out.println(info);
    }

}