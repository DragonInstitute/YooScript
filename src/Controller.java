import Frontend.Main.Main;
import Backend.Generator.IntermediateTranslator;
import Backend.Generator.AssemblyTranslator;
import Backend.SysVim.VirtualMachine;

import java.io.IOException;

public class Controller {
    public static void main(String[] args) {
        try {
            Main.main(args);
            IntermediateTranslator.main(args);
            AssemblyTranslator.main(args);
            VirtualMachine.main(args);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}
