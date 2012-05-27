package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Token;

/**
 * Variable syntax object
 */
public class Variable extends SyntaxObject {
	private String name;
	private Token token;
	
	public Variable(Token token) {
		assert(token.isIdentifier());
		this.name = token.getValue();
		this.token = token;
//		GluonVariable.testVariableRegistered(name);
	}
	
	@Override
	public Token parse() {
		return token.getNext();
	}

	
	@Override
	public String emitCode() {
		return GluonOutput.codeLine("MOV EAX, [" + GluonLibrary.varToLabel(name) + "]");
	}

	@Override
	public void print(int level) {
		printLevel(level);
		printLn("VARIABLE " + name);
	}
	
	public String getName() {
		return name;
	}
}