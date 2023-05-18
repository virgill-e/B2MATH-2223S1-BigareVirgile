package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";

	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static final Pattern PATTERN_ALL_WORD = Pattern.compile("(\\w+)");
	private static final Comparator<String> COMP_STRING_BY_LENGTH = (word1, word2) -> word2.length() - word1.length();

	private List<String> encodedWords;
	private final LexicographicTree dict;
	private Map<Integer, List<String>> wordsByLength;
	private Map<String,String> solvedWords;
	private int afac=0;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.solvedWords=new HashMap<>();
		this.wordsByLength = new HashMap<>();
		this.dict = dict;
		this.encodedWords = new ArrayList<String>(Arrays.asList(cryptogram.split(" "))).stream()
				.filter(word -> PATTERN_ALL_WORD.matcher(word).matches() && word.length() >= 3).map(String::trim)
				.distinct().sorted(COMP_STRING_BY_LENGTH).collect(Collectors.toList());
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an
	 * approximated decoding alphabet.
	 * 
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		if(alphabet.length()!=26) {
			throw new IllegalArgumentException("the alphabet must be 26 in length");
		}
		int score = this.alphabetScore(alphabet);
		int actualScore;
		String actualAlphabet;
		for (String encodedWord : encodedWords) {
			if(solvedWords.containsKey(encodedWord))continue;
			String word = getCompatibleWord(encodedWord);
			if(word==null)continue;
			System.out.print(encodedWord);
			System.out.println(word);
			actualAlphabet = generateAlphabet(encodedWord, word.toUpperCase(),alphabet);
			actualScore = this.alphabetScore(actualAlphabet);
			
			if (actualScore > score) {
				score = actualScore;
				alphabet = actualAlphabet;
			}

		}

		return alphabet;
	}

	

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * 
	 * @param text     A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
//		if(!PATTERN_ALL_WORD.matcher(alphabet).matches()) {
//			throw new IllegalArgumentException("incorrect alphabet.");
//		}
		String result = "";
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == '\n') {
				result += c;
			} else {
				int index = LETTERS.indexOf(c);
				if (index != -1) {
					result += alphabet.charAt(index);
				}
			}
		}
		return result;
	}

	/*
	 * PRIVATE METHODS
	 */
	/**
	 * Compares two substitution alphabets.
	 * 
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	private String generateAlphabet(String encoded, String word,String alphabet) {
		//TODO: inverser les lettre et pas regen tout l'alphabet
		Set<Character> adedLetters=new HashSet<>();
		
		char[] inverseAlphabet = new char[26];
		for (int i = 0; i < alphabet.length(); i++) {
			inverseAlphabet[i] = alphabet.charAt(i);
		}

		for (int i = 0; i < encoded.length(); i++) {
			char encodedChar = encoded.charAt(i);
			char wordChar = word.charAt(i);
			if (LETTERS.indexOf(encodedChar + "") == -1||LETTERS.indexOf(wordChar + "") == -1)
				continue;
			int encodedIndex = encodedChar - 'A';

			inverseAlphabet[encodedIndex] = wordChar;
		}

		return new String(inverseAlphabet);
	}

	/**
	 * Load the text file pointed to by pathname into a String.
	 * 
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private int alphabetScore(String alphabet) {
		System.out.println(afac++);
		if (alphabet.length() != 26)
			return 0;
		int score = 0;
		for (String encodedword : this.encodedWords) {
			String word=applySubstitution(encodedword, alphabet).toLowerCase();

			if(this.solvedWords.get(encodedword)==word) {
				score++;
				continue;
			}
			if (dict.containsWord(word)) {
				this.solvedWords.put(encodedword,word);
				score += 1;
			}
		}
		return score;
	}


	private String getCompatibleWord(String encodedWord) {		
		List<String> words=this.wordsByLength.get(encodedWord.length());
		if(words==null) {
			words=dict.getWordsOfLength(encodedWord.length());
			this.wordsByLength.put(encodedWord.length(), words);
		}
		for(String word:words) {
			if(wordToCorrespondence(word).equals(wordToCorrespondence(encodedWord))) {
				return word;
			}
		}
		return null;
	}
	
	
	private String wordToCorrespondence(String word) {
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

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();

		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 * Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		String startAlphabet = LETTERS;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();

		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}