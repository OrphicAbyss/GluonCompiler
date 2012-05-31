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

	public IfStatement(Token start, ScopeObject parentScope) {
		super(start, parentScope);
		falseCondition = null;
	}
	
	@Override
	public Token parse() {
		Token test = first.getNext();
		
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' after IF, found: " + test.toString());
		
		testExpression = new BooleanExpression(test.getNext(), scope);
		test = testExpression.parse();
		
		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' after IF condition, found: " + test.toString());
		
		test = test.getNext();
		if (!test.isNewline())
			throw new RuntimeException("Expected newline, found: " + test.toString());
		
		Keyword[] targets = {Keyword.ELSE,Keyword.END};
		trueCondition = new StatementGroup(test.getNext(), targets, scope);
		test = trueCondition.parse();
		
		if (Keyword.ELSE.equals(test.getKeyword())) {
			test = test.getNext();
			assert(test.isNewline());
			targets = new Keyword[1];
			targets[0] = Keyword.END;
			falseCondition = new StatementGroup(test.getNext(), targets, scope);
			test = falseCondition.parse();
		}
		
		if (!test.isKeyword(Keyword.END))
			throw new RuntimeException("Expected END keyword, found: " + test.toString());
		
		return test.getNext();
	}

	@Override
	public void emitCode(StringBuilder code) {
		String labelEnd = GluonLabels.createLabel(first, "end");
		String labelElse = GluonLabels.createLabel(first, "else");
		
		code.append(GluonOutput.commentLine("If Statement"));
		testExpression.emitCode(code);
		code.append(GluonOutput.codeLine("TEST EAX, EAX"));
		if (falseCondition == null)
			code.append(GluonOutput.codeLine("JZ " + labelEnd));
		else
			code.append(GluonOutput.codeLine("JZ " + labelElse));
		trueCondition.emitCode(code);
		if (falseCondition != null){
			code.append(GluonOutput.codeLine("JMP " + labelEnd));
			code.append(GluonOutput.labelLine(labelElse));
			falseCondition.emitCode(code);
		}
		code.append(GluonOutput.labelLine(labelEnd));
		code.append(GluonOutput.commentLine("End If"));
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
