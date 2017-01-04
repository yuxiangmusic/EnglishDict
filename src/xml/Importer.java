package xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import core.Def;
import core.TrieMap;
import core.Word;

public class Importer {

	public static void main(String[] args) throws FileNotFoundException {
		TrieMap trieMap = new TrieMap();
		XMLParser.parse(trieMap, XMLIO.DICT_XML);

		// import
		Scanner sc = new Scanner(new File("oxford.txt"));
		while (sc.hasNext()) {
			String key = sc.next().toLowerCase();
			String pa = sc.next();
			String en = sc.nextLine();

			if (pa.endsWith(".")) {
				Word word = new Word(key);
				Def def = new Def();
				word.addDef(def);
				def.setDef(en, "", pa, "", new ArrayList<String>());
				trieMap.add(word);
			}
		}
		sc.close();

		XMLWriter.write(XMLIO.DICT_XML, trieMap);
	}

}
