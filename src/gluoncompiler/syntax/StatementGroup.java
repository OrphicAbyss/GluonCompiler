package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Operator;
import gluoncompiler.Token;
import gluoncompiler.TokenType;
import java.util.ArrayList;

/**
 * Statement Group := (<DefineVariable> | <If Statement> | <ForStatement> | <WhileStatement> | <BreakStatement>) \n
 *					  [<Statement Group>]
 */
public class StatementGroup extends SyntaxObject {
	
	Token first;
	Keyword[] targetKeywords;
	Operator[] targetOperators;
	ArrayList<Statement> children;
	
	public StatementGroup(Token start, Keyword[] targets, ScopeObject parentScope) {
		first = start;
		children = new ArrayList<>();
		targetKeywords = targets;
		targetOperators = new Operator[0];
		scope = new ScopeObject(parentScope);
	}
	
	public StatementGroup(Token start, Operator[] targets, ScopeObject parentScope) {
		first = start;
		children = new ArrayList<>();
		targetKeywords = new Keyword[0];
		targetOperators = targets;
		scope = new ScopeObject(parentScope);
	}
	
	@Override
	public Token parse() {
		Token next = first;
		
		while (!next.isEOF() && !isTargetKeyword(next) && !isTargetOperator(next)) {
			Statement stmt;
			switch (next.getType()) {
				case KEYWORD:
					switch (next.getKeyword()) {
						case VAR:
							stmt = new DefineVariable(next, scope);
							break;
						case IF:
							stmt = new IfStatement(next, scope);
							break;
						case FOR:
							stmt = new ForStatement(next, scope);
							break;
						case WHILE:
							stmt = new WhileStatement(next, scope);
							break;
						case BREAK:
							stmt = new BreakStatement(next, scope);
							break;
						default:
							gluoncompiler.Error.abort("Unexpected keyword found: " + first.getKeyword().name() + " when parsing Statement.");
							return null;
					}
					break;
				case IDENTIFIER:
					stmt = new Statement(next, scope);
					break;
				default:
					TokenType[] expected = {TokenType.KEYWORD, TokenType.IDENTIFIER};
					gluoncompiler.Error.expected(expected, first, "Statement");
					return null;
			}
			
			children.add(stmt);
			next = stmt.parse();
			assert(next.isNewline());
			next = next.getNext();
		}
		
		return next;
	}
	
	private boolean isTargetKeyword(Token cur) {
		if (!cur.isKeyword())
			return false;
		
		for (Keyword target: targetKeywords) {
			if (target.equals(cur.getKeyword()))
				return true;
		}
		
		return false;
	}
	
	private boolean isTargetOperator(Token cur) {
		if (!cur.isOperator())
			return false;
		
		for (Operator target: targetOperators) {
			if (target.equals(cur.getOperator()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public void emitCode(StringBuilder code) {
		for (Statement stmt: children)
			stmt.emitCode(code);
	}

	@Override
	public void print(int level) {
		printClass(level);
		for (Statement stmt: children) {
			stmt.print(level + 1);
		}
	}
}
