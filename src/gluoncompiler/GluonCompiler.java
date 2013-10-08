package gluoncompiler;

import gluoncompiler.syntax.SyntaxBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple compiler for language code named 'Gluon'
 *
 * TODO: add functions TODO: add classes TODO: add strings TODO: add arrays
 */
public class GluonCompiler {

    static GluonScanner scanner;
    static Tokeniser tokeniser;
    static SyntaxBuilder syntaxBuilder;
    static GluonOutput output;

    /**
     * Main code compiling
     */
    public static void compile(boolean linux) {
        GluonFunction.init();

        tokeniser = new Tokeniser(scanner);
        tokeniser.tokenise();

        syntaxBuilder = new SyntaxBuilder(tokeniser);
        syntaxBuilder.buildTree();

        Error.init(scanner);

        output = new GluonOutput();
        if (linux) {
            GluonLibrary.printLinuxASMStart(output);
        } else {
            GluonLibrary.printASMStart(output);
        }

        syntaxBuilder.emitCode(output);

        GluonLibrary.printVariableFunction(output);
        
        if (linux) {
            GluonLibrary.printLinuxASMEnd(output);
        } else {
            GluonLibrary.printASMEnd(output);
        }
    }

    /**
     * Takes a filename as the main argument.
     */
    public static void main(String[] args) {
        System.out.println("Gluon Compiler");
        if (args.length == 0) {
            // Print out help
            
            System.out.println("Usage: GluonCompiler [-linux] <input_file> [output_file]");
            //scanner = new GluonScanner();
        } else {
            int parsedArguments = 0;
            boolean linuxBuild = false;
            if ("-linux".equalsIgnoreCase(args[parsedArguments])) {
                linuxBuild = true;
                parsedArguments++;
            }
            
            if (parsedArguments >= args.length) {
                System.err.println("Not enough arguments expected filename.");
                return;
            }
            
            System.out.println("Reading source file: " + args[parsedArguments]);
            // Make sure the source file exists
            File source = new File(args[parsedArguments]);
            parsedArguments++;
            if (!source.exists()) {
                Error.abort("Source file does not exist.");
            } else {
                scanner = new GluonScanner(source);
            }

            compile(linuxBuild);

            if (parsedArguments < args.length) {
                FileWriter fw = null;
                try {
                    File file = new File(args[parsedArguments]);
                    fw = new FileWriter(file);
                    fw.append(output.getOutput());
                } catch (IOException ex) {
                    Logger.getLogger(GluonCompiler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(GluonCompiler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                System.out.print(output.getOutput());
            }
        }
    }
}
