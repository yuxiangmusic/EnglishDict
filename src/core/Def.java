package core;

public class Def {
	String en, zh, partofspeech;

	public Def(String en, String zh) {
		this.en = en;
		this.zh = zh;
	}

	public Def(String en, String zh, String ps) {
		this.en = en;
		this.zh = zh;
		this.partofspeech = ps;
	}

	public String zh() {
		return zh;
	}

	public String en() {
		return en;
	}

	public String partofspeech() {
		return partofspeech;
	}

	public boolean isEmpty() {
		return en.isEmpty() && zh.isEmpty() && partofspeech.isEmpty();
	}
}
