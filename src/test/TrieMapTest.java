package test;

import java.io.FileNotFoundException;

import org.junit.Test;

import core.TrieMap;
import core.Word;
import xml.XMLParser;

public class TrieMapTest {

	@Test
	public void test() throws FileNotFoundException {
		TrieMap map = new TrieMap();

		XMLParser.parse(map, "GRE.xml");

		System.out.println(map);
	}

	@Test
	public void testWordList() throws FileNotFoundException {
		TrieMap map = new TrieMap();

		XMLParser.parse(map, "GRE.xml");

		for (Word word : map.wordList("sw"))
			System.out.println(word);
	}

}
