package npuzzlegame;

import java.util.ArrayList;


public class Node {
	String name;
	ArrayList<Integer> state;
	Node parent;
	int distance;
	String move;
	public int priority;
	
	public Node(String name){
		this.name = name;
	}
	
	public Node(){
		
	}
	
	
	public String getName(){
		return this.name;
	}
}
