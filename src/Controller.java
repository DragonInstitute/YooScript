import Backend.Generator.AssemblyTranslator;
import Backend.Generator.IntermediateTranslator;
import Frontend.Main.Main;

import javax.annotation.processing.FilerException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class Controller {
    public static void main(String[] args) {
        try {
            String path = "/home/lingsamuel/Documents/IdeaProjects/Compiler/";
            String[] defaultArgs = {path + "test.yoo", path + "test.inter", path + "test.output", path + "test.ass", path + "test.sysvim", path + "test.txt"};
            System.arraycopy(args, 0, defaultArgs, 0, args.length);

            for (String fileName : defaultArgs) {
                if (fileName.equals(path + "test.yoo")) {
                    continue;
                }
                File file = new File(fileName);
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new FilerException("Delete " + fileName + " failed");
                    }
                }
            }
            PrintStream console = System.out;
            Main.main(Arrays.copyOfRange(defaultArgs, 0, 2));
            System.setOut(console);
            IntermediateTranslator.main(Arrays.copyOfRange(defaultArgs, 1, 4));
            System.setOut(console);
            AssemblyTranslator.main(Arrays.copyOfRange(defaultArgs, 3, 5));
            System.setOut(console);
//            VirtualMachine.main(Arrays.copyOfRange(defaultArgs, 4, 6));
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

}
