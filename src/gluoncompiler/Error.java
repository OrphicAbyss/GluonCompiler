package gluoncompiler;

/**
 * Class for handling errors.
 */
public class Error {
	static GluonScanner scanner = null;

	public static void init(GluonScanner scanner){
		Error.scanner = scanner;
	}

	/**
	 * Print out an error message.
	 */
	public static void error(String error){
		if (scanner != null){
			System.out.printf("Error on line %d position %d\n%s\n",scanner.lineNumber,scanner.position,error);
			// Print out a marker to the line we were on
			System.out.print(scanner.line);
			for (int i=1; i<scanner.position; i++){
				System.out.print("-");
			}
			System.out.println("^");
		} else {
			System.out.printf("Error compiling: %s\n",error);
		}
	}

	/**
	 * Print out an error message and exit.
	 */
	public static void abort(String error){
		error(error);
		System.exit(1);
	}

	public static void expected(TokenType type, Token found, String inStatement){
		abort("Statement: " + inStatement + "\nExpected token type: " + type.toString() + "\nFound token: " + found.toString());
	}

	private static void printStatement(StringBuilder sb, String inStatement){
		if (inStatement != null){
			sb.append("Statement: ");
			sb.append(inStatement);
			sb.append("\n");			
		}		
	}
	
	public static void expected(Operator operator, Token found, String inStatement){
		StringBuilder sb = new StringBuilder();
		printStatement(sb, inStatement);
		
		sb.append("Expected operator: ");
		sb.append(operator.name());
		sb.append("\n");
		
		sb.append("Found token: ");
		sb.append(found.toString());
		
		abort(sb.toString());
	}

	public static void expected(TokenType[] types, Token found, String inStatement){
		StringBuilder sb = new StringBuilder();
		printStatement(sb, inStatement);

		sb.append("Expected token types: ");
		boolean first = true;
		for (TokenType type: types){
			if (!first)
				sb.append(", ");
			sb.append(type);
			first = false;
		}
		sb.append("\n");

		sb.append("Found token: ");
		sb.append(found.toString());

		abort(sb.toString());
	}

	/**
	 * Expected a different value/token in the data
	 */
	public static void expected(String value, String matchingStatement){
		if (matchingStatement == null)
			abort("Expected: " + value);
		else
			abort("Matching statement: " + matchingStatement + "\nExpected: " + value);
	}
}

















