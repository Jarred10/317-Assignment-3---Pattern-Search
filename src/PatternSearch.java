import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PatternSearch {

	public static FSM regexp = new FSM();
	public static File path;
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(new File(args[0])); //scanner to read the fsm
		
		String line = s.nextLine(); //first line of fsm
		line = (String) line.subSequence(1, line.length() - 1); //takes off [ and ]	
		String[] states = line.split(", "); //splits into array seperated by comma
		//repeat for all lines
		line = s.nextLine(); 
		line = (String) line.subSequence(1, line.length() - 1);
		String[] chars = line.split(", ");
		line = s.nextLine();
		line = (String) line.subSequence(1, line.length() - 1);
		String[] next1 = line.split(", ");
		line = s.nextLine();
		line = (String) line.subSequence(1, line.length() - 1);
		String[] next2 = line.split(", ");
		
		regexp.st = new int[states.length];		
		regexp.ch = new int[chars.length];
		regexp.next1 = new int[next1.length];
		regexp.next2 = new int[next2.length];
		
		for(int i = 0; i < states.length; i++){ //copies read chars as integers
			regexp.st[i] = Integer.parseInt(states[i]);
			regexp.ch[i] = Integer.parseInt(chars[i]);
			regexp.next1[i] = Integer.parseInt(next1[i]);
			regexp.next2[i] = Integer.parseInt(next2[i]);
		}
		
		path = new File(args[1]); //path to read file from
		
		findPattern fP = new findPattern(); //makes instance of findPattern class
		fP.find(); //calls method to find all matches
		
		s.close(); //closes scanner
		
	}

}
