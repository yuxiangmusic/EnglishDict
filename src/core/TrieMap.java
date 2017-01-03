package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class TrieMap implements Map<String, Word> {

	static class TrieEntry implements Map.Entry<String, Word> {
		final SortedMap<Character, TrieEntry> children = new TreeMap<>();
		final String key;

		final TrieEntry parent;

		Word word;

		TrieEntry(final String key, final TrieEntry parent) {
			this.key = key;
			this.parent = parent;
		}

		@Override
		public String getKey() {
			return word.key;
		}

		@Override
		public Word getValue() {
			return word;
		}

		@Override
		public Word setValue(Word value) {
			Word old = word;
			word = value;
			return old;
		}

	}

	final TrieEntry root = new TrieEntry("", null);

	private int size;

	/**
	 * For test
	 * 
	 * @param word
	 */
	public void add(String word) {
		put(word, new Word(word));
	}

	public void add(Word word) {
		put(word.key, word);
	}

	@Override
	public void clear() {
		root.children.clear();
		size = 0;
	}

	private void clearAncestors(TrieEntry entry) {
		while (entry.word == null) {
			final String key = entry.key;
			entry = entry.parent;
			entry.children.remove(key);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return getEntry((String) key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return containsKey(((Word) value).key);
	}

	private List<TrieEntry> entryList(TrieEntry root) {
		List<TrieEntry> list = new ArrayList<>(size);
		entryListRec(list, root);
		return list;
	}

	public List<Word> wordList(String root) {
		List<TrieEntry> list = entryList(getEntry(root));
		List<Word> wordList = new ArrayList<>();
		for (TrieEntry entry : list)
			wordList.add(entry.word);
		return wordList;
	}

	private void entryListRec(List<TrieEntry> list, TrieEntry root) {
		if (root == null)
			return;
		if (root.word != null)
			list.add(root);
		for (TrieEntry entry : root.children.values())
			entryListRec(list, entry);
	}

	@Override
	public Set<Entry<String, Word>> entrySet() {
		Set<Entry<String, Word>> set = new TreeSet<>();
		set.addAll(entryList(root));
		return set;
	}

	@Override
	public Word get(Object key) {
		TrieEntry entry = getEntry((String) key);
		return entry == null ? null : entry.word;
	}

	private TrieEntry getEntry(String key) {
		TrieEntry cur = root;
		for (char c : key.toCharArray()) {
			TrieEntry next = cur.children.get(c);
			if (next == null)
				return null;
			cur = next;
		}
		return cur;
	}

	/**
	 * Returns entry of the key <br>
	 * Creates a new entry if it does not exist
	 * 
	 * @param key
	 * @return
	 */
	private TrieEntry getEntryCreate(String key) {
		TrieEntry cur = root;
		StringBuilder sb = new StringBuilder();
		for (char c : key.toCharArray()) {
			sb.append(c);
			TrieEntry next = cur.children.get(c);
			if (next == null)
				cur.children.put(c, next = new TrieEntry(sb.toString(), cur));
			cur = next;
		}
		return cur;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new TreeSet<>();
		for (Entry<String, Word> entry : entryList(root))
			set.add(entry.getKey());
		return set;
	}

	@Override
	public Word put(String key, Word value) {
		TrieEntry entry = getEntryCreate(key);
		if (entry.word == null)
			size++;
		return entry.setValue(value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Word> m) {
		for (Map.Entry<? extends String, ? extends Word> entry : m.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public Word remove(Object key) {
		TrieEntry entry = getEntryCreate((String) key);
		if (entry.word != null)
			size--;
		Word word = entry.setValue(null);
		clearAncestors(entry);
		return word;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toStringRec(sb, root, 0);
		return sb.toString();
	}

	private void toStringRec(StringBuilder sb, TrieEntry entry, int depth) {
		for (int i = 0; i < depth; i++)
			sb.append(' ');
		sb.append(entry.key);
		if (entry.word != null)
			sb.append('.');
		sb.append(System.lineSeparator());
		for (TrieEntry child : entry.children.values())
			toStringRec(sb, child, depth + 1);
	}

	@Override
	public Collection<Word> values() {
		Collection<Word> coll = new ArrayList<>(size);
		for (Entry<String, Word> entry : entryList(root))
			coll.add(entry.getValue());
		return coll;
	}

}
