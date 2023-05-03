package boggle;

import java.util.Objects;

import org.hamcrest.core.IsInstanceOf;

public class Sommet {
	private final char letter;
	private boolean isVisited;

	public Sommet(char letter) {
		this.letter = letter;
		this.isVisited = false;
	}

	public char getLetter() {
		return letter;
	}

	public void setVisited(boolean value) {
		this.isVisited = value;
	}

	public boolean isVisited() {
		return this.isVisited;
	}
	
	

}
