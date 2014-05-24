import java.io.FileNotFoundException;
import java.util.Scanner;

public class findPattern {

	Scanner s;
	int scan = -5934659; //random negative int to represent scan
	FSM f = PatternSearch.regexp; //pointer to the regexp

	public findPattern() throws FileNotFoundException{ //contructs the scanner
		s = new Scanner(PatternSearch.path);
	}

	public void find(){ //finds matches and then prints line

		while(s.hasNextLine()){ //while there are lines in the file left to try and match
			char[] line = s.nextLine().toCharArray(); //splits line into chars
			int indexIntoLine = 0; //how far into original string we have checked for pattern
			boolean success = false; //boolean set if we find match, dont need to try and match at other indexes of line
			while(indexIntoLine < line.length && !success){ //while we havent checked for pattern at every character in line and havent already found a match

				boolean fail = false; //for this line, start with no match
				int j = indexIntoLine; //temp variable to progress through the line
				deque dq = new deque(); //instance of deque
				dq.addFirst(scan); //starts with scan
				dq.addFirst(f.next1[0]); //pushing start state
				if(f.next1[0] != f.next2[0]) dq.addFirst(f.next2[0]); //adds the next1 of first state, which is always 0. if its next2 is different, add it too

				while(!fail && !success){ //while we haven't reached end state
					int state = dq.removeFirst(); //pop current state
					if(state == scan) { //if we read scan, move to bottom of deque
						dq.addLast(scan); 
						if(dq.first.value == scan) fail = true; //if scan is only thing left, we failed
					}
					else if(state == -1) //if we get to -1 we have traversed machine and succeeded in matching 
						success = true;
					else if(j > line.length) fail = true; //if we are trying to read past our line length
					else if(f.ch[state] != -2){ //non-branching machine
						if(j < line.length && (int)line[j] == f.ch[state]){ //if Unicode values of chars matches, current state was consumed, add at least next1
							dq.addLast(f.next1[state]);
							if(f.next1[state] != f.next2[state]) //if next2 is different, add it as well
								dq.addLast(f.next2[state]);
							j++; //char consumption
						}
						//else we just move on, essentially tossing the state
					}
					else { //branching machine
						dq.addFirst(f.next1[state]);
						if(f.next1[state] != f.next2[state]) //if next2 is different, add it aswell
							dq.addFirst(f.next2[state]);
					}
				}
				if(success) System.out.println(line); //after we either fail or succeed, print if we succeeded
				indexIntoLine++; //move to next point in line. only used if we failed
			}
		}
	}
}
