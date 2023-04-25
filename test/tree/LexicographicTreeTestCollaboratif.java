package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTestCollaboratif {
	private static final String[] WORDS = new String[] { "a-cote", "aide", "as", "au", "aujourd'hui", "aux", "bu",
			"bus", "but", "cote", "et", "ete" };
	private static final LexicographicTree DICT = new LexicographicTree();
	private static final String filename = "mots/dictionnaire_FR_sans_accents.txt";

	@BeforeAll
	public static void initTestDictionary() {
		for (int i = 0; i < WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}

	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i = 0; i < WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}

	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] { "", "aid", "ai", "aides", "mot", "e" }) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}

	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());

		assertEquals(0, DICT.getWords("x").size());

		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] { "bu", "bus", "but" }, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, DICT.getWordsOfLength(3).toArray());
	}

	@Test
	void testEmptyTree() {
		LexicographicTree tree = new LexicographicTree();
		assertEquals(0, tree.size());
	}

	@Test
	void testInsertWord() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		assertTrue(tree.containsWord("chat"));
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertDuplicateWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chat");
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertWordWithHyphenAndApostrophe() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("aujourd'hui");
		tree.insertWord("tire-bouchon");
		assertTrue(tree.containsWord("aujourd'hui"));
		assertTrue(tree.containsWord("tire-bouchon"));
		assertEquals(2, tree.size());
	}

	@Test
	void testContainsWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chien");
		assertTrue(tree.containsWord("chien"));
		assertFalse(tree.containsWord("chat"));
	}

	@Test
	void testGetWordsWithPrefix() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithPrefix = tree.getWords("ch");
		assertEquals(3, wordsWithPrefix.size());
		assertEquals("chat", wordsWithPrefix.get(0));
		assertEquals("cheval", wordsWithPrefix.get(1));
		assertEquals("chien", wordsWithPrefix.get(2));
	}

	@Test
	void testGetWordsOfLength() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsOfLength = tree.getWordsOfLength(4);
		assertEquals(1, wordsOfLength.size());
		assertTrue(wordsOfLength.contains("chat"));
	}

	@Test
	void testGetWordsWithEmptyPrefix() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithEmptyPrefix = tree.getWords("");
		assertEquals(4, wordsWithEmptyPrefix.size());
		assertTrue(wordsWithEmptyPrefix.containsAll(Arrays.asList("chat", "cheval", "chien", "oiseau")));
	}

	@Test
	void testGetWordsWithNonExistentPrefix() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithPrefix = tree.getWords("xyz");
		assertEquals(0, wordsWithPrefix.size());
	}

	@Test
	void testInsertWordWithSpecialCharacters() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat@123~");
		tree.insertWord("chien$%^");
		tree.insertWord("cheval*&(");
		tree.insertWord("oiseau)_+");

		assertTrue(tree.containsWord("chat@123~"));
		assertTrue(tree.containsWord("chien$%^"));
		assertTrue(tree.containsWord("cheval*&("));
		assertTrue(tree.containsWord("oiseau)_+"));
	}

	@Test
	void testInsertDictionnary() {
		LexicographicTree tree = new LexicographicTree(filename);
		List<String> dict = tree.getWords("");
		assertEquals(327956, dict.size());
	}

	@Test
	void testSearchingForWordsOfIncreasingLength() {
		LexicographicTree dico = new LexicographicTree(filename);
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			assertEquals(dico.size(), total);
		}
	}

	@Test
	void testSearchingNonExistingWordsInDictionary() {
		int repeatCount = 20;
		File file = new File(filename);
		LexicographicTree dico = new LexicographicTree(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine() + "xx";
					boolean found = dico.containsWord(word);
					if (found) {
						assertTrue(false, word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				assertTrue(false, "File not found: " + filename);
			}
		}
	}

	@Test
	void testSearchingExistingWordsInDictionary() {
		int repeatCount = 20;
		File file = new File(filename);
		LexicographicTree dico = new LexicographicTree(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine();
					boolean found = dico.containsWord(word);
					if (!found) {
						assertTrue(false, word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				assertTrue(false, "File not found: " + filename);
			}
		}
	}
}