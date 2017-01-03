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

public class XMLWriter {

	public static void write(String fileName, TrieMap trieMap) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("GRE");
			doc.appendChild(rootElement);

			// words
			for (Word word : trieMap.values()) {
				Element wordElement = doc.createElement("word");
				rootElement.appendChild(wordElement);
				wordElement.setAttribute("key", word.key());

				for (Def def : word.defList()) {
					if (!def.isEmpty()) {
						Element defElement = doc.createElement("def");
						wordElement.appendChild(defElement);

						// EN
						if (!def.en().isEmpty()) {
							Element enElement = doc.createElement("en");
							defElement.appendChild(enElement);
							enElement.appendChild(doc.createTextNode(def.en()));
						}

						// ZH
						if (!def.zh().isEmpty()) {
							Element zhElement = doc.createElement("zh");
							defElement.appendChild(zhElement);
							zhElement.appendChild(doc.createTextNode(def.zh()));
						}
					}
				}
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

}
