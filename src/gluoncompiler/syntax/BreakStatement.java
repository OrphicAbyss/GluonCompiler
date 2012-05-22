package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Token;

/**
 *
 */
class BreakStatement extends Statement {
	
	public BreakStatement(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		assert(first.isKeyword());
		assert(Keyword.BREAK.equals(first.getKeyword()));
		Token next = first.getNext();
		assert(next.isNewline());
		return next;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
