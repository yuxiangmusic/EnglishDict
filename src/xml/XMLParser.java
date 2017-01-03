package xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import core.Def;
import core.TrieMap;
import core.Word;

public class XMLParser {

	public static void main(String[] args) {
		TrieMap map = new TrieMap();
		parse(map, "GRE.xml");

		for (Word word : map.values())
			System.out.println(word);
	}

	public static void parse(TrieMap trieMap, String fileName) {
		try {
			File inputFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();
			NodeList wordList = rootElement.getElementsByTagName("word");
			for (int w = 0; w < wordList.getLength(); w++) {
				Node wordNode = wordList.item(w);
				if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
					Element wordElement = (Element) wordNode;
					final String key = wordElement.getAttribute("key");
					Word word = new Word(key);
					trieMap.add(word);

					// definitions
					NodeList defList = wordElement.getElementsByTagName("def");
					for (int d = 0; d < defList.getLength(); d++) {
						Node defNode = defList.item(d);
						if (defNode.getNodeType() == Node.ELEMENT_NODE) {
							Element defElement = (Element) defNode;
							String en = getText(wordElement, "en");
							String zh = getText(wordElement, "zh");
							String partofspeech = getText(defElement, "partofspeech");
							Def def = new Def(en, zh, partofspeech);
							word.addDef(def);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String getText(Element e, String tag) {
		NodeList nl = e.getElementsByTagName(tag);
		Node n = nl.item(0);
		return n == null ? "" : n.getTextContent();
	}
}
