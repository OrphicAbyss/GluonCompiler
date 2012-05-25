package gluoncompiler.syntax;

import gluoncompiler.GluonLabels;
import gluoncompiler.GluonOutput;
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
		
		if (Keyword.ELSE.equals(current.getKeyword())){
			current = current.getNext();
			assert(current.isNewline());
			targets = new Keyword[1];
			targets[0] = Keyword.END;
			falseCondition = new StatementGroup(current.getNext(),targets);
			current = falseCondition.parse();
		}
		
		if (!Keyword.END.equals(current.getKeyword())){
			throw new RuntimeException("Expected END keyword, found: " + current.toString());
		}
		
		current = current.getNext();
		return current;
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
