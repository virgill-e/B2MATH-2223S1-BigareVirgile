package cryptanalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class FindCorrespondance {
	
	
	public static String correspondance(String word) {
		StringJoiner wordCorrespondance=new StringJoiner("");
		Map<String, Integer> letters=new HashMap<>();
		
		for(String let:word.split("")) {
			if(!letters.containsKey(let)) {
				letters.put(let, letters.size());
			}
			wordCorrespondance.add(String.valueOf(letters.get(let)));
		}
		return wordCorrespondance.toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(FindCorrespondance.correspondance("virgile"));
		System.out.println(FindCorrespondance.correspondance("azerzop"));

	}
}
