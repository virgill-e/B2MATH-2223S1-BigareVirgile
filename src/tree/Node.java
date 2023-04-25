package tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;

public class Node implements Comparable<Node>{
	private final char letter;
	private boolean isFinal;
	private Node[] childs;
	
	public Node(char letter) {
		this.letter=letter;
		childs=new Node[0];
		
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	public boolean isFinal() {
		return this.isFinal;
	}
	
	public Node[] getChilds(){
			return childs;
	}
	
	public void setFinal() {
		this.isFinal=true;
	}

	public Node getChild(char c) {
		if(childs.length==0)return null;
		for(Node node:childs) {
			if(node.getLetter()==c)return node;
		}
		return null;
	}
	
	

	public void addChild(char c) {
		if(getChild(c)!=null)return;
		childs=Arrays.copyOf(childs, childs.length+1);
		childs[childs.length-1]=new Node(c);
	}
	public void addChild(Node n) {
		if(getChild(n.getLetter())!=null)return;
		childs=Arrays.copyOf(childs, childs.length+1);
		childs[childs.length-1]=n;
	}
	
	
	
	
	
	public void addWord(String word) {
	    if (word == null || word.isEmpty()) {
	        return;
	    }
	    char firstChar = word.charAt(0);
	    Node child = getChild(firstChar);
	    if (child == null) {
	    	child=new Node(firstChar);
	        addChild(child);
	    }
	    if (word.length() == 1) {
	        child.setFinal();
	    } else {
	        child.addWord(word.substring(1));
	    }
	}
	
	public boolean containsWord(String word) {
	    if (word == null) {
	        return true;
	    }
	    if(word.isEmpty())return false;
	    char firstChar = word.charAt(0);
	    Node child = getChild(firstChar);
	    if (child == null) {
	        return false;
	    }
	    if (word.length() == 1) {
	        return child.isFinal();
	    } else {
	        return child.containsWord(word.substring(1));
	    }
	}

	
	

	
	@Override
	public int compareTo(Node o) {
		return Character.valueOf(o.getLetter()).compareTo(this.getLetter());
	}

	
	
}

