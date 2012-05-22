package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Token;

/**
 * While Statement := While <BooleanExpression>
 *						  <Statement Group>
 *					  End
 */
class WhileStatement extends Statement {
	BooleanExpression testExp;
	StatementGroup statements;
	
	public WhileStatement(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		Token next = first.getNext();
		testExp = new BooleanExpression(next);
		next = testExp.parse();
		assert(next.isNewline());
		next = next.getNext();
		
		Keyword[] target = { Keyword.END };
		statements = new StatementGroup(next,target);
		next = statements.parse();
		
		return next.getNext();
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
