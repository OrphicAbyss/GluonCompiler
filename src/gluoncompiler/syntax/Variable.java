package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.GluonVariable;
import gluoncompiler.Token;

/**
 * Variable syntax object
 */
public class Variable extends SyntaxObject {
	private String name;
	private Token token;
	
	public Variable(Token token, ScopeObject parentScope) {
		scope = parentScope;
		this.token = token;
	}
	
	@Override
	public Token parse() {
		if (!token.isIdentifier())
			throw new RuntimeException("Expected identifier, found: " + token);

		name = token.getValue();
		return token.getNext();
	}

	
	@Override
	public void emitCode(GluonOutput code) {
		GluonVariable.testVariableRegistered(name);
		code.code("MOV EAX, [" + GluonLibrary.varToLabel(name) + "]");
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
