package xml;

import core.TrieMap;
import core.Word;

public class XMLSorter {

	public static void main(String[] args) {
		TrieMap trieMap = new TrieMap();
		XMLParser.parse(trieMap, "GRE.xml");

		for (Word word : trieMap.values())
			System.out.println(word);

		XMLWriter.write("GRE.xml", trieMap);
	}

}
