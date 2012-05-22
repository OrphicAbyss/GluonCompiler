package gluoncompiler.syntax;

import gluoncompiler.Token;

/**
 * For Statement := For 
 */
class ForStatement extends Statement {
	
	public ForStatement(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
