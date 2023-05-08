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
	private String alphabet;
	private Map<Integer, List<String>> wordsByLength;
	private Set<String> solvedWords;

	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.solvedWords=new HashSet<>();
		this.wordsByLength = new HashMap<>();
		this.dict = dict;
		this.alphabet = generateRandomAlphabet();
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
		int score = this.alphabetScore(this.alphabet);
		int actualScore;
		String actualAlphabet;
		int boucleI = 0;
		for (String encodedWord : encodedWords) {
			boucleI++;
			System.out.println(boucleI + "/" + encodedWords.size());
			String word = getCompatibleWord(encodedWord);
			if(word==null)continue;
			actualAlphabet = generateAlphabet(encodedWord, word.toUpperCase());
			actualScore = this.alphabetScore(actualAlphabet);
			
			if (actualScore > score) {
				score = actualScore;
				this.alphabet = actualAlphabet;
			}

		}

		return this.alphabet;// TODO
	}

	

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * 
	 * @param text     A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
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
	
	private String generateAlphabet(String encoded, String word) {
		char[] inverseAlphabet = new char[26];
		for (int i = 0; i < this.alphabet.length(); i++) {
			inverseAlphabet[i] = this.alphabet.charAt(i);
		}

		for (int i = 0; i < encoded.length(); i++) {
			char encodedChar = encoded.charAt(i);
			char wordChar = word.charAt(i);
			if (LETTERS.indexOf(encodedChar + "") == -1)
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

	private String generateRandomAlphabet() {
		List<Character> shuffledAlphabet = new ArrayList<>();
		for (int i = 0; i < LETTERS.length(); i++) {
			shuffledAlphabet.add(LETTERS.charAt(i));
		}
		Collections.shuffle(shuffledAlphabet);

		StringBuilder sb = new StringBuilder();
		for (char c : shuffledAlphabet) {
			sb.append(c);
		}
		return sb.toString();
	}

	private int alphabetScore(String alphabet) {
		if (alphabet.length() != 26)
			return 0;
		int score = 0;
		for (String word : this.encodedWords) {
			if(this.solvedWords.contains(word))continue;
			if (dict.containsWord(applySubstitution(word, alphabet).toLowerCase())) {
				this.solvedWords.add(word);
				score += 1;
			}
		}
		return score;
	}

	private boolean isCompatible(String word, String encodedWord) {
		Map<Character, Character> correspondance=new HashMap<>();
		word=word.toUpperCase();
		if (encodedWord.length() != word.length()) {
			return false;
		}
		
		for(int i=0;i<word.length();i++) {
			Character wordCharacter = Character.valueOf(word.charAt(i));
			Character encodedCharacter = Character.valueOf(encodedWord.charAt(i));
			if(correspondance.get(wordCharacter)!=null&&correspondance.get(wordCharacter)!=encodedCharacter) {
				return false;
			}
			correspondance.put(wordCharacter, encodedCharacter);
		}
		
		return true;
	}

	private String getCompatibleWord(String encodedWord) {
		List<String> words=this.wordsByLength.get(encodedWord.length());
		if(words==null) {
			words=dict.getWordsOfLength(encodedWord.length());
			this.wordsByLength.put(encodedWord.length(), words);
		}
		for (String word : words) {
			if (isCompatible(word, encodedWord)) {
				return word;
			}
		}
		return null;
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
		//System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		//System.out.println();
	}
}
