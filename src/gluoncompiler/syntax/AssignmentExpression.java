package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Assignment Expression := [<Identifier> [+|-|*|/]=] <Boolean Expression>
 */
public class AssignmentExpression extends SyntaxObject {

	private Token first;
	private Variable variable;
	private Operator assignmentOp;
	private BooleanExpression assignmentExp;
	private BooleanExpression expression;
	
	public AssignmentExpression(Token next, ScopeObject parentScope){
		first = next;
		scope = parentScope;
	}
	
	@Override
	public Token parse() {
		Token test = first.getNext();
		if (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (testOp.name().startsWith("ASSIGN")) {
				variable = new Variable(first, scope);
				assignmentOp = testOp;
				test = test.getNext();
				assignmentExp = new BooleanExpression(test, scope);
				test = assignmentExp.parse();
			}
		}
		
		if (variable == null) {
			expression = new BooleanExpression(first, scope);
			test = expression.parse();
		}
		
		return test;
	}

	@Override
	public void emitCode(GluonOutput code) {
		if (variable != null){
			String varName = GluonLibrary.varToLabel(variable.getName());
			assignmentExp.emitCode(code);
			
			switch (assignmentOp){
				case ASSIGN:
					break;
				case ASSIGN_ADD:
					code.code("MOV EBX,[" + varName + "]");
					code.code("ADD EAX,EBX");
					break;
				case ASSIGN_SUBTRACT:
					code.code("MOV EBX, EAX");
					code.code("MOV EAX,[" + varName + "]");
					code.code("SUB EAX,EBX");
					break;
				case ASSIGN_MULTIPLY:
					code.code("MOV EBX,[" + varName + "]");
					code.code("IMUL EAX,EBX");
					break;
				case ASSIGN_DIVIDE:
					code.code("MOV EBX, EAX");
					code.code("MOV EDX, 0");
					code.code("MOV EAX,[" + varName + "]");
					code.code("IDIV EBX");
					break;
			}
			code.code("MOV [" + varName + "],EAX");
		} else {
			expression.emitCode(code);
		}
	}

	@Override
	public void print(int level) {
		if (assignmentExp != null) {
			variable.print(level);
			printLevel(level + 1);
			printLn(assignmentOp.name());
			assignmentExp.print(level + 1);
		} else {
			expression.print(level);
		}
	}
	
}
