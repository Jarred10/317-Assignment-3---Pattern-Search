import java.util.Arrays;

public class FSM {

	int[] st = new int[100]; //states
	int[] ch = new int[100]; //char to consume (unicode representation)
	int[] next1 = new int[100]; //states to move to after this state
	int[] next2 = new int[100];

	public FSM(){}

	@Override
	public String toString(){ //prints the contents of arrays
		return Arrays.toString(st) + "\n" + Arrays.toString(ch) + "\n" + Arrays.toString(next1) + "\n" + Arrays.toString(next2);
	}

}
