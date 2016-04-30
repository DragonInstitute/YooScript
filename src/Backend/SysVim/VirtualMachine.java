package Backend.SysVim;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class VirtualMachine {
    //instructions.get()
    private final static int STOP = 0;
    private final static int PUSH = 1;
    private final static int POP = 2;
    private final static int ADD = 3; // add or add reg_a reg_b     :: adds top two vals on stack
    private final static int MUL = 4; // mul                         :: multiplies top two vals on stack
    private final static int DIV = 5; // div                       :: divides top two vals on stack
    private final static int SUB = 6; // sub                       :: subtracts top two vals on stack
    private final static int LESS = 7; // less reg_a, reg_b         :: pushes (reg_a < reg_b) to stack
    private final static int MOV = 8; // mov reg_a, reg_b            :: moves the value in reg_a to reg_b
    private final static int SET = 9; // set reg, 5                 :: sets the reg to value
    private final static int LOG = 10; // log reg
    private final static int IF = 11; // if ip              :: if the flag == 0 branch to the ip
    private final static int IFN = 12; // ifn ip   :: if the flag != 0 val branch to the ip
    private final static int LODR = 13; // lodr reg          :: loads a register to the stack
    private final static int PSHR = 14; // pshr reg          :: pushes top of stack to the given register
    private final static int NOP = 15; // nop              :: nothing
    private final static int JUMP = 16; // jump ip          :: jump to the ip
    private final static int EQ = 17;   // eq reg_a reg_b :: if reg_a == reg_b, flag == 1
    private final static int NEQ = 18;  // eq reg_a reg_b :: if reg_a != reg_b, flag == 1
    private final static int SEQ = 19;  // seq              :: if stack == stack - 1, flag == 1
    private final static int SNEQ = 20; // sneq             :: if stack != stack - 1, flag == 1
    private final static int CLRF = 21; // clrf             :: clear flag, flag = 0
    private final static int BIG = 22;

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

    private static Vector<Integer> stack = new Vector<Integer>();
    private static Vector<Integer> registers = new Vector<Integer>();
    private static Vector<Integer> instructions = new Vector<Integer>();
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

    private void dumpRegisters() {
        System.out.println("Register Dump:");
        for (Integer i : registers) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private int findEmptyRegister() {
        for (int i = 0; i < REGISTER_SIZE; i++) {
            if (i != registers.get(EX) && i != registers.get(EXA)) {
                return i;
            }
        }
        return EX;
    }

    private void spSub() {
        registers.set(SP, sp() - 1);
    }

    private void spPlus() {
        registers.set(SP, sp() + 1);
    }

    private void forward(int n) {
        registers.set(IP, ip() + n);
    }

    private int getThisInst() {
        return instructions.get(ip());
    }

    private int getInst(int ip) {
        return instructions.get(ip);
    }

    private int sp() {
        return registers.get(SP);
    }

    private int ip() {
        return registers.get(IP);
    }

    private void setStack(int sp, int value) {
        stack.set(sp, value);
    }

    private int top() {
        return stack.get(sp());
    }

    private void stop() {
        running = false;
    }

    private void eval(int instruction) {
        isJump = false;
        switch (instruction) {
            case STOP: {
                stop();
                System.out.println("Executed");
                break;
            }
            case PUSH: {
                spPlus();
                forward(1);
                setStack(sp(), getThisInst());
                // push the value to the top of stack
                break;
            }
            case POP: {
                spSub();
                // stack pointer lower
                break;
            }
            case ADD: {
                registers.set(A, top());
                spSub();

                registers.set(B, top());
                registers.set(C, registers.get(A) + registers.get(B));

                setStack(sp(), registers.get(C));
                System.out.println(registers.get(B) + " + " + registers.get(A) + " = " + registers.get(C));
                break;
            }
            case MUL: {
                registers.set(A, top());
                spSub();

                registers.set(B, top());
            /*SP = SP - 1;*/

                registers.set(C, registers.get(B) * registers.get(A));

            /*SP = SP + 1;*/
                setStack(sp(), registers.get(C));
                System.out.println(registers.get(B) + " * " + registers.get(A) + " = " + registers.get(C));
                break;
            }
            case DIV: {
                registers.set(A, top());
                spSub();

                registers.set(B, top());
            /* SP = SP - 1;*/

                registers.set(C, registers.get(B) / registers.get(A));

            /* SP = SP + 1; */
                setStack(sp(), registers.get(C));
                System.out.println(registers.get(B) + " / " + registers.get(A) + " = " + registers.get(C));
                break;
            }
            case SUB: {
                registers.set(A, top());
                spSub();

                registers.set(B, top());
            /* SP = SP - 1; */

                registers.set(C, registers.get(B) - registers.get(A));

            /* SP = SP + 1; */
                setStack(sp(), registers.get(C));
                System.out.println(registers.get(B) + " - " + registers.get(A) + " = " + registers.get(C));
                break;
            }
            case LESS: {
                registers.set(FLAG, (registers.get(getInst(ip() + 1)) < registers.get(getInst(ip() + 2))) ? 1 : 0);
                forward(2);
                break;
            }
            case MOV: {
                registers.set(getInst(ip() + 1), registers.get(getInst(ip() + 2)));
                forward(2);
                break;
            }
            case SET: {
                registers.set(getInst(ip() + 1), getInst(ip() + 2));
                forward(2);
                break;
            }
            case LOG: {
                System.out.println("Log: " + registers.get(getInst(ip() + 1)));
                forward(1);
                break;
            }
            case IF: {
                if (registers.get(FLAG) == 1) {
                    registers.set(IP, getInst(ip() + 1));
                    isJump = true;
                } else {
                    forward(1);
                }
                break;
            }
            case IFN: {
                if (registers.get(FLAG) == 0) {
                    registers.set(IP, getInst(ip() + 1));
                    isJump = true;
                } else {
                    forward(1);
                }
                break;
            }
            case LODR: {
                spPlus();
                forward(1);
                stack.set(sp(), registers.get(getInst(ip())));
                break;
            }
            case PSHR: {
                registers.set(getInst(ip() + 1), stack.get(sp()));
                forward(1);
                break;
            }
            case NOP: {
                System.out.println("Do Nothing");
                break;
            }
            case JUMP: {
                registers.set(IP, getInst(ip() + 1));
                isJump = true;
                break;
            }
            case EQ: {
                registers.set(FLAG, (registers.get(getInst(ip() + 1)).intValue() == registers.get(getInst(ip() + 2)).intValue()) ? 1 : 0);
                forward(2);
                break;
            }
            case NEQ: {
                registers.set(FLAG, (registers.get(getInst(ip() + 1)).intValue() != registers.get(getInst(ip() + 2)).intValue()) ? 1 : 0);
                forward(2);
                break;
            }
            case SEQ: {
                registers.set(FLAG, (registers.get(top()).intValue() == registers.get(top() - 1).intValue()) ? 1 : 0);
                break;
            }
            case SNEQ: {
                registers.set(FLAG, (registers.get(top()).intValue() != registers.get(top() - 1).intValue()) ? 1 : 0);
                break;
            }
            case CLRF: {
                registers.set(FLAG, 0);
                break;
            }
            case BIG: {
                registers.set(FLAG, (registers.get(getInst(ip() + 1)) > registers.get(getInst(ip() + 2))) ? 1 : 0);
                forward(2);
                break;
            }
            default: {
                System.out.println("Unknown Instruction " + instruction);
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        VirtualMachine virtualMachine = new VirtualMachine();


        for (int i = 0; i < REGISTER_SIZE; i++) {
            registers.add(-2);
        }
        for (int i = 0; i < 256; i++) {
            stack.add(-3);
        }

        if (args.length == 0) {
            System.out.println("No input file!");
            return;
        }
        File in = null;
        try {
            in = new File(args[0]);
            Scanner scanner = new Scanner(in);
            int i = 0;
            while (scanner.hasNextInt()) {
                instructions.add(i, scanner.nextInt());
                System.out.print(instructions.get(i) + " ");
                i++;
            }
            System.out.println();
            virtualMachine.insCount = i;
            registers.set(SP, -1);
            registers.set(IP, 0);
            while (running && registers.get(IP) < virtualMachine.insCount) {
                virtualMachine.eval(instructions.get(registers.get(IP)));
                if (!isJump) {
                    registers.set(IP, registers.get(IP) + 1);
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }


    }


}

