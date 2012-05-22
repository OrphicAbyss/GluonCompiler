package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Token;

/**
 * If Statement := If <Boolean Expression> \n
 *                     <StatementGroup>
 *				   [Else
 *					   <StatementGroup>]
 *                 End
 */
public class IfStatement extends Statement {
	private SyntaxObject testExpression;
	private SyntaxObject trueCondition;
	private SyntaxObject falseCondition;

	public IfStatement(Token start) {
		super(start);
		falseCondition = null;
	}
	
	@Override
	public Token parse() {
		testExpression = new BooleanExpression(first.getNext());
		Token current = testExpression.parse();
		assert(current.isNewline());
		Keyword[] targets = {Keyword.ELSE,Keyword.END};
		trueCondition = new StatementGroup(current.getNext(),targets);
		current = trueCondition.parse();
		
		assert(current.isKeyword());
		if (current.getKeyword().equals(Keyword.ELSE)){
			current = current.getNext();
			assert(current.isNewline());
			targets = new Keyword[1];
			targets[0] = Keyword.END;
			falseCondition = new StatementGroup(current.getNext(),targets);
		}
		
		assert(current.isKeyword());
		if (!current.getKeyword().equals(Keyword.END)){
			
		}
		
		current = current.getNext();
		return current;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
