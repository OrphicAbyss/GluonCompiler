package simplecompiler;

/**
 * Class for handling errors.
 */
public class Error {
	/**
	 * Print out an error message.
	 *
	 * @param error message to print to console
	 */
	public static void error(String error){
		System.out.printf("Error on line %d position %d\n%s\n%s",GluonScanner.lineNumber,GluonScanner.position,error,GluonScanner.line);
		for (int i=1; i<GluonScanner.position; i++){
			System.out.print("-");
		}
		System.out.println("^");
	}

	/**
	 * Print out an error message and exit.
	 *
	 * @param error message to print to console
	 */
	public static void abort(String error){
		error(error);
		System.exit(1);
	}

		/**
	 * Expected a different value/token in the data
	 *
	 * @param value
	 */
	public static void expected(String value){
		abort("Expected: " + value);
	}
}
