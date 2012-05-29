package gluoncompiler.syntax;

import gluoncompiler.*;

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
		
		if (!next.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after WHILE, found: " + next);
		
		next = next.getNext();
		testExp = new BooleanExpression(next);
		next = testExp.parse();
		
		if (!next.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after WHILE condition, found: " + next);
		
		next = next.getNext();
		if (!next.isNewline())
			throw new RuntimeException("Expected newline, found: " + next);

		next = next.getNext();
		
		Keyword[] target = { Keyword.END };
		statements = new StatementGroup(next,target);
		next = statements.parse();
		
		return next.getNext();
	}

	@Override
	public String emitCode() {
		// Creae labels
		String labelStart = GluonLabels.createLabel(first, "start");
		String labelEnd = GluonLabels.createLabel(first, "end");
		GluonLabels.addEndLabel(labelEnd);
		
		StringBuilder sb = new StringBuilder();
		sb.append(GluonOutput.commentLine("While Statement"));
		sb.append(GluonOutput.labelLine(labelStart));
		sb.append(testExp.emitCode());
		sb.append(GluonOutput.codeLine("TEST EAX, EAX"));
		sb.append(GluonOutput.codeLine("JZ " + labelEnd));
		sb.append(statements.emitCode());
		sb.append(GluonOutput.codeLine("JMP " + labelStart));
		sb.append(GluonOutput.labelLine(labelEnd));
		sb.append(GluonOutput.commentLine("End While"));
		
		GluonLabels.removeEndLabel(labelEnd);
		
		return sb.toString();
	}
	
	@Override
	public void print(int level) {
		printLevel(level);
		printLn("WHILE");
		testExp.print(level + 1);
		
		printLevel(level);
		printLn("DO");
		statements.print(level + 1);
	}
	
}
