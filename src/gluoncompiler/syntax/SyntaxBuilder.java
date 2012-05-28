package gluoncompiler.syntax;

import gluoncompiler.*;
import java.io.File;

/**
 * Builds a syntax tree out of tokens from the tokeniser
 * 
 * Java Operators for reference:
 * postfix 	expr++ expr--
 * unary 	++expr --expr +expr -expr ~ !
 * multiplicative 	* / %
 * additive 	+ -
 * shift 	<< >> >>>
 * relational 	< > <= >= instanceof
 * equality 	== !=
 * bitwise AND 	&
 * bitwise exclusive OR 	^
 * bitwise inclusive OR 	|
 * logical AND 	&&
 * logical OR 	||
 * ternary 	? :
 * assignment 	= += -= *= /= %= &= ^= |= <<= >>= >>>=
 */
public class SyntaxBuilder {
	
	public static void main(String[] args){
		GluonVariable.init();
		GluonFunction.init();
		
		GluonScanner scanner = new GluonScanner(new File("testProg.txt"));
		Tokeniser tk = new Tokeniser(scanner);
		tk.tokenise();
		//tk.printTokens();
		//System.out.println("\n\nBuilding syntax tree...");
		SyntaxBuilder sb = new SyntaxBuilder(tk);
		sb.buildTree();
		//sb.printTree();
		
		GluonOutput out = new GluonOutput();
		GluonLibrary.printASMStart(out);
		out.outputLine(sb.emitCode(), false);
		GluonLibrary.printASMEnd(out);
		GluonLibrary.printVariables(out, GluonVariable.getVariables());

		System.out.println(out.getOutput());
	}

	private Tokeniser tokeniser;
	private SyntaxObject root;
	
	public SyntaxBuilder(Tokeniser tokeniser){
		this.tokeniser = tokeniser;
	}
	
	public void buildTree(){
		root = new Program(tokeniser.getCurrentToken());
		Token last = root.parse();
		// should be EOF token
		assert(last.isEOF());
	}
	
	public void printTree(){
		root.print(0);
	}
	
	public String emitCode(){
		return root.emitCode();
	}
}
