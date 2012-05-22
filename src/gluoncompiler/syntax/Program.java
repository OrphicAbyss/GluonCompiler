package gluoncompiler.syntax;

import gluoncompiler.Token;

/**
 * Represents a program
 */
public class Program extends SyntaxObject {
	StatementGroup child;
	
	public Program(Token start){
		child = new StatementGroup(start);
	}
	
	@Override
	public String emitCode() {
		return null;
	}

	@Override
	public Token parse() {
		return child.parse();
	}
	
}
