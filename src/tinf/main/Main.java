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
import javax.swing.JOptionPane;
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
 */

public class Main {
	public static void main(String[] args) {
						
		// konstruiranje framea
		// napomena: obicno se ovo radi u posebnoj klasi, ali posto zadatak zahtjeva
		// unos i provjeru podataka u mainu, UI mora biti napisan ovdje
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());						// postavljanje layouta
		
		// stvaranje 4 glavna panela framea
		JPanel codeWordPanel = new JPanel();
		JPanel resultsPanel = new JPanel();
		JPanel consolePanel = new JPanel();
		
		JButton calcButton = new JButton("Izračunaj");				// button koji aktivira racunanje
		calcButton.setEnabled(false);
		JButton encodeBtn = new JButton("Kodiraj");								// button koji kodira zadan k-bitnu poruku
		encodeBtn.setEnabled(false);
		
		
		// definiranje GridBagConstraintsa svakog panela
		GridBagConstraints codeWordPanelC = new GridBagConstraints();
		codeWordPanelC.gridx = 0;		codeWordPanelC.gridy = 0;
		//codeWordPanelC.weightx = 0.1;	codeWordPanelC.weighty = 0.1;
		codeWordPanelC.insets = new Insets(10, 10, 10, 10);
		codeWordPanelC.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints resultsPanelC = new GridBagConstraints();
		resultsPanelC.gridx = 1; 		resultsPanelC.gridy = 0;
		//resultsPanelC.weightx = 0.1;	resultsPanelC.weighty = 0.1;
		resultsPanelC.insets = new Insets(10, 10, 10, 10);
		resultsPanelC.fill = GridBagConstraints.BOTH;		
		
