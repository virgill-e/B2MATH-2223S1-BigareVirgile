package tree;

import java.util.HashMap;

public class Node {
	private final char letter;
	private boolean isFinal;
	private HashMap<Character, Node> childs;
	
	public Node(char letter) {
		this.letter=letter;
		childs=new HashMap<>();
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
		return this.childs.get(Character.valueOf(c));
	}

	public void addChild(Node child) {
		this.childs.put(Character.valueOf(child.getLetter()), child);
		
	}
}
