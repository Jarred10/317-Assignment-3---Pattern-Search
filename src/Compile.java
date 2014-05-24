import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class Compile {

	// Track how far through the expression we are
	public static int j = 0;
	// Grab the input expression
	public static String expression = "b|ca";
	// String holding all the special chars
	public static String specialChars = "()[]|?*+";
	// Boolean that lets you know if you should ignore that the char you're looking at is not a literal
	public static boolean ignore = false;
	// Integer to track what state you're up to
	public static int state = 1;
	
	// Finite state machine
	public static FSM fsm = new FSM();

	public static void main(String[] args) throws IOException {
		// Go find out if it's an expression
		parse();
		// Make a new file
		File f = new File("regexp.txt");
		f.createNewFile();
		// Print the finite state machine to that file
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
		// Set the last state to be the end state
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
		// Start of the term is the current last state
		int start = state;
		// Increment state to make space for a branching machine
		state++;
		// Terms are a literal
		int fact = literal();
		// If we have run off the end or finished the term
		if(isLiteral()||j>=expression.length()){
			// Then clean up the branching state and return
			return setState(start, -2, fact, fact);
		}
		// Grab the next character
		char c = expression.charAt(j);
		// If it's closure
		if(c == '*'){
			// Consume the *
			j++; 
			// Branch to either the factor or continue on
			setState(start, -2, fact, state);
			// Factor points to itself so it can be repeated or can move onto state
			setState(state-1, (char)fsm.ch[state-1], fact, state);
			return start;
		}
		// If it's +
		if(c == '+'){
			// Consume +
			j++; 
			// Don't need to branch just point to the factor
			setState(start, -2, fact, fact);
			// Make state to loop back to the start or continue on
			setState(state-1, (char)fsm.ch[state-1], start, state);
			return start;
		}
		// If it's ?
		if(c == '?'){
			// Consume ?
			j++;
			// Branch to the factor of skip past the factor
			setState(start, -2, fact, state);
			return start;
		}
		// If it's Or
		if(c == '|'){
			// Consume |
			j++;
			// | Is followed by a term
			int term = term();
			// Make a branch to the factor and the term
			setState(start, -2, fact, term);
			// Update the state that points to the or
			setState(term-1, (char)fsm.ch[term-1], state, state);
			return start;
		}
		else 
			// Else just tidy up the branching machine to point only to factor
			return setState(start, -2, fact, fact);
	}
	
	public static int literalSet() {
		// Hold the start state
		int start = state;
		// Case to deal with []]
		if(expression.charAt(j) == ']'){
			// Consume the bracket
			j++;
			// Branch to future states
			setState(state, -2, state+1, state+2);
			state++;
			// Set the state that consumes ], -3 placeholds the end of the literal set
			setState(state, ']', -3, -3);
			state++;
		}
		// While we're still inside the literal set
		while(expression.charAt(j) != ']'){
			// Grab the character
			char c = expression.charAt(j);
			// Consume the character
			j++; 
			// Branch to future states
			setState(state, -2, state+1, state+2);
			state++;
			// Set the state that consumes the character, -3 placeholds the end of the literal set
			setState(state, c, -3, -3); //-3 = place holder for end of literal set;
			state++;
			// If we don't find the end bracket throw an error
			if(j >= expression.length()) error();
		}
		// Update the reference for the last char
		fsm.next2[state-2] = state - 1;
		return start;
	}

	public static int literal() {
		// Literals are literals or we just saw a backslash so it can be any char
		if(ignore || isLiteral()) {
			// Make a state for it
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
				// Set the references for the end state of the literal set
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
		// Print an error to standard err.
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
