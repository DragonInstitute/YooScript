package Backend.Generator;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

import static Backend.Generator.Util.getInput;
import static Backend.Generator.Util.getOutput;

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

    private final static int EX = 208;
    private final static int EXA = 209;
    private final static int IP = 210;
    private final static int SP = 211;
    private final static int FLAG = 212; // flag
    private final static int REGISTER_SIZE = 214;

    private final static int RegType = 0;
    private final static int VarType = 1;
    // int =  {var, Reg}
    // Mapping like this
    //  |------------------------|
    //  |  a   |  b  |  c   | d  |
    //  |------------------------|
    //  | a,R1 | b,0 | c,R2 | R4 |
    //  |------------------------|
    private HashMap<Integer, int[]> variables = new HashMap<>();
    private HashMap<Integer, Vector<Integer>> registers = new HashMap<>();

    {
        registers.put(A, new Vector<>());
        registers.put(B, new Vector<>());
        registers.put(C, new Vector<>());
        registers.put(D, new Vector<>());
        registers.put(E, new Vector<>());
        registers.put(F, new Vector<>());
        registers.put(I, new Vector<>());
        registers.put(J, new Vector<>());
    }

    public int getFlag() {
        return FLAG;
    }

    public void newVar(int varId) {
        int[] val = {0, 0};
        variables.put(varId, val);
    }

    public void newVar(int varId, int[] val) {
        variables.put(varId, val);
    }

    /**
     * Add var description to reg without any check which ensure that the value of them are equal
     */
    public void regAddVar(int regId, int varId) {
        Vector<Integer> vars = registers.get(regId);
        if (!vars.contains(varId)) {
            vars.add(varId);
        }
    }

    /**
     * return reg has a var or not
     */
    public boolean regHasVar(int regId, int varId) {
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
    private int getRegOfVar(int varId) {
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
    public Vector<Integer> getVarOfReg(int regId) {
        if (!registers.containsKey(regId)) {
            return null;
        }
        return registers.get(regId);
    }

    /**
     * return a empty reg or return 0 if there is not empty reg
     */
    public int getEmptyReg() {
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
    public void varAddRegDesc(int varId, int regId) {
        varAddDesc(varId, regId, RegType);
    }

    /**
     * remove reg description of a var
     */
    public boolean varRmRegDesc(int varId) {
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
    public void stackAddVarDesc(int varId) {
        varAddDesc(varId, 0, VarType);
    }

    /**
     * return if there is a reg which store the var or not
     * TODO: REFACTOR, this function may return false always according to the logic
     */
    public boolean varSavedExceptReg(int varId, int regId) {
        int reg = getRegOfVar(varId);
        // this var is saved in a reg and is not the reg we want to rewrite
        return reg != regId;
    }

    /**
     * basic function of add description for var
     */
    public void varAddDesc(int varId, int regId, int type) {
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
    public boolean varHasRegDesc(int varId, int regId) {
        return varHasDesc(varId, regId, RegType);
    }

    /**
     * return if the place of var in stack have stored the value of the var
     */
    public boolean stackHasVarDesc(int varId) {
        return varHasDesc(varId, 0, VarType);
    }

    /**
     * basic function of judge description of var
     */
    public boolean varHasDesc(int varId, int regId, int type) {
        if (!variables.containsKey(varId)) {
            return false;
        }
        int[] previous = variables.get(varId);
        int val = previous[type];

        return type == VarType ? val == varId : val == regId;
    }

    /**
     * return a valid reg which every value of the vars in it was stored
     * the reg is "SAFE" to be reuse
     */
    public int safeReg() {
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
    public void spill(int regId) {
        Vector<Integer> vars = getVarOfReg(regId);
        if (vars != null) {
            for (Integer varId : vars) {
                System.out.println("save " + varId + " " + regId);
            }
        }
    }

    /**
     * return a valid reg which can be use directly according to var
     * for variable
     */
    public int getReg(int var) {
        int y = getRegOfVar(var);
        if (y == 0) {
            y = getEmptyReg();
            if (y == 0) {
                y = safeReg();
                if (y == 0) {
                    spill(A);
                    y = A;
                }
            }
        }
        return y;
    }

    /**
     * return a valid reg which can be use directly according to nothing
     * for const
     */
    public int getReg() {
        int y;
        y = getEmptyReg();
        if (y == 0) {
            y = safeReg();
            if (y == 0) {
                spill(A);
                y = A;
            }
        }
        return y;
    }

    public int[] getDescOfVar(int varId) {
        return variables.get(varId);
    }

    public void updateDescOfVar(int varId, int[] val) {
        if (variables.containsKey(varId)) {
            // should NOT put it anyway, in case cause some unexpected exception
            variables.put(varId, val);
        } else {
            System.out.println("not such variable " + varId);
        }
    }

    public boolean stackHasVar(int varId) {
        return variables.containsKey(varId);
    }

    public static void main(String[] args) {
        DataHelper dataHelper = new DataHelper();

        dataHelper.regAddVar(A, 1);
        dataHelper.regAddVar(A, 3);
        dataHelper.regAddVar(A, 2);
        dataHelper.regAddVar(A, 4);
        dataHelper.regAddVar(B, 5);
        dataHelper.regAddVar(C, 6);
        dataHelper.regAddVar(D, 7);
        dataHelper.regAddVar(E, 8);
        dataHelper.regAddVar(F, 9);
        dataHelper.regAddVar(I, 10);
        dataHelper.regAddVar(J, 11);

//        assert(getRegOfVar(3) == 0);
        System.out.println(dataHelper.getRegOfVar(3));
        System.out.println(dataHelper.getRegOfVar(4));
        System.out.println(dataHelper.getRegOfVar(100));
//        assert getRegOfVar(4) == A;
//        assert getRegOfVar(100) == 0;
        System.out.println(dataHelper.getReg(15));
    }
}

public class IntermediateTranslator {

    private enum Condition {
        IF, IFFALSE
    }

    private enum Type {
        CONDITION, ASSIGN, GOTO, LABEL, VARIABLE, CONST
    }

    private static final boolean DEBUG = false, RELEASE = true;

    // TODO: Pattern failed should push back code
    // HashTable <LabelId, InstructionPosition(lineNumber)>
    private Hashtable<Integer, Integer> labels = new Hashtable<>();
    private Scanner scanner;
    private FileWriter writer;
    private int lineNumber = 1;
    private String current;
    private String next;
    private DataHelper dataHelper = new DataHelper();
    private File file;
    private Hashtable<String, Integer> varMap = new Hashtable<>();
    private int varId = 1;
    private LineNumberReader lineNumberReader;
    // map relationship
    // varName -> varId -> hashcode -> stack / reg  -> value
    //   "a"   ->  var3 -> 34523524 -> var3, <0, 10>
    //   "1"   ->  var5 -> 45365564 -> var5, <0,  1>

    private IntermediateTranslator(String input, String output) {
        try {
            file = new File(input);
            writer = new FileWriter(output);
            PrintStream ps=new PrintStream(new FileOutputStream(output));
            System.setOut(ps);
            lineNumberReader = new LineNumberReader(new FileReader(file));
            scanner = new Scanner(lineNumberReader.readLine());
            next = scanner.next();

        } catch (IOException e) {
            System.out.println("Build translator failed");
            System.out.println(e.toString());
        }
    }

    // TODO: 16-5-14 after every function ended current should be the next char

    public static void main(String[] args) {
        String input = getInput(args, null);
        String output = getOutput(args, "out.sysvim");
        IntermediateTranslator translator = new IntermediateTranslator(input, output);
        translator.buildLabelTable();
        translator.eval();
    }

    private void next() {
        try {
            if (!scanner.hasNext()) {
                String line = lineNumberReader.readLine();
                if (line == null) {
                    current = null;
                    return;
                }
                scanner = new Scanner(line);
                lineNumber++;
            }
            current = next;
            next = scanner.next();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private void eval() {
        do {
            switch (judge()) {
                case LABEL:
                    log("Var");
                    labelEmit();
                    break;
                case CONDITION:
                    log("Condition");
                    ifEmit();
                    break;
                case ASSIGN:
                    log("Assign");
                    assignEmit();
                    break;
                case GOTO:
                    log("Goto");
                    gotoEmit();
            }
        } while (current != null);
    }

    private Type judge() {
        if (current.charAt(0) == 'L' && current.charAt(current.length() - 1) == ':') {
            return Type.LABEL;
        } else if (current.equals("if") || current.equals("iffalse")) {
            return Type.CONDITION;
        } else if (current.equals("goto")) {
            return Type.GOTO;
        } else {
            return Type.ASSIGN;
        }
    }

    private void buildLabelTable() {
        String line;
        try {
            while ((line = lineNumberReader.readLine()) != null) {
                lineNumber++;
                scanner = new Scanner(line);

                while (scanner.hasNext()) {
                    labelEmit();
                }
            }
            lineNumberReader = new LineNumberReader(new FileReader(file));
            scanner = new Scanner(lineNumberReader.readLine());
            current = scanner.next();
            next = scanner.next();
            lineNumber = 1;
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private boolean labelEmit() {
        next();
        int label;
        if ((label = readLabel()) != 0) {
            if (current.charAt(current.length() - 1) == ':') {
                labels.put(label, lineNumber);
                log("label " + label + " is saved in lineNumber " + lineNumber);
            }
            return true;
        } else {
            return false;
        }
    }

    private void gotoEmit() {
        next();
        int label = readLabel();
        emit("jump " + labels.get(label) + " nop");
        lineNumber++;
        next();
    }

    private void ifEmit() {
        switch (current) {
            case "if":
                conditionEmit(Condition.IF);
                break;
            case "iffalse":
                conditionEmit(Condition.IFFALSE);

        }
        next();
    }

    private void conditionEmit(Condition condition) {
        boolEmit();
        match("goto");
        int label = readLabel();
        switch (condition) {
            case IF:
                emit("if " + labels.get(label) + " nop");
                break;
            case IFFALSE:
                emit("ifn " + labels.get(label) + " nop");
                break;
        }
    }

    private void match(String s) {
        next();
        if (current.equals(s)) {
            next();
        } else {
            throw new IllegalArgumentException("unexpected " + current + ", expected " + s);
        }
    }

    private void boolEmit() {
        operatorEmit(-1);
    }

    private void assignEmit() {
        int reg = idEmit();
        next();
        if (!current.equals("=")) {
            throw new IllegalArgumentException("expected = but receive " + current + " in line " + lineNumber);
        }
        operatorEmit(reg);
    }

    private int idEmit() {
        int hash = this.newMappingOrGetHash(current);
        if (!dataHelper.stackHasVar(hash)) {
            dataHelper.newVar(hash);
        }
        int reg = dataHelper.getReg(hash);
        dataHelper.regAddVar(reg, hash);
        return reg;
    }

    private int newMappingOrGetHash(String key) {
        int hash;
        if (!varMap.containsKey(key)) {
            String var = "var" + varId;
            hash = var.hashCode();
            varMap.put(key, hash);
            varId++;
        } else {
            hash = varMap.get(key);
        }
        return hash;
    }

    private void operatorEmit(int resultReg) {
        // result op param1 [ op2, param2 ]
        int param1, param2;
        next();
        param1 = getParamAndEmitReg();

        //TODO here get next operator no only can validate it but also can move the char to next command
        next();
        String op2 = getOperator();

        if (resultReg == -1) {
            // Bool Operator
            next();
            param2 = getParamAndEmitReg();
            emit(op2 + " " + param1 + " " + param2);
        } else {
            if (op2 == null) {
                // common assign
                emit("mov " + resultReg + " " + param1);
            } else {
                // operate and assign
                next();
                param2 = getParamAndEmitReg();
                emit(op2 + " " + param1 + " " + param2);
                emit("pop " + resultReg + " nop");
                next();
            }
        }
    }

    private String getOperator() {
        if (current == null) {
            return null;
        }
        switch (current) {
            case "=":
                return "mov";
            case "+":
                return "add";
            case "-":
                return "sub";
            case "*":
                return "mul";
            case "/":
                return "div";
            case "==":
                return "eq";
            case "!=":
                return "neq";
            case ">":
                return "big";
            case ">=":
                return "bige";
            case "<":
                return "less";
            case "<=":
                return "lese";
            default:
                return null;
        }
    }

    /**
     * return a reg of a var or return the value of a const
     */
    private int getParamAndEmitReg() {
        if (StringUtils.isNumeric(current)) {
            // if the param is a constant,
            // then save to mapping and store a new var in dataHelper
            // Sample:
            // consider constant 1, it will be store to varMap like "1" -> "var25"
            // and it will store in the stack of varMap
            // then return the "1" directly because 1 is the key of it in varMap;

            int reg = dataHelper.getReg();
            emit("set " + reg + " " + current);
            return reg;
        } else {
            // consider it "sampleVar" -> "var25"
            // so there must a reg or stack store the var which varId is var25.hashcode
            if (varMap.containsKey(current)) {
                int reg = dataHelper.getReg(varMap.get(current));
                dataHelper.regAddVar(reg, varMap.get(current));
                return reg;
            } else {
                throw new IllegalArgumentException("Receive a var " + current + " without initialize");
            }

        }
    }

    private int readLabel() {
        if (current.charAt(0) == 'L') {
            int i = 1;
            int label = 0;
            while (current.length() > i && Character.isDigit(current.charAt(i))) {
                label = label * 10 + Integer.valueOf(Character.valueOf(current.charAt(i)).toString());
                i++;
            }
            return label;
        } else {
            return 0;
        }
    }

    private void emit(String code) {
        if (RELEASE)
            System.out.println(code);
    }

    private void log(String info) {
        if (DEBUG)
            System.out.println(info);
    }

}
