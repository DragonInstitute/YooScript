package Backend.Generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class IntermediateTranslator {

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

    private static void regAddVar(int regId, int varId) {
        Vector<Integer> vars = registers.get(regId);
        if (!vars.contains(varId)) {
            vars.add(varId);
        }
    }

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

    private static Vector<Integer> getVarOfReg(int regId) {
        if (!registers.containsKey(regId)) {
            return null;
        }
        return registers.get(regId);
    }

    private static int getEmptyReg() {
        for (Map.Entry<Integer, Vector<Integer>> entry : registers.entrySet()) {
            if (entry.getValue().size() == 0) {
                return entry.getKey();
            }
        }
        return 0;
    }

    private static void varAddRegDesc(int varId, int regId) {
        varAddDesc(varId, regId, RegType);
    }

    private static void varAddVarDesc(int varId) {
        varAddDesc(varId, 0, VarType);
    }

    private static boolean varSavedExceptReg(int varId, int regId) {
        int reg = getRegOfVar(varId);
        // this var is saved in a reg and is not the reg we want to rewrite
        return reg != regId;
    }

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

    private static boolean varHasRegDesc(int varId, int regId) {
        return varHasDesc(varId, regId, RegType);
    }

    private static boolean varHasVarDesc(int varId) {
        return varHasDesc(varId, 0, VarType);
    }

    private static boolean varHasDesc(int varId, int regId, int type) {
        if (!variables.containsKey(varId)) {
            return false;
        }
        int[] previous = variables.get(varId);
        int val = previous[type];

        return type == VarType ? val == varId : val == regId;
    }

    private static int candidateReg() {
        for (Map.Entry<Integer, Vector<Integer>> entry : registers.entrySet()) {
            Vector<Integer> previous = entry.getValue();
            for (Integer val : previous) {
                if (varHasVarDesc(val)) {
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    private static void spill(int regId) {
        Vector<Integer> vars = getVarOfReg(regId);
        if (vars != null) {
            for (Integer varId : vars) {
                System.out.println("save " + varId +" "+ regId);
            }
        }
    }

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
        System.out.println(getReg(15));
    }
}
