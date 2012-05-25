package gluoncompiler.syntax;

import gluoncompiler.GluonLabels;
import gluoncompiler.GluonOutput;
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
		String label = GluonLabels.getEndLabel();
		
		StringBuilder sb = new StringBuilder();
		sb.append(GluonOutput.commentLine("Break"));
		sb.append(GluonOutput.codeLine("JMP " + label));
		return sb.toString();
	}
	
	@Override
	public void print(int level) {
		printClass(level);
	}
}
