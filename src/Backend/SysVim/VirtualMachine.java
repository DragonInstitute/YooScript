package Backend.SysVim;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class VirtualMachine {
    //instructions.get()
    //repeat private final static can make them easy to read
    private final static int STOP = 0;
    private final static int PUSH = 1;
    private final static int POP = 2;
    private final static int ADD = 3;
    private final static int MUL = 4;
    private final static int DIV = 5;
    private final static int SUB = 6;
    private final static int LESS = 7;
    private final static int MOV = 8;
    private final static int SET = 9;
    private final static int LOG = 10;
    private final static int IF = 11;
    private final static int IFN = 12;
    private final static int LODR = 13;
    private final static int PSHR_ABANDONED = 14;
    private final static int NOP = 15;
    private final static int JUMP = 16;
    private final static int EQ = 17;
    private final static int NEQ = 18;
    private final static int SEQ = 19;
    private final static int SNEQ = 20;
    private final static int CLRF = 21;
    private final static int BIG = 22;
    private final static int SAVE = 23;
    private final static int LOAD_ABANDONED = 24;
    private final static int SETA = 25;
    private final static int LODA = 26;

    // registers.get()
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

    private static Vector<Integer> stack = new Vector<>();
    // calling frame
    private static Vector<Integer> frame = new Vector<>();
    // data segment
    private static Vector<Integer> data = new Vector<>();
    private static HashMap<Integer, Integer> registers = new HashMap<>();

    static {
        registers.put(A, 0);
        registers.put(B, 0);
        registers.put(C, 0);
        registers.put(D, 0);
        registers.put(E, 0);
        registers.put(F, 0);
        registers.put(I, 0);
        registers.put(J, 0);
    }

    private static Vector<Integer> instructions = new Vector<>();
    private static boolean running = true;
    private static boolean isJump = false;

    private int insCount = 0;
    private int ins;

    private void dumpStack() {
        System.out.println("Stack Dump:");
        for (Integer i : stack) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

//    private void dumpRegisters() {
//        System.out.println("Register Dump:");
//        for (Integer i : registers) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//    }

    private int findEmptyRegister() {
        for (int i = 0; i < REGISTER_SIZE; i++) {
            if (i != registers.get(EX) && i != registers.get(EXA)) {
                return i;
            }
        }
        return EX;
    }

    private static void spSub() {
        registers.put(SP, getSPValue() - 1);
    }

    private static void spPlus() {
        registers.put(SP, getSPValue() + 1);
    }

    private static void forward(int n) {
        registers.put(IP, getIPValue() + n);
    }

    private static int getThisInst() {
        return instructions.get(getIPValue());
    }

    private static int getInstByIndex(int ip) {
        return instructions.get(ip);
    }

    private static int getSPValue() {
        return registers.get(SP);
    }

    private static int getIPValue() {
        return registers.get(IP);
    }

    private static void setStack(int sp, int value) {
        stack.set(sp, value);
    }

    private static int getTopValue() {
        return stack.get(getSPValue());
    }

    private static int getStackValue(int index) {
        return stack.get(index);
    }

    private static void push(int val) {
        spPlus();
        setStack(getSPValue(), val);
    }

    private static void stop() {
        running = false;
    }

    private static int getRegValue(int reg) {
        return registers.get(reg);
    }

    private static void setRegValue(int inst, int val) {
        registers.put(inst, val);
    }

    private static int getInstByOffset(int index) {
        return getInstByIndex(getIPValue() + index);
    }

    private static void setData(int offset, int val) {
        data.set(offset, val);
    }

    private static int getDataByOffset(int offset) {
        return data.get(offset);
    }

    private void eval(int instruction) {
        isJump = false;
        switch (instruction) {
            case STOP: {
                stop();
                System.out.println("Terminated");
                break;
            }
            case PUSH: {
                spPlus();
                setStack(getSPValue(), getRegValue(getInstByOffset(1)));
                break;
            }
            case POP: {
                setRegValue(getInstByOffset(1), getTopValue());
                spSub();
                break;
            }
            case ADD: {
                int a = getRegValue(getInstByOffset(1));
                int b = getRegValue(getInstByOffset(2));
                push(a + b);
                System.out.println(a + " + " + b + " = " + getTopValue());
                break;
            }
            case MUL: {
                int a = getRegValue(getInstByOffset(1));
                int b = getRegValue(getInstByOffset(2));
                push(a * b);
                System.out.println(a + " * " + b + " = " + getTopValue());
                break;
            }
            case DIV: {
                int a = getRegValue(getInstByOffset(1));
                int b = getRegValue(getInstByOffset(2));
                push(a / b);
                System.out.println(a + " / " + b + " = " + getTopValue());
                break;
            }
            case SUB: {
                int a = getRegValue(getInstByOffset(1));
                int b = getRegValue(getInstByOffset(2));
                push(a - b);
                System.out.println(a + " - " + b + " = " + getTopValue());
                break;
            }
            case MOV: {
                setRegValue(getInstByOffset(1), getRegValue(getInstByOffset(2)));
                break;
            }
            case SET: {
                setRegValue(getInstByOffset(1), getInstByOffset(2));
                break;
            }
            case SETA: {
                setData(getInstByOffset(1), getRegValue(getInstByOffset(2)));
                break;
            }
            case SAVE: {
                setData(getInstByOffset(1), getInstByOffset(2));
                break;
            }
            case LODA: {
                setRegValue(getInstByOffset(1), getDataByOffset(getInstByOffset(2)));
                break;
            }
            case LOG: {
                System.out.println("Log: " + getRegValue(getInstByOffset(1)));
                break;
            }
            case IF: {
                if (getRegValue(FLAG) == 1) {
                    setRegValue(IP, getInstByOffset(1));
                    isJump = true;
                }
                break;
            }
            case IFN: {
                if (getRegValue(FLAG) == 0) {
                    setRegValue(IP, getInstByOffset(1));
                    isJump = true;
                }
                break;
            }
            case LODR: {
                setRegValue(getInstByOffset(1), getTopValue());
                break;
            }
            case NOP: {
                break;
            }
            case JUMP: {
                setRegValue(IP, getInstByOffset(1));
                isJump = true;
                break;
            }
            case EQ: {
                setRegValue(FLAG, (getRegValue(getInstByOffset(1)) == getRegValue(getInstByOffset(2))) ? 1 : 0);
                break;
            }
            case NEQ: {
                setRegValue(FLAG, (getRegValue(getInstByOffset(1)) != getRegValue(getInstByOffset(2))) ? 1 : 0);
                break;
            }
            case SEQ: {
                setRegValue(FLAG, (getRegValue(getTopValue()) == getRegValue(getStackValue(getSPValue() - 1))) ? 1 : 0);
                break;
            }
            case SNEQ: {
                setRegValue(FLAG, (getRegValue(getTopValue()) != getRegValue(getStackValue(getSPValue() - 1))) ? 1 : 0);
                break;
            }
            case CLRF: {
                setRegValue(FLAG, 0);
                break;
            }
            case BIG: {
                setRegValue(FLAG, (getRegValue(getInstByOffset(1)) > getRegValue(getInstByOffset(2))) ? 1 : 0);
                break;
            }
            case LESS: {
                setRegValue(FLAG, (getRegValue(getInstByOffset(1)) < getRegValue(getInstByOffset(2))) ? 1 : 0);
                break;
            }
            default: {
                System.out.println("Unknown Instruction " + instruction + ", Skipped.");
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        VirtualMachine virtualMachine = new VirtualMachine();


//        for (int i = 0; i < REGISTER_SIZE; i++) {
//            registers.add(-2);
//        }
        for (int i = 0; i < 256; i++) {
            stack.add(-3);
        }

        if (args.length == 0) {
            System.out.println("No input file!");
            return;
        }
        File in;
        try {
            in = new File(args[0]);
            Scanner scanner = new Scanner(in);
            int i = 0;
            while (scanner.hasNextInt()) {
                instructions.add(i, scanner.nextInt());
                System.out.print(getInstByIndex(i) + " ");
                i++;
            }
            System.out.println();
            virtualMachine.insCount = i;
            setRegValue(SP, -1);
            setRegValue(IP, 0);
            while (running && getRegValue(IP) < virtualMachine.insCount) {
                virtualMachine.eval(getInstByIndex(getRegValue(IP)));
                if (!isJump) {
                    forward(3);
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
