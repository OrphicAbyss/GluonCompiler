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
	
	public WhileStatement(Token next, ScopeObject parentScope) {
		super(next, parentScope);
	}

	@Override
	public Token parse() {
		Token next = first.getNext();
		
		if (!next.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after WHILE, found: " + next);
		
		next = next.getNext();
		testExp = new BooleanExpression(next, scope);
		next = testExp.parse();
		
		if (!next.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after WHILE condition, found: " + next);
		
		next = next.getNext();
		if (!next.isNewline())
			throw new RuntimeException("Expected newline, found: " + next);

		next = next.getNext();
		
		Keyword[] target = { Keyword.END };
		statements = new StatementGroup(next, target, scope);
		next = statements.parse();
		
		return next.getNext();
	}

	@Override
	public void emitCode(StringBuilder code) {
		// Creae labels
		String labelStart = GluonLabels.createLabel(first, "start");
		String labelEnd = GluonLabels.createLabel(first, "end");
		GluonLabels.addEndLabel(labelEnd);
		
		code.append(GluonOutput.commentLine("While Statement"));
		code.append(GluonOutput.labelLine(labelStart));
		testExp.emitCode(code);
		code.append(GluonOutput.codeLine("TEST EAX, EAX"));
		code.append(GluonOutput.codeLine("JZ " + labelEnd));
		statements.emitCode(code);
		code.append(GluonOutput.codeLine("JMP " + labelStart));
		code.append(GluonOutput.labelLine(labelEnd));
		code.append(GluonOutput.commentLine("End While"));
		
		GluonLabels.removeEndLabel(labelEnd);
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
