package simplecompiler;

/**
 * Output class to hold output ready to be written to a file.
 * 
 * @author DrLabman
 */
public class GluonOutput {
	static StringBuilder output;
	
		/**
	 * Output our code.
	 *
	 * @param s code to emit
	 */
	static void emitLn(String s, boolean prependTab){
		if (prependTab)
			output.append(String.format("\t%s\n",s));
		else
			output.append(String.format("%s\n",s));
	}

	static String getOutput(){
		return output.toString();
	}
}
