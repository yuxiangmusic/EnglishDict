package awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import core.Def;
import core.TrieMap;
import core.Word;
import xml.XMLParser;
import xml.XMLWriter;

/**
 * @author Yuxiang Zhang
 */
public class App {

	static final TrieMap trieMap = new TrieMap();
	static final String DICT_XML = "dict.xml";

	static class DictFrame extends JFrame {

		private static final long serialVersionUID = 1L;
		private static final int NUM_ROWS = 33;

		private JTextField wordField;
		private JPanel contentPanel, resultPanel;

		private JTable table;
		private String selectedKey;

		public DictFrame() {
			setBounds(0, 0, 600, 600);
			setMinimumSize(new Dimension(600, 600));
			setMaximumSize(new Dimension(999, 600));

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Dictionary");

			contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setContentPane(contentPanel);

			JPanel mainPanel = new JPanel();
			contentPanel.add(mainPanel);

			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
			JPanel mainLPanel = new JPanel();
			JPanel mainRPanel = new JPanel();
			mainLPanel.setLayout(new BoxLayout(mainLPanel, BoxLayout.PAGE_AXIS));
			mainRPanel.setLayout(new BoxLayout(mainRPanel, BoxLayout.PAGE_AXIS));
			mainPanel.add(mainLPanel);
			mainPanel.add(mainRPanel);

			// word search
			mainLPanel.add(createTablePanel());
			mainRPanel.add(createSearchPanel());
			mainRPanel.add(createResultPanel());

			setVisible(true);

			// close event
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowEvent) {
					XMLWriter.write(DICT_XML, trieMap);
				}
			});
		}

		private JPanel createTablePanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.setMaximumSize(new Dimension(150, 750));
			table = new JTable(NUM_ROWS, 1) {
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setTableHeader(null); // no header
			table.setPreferredScrollableViewportSize(new Dimension(150, 750));
			table.setDefaultRenderer(Object.class, new TableCellRenderer() {
				final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (isSelected) {
						if (hasFocus) {
							selectedKey = (String) value;
							updateResultPanel(selectedKey);
						}

						c.setBackground(new Color(125, 125, 255));
					} else {
						int rgb = row % 2 == 0 ? 250 : 245;
						c.setBackground(new Color(rgb, rgb, rgb));
					}
					return c;
				}
			});
			table.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					JTable table = (JTable) me.getSource();
					Point p = me.getPoint();
					int row = table.rowAtPoint(p);
					if (me.getClickCount() == 2) {
						wordField.setText((String) table.getValueAt(row, 0));
						table.clearSelection();
						wordField.requestFocus();
					}
				}
			});

			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			panel.add(scrollPane);
			return panel;
		}

		private JPanel createSearchPanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.setAlignmentY(TOP_ALIGNMENT);
			panel.add(wordField = new JTextField());

			wordField.setMaximumSize(new Dimension(900, 50));
			wordField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					table.clearSelection();
					updateResultPanel(wordField.getText());
				}

				@Override
				public void focusLost(FocusEvent e) {
				}

			});
			wordField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					selectedKey = wordField.getText();

					if (wordField.getText().isEmpty())
						clearTableRows();
					else
						updateTableRows(trieMap.wordList(wordField.getText()));

					updateResultPanel(wordField.getText());
				}
			});

			return panel;
		}

		private JPanel createResultPanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.setAlignmentY(TOP_ALIGNMENT);

			resultPanel = new JPanel();
			resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));

			JScrollPane scrollPane = new JScrollPane(resultPanel);
			panel.add(scrollPane);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());

			return panel;
		}

		private void updateResultPanel(String key) {
			resultPanel.removeAll();
			if (key == null)
				return;
			if (!key.isEmpty()) {
				Word word = trieMap.get(key);
				if (word == null)
					resultPanel.add(createNoDefPanel(key));
				else
					for (Def def : word.defList())
						resultPanel.add(createDefPanel(key, def));
				resultPanel.add(createAddDefPanel());
			}
			resultPanel.revalidate();
			resultPanel.repaint();
		}

		private JPanel createNoDefPanel(String key) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JLabel keyLabel = new JLabel(key);
			Font font = keyLabel.getFont();
			font = new Font(font.getFontName(), Font.BOLD, font.getSize());
			keyLabel.setFont(font);
			panel.add(keyLabel);

			JLabel nosuchwordLabel = new JLabel("no such word");
			nosuchwordLabel.setForeground(Color.RED);
			panel.add(nosuchwordLabel);

			return panel;
		}

		private JPanel createDefPanel(String key, Def def) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JLabel keyLabel = new JLabel(key);
			Font font = keyLabel.getFont();
			font = new Font(font.getFontName(), Font.BOLD, font.getSize());
			keyLabel.setFont(font);
			panel.add(keyLabel);

			if (!def.pr().isEmpty())
				panel.add(new JLabel("[" + def.pr() + "]"));
			if (!def.pa().isEmpty())
				panel.add(new JLabel(def.pa()));
			if (!def.zh().isEmpty())
				panel.add(new JLabel(def.zh()));
			JLabel enLabel = new JLabel("<html>" + def.en() + "</html>");
			if (!def.en().isEmpty())
				panel.add(enLabel);

			JButton editButton = new JButton("edit");
			panel.add(editButton);
			editButton.setForeground(Color.BLUE);
			editButton.setBorder(BorderFactory.createEmptyBorder());
			editButton.addActionListener((e) -> {
				new EditFrame(def, null, selectedKey); // edit
			});
			return panel;
		}

		private JPanel createAddDefPanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JButton addDefButton = new JButton("add definition");
			panel.add(addDefButton);
			addDefButton.setForeground(Color.BLUE);
			addDefButton.setBorder(BorderFactory.createEmptyBorder());
			addDefButton.addActionListener((e) -> {
				Word word = trieMap.get(selectedKey);
				if (word == null)
					trieMap.put(selectedKey, word = new Word(selectedKey));
				new EditFrame(new Def(), word, selectedKey); // add
			});
			return panel;
		}

		private void updateTableRows(List<Word> wordList) {
			for (int i = 0; i < NUM_ROWS; i++)
				if (i < wordList.size())
					table.getModel().setValueAt(wordList.get(i).key(), i, 0);
				else
					table.getModel().setValueAt("", i, 0);
		}

		private void clearTableRows() {
			for (int i = 0; i < NUM_ROWS; i++)
				table.getModel().setValueAt("", i, 0);
		}

		public void refreshResultPanel() {
			updateResultPanel(selectedKey);
		}
	}

	static class EditFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private JLabel enLabel, zhLabel, paLabel, phLabel;
		private JTextField enField, zhField, paField, prField;
		private JButton updateButton, saveButton;
		private JPanel contentPanel;

		final Def def;
		final String key;
		final Word word;

		public EditFrame(final Def def, final Word word, final String key) {
			this.def = def == null ? new Def() : def;
			this.key = key;
			this.word = word; // edit DEF if word == null, add new DEF to word otherwise

			setTitle((word == null ? "Edit" : "Add") + " definition for [" + key + "]");

			setBounds(600, 0, 600, 200);
			setMinimumSize(new Dimension(600, 200));
			setMaximumSize(new Dimension(600, 200));

			setVisible(true);

			contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setContentPane(contentPanel);

			contentPanel.add(createEditPanel());

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		private JPanel createEditPanel() {
			JPanel panel = new JPanel(), subPanel;
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setAlignmentX(LEFT_ALIGNMENT);

			// EN
			subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.add(enLabel = new JLabel("en"));
			subPanel.add(enField = new JTextField(def.en()));
			panel.add(subPanel);

			// ZH
			subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.add(zhLabel = new JLabel("zh"));
			subPanel.add(zhField = new JTextField(def.zh()));
			panel.add(subPanel);

			// Part of Speech
			subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.add(paLabel = new JLabel("part of speech"));
			subPanel.add(paField = new JTextField(def.pa()));
			panel.add(subPanel);

			// Pronunciation
			subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.add(phLabel = new JLabel("pronunciation"));
			subPanel.add(prField = new JTextField(def.pr()));
			panel.add(subPanel);

			enLabel.setPreferredSize(new Dimension(50, 0));
			zhLabel.setPreferredSize(new Dimension(50, 0));
			paLabel.setPreferredSize(new Dimension(100, 0));
			phLabel.setPreferredSize(new Dimension(100, 0));

			enField.setMaximumSize(new Dimension(750, 50));
			zhField.setMaximumSize(new Dimension(750, 50));
			paField.setMaximumSize(new Dimension(750, 50));
			prField.setMaximumSize(new Dimension(750, 50));

			panel.add(createUpdatePanel());

			return panel;
		}

		private JPanel createUpdatePanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.add(updateButton = new JButton("update"));
			panel.add(saveButton = new JButton("save"));
			updateButton.setAlignmentX(RIGHT_ALIGNMENT);
			saveButton.setAlignmentX(RIGHT_ALIGNMENT);

			updateButton.addActionListener((e) -> {
				if (key.isEmpty())
					return;
				String en = enField.getText();
				String zh = zhField.getText();
				String pa = paField.getText();
				String pr = prField.getText();
				List<String> ex = new ArrayList<>(); // TODO examples

				if (word == null) { // edit
					def.setDef(en, zh, pa, pr, ex);
				} else { // add
					def.setDef(en, zh, pa, pr, ex);
					if (!def.isEmpty())
						word.addDef(def);
				}
				dictFrame.refreshResultPanel();
			});

			saveButton.addActionListener((e) -> {
				XMLWriter.write(DICT_XML, trieMap);
			});

			return panel;
		}

		public void resetField() {
			enField.setText("");
			zhField.setText("");
			paField.setText("");
			prField.setText("");
		}
	}

	static DictFrame dictFrame;

	public static void main(String[] args) {
		XMLParser.parse(trieMap, DICT_XML);
		dictFrame = new DictFrame();
	}

}
