package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Token;
import gluoncompiler.TokenType;
import java.util.ArrayList;

/**
 * Statement Group := (<DefineVariable> | <If Statement> | <ForStatement> | <WhileStatement> | <BreakStatement>) \n
 *					  [<Statement Group>]
 */
public class StatementGroup extends SyntaxObject {
	Token first;
	Keyword[] targets;
	ArrayList<Statement> children;
	
	public StatementGroup(Token start){
		first = start;
		children = new ArrayList<>();
		targets = null;
	}
	
	public StatementGroup(Token start, Keyword[] targets){
		this.first = start;
		this.children = new ArrayList<>();
		this.targets = targets;
	}
	
	@Override
	public Token parse() {
		Token next = first;
		
		while (!next.isEOF() && (targets == null || !isTarget(next))){
			Statement stmt;
			switch (next.getType()){
				case KEYWORD:
					switch (next.getKeyword()){
						case VAR:
							stmt = new DefineVariable(next);
							break;
						case IF:
							stmt = new IfStatement(next);
							break;
						case FOR:
							stmt = new ForStatement(next);
							break;
						case WHILE:
							stmt = new WhileStatement(next);
							break;
						case BREAK:
							stmt = new BreakStatement(next);
							break;
						default:
							gluoncompiler.Error.abort("Unexpected keyword found: " + first.getKeyword().name() + " when parsing Statement.");
							return null;
					}
					break;
				case IDENTIFIER:
					stmt = new Statement(next);
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
	
	private boolean isTarget(Token cur){
		if (!cur.isKeyword())
			return false;
		
		for (Keyword target: targets){
			if (target.equals(cur.getKeyword()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void print(int level) {
		printClass(level);
		for (Statement stmt: children){
			stmt.print(level + 1);
		}
	}
}
