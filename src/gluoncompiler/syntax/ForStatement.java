package gluoncompiler.syntax;

import gluoncompiler.*;

/**
 * For Statement := For '(' <AssignmentExpression> : <BooleanExpression> : <AssignmentExpression> ')'
 *						<StatementGroup>
 *					End
 */
class ForStatement extends Statement {
	
	AssignmentExpression preForAssign;
	BooleanExpression conditionTest;
	AssignmentExpression postForAssign;
	StatementGroup statements;
	
	public ForStatement(Token next, ScopeObject parentScope) {
		super(next, parentScope);
	}

	@Override
	public Token parse() {
		Token test = first.getNext();
		
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after FOR, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isOperator()) {
			preForAssign = new AssignmentExpression(test, scope);
			test = preForAssign.parse();
		}
		
		if (!test.isOperator(Operator.COLON))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString()); 
		
		test = test.getNext();
		if (!test.isOperator()) {
			conditionTest = new BooleanExpression(test, scope);
			test = conditionTest.parse();
		}
		
		if (!test.isOperator(Operator.COLON))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString());
		
		
		test = test.getNext();
		if (!test.isOperator(Operator.BRACKET_RIGHT)) {
			postForAssign = new AssignmentExpression(test, scope);
			test = postForAssign.parse();
		}
		
		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after post for loop expression, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isNewline())
			throw new RuntimeException("Expected Newline, found: " + test.toString());
		
		test = test.getNext();
		Keyword[] targets = { Keyword.END };		
		statements = new StatementGroup(test, targets, scope);
		test = statements.parse();
		
		return test.getNext();
	}

	@Override
	public void emitCode(GluonOutput code) {
		String testLabel = GluonLabels.createLabel(first, "test");
		String endLabel = GluonLabels.createLabel(first, "end");
		
		code.comment("For Statement");
		if (preForAssign != null)
			preForAssign.emitCode(code);
		
		code.label(testLabel);
		conditionTest.emitCode(code);
		code.code("TEST EAX, EAX");
		code.code("JZ " + endLabel);
		statements.emitCode(code);
	
		if (postForAssign != null)
			postForAssign.emitCode(code);
		code.code("JMP " + testLabel);
		code.label(endLabel);
		code.comment("For End");
	}
	
}
