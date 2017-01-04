package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Def {
	String en, zh, pa, pr;
	List<String> ex = new ArrayList<>();

	public Def() {
		en = zh = pa = pr = "";
	}

	public Def(Def def) {
		this.en = def.en();
		this.zh = def.zh();
		this.pa = def.pa();
		this.pr = def.pr();
		this.ex = def.ex();
	}

	public Def(String en, String zh, String pa, String pr, List<String> ex) {
		setDef(en, zh, pa, pr, ex);
	}

	public void setDef(String en, String zh, String pa, String pr, List<String> ex) {
		Objects.requireNonNull(en);
		Objects.requireNonNull(zh);
		Objects.requireNonNull(pa);
		Objects.requireNonNull(pr);
		Objects.requireNonNull(ex);
		this.en = en;
		this.zh = zh;
		this.pa = pa;
		this.pr = pr;
		this.ex = ex;
	}

	public void addExample(String example) {
		ex.add(example);
		Collections.sort(ex);
	}

	public boolean isEmpty() {
		return en.isEmpty() && zh.isEmpty() && pa.isEmpty() && pr.isEmpty() && ex.isEmpty();
	}

	public String en() {
		return en;
	}

	public String zh() {
		return zh;
	}

	public String pa() {
		return pa;
	}

	public String pr() {
		return pr;
	}

	public List<String> ex() {
		return ex;
	}
}
