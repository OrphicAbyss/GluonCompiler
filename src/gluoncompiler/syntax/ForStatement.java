package gluoncompiler.syntax;

import gluoncompiler.GluonLabels;
import gluoncompiler.GluonOutput;
import gluoncompiler.Keyword;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * For Statement := For <AssignmentExpression>:<BooleanExpression>:<AssignmentExpression>
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
		
		if (!test.isOperator()){
			preForAssign = new AssignmentExpression(test);
			test = preForAssign.parse();
		}
		
		if (!test.isOperator() || !Operator.COLON.equals(test.getOperator()))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString()); 
		
		test = test.getNext();
		if (!test.isOperator()){
			conditionTest = new BooleanExpression(test);
			test = conditionTest.parse();
		}
		
		if (!test.isOperator() || !Operator.COLON.equals(test.getOperator()))
			throw new RuntimeException("Expected Colon Operator, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isNewline()){
			postForAssign = new AssignmentExpression(test);
			test = postForAssign.parse();
		}
		
		if (!test.isNewline())
			throw new RuntimeException("Expected Newline, found: " + test.toString());
		
		test = test.getNext();
		Keyword[] targets = { Keyword.END };		
		statements = new StatementGroup(test, targets);
		test = statements.parse();
		
		return test.getNext();
	}

	@Override
	public String emitCode() {
		String testLabel = GluonLabels.createLabel(first, "test");
		String endLabel = GluonLabels.createLabel(first, "end");
		StringBuilder sb = new StringBuilder();
		
		sb.append(GluonOutput.commentLine("For Statement"));
		if (preForAssign != null)
			sb.append(preForAssign.emitCode());
		
		sb.append(GluonOutput.labelLine(testLabel));
		sb.append(conditionTest.emitCode());
		sb.append(GluonOutput.codeLine("TEST EAX, EAX"));
		sb.append(GluonOutput.codeLine("JZ " + endLabel));
		sb.append(statements.emitCode());
	
		if (postForAssign != null)
			sb.append(postForAssign.emitCode());
		sb.append(GluonOutput.codeLine("JMP " + testLabel));
		sb.append(GluonOutput.labelLine(endLabel));
		sb.append(GluonOutput.commentLine("For End"));
		
		return sb.toString();
	}
	
}
