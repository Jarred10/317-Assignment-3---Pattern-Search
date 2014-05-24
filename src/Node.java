
public class Node {
	Node previous; //link to previous node
	Node next; //link to next node
	int value; //value of current node
	
	public Node(int value){ //creates a node with no previous or next. value set to parameter
		this.value = value;
		previous = null;
		next = null;
	}
	
	@Override //prints value of node
	public String toString(){
		return Integer.toString(value);
	}
}
