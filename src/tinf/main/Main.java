package tinf.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/*
 * ZADATAK: Binarni blok kod K
 * 
 * 	Potrebno je implementirati programsko rješenje koje će omogućiti
 * 	unos generirajuće matrice G binarnog blok koda K proizvoljne
 * 	veličine te za unesenu matricu, tj. kod K odrediti i ispisati sljedeće
 *		
 *		1) n i k zadanog koda K
 *		2) je li kod K linearan?
 *		3) je li generirajuća matrica G u standardnom obliku? U slučaju da nije, matricu je
 *			potrebno transformirati u standardni oblik
 *		4) Kodnu brzinu koda K
 *
 *	Također, potrebno je na primjeru (ispisu u konzoli) prikazati zaštitno kodiranje proizvoljno
 *	unesene poruke, duljine k bita, binarnim blok kodom zadanim matricom G. Napomena: Zaštitno
 *	kodiranje mora biti provedeno postupkom najmanje složenosti ([1] str. 148)
 */


/*
 * Main izvršava:
 * 	Unos podataka i provjera njihove ispravnosti
 * 	pozivanje metoda
 * 	ispis rješenja
 * 	
 * 
 * 	Iskreno, u ovoj klasi nema se puno toga vidjeti,
 * 	vecina je samo javin boilerplate definiranja UI-a
 * 	dijelovi na koje treba obratiti pozornost je u action listenerima buttona
 * 	tamo se provjerava ispravnost unesenih podataka.
 * 	Provjerava se je li unesen redak dobre duljine
 * 	sadrzi li redak znakove razlicite od 1 ili 0
 * 	postoji li taj redak vec u matrici
 * 	je li redak nul-redak
 * 	je li redak linearno nezavisan sa ostalim retcima
 * 		ovo se provjerava na nacin da se iz postojecih redaka izracunaju sve
 * 		linearne kombinacije i onda provjeri postoji li input medu tim kombinacijama
 * 	
 * 
 */

public class Main {
	public static void main(String[] args) {
						
		// konstruiranje framea
		// napomena: obicno se ovo radi u posebnoj klasi, ali posto zadatak zahtjeva
		// unos i provjeru podataka u mainu, UI mora biti napisan ovdje
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());						// postavljanje layouta
		
		// stvaranje 3 glavna panela framea
		JPanel codeWordPanel = new JPanel();
		JPanel resultsPanel = new JPanel();
		JPanel consolePanel = new JPanel();
		
		
		// ova 3 elementa UI-a definiramo ranije jer cemo ih pozivati u funkcijama ispod
		JButton calcButton = new JButton("Izračunaj");				// button koji aktivira racunanje
		calcButton.setEnabled(false);
		JButton encodeBtn = new JButton("Kodiraj");					// button koji kodira zadan k-bitnu poruku
		encodeBtn.setEnabled(false);
		JTextArea console = new JTextArea();						// konzola (sort of)
		
		
		// definiranje GridBagConstraintsa svakog panela
		GridBagConstraints codeWordPanelC = new GridBagConstraints();
		codeWordPanelC.gridx = 0;		codeWordPanelC.gridy = 0;
		codeWordPanelC.insets = new Insets(10, 10, 10, 10);
		codeWordPanelC.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints resultsPanelC = new GridBagConstraints();
		resultsPanelC.gridx = 1; 		resultsPanelC.gridy = 0;
		resultsPanelC.insets = new Insets(10, 10, 10, 10);
		resultsPanelC.fill = GridBagConstraints.BOTH;		
		
		GridBagConstraints consolePanelC = new GridBagConstraints();
		consolePanelC.gridx = 0;			consolePanelC.gridy = 1;
		consolePanelC.gridwidth = 2;
		consolePanelC.insets = new Insets(10, 10, 10, 10);
		consolePanelC.fill = GridBagConstraints.BOTH;
		
