//double ended queue
public class deque {
	Node first; //pointer to first node
	Node last; //pointer to last node

	public deque(){ //new deque with null first and last
		first = last = null;
	}

	public void addFirst(int e){ //adds a node to the start
		if(first != null){ //if there is already items in queue
			Node oldFirst = first; //temp var of old first
			first = new Node(e); //first is now new node passed in
			oldFirst.previous = first; //update pointers of old first and new first
			first.next = oldFirst;
		}
		else first = last = new Node(e); //else queue is empty, so first and last are equal to new node passed in
	}

	public void addLast(int e){ //adds node to end
		if(last != null){ //if queue isnt empty
			Node oldLast = last; 
			last = new Node(e); //sets new last and updates pointers
			oldLast.next = last;
			last.previous = oldLast;
		}
		else first = last = new Node(e);
	}

	public int removeFirst(){ //removes first node in queue
		Node oldFirst = first; 
		first = oldFirst.next; //sets new first and pointers
		if(first != null) first.previous = null; //if we didnt just remove the only node, set the pointer
		else last = null; //if we did remove the only node, last is null aswell as first
		return oldFirst.value;
	}

	public int removeLast(){ //removes last node in queue
		Node oldLast = last;
		last = oldLast.previous; //sets new last and pointers
		if(last != null) last.next = null; //set pointer if there is still nodes in quue
		else first = null;
		return oldLast.value;
	}

	@Override
	public String toString(){ //string representation of the queue
		String line = "";
		Node temp = first;
		while(temp != null){
			line += temp.value + ", ";
			temp = temp.next;
		}
		return line;
	}
}