		GridBagConstraints consolePanelC = new GridBagConstraints();
		consolePanelC.gridx = 0;			consolePanelC.gridy = 1;
		consolePanelC.gridwidth = 2;
		//buttonPanelC.weightx = 0.1;		buttonPanelC.weighty = 0.1;
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
		 * jedina stvar koja se ne provjerava je jesu li retci linearno nezavisni
		 */
		addBtn.addActionListener((l) -> {
			String kodnaRijec = inputArea.getText();
			int nKodneRijeci = kodnaRijec.length();
			
			if(kodnaRijec.isEmpty()) {
				return;
			}
			
			for(char bit : kodnaRijec.toCharArray()) {
				if(bit != '1' && bit != '0') {
					JOptionPane.showMessageDialog(frame,  "Redak sadrži znakove različite od 0 i 1", "Greška", JOptionPane.ERROR_MESSAGE);
					inputArea.requestFocusInWindow();
					inputArea.selectAll();
					return;
				}
			}
			
			if(listModel.contains(kodnaRijec)) {
				JOptionPane.showMessageDialog(frame,  "Redak već postoji", "Greška", JOptionPane.ERROR_MESSAGE);
				inputArea.requestFocusInWindow();
				inputArea.selectAll();
				return;
			}
			
			if(listModel.getSize() != 0) {
				int n = listModel.get(0).length();
				if(n != nKodneRijeci) {
					JOptionPane.showMessageDialog(frame, "Redak nije jednake duljine kao ostali retci", "Greška", JOptionPane.ERROR_MESSAGE);
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
		 * definiramo 7 panela:
		 * 0 - n koda
		 * 1 - k koda
		 * 2 - d(K)
		 * 3 - koliko moze gresaka otkriti
		 * 4 - koliko moze gresaka ispraviti
		 * 5 - je li perfektan
		 * 6 - je li linearan
		 * */
		JPanel[] panels = new JPanel[7];
		for(int i = 0; i < 7; ++i) {
			panels[i] = new JPanel();
			panels[i].setLayout(new GridLayout(0, 2));
			resultsPanel.add(panels[i]);
		}
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(calcButton);
		resultsPanel.add(btnPanel);
		
		JLabel[] nLabel = new JLabel[2];
		nLabel[0] = new JLabel("n:");
		nLabel[1] = new JLabel("-");				nLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[0].add(nLabel[0]);
		panels[0].add(nLabel[1]);
		
		JLabel[] kLabel = new JLabel[2];
		kLabel[0] = new JLabel("k:");
		kLabel[1] = new JLabel("-");				kLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[1].add(kLabel[0]);
		panels[1].add(kLabel[1]);
		
		JLabel[] dLabel = new JLabel[2];
		dLabel[0] = new JLabel("d(K):");
		dLabel[1] = new JLabel("-");				dLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[2].add(dLabel[0]);
		panels[2].add(dLabel[1]);
		
		JLabel[] otkLabel = new JLabel[2];
		otkLabel[0] = new JLabel("Otkriti:");
		otkLabel[1] = new JLabel("-");				otkLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[3].add(otkLabel[0]);
		panels[3].add(otkLabel[1]);
		
		JLabel[] ispLabel = new JLabel[2];
		ispLabel[0] = new JLabel("Ispraviti:");
		ispLabel[1] = new JLabel("-");				ispLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[4].add(ispLabel[0]);
		panels[4].add(ispLabel[1]);
		
		JLabel[] perfLabel = new JLabel[2];
		perfLabel[0] = new JLabel("Perfektan:");
		perfLabel[1] = new JLabel("-");				perfLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[5].add(perfLabel[0]);
		panels[5].add(perfLabel[1]);
		
		JLabel[] linLabel = new JLabel[2];
		linLabel[0] = new JLabel("Linearan:");
		linLabel[1] = new JLabel("-");				linLabel[1].setHorizontalAlignment(SwingConstants.CENTER);
		panels[6].add(linLabel[0]);
		panels[6].add(linLabel[1]);
		
		/*
		 * definiranje consolePanel-a
		 * 
		 * sadrzi console window i mjesto za upisivanje kodne rijeci koje treba kodirati
		 * 
		 */
		consolePanel.setLayout(new BorderLayout());
		consolePanel.setBorder(BorderFactory.createTitledBorder("Konzola"));
		JTextArea console = new JTextArea();
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setPreferredSize(new Dimension(250, 175));
		console.setEditable(false);
		JPanel southConsolePanel = new JPanel();
		southConsolePanel.setLayout(new FlowLayout());
		JTextField fieldToDecode = new JTextField();
		fieldToDecode.setColumns(30);
		
		southConsolePanel.add(fieldToDecode);
		southConsolePanel.add(encodeBtn);
		
		consolePanel.add(console, BorderLayout.CENTER);
		consolePanel.add(southConsolePanel, BorderLayout.SOUTH);
		
		// definiranje encodeBtn-a, tj. provjeravanje inputa i pozivanje metode kodiranja
		encodeBtn.addActionListener((e) -> {
			
		});
		
		// definiranje calcButtona, tj. pozivanje metoda racunanja
		calcButton.addActionListener((e) -> {
			// za svaki slucaj provjeri ima li u listi vise od 2 elementa
			if(listModel.size() < 2) { return; }
			
			// pretvori listModel u ArrayList kojeg zahtjeva objekt tipa Blok
			ArrayList<String> arrayList = new ArrayList<String>();
			for(int i = 0; i < listModel.size(); ++i) {
				arrayList.add(listModel.get(i));
			}
			
			// stvaranje objekta tipa Blok koji moze izracunati sve potrebne vrijednosti
			Blok blok = new Blok(arrayList);
			
			nLabel[1].setText(blok.calculateN() + "");
			kLabel[1].setText(blok.calculateK() + "");
			dLabel[1].setText(blok.calculateDistance() + "");
			otkLabel[1].setText(blok.calculateErrorDetection() + "");
			ispLabel[1].setText(blok.calculateErrorCorrection() + "");
			perfLabel[1].setText(blok.isCodePerfect() ? "Da" : "Ne");
			linLabel[1].setText(blok.isCodeLinear() ? "Da" : "Ne");
			encodeBtn.setEnabled(true);
			
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
