package gluoncompiler.syntax;

import gluoncompiler.*;

/**
 * If Statement := If '(' <Boolean Expression> ')'
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
		Token test = first.getNext();
		
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after IF, found: " + test.toString());
		
		testExpression = new BooleanExpression(test.getNext());
		test = testExpression.parse();
		
		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after IF condition, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isNewline())
			throw new RuntimeException("Expected newline, found: " + test.toString());
		
		Keyword[] targets = {Keyword.ELSE,Keyword.END};
		trueCondition = new StatementGroup(test.getNext(),targets);
		test = trueCondition.parse();
		
		if (Keyword.ELSE.equals(test.getKeyword())) {
			test = test.getNext();
			assert(test.isNewline());
			targets = new Keyword[1];
			targets[0] = Keyword.END;
			falseCondition = new StatementGroup(test.getNext(),targets);
			test = falseCondition.parse();
		}
		
		if (!test.isKeyword(Keyword.END))
			throw new RuntimeException("Expected END keyword, found: " + test.toString());
		
		return test.getNext();
	}

	@Override
	public String emitCode() {
		String labelEnd = GluonLabels.createLabel(first, "end");
		String labelElse = GluonLabels.createLabel(first, "else");
		
		StringBuilder sb = new StringBuilder();
		sb.append(GluonOutput.commentLine("If Statement"));
		sb.append(testExpression.emitCode());
		sb.append(GluonOutput.codeLine("TEST EAX, EAX"));
		if (falseCondition == null)
			sb.append(GluonOutput.codeLine("JZ " + labelEnd));
		else
			sb.append(GluonOutput.codeLine("JZ " + labelElse));
		sb.append(trueCondition.emitCode());
		if (falseCondition != null){
			sb.append(GluonOutput.codeLine("JMP " + labelEnd));
			sb.append(GluonOutput.labelLine(labelElse));
			sb.append(falseCondition.emitCode());
		}
		sb.append(GluonOutput.labelLine(labelEnd));
		sb.append(GluonOutput.commentLine("End If"));
		return sb.toString();
	}
	
	@Override
	public void print(int level) {
		printLevel(level);
		printLn("IF");
		testExpression.print(level + 1);
		
		printLevel(level);
		printLn("THEN");
		trueCondition.print(level + 1);
		
		if (falseCondition != null) {
			printLevel(level);
			printLn("ELSE");
			falseCondition.print(level + 1);
		}
	}
}
