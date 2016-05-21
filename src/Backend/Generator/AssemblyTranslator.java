package Backend.Generator;

import java.io.*;
import java.util.Scanner;

import static Backend.Generator.Util.getInput;
import static Backend.Generator.Util.getOutput;

public class AssemblyTranslator {

    /**
     * Generate "binary" for SYSVim from assembly code
     */

    public AssemblyTranslator() {

    }

    private static void process(String input, String output) throws FileNotFoundException, IOException {

        File file = new File(input);
        Scanner scanner = new Scanner(file);
        FileWriter writer = new FileWriter(output);
        while (scanner.hasNext()) {
            String code = emit(scanner.next());
            System.out.print(code + " ");
            writer.write(code + " ");
        }
        System.out.println();
        writer.close();
    }

    public static void main(String[] args) {
        String input = getInput(args, null);
        String output = getOutput(args, "out.sysvim");
        try {
            process(input, output);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String emit(String in) {
        int code = -1; // NOP
        switch (in) {
            case "STOP":
            case "stop":
                code = 0;
                break;
            case "PUSH":
            case "push":
                code = 1;
                break;
            case "POP":
            case "pop":
                code = 2;
                break;
            case "ADD":
            case "add":
                code = 3;
                break;
            case "MUL":
            case "mul":
                code = 4;
                break;
            case "DIV":
            case "div":
                code = 5;
                break;
            case "SUB":
            case "sub":
                code = 6;
                break;
            case "LESS":
            case "less":
                code = 7;
                break;
            case "MOV":
            case "mov":
                code = 8;
                break;
            case "SET":
            case "set":
                code = 9;
                break;
            case "LOG":
            case "log":
                code = 10;
                break;
            case "IF":
            case "if":
                code = 11;
                break;
            case "IFN":
            case "ifn":
                code = 12;
                break;
            case "LODR":
            case "loadr":
                code = 13;
                break;
            case "NOP":
            case "nop":
                code = 15;
                break;
            case "JUMP":
            case "jump":
                code = 16;
                break;
            case "EQ":
            case "eq":
                code = 17;
                break;
            case "NEQ":
            case "neq":
                code = 18;
                break;
            case "SEQ":
            case "seq":
                code = 19;
                break;
            case "SNEQ":
            case "sneq":
                code = 20;
                break;
            case "CLRF":
            case "clrf":
                code = 21;
                break;
            case "BIG":
            case "big":
                code = 22;
                break;
            case "SAVE":
            case "save":
                code = 23;
                break;
            case "SETA":
            case "seta":
                code = 25;
                break;
            case "LODA":
            case "loda":
                code = 26;
                break;
            case "A":
            case "a":
                code = 200;
                break;
            case "B":
            case "b":
                code = 201;
                break;
            case "C":
            case "c":
                code = 202;
                break;
            case "D":
            case "d":
                code = 203;
                break;
            case "E":
            case "e":
                code = 204;
                break;
            case "F":
            case "f":
                code = 205;
                break;
            case "I":
            case "i":
                code = 206;
                break;
            case "J":
            case "j":
                code = 207;
                break;
            case "EX":
            case "ex":
                code = 208;
                break;
            case "EXA":
            case "exa":
                code = 209;
                break;
            case "IP":
            case "ip":
                code = 210;
                break;
            case "SP":
            case "sp":
                code = 211;
                break;
            case "FLAG":
            case "flag":
                code = 212;
                break; // flag
        }
        return code != -1 ? String.valueOf(code) : in;
    }

}
