package simplecompiler;

/**
 * Class for handling errors.
 */
public class Error {
	/**
	 * Print out an error message.
	 */
	public static void error(GluonScanner scanner, String error){
		System.out.printf("Error on line %d position %d\n%s\n",scanner.lineNumber,scanner.position,error);
		// Print out a marker to the line we were on
		System.out.println(scanner.line);
		for (int i=1; i<scanner.position; i++){
			System.out.print("-");
		}
		System.out.println("^");
	}

	/**
	 * Print out an error message and exit.
	 */
	public static void abort(GluonScanner scanner, String error){
		error(scanner, error);
		System.exit(1);
	}

	/**
	 * Expected a different value/token in the data
	 */
	public static void expected(GluonScanner scanner, String value){
		abort(scanner, "Expected: " + value);
	}
}
