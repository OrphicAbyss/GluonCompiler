package gluoncompiler.syntax;

import gluoncompiler.GluonScanner;
import gluoncompiler.Token;
import gluoncompiler.Tokeniser;
import java.io.File;

/**
 * Builds a syntax tree out of tokens from the tokeniser
 */
public class SyntaxBuilder {
	
	public static void main(String[] args){
		GluonScanner scanner = new GluonScanner(new File("testProg.txt"));
		Tokeniser tk = new Tokeniser(scanner);
		tk.tokenise();
		tk.printTokens();
		System.out.println("\n\nBuilding syntax tree...");
		SyntaxBuilder sb = new SyntaxBuilder(tk);
		sb.buildTree();
		sb.printTree();
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
		
	}
}
