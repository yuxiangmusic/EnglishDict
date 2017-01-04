package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import core.Def;
import core.TrieMap;
import core.Word;

public class XMLParser implements XMLIO {

	public static void main(String[] args) {
		TrieMap map = new TrieMap();
		parse(map, DICT_XML);

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
			NodeList wordList = rootElement.getElementsByTagName(WORD);
			for (int w = 0; w < wordList.getLength(); w++) {
				Node wordNode = wordList.item(w);
				if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
					Element wordElement = (Element) wordNode;
					final String key = wordElement.getAttribute(KEY);
					Word word = new Word(key);
					trieMap.add(word);

					// definitions
					NodeList defList = wordElement.getElementsByTagName(DEF);
					for (int d = 0; d < defList.getLength(); d++) {
						Node defNode = defList.item(d);
						if (defNode.getNodeType() == Node.ELEMENT_NODE) {
							Element defElement = (Element) defNode;
							String en = getText(defElement, EN);
							String zh = getText(defElement, ZH);
							String pa = getText(defElement, PA);
							String pr = getText(defElement, PR);
							List<String> ex= getTextList(defElement, S);
							Def def = new Def(en, zh, pa, pr, ex);
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

	static List<String> getTextList(Element e, String tag) {
		List<String> l = new ArrayList<>();
		NodeList nl = e.getElementsByTagName(tag);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			l.add(n.getTextContent());
		}
		return l;
	}
}
