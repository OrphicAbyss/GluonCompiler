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
 * TODO: add functions
 * TODO: add classes
 * TODO: add strings
 * TODO: add arrays
 */
public class GluonCompiler {
	static GluonScanner scanner;
	static Tokeniser tokeniser;
	static SyntaxBuilder syntaxBuilder;
	static GluonOutput output;

	/** Parse and Translate Parentheses */
	public static void Parentheses() {
		tokeniser.matchOperator(Operator.BRACKET_LEFT, null);
		//BooleanExpression();
		tokeniser.matchOperator(Operator.BRACKET_RIGHT, null);
	}
	
	/** Main code compiling */
	public static void Init() {
        GluonFunction.init();
        
		tokeniser = new Tokeniser(scanner);
		tokeniser.tokenise();
		
		syntaxBuilder = new SyntaxBuilder(tokeniser);
		syntaxBuilder.buildTree();
		
		Error.init(scanner);
		
		output = new GluonOutput();
		GluonLibrary.printASMStart(output);

		syntaxBuilder.emitCode(output);

		GluonLibrary.printASMEnd(output);
		GluonLibrary.printVariableFunction(output);
		
		System.out.print(output.getOutput());
	}

	/**
	 * Takes a filename as the main argument.
	 */
	public static void main(String[] args) {
		if (args.length == 0){
			// Print out help
			System.out.println("Gluon Compiler - No filename provided input from terminal:");
			scanner = new GluonScanner();
		} else {
			System.out.println("Gluon Compiler - Reading source file: " + args[0]);
			// Make sure the source file exists
			File source = new File(args[0]);
			if (!source.exists()){
				Error.abort("Source file does not exist.");
			} else {
				scanner = new GluonScanner(source);
			}
		}

		Init();
        if (args.length == 2) {
            FileWriter fw = null;
            try {
                File file = new File(args[1]);
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
        }
	}
}
