import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class Compile {

	// Track how far through the expression we are
	public static int j = 0;
	// Grab the input expression
	public static String expression;
	// String holding all the special chars
	public static String specialChars = "()[]|?*+";
	// Boolean that lets you know if you should ignore that the char you're looking at is not a literal
	public static boolean ignore = false;
	// Integer to track what state you're up to
	public static int state = 1;
	
	// Finite state machine
	public static FSM fsm = new FSM();

	public static void main(String[] args) throws IOException {
		try{
		expression = args[0];
		}
		catch(Exception e){
			System.out.println("Invalid expression entered.");
			return;
		}
		// Go find out if it's an expression
		parse();
		File f = new File("regexp.txt");
		f.createNewFile();
		PrintWriter wr = new PrintWriter(f);
		wr.print(fsm.toString());
		wr.close();
	}

	public static void parse() {

		// The start state is the result of this expression
		int start = expression();

		// If you're looking at the end of the expression then it's legal so set the start state
		if(j >= expression.length())
			setState(0, -2, start, start);
		// Otherwise we didn't make it to the end so the regular expression isn't legal
		else
			error();
		setState(state, -2, -1, -1);
	}

	public static int expression() {
		// Expressions are terms
		int start = term();

		// If there is nothing in the string return false
		if(j >= expression.length()) return start;
		// And possibly more expression
		if(expression.charAt(j) == '(' || isLiteral())
			expression();

		return start;
	}

	public static boolean isLiteral() {
		// If there is nothing in the string return false
		if(j >= expression.length()) return false;
		// Grab the current char
		char c = expression.charAt(j);

		// If it's a backslash
		if(c == '\\') {
			// Consume it
			j++;
			// Set the ignore flag
			ignore = true;
			// Return true
			return true;
		}

		// Return if the current char is in the special character string
		return (specialChars.indexOf(c) == -1);
	}

	public static int term(){
		int start = state;
		state++;
		int fact = literal();
		if(isLiteral()||j>=expression.length()){
			return setState(start, -2, fact, fact);
		}
		char c = expression.charAt(j);
		if(c == '*'){
			j++; //consume *
			setState(start, -2, fact, state);
			setState(state-1, (char)fsm.ch[state-1], fact, state);
			return start;
		}
		if(c == '+'){
			j++; //consume +
			setState(start, -2, fact, fact);
			setState(state-1, (char)fsm.ch[state-1], start, state);
			return start;
		}
		if(c == '?'){
			j++; //consume ?
			setState(start, -2, fact, state);
			return start;
		}
		if(c == '|'){
			j++; //consume |
			int term = term();
			setState(start, -2, fact, term);
			
			setState(term-1, (char)fsm.ch[term-1], state, state);

			return start;
		}
		else 
			return setState(start, -2, fact, fact);
	}
	
	public static int literalSet() {
		//TODO: Fix
		// Seek the end bracket
		int start = state;
		if(expression.charAt(j) == ']'){
			j++;
			setState(state, -2, state+1, state+2);
			state++;
			setState(state, ']', -3, -3); //-3 = place holder for end of literal set;
			state++;
		}
		
		while(expression.charAt(j) != ']'){
			char c = expression.charAt(j);
			j++; //save and consume character;
			setState(state, -2, state+1, state+2);
			state++;
			setState(state, c, -3, -3); //-3 = place holder for end of literal set;
			state++;
			// If we don't find the end bracket throw an error
			if(j >= expression.length()) error();
		}
		fsm.next2[state-2] = state - 1;
		return start;
	}

	public static int literal() {
		// Literals are literals or we just saw a backslash so it can be any char
		if(ignore || isLiteral()) {
			setState(state, expression.charAt(j), state + 1, state + 1);
			state++;
			// Consume it
			j++;
			// Reset the ignore flag
			ignore = false;
			return state - 1;
		}
		// If it's an expression in brackets
		else if (expression.charAt(j) == '('){
			// Consume the bracket
			j++;
			// Go looking for an expression
			int r = expression();
			// If we're looking at a close bracket then consume it
			if (expression.charAt(j) == ')'){
				j++;
				return r;
			}
			// Else it's not legal
			else error();
		}
		// If we're looking at a square bracket then we're expecting a set of literals
		else if(expression.charAt(j) == '['){
			//consume the open square bracket
			j++;
			// Go looking for a set of literals
			int r = literalSet();
			// If we're looking at a close bracket then consume it
			if (expression.charAt(j) == ']'){
				j++;
				for(int i = 0; i < fsm.ch.length; i++){
					if(fsm.next1[i] == -3){
						fsm.next1[i] = fsm.next2[i] = state;
					}
				}
				return r;
			}
			// Otherwise throw an error
			else error();
		}
		return error();
	}

	public static int error() {
		System.err.println("illegal regex provided");
		System.exit(0);
		return -1;
	}

	public static int setState(int s, int c, int n1, int n2){
		// Update the finite state machine
		fsm.st[s] = s;
		fsm.ch[s] = c;
		fsm.next1[s] = n1;
		fsm.next2[s] = n2;
		return s;
	}

}
