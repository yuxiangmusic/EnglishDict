package core;

import java.util.LinkedList;
import java.util.List;

public class Word {

	final String key;

	final List<Def> defList = new LinkedList<>();

	public Word(String key) {
		this.key = key;
	}

	public Word(Word word) {
		this.key = word.key;
		defList.clear();
		for (Def def : word.defList())
			defList.add(new Def(def));
	}

	public String key() {
		return key;
	}

	public List<Def> defList() {
		return defList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[" + key + "]\n");
		for (Def def : defList) {
			sb.append(" [def]\n");
			if (!def.en().isEmpty())
				sb.append("  [en] " + def.en() + "\n");
			if (!def.zh().isEmpty())
				sb.append("  [zh] " + def.zh() + "\n");
			if (!def.pa().isEmpty())
				sb.append("  [pa] " + def.pa() + "\n");
			if (!def.pr().isEmpty())
				sb.append("  [pr] " + def.pr() + "\n");
		}
		return sb.toString().trim();
	}

	public void addDef(Def def) {
		defList.add(def);
	}
}