		/*
		 * definiranje codeWordPanel-a
		 *
		 * u njemu se upisuju i uklanjaju redovi generirajuce matrice G blok koda K	
		 *
		 */
		codeWordPanel.setBorder(BorderFactory.createTitledBorder("Generirajuća matrica"));
		codeWordPanel.setLayout(new BorderLayout());
		JButton addBtn = new JButton("Dodaj redak");
		JButton rmvBtn = new JButton("Ukloni"); rmvBtn.setEnabled(false);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		JList<String> list = new JList<String>(listModel);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener((e) -> {
			if(!e.getValueIsAdjusting()) {
				rmvBtn.setEnabled(list.getSelectedIndex() == -1 ? false : true);
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(150, 200));
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout());
		JTextField inputArea = new JTextField();
		inputArea.setColumns(15);
		
		/*
		 * ovdje se vrsi provjeravanje ispravnosti redaka generirajuce matrice
		 */
		addBtn.addActionListener((l) -> {
			String kodnaRijec = inputArea.getText();
			int nKodneRijeci = kodnaRijec.length();
			
			if(kodnaRijec.isEmpty()) {
				return;
			}
			
			for(char bit : kodnaRijec.toCharArray()) {
				if(bit != '1' && bit != '0') {
					console.append("Greška: Redak sadrži znakove različite od 0 i 1\n");
					inputArea.requestFocusInWindow();
					inputArea.selectAll();
					return;
				}
			}
			
			if(listModel.contains(kodnaRijec)) {
				console.append("Greška: Redak već postoji\n");
				inputArea.requestFocusInWindow();
				inputArea.selectAll();
				return;
			}
			
			if(listModel.getSize() != 0) {
				int n = listModel.get(0).length();
				if(n != nKodneRijeci) {
					console.append("Greška: Redak nije jednake duljine kao ostali retci\n");
					inputArea.requestFocusInWindow();
					inputArea.selectAll();
					return;
				}
			}
			
			// provjeravanje je li redak nul vektor, koji nema smisla u generirajucoj matrici
			// jer nikada ne bi bio linearno nezavisan s drugim vektorima
			String zeroVector = "";
			for(int i = 0; i < nKodneRijeci; ++i) { zeroVector += "0"; }
			if(kodnaRijec.equals(zeroVector)) {
				console.append("Greška: Redak je nul-vektor (zavisan sa svim ostalim vektorima)\n");
				inputArea.requestFocusInWindow();
				inputArea.selectAll();
				return;
			} else { 		// provjera je li redak zavisan sa ostalim vektorima
				ArrayList<String> currentCodeWords= new ArrayList<String>();
				for(int i = 0; i < listModel.size(); ++i) { currentCodeWords.add(listModel.get(i)); }
				ArrayList<String> tempCodeWords = new ArrayList<String>();
				for(int h = 0; h < listModel.size(); ++h) {
					currentCodeWords.addAll(tempCodeWords);
					tempCodeWords.clear();
					for(int i = 0; i < currentCodeWords.size() - 1; ++i) {
						for(int j = i + 1; j < currentCodeWords.size(); ++j) {
							String word = "";
							for(int k = 0; k < nKodneRijeci; ++k) {
								word += currentCodeWords.get(i).charAt(k) == currentCodeWords.get(j).charAt(k) ? "0" : "1";
							}
							if(!tempCodeWords.contains(word) && !currentCodeWords.contains(word)) { tempCodeWords.add(word); }
						}
					}
				}
				if(currentCodeWords.contains(kodnaRijec)) {
					console.append("Greška: Redak je linearno zavisan s ostalim retcima\n");
					inputArea.requestFocusInWindow();
					inputArea.selectAll();
					return;
				}
			}
			
			
			listModel.addElement(kodnaRijec);
			int lastIndex = listModel.size() - 1;
			if(lastIndex >= 1) { calcButton.setEnabled(true); encodeBtn.setEnabled(true); }
			list.setSelectedIndex(lastIndex);
			list.ensureIndexIsVisible(lastIndex);
			inputArea.requestFocusInWindow();
			inputArea.setText("");
		});
		
		// uklanjanje redaka s liste
		rmvBtn.addActionListener((l) -> {
			int index = list.getSelectedIndex();
			listModel.remove(index);
			
			int size = listModel.getSize();
			if(size < 2) { calcButton.setEnabled(false); encodeBtn.setEnabled(false); }
			
			if(size == 0) {
				rmvBtn.setEnabled(false);
			} else {
				if(index == listModel.getSize()) {
					--index;
				}
				
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
		});

		southPanel.add(inputArea);
		southPanel.add(addBtn);
		southPanel.add(rmvBtn);
				
		codeWordPanel.add(listScrollPane, BorderLayout.CENTER);
		codeWordPanel.add(southPanel, BorderLayout.SOUTH);
		
		
		/*
		 * definiranje resultsPanel-a
		 * 
		 */
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Rezultati"));
		resultsPanel.setLayout(new GridLayout(0, 1));
		
		/*
		 * definiramo 5 panela:
		 * 0 - n koda
		 * 1 - k koda
		 * 2 - je li linearan
		 * 3 - je li generirajuca matrica u standardnom obliku
		 * 4 - kodna brzina koda
		 * */
		JPanel[] panels = new JPanel[7];
		for(int i = 0; i < 5; ++i) {
			panels[i] = new JPanel();
			panels[i].setLayout(new GridLayout(0, 2));
			resultsPanel.add(panels[i]);
		}
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(calcButton);
		resultsPanel.add(btnPanel);
		
		JLabel[] nLabel = new JLabel[2];
		nLabel[0] = new JLabel("n:");				nLabel[0].setHorizontalAlignment(SwingConstants.CENTER);
		nLabel[1] = new JLabel("-");				nLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[0].add(nLabel[0]);
		panels[0].add(nLabel[1]);
		
		JLabel[] kLabel = new JLabel[2];
		kLabel[0] = new JLabel("k:");				kLabel[0].setHorizontalAlignment(SwingConstants.CENTER);
		kLabel[1] = new JLabel("-");				kLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[1].add(kLabel[0]);
		panels[1].add(kLabel[1]);
		
		JLabel[] linLabel = new JLabel[2];
		linLabel[0] = new JLabel("Linearan:");
		linLabel[1] = new JLabel("-");				linLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[2].add(linLabel[0]);
		panels[2].add(linLabel[1]);
		
		JLabel[] stdLabel = new JLabel[2];
		stdLabel[0] = new JLabel("Std. oblik:");
		stdLabel[1] = new JLabel("-");				stdLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[3].add(stdLabel[0]);
		panels[3].add(stdLabel[1]);
		
		JLabel[] spdLabel = new JLabel[2];
		spdLabel[0] = new JLabel("Brzina koda:");
		spdLabel[1] = new JLabel("-");				spdLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[4].add(spdLabel[0]);
		panels[4].add(spdLabel[1]);
		
		/*
		 * definiranje consolePanel-a
		 * 
		 * sadrzi console window i mjesto za upisivanje kodne rijeci koje treba kodirati
		 * 
		 */
		consolePanel.setLayout(new BorderLayout());
		consolePanel.setBorder(BorderFactory.createTitledBorder("Konzola"));
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setEditable(false);
		JScrollPane consolePane = new JScrollPane(console);
		consolePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		consolePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consolePane.setPreferredSize(new Dimension(200, 175));
		JPanel southConsolePanel = new JPanel();
		southConsolePanel.setLayout(new FlowLayout());
		JTextField fieldToDecode = new JTextField();
		fieldToDecode.setColumns(30);
		JButton clearBtn = new JButton("Očisti");
		
		southConsolePanel.add(fieldToDecode);
		southConsolePanel.add(encodeBtn);
		southConsolePanel.add(clearBtn);
		
		consolePanel.add(consolePane, BorderLayout.CENTER);
		consolePanel.add(southConsolePanel, BorderLayout.SOUTH);
		
		clearBtn.addActionListener((e) -> { console.setText(""); });
		
		// definiranje encodeBtn-a, tj. provjeravanje inputa i pozivanje metode kodiranja
		encodeBtn.addActionListener((e) -> {
			int n = listModel.get(0).length();
			int m = listModel.getSize();
			if(m >= n) { console.append("Matrica nije generirajuća\n"); return; }
			
			
			String input = fieldToDecode.getText();
			if(input.isEmpty()) { return; }
			
			for(char bit : input.toCharArray()) {
				if(bit != '1' && bit != '0') {
					console.append("> " + input + "\n");
					console.append("Poruka sadrži znakove različite od 0 i 1\n");
					fieldToDecode.requestFocusInWindow();
					fieldToDecode.selectAll();
					return;
				}
			}
			
			ArrayList<String> arrayList = new ArrayList<String>();
			for(int i = 0; i < listModel.size(); ++i) {
				arrayList.add(listModel.get(i));
			}
			Blok blok = new Blok(arrayList);
			
			int k = blok.calculateK();
			if(input.length() != k) {
				console.append("> " + input + "\n");
				console.append("Poruka mora biti duljine " + k + "\n");
				fieldToDecode.requestFocusInWindow();
				fieldToDecode.selectAll();
				return;
			}
			
			console.append("> " + input + "\n");
			console.append("Kodirana poruka: " + blok.encodeMessage(input) + "\n");
			
		});
		
		// definiranje calcButtona, tj. pozivanje metoda racunanja
		calcButton.addActionListener((e) -> {
			int n = listModel.get(0).length();
			int m = listModel.getSize();
			if(m >= n) {
				console.append("Matrica nije generirajuća\n");
				nLabel[1].setText("-");
				kLabel[1].setText("-");
				linLabel[1].setText("-");
				stdLabel[1].setText("-");
				spdLabel[1].setText("-");
				return;
			}
			
			
			
			// pretvori listModel u ArrayList kojeg zahtjeva objekt tipa Blok
			ArrayList<String> arrayList = new ArrayList<String>();
			for(int i = 0; i < listModel.size(); ++i) {
				arrayList.add(listModel.get(i));
			}
			
			// stvaranje objekta tipa Blok koji moze izracunati sve potrebne vrijednosti
			Blok blok = new Blok(arrayList);

			nLabel[1].setText(blok.calculateN() + "");
			kLabel[1].setText(blok.calculateK() + "");
			linLabel[1].setText(blok.isCodeLinear() ? "Da" : "Ne");
			stdLabel[1].setText(blok.isGenMatrixStandard() ? "Da" : "Ne");
			spdLabel[1].setText(blok.calculateSpeed() + "");
			
			// ispisivanje u konzolu svih kodnih riječi blok koda
			console.append("Sve kodne riječi su:\n");
			arrayList = blok.getCodeWords();
			for(String s : arrayList) {
				console.append(s + '\n');
			}
			
			if(!blok.isGenMatrixStandard()) {
				console.append("Generirajuća matrica u standardnom obliku:\n");
				ArrayList<String> stdMatrix = blok.getStandardMatrix();
				for(String s : stdMatrix) {
					console.append(s + "\n");
				}
			}
			
		});
				
		// dodavanje panela u frame & all the other finishing touches
		frame.add(codeWordPanel, codeWordPanelC);
		frame.add(resultsPanel, resultsPanelC);
		frame.add(consolePanel, consolePanelC);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

}





