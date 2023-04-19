package tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class Node {
	private final char letter;
	private boolean isFinal;
	private List<Node> childs;
	//private HashMap<Character, Node> childs;
	//private Node [] childs;
	public Node(char letter) {
		this.letter=letter;
		//childs=new HashMap<>();
		//childs=new Node[0];
		childs=new ArrayList<>();
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	public boolean isFinal() {
		return this.isFinal;
	}
	
	public void setFinal(boolean isFinal) {
		this.isFinal=isFinal;
	}

	public Node getChild(char c) {
		if(childs.size()==0)return null;
		for(Node child:childs) {
			if(child.getLetter()==c)return child;
		}
		return null;
	}

	public void addChild(Node child) {
		if(getChild(child.getLetter())!=null)return;
		this.childs.add(child);
		
	}
}
