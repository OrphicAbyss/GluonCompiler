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
	
	public ForStatement(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		Token test = first.getNext();
		
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after FOR, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isOperator()){
			preForAssign = new AssignmentExpression(test);
			test = preForAssign.parse();
		}
		
		if (!test.isOperator(Operator.COLON))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString()); 
		
		test = test.getNext();
		if (!test.isOperator()){
			conditionTest = new BooleanExpression(test);
			test = conditionTest.parse();
		}
		
		if (!test.isOperator(Operator.COLON))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString());
		
		
		test = test.getNext();
		if (!test.isOperator(Operator.BRACKET_RIGHT)){
			postForAssign = new AssignmentExpression(test);
			test = postForAssign.parse();
		}
		
		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after post for loop expression, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isNewline())
			throw new RuntimeException("Expected Newline, found: " + test.toString());
		
		test = test.getNext();
		Keyword[] targets = { Keyword.END };		
		statements = new StatementGroup(test, targets);
		test = statements.parse();
		
		return test.getNext();
	}

	@Override
	public void emitCode(StringBuilder code) {
		String testLabel = GluonLabels.createLabel(first, "test");
		String endLabel = GluonLabels.createLabel(first, "end");
		
		code.append(GluonOutput.commentLine("For Statement"));
		if (preForAssign != null)
			preForAssign.emitCode(code);
		
		code.append(GluonOutput.labelLine(testLabel));
		conditionTest.emitCode(code);
		code.append(GluonOutput.codeLine("TEST EAX, EAX"));
		code.append(GluonOutput.codeLine("JZ " + endLabel));
		statements.emitCode(code);
	
		if (postForAssign != null)
			postForAssign.emitCode(code);
		code.append(GluonOutput.codeLine("JMP " + testLabel));
		code.append(GluonOutput.labelLine(endLabel));
		code.append(GluonOutput.commentLine("For End"));
	}
	
}
