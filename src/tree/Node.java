package tree;

import java.util.Arrays;
import java.util.HashMap;

public class Node {
	private final char letter;
	private boolean isFinal;
	//private HashMap<Character, Node> childs;
	private Node [] childs;
	public Node(char letter) {
		this.letter=letter;
		//childs=new HashMap<>();
		childs=new Node[0];
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
		if(childs.length==0)return null;
		for(Node child:childs) {
			if(child.getLetter()==c)return child;
		}
		return null;
		//return this.childs.get(Character.valueOf(c));
	}

	public void addChild(Node child) {
		if(getChild(child.getLetter())!=null)return;
		childs=Arrays.copyOf(childs, childs.length+1);
		childs[childs.length-1]=child;
		//this.childs.put(Character.valueOf(child.getLetter()), child);
		
	}
}
