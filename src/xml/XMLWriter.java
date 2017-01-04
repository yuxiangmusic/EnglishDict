package xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import core.Def;
import core.TrieMap;
import core.Word;

public class XMLWriter implements XMLIO {

	public static void write(String fileName, TrieMap trieMap) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(DICT);
			doc.appendChild(rootElement);

			// words
			for (Word word : trieMap.values()) {
				if (word.defList().isEmpty())
					continue;
				Element wordElement = doc.createElement(WORD);
				rootElement.appendChild(wordElement);
				wordElement.setAttribute(KEY, word.key());

				for (Def def : word.defList()) {
					if (def.isEmpty())
						continue;
					Element defElement = doc.createElement(DEF);
					wordElement.appendChild(defElement);
					// EN
					if (!def.en().isEmpty()) {
						Element enElement = doc.createElement(EN);
						defElement.appendChild(enElement);
						enElement.appendChild(doc.createTextNode(def.en()));
					}
					// ZH
					if (!def.zh().isEmpty()) {
						Element zhElement = doc.createElement(ZH);
						defElement.appendChild(zhElement);
						zhElement.appendChild(doc.createTextNode(normalize(def.zh())));
					}
					// Part of Speech
					if (!def.pa().isEmpty()) {
						Element paElement = doc.createElement(PA);
						defElement.appendChild(paElement);
						paElement.appendChild(doc.createTextNode(def.pa()));
					}
					// Pronunciation
					if (!def.pr().isEmpty()) {
						Element phElement = doc.createElement(PR);
						defElement.appendChild(phElement);
						phElement.appendChild(doc.createTextNode(def.pr()));
					}
					// Examples
					if (!def.ex().isEmpty()) {
						Element exElement = doc.createElement(EX);
						defElement.appendChild(exElement);
						for (String s : def.ex()) {
							Element sElement = doc.createElement(S);
							exElement.appendChild(sElement);
							sElement.appendChild(doc.createTextNode(s));
						}
					}
				} // end of DEF
			}

			// write to XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	static String normalize(String s) {
		s = s.replaceAll("，", ", ");
		s = s.replaceAll("。", ". ");
		s = s.replaceAll("；", "; ");
		s = s.replaceAll("（", "(");
		s = s.replaceAll("）", ")");
		s = s.replaceAll("“", "\"");
		s = s.replaceAll("”", "\"");
		s = s.replaceAll("‘", "'");
		s = s.replaceAll("’", "'");
		return s;
	}

}
