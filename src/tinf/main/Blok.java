package tinf.main;

import java.util.ArrayList;
import java.util.HashSet;


/*
 * Blok je klasa koja sluzi za definiranje linearnog blok koda pomocu generirajuce matrice
 * Sadrži razne metode koje služe kako bi se rješili 1. i 2. zadatak laboratorijskih vježbi
 * predmeta Teorija Informacije.
 * Razlog zašto sadrži metode za rješavanje dva zadatka, a ne samo jednog, je zato što su se
 * neki od programera zabunili i slučajno napisali metode potrebne za 1. zadatak, ostavili smo ih
 * u kodu da se trud ne bi izgubio.
 * 
 * Matrice i kodne riječi zapisane su kao lista Stringova
 * 
 * n i d su ovdje kao privatne varijable jer ih se često koristi u metodama, pa da se ne računaju
 * svaki put ispočetka
 * */
public class Blok {
	private ArrayList<String> genMatrix;	// generirajuca matrica
	private ArrayList<String> codeWords;	// kodne rijeci
	private int n;							// duljna kodne rijeci / retka
	private int d = -1;						// udaljenost kodnih rijeci
	
	
	/*
	 * Konstruktor iz generirajuce matrice racuna sve kodne rijeci blok koda
	 * 
	 * */
	public Blok(ArrayList<String> genMatrix) {
		
		int m = genMatrix.size();
		int n = genMatrix.get(0).length();
		if (n <= m) { throw new IllegalArgumentException("Matrica nije generirajuća"); }
		for(int i = 0; i < m; ++i) {
			if(genMatrix.get(i).length() != n) { throw new IllegalArgumentException("Redovi su razlicitih duljina."); }
		}
		
		this.n = n;
		this.genMatrix = genMatrix;
		calculateN();
		
		/* iz generirajuce matrice odmah izvuci sve kodne rijeci */
		ArrayList<String> codeWords = new ArrayList<String>(genMatrix);
		ArrayList<String> tempCodeWords = new ArrayList<String>();
		
		// dodavanje kodnih rijeci dobivenih zbrajanjem postojecih
		for(int i = 0; i < genMatrix.size() - 1; ++i) {
			for(int j = i + 1; j < genMatrix.size(); ++j) {
				String word = "";
				for(int k = 0; k < n; ++k) {
					word += codeWords.get(i).charAt(k) == codeWords.get(j).charAt(k) ? "0" : "1";
				}
				if(!tempCodeWords.contains(word) && !codeWords.contains(word)) { tempCodeWords.add(word); }
			}
		}
		codeWords.addAll(tempCodeWords);
		
		// dodavanje 0-vektora ako ga nema
		String zeroVector = "";
		for(int i = 0; i < codeWords.get(0).length(); ++i) { zeroVector += "0"; }
		if(!codeWords.contains(zeroVector)) { codeWords.add(zeroVector); }
		
		this.codeWords = codeWords;
	}
	
	/*
	 * Funkcija vraca duljinu kodne rijeci
	 * */
	public int calculateN() {
		return n;
	}
	
	public ArrayList<String> getCodeWords() {
		return this.codeWords;
	}
	
	// iskreno ne znam kako ovo izracunati
	// pokusati cu tako da gledam kojih prvih k znakova se u potpunosti razlikuju
	public int calculateK() {
		int k;
		boolean flag = false;
		HashSet<String> set = new HashSet<String>();
		
		for(k = 1; !flag; ++k) {
			flag = true;
			for(String s : codeWords) {
				String toAdd = s.substring(0, k);
				if(!set.add(toAdd)) {
					flag = false;
					set.clear();
					break;
				}
			}
			if(flag) {
				break;
			}
		}
		
		return k;
	}
	
	public float calculateSpeed() {
		float k = (float) calculateK();
		return k / (float) n;
	}
	
	/*
	 * Vraca udaljenost kodnih rijeci
	 * */
	public int calculateDistance() {
		if(d != -1) { return d; }
		int m = codeWords.size();
		int d = Integer.MAX_VALUE;
		int i, j;
		for(i = 0; i < (m - 1); ++i) {
			for(j = i + 1; j < m; ++j) {
				int currentD = 0;
				for(int k = 0; k < n; ++k) {
					if(codeWords.get(i).charAt(k) != codeWords.get(j).charAt(k)) { ++currentD; }
				}
				if(currentD < d) { d = currentD; }
			}
		}
	
		this.d = d;
		return d;
	}
	
	
	/*
	 * Vraca koliko gresaka kod moze detektirati
	 * */
	public int calculateErrorDetection() {
		if(d == -1) { this.calculateDistance(); }
		return d - 1;
	}
	
	
	/*
	 * Vraca koliko gresaka kod moze ispraviti
	 * */
	public int calculateErrorCorrection() {
		if(d == -1) { this.calculateDistance(); }
		return (d - 1) / 2;
	}
	
	/*
	 * Vraca je li kod perfektan
	 * */
	public boolean isCodePerfect() {
		if(d == -1) { calculateDistance(); }
		int M = codeWords.size();
		int t = (d - 1) / 2;
		
		double dividend = Math.pow(2., (double) n);
		double divisor = 0;
		for(int i = 0; i <= t; ++i) {
			divisor += binomialCoefficient(n, i);
		}
		
		if((double) M == (dividend / divisor)) {
			return true;
		}
		
		return false;
	}
	
	
	/*
	 * Vraca je li kod linearan. uvijek bi trebao vracati true
	 * jer generirajuca matrica opisuje linearan kod, iskreno ne znam
	 * zasto je uopce potrebno pisati ovu metodu
	 * */
	public boolean isCodeLinear() {
		int m = codeWords.size();

		String zeroString = "";
		for(int i = 0; i < n; ++i) { zeroString += "0"; }
		if(!codeWords.contains(zeroString)) { return false; }
		
		int i, j, k;
		for(i = 0; i < (m - 1); ++i) {
			for(j = i + 1; j < m; ++j) {
				String newString = "";
				for(k = 0; k < n; ++k) {
					if(codeWords.get(i).charAt(k) != codeWords.get(j).charAt(k)) {
						newString += "1";
					} else {
						newString += "0";
					}
				}
				if(!codeWords.contains(newString)) { return false; }
			}
		}
		
		return true;
	}
	
	public boolean isGenMatrixStandard() {
		int m = genMatrix.size();
		if (m > n) { throw new IllegalArgumentException("Matrica nije generirajuca"); }
		for(int i = 0; i < m; ++i) {
			for(int j = 0; j < m; ++j) {
				if(i == j) {
					if(genMatrix.get(i).charAt(j) == '0') {
						return false;
					}
				} else {
					if(genMatrix.get(i).charAt(j) == '1') {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public ArrayList<String> getStandardMatrix() {
		if(isGenMatrixStandard()) {
			return genMatrix;
		}
		
		ArrayList<String> stdMatrix = new ArrayList<String>(genMatrix);
		
		// somehow transform into std form
		
		return stdMatrix;
	}
	
	public String encodeMessage(String input) {
		String redundancy = "";
		ArrayList<String> stdMatrix = getStandardMatrix();
		ArrayList<String> rightMatrix = new ArrayList<String>();
		int m = stdMatrix.size();
		
		for(String row : stdMatrix) {
			rightMatrix.add(row.substring(m));
		}
		
		for(int k = 0; k < n - m; ++k) {
			int counter = 0;
			for(int i = 0, j = 0; i < m; ++i, ++j) {
				if(input.charAt(i) == '1' && rightMatrix.get(i).charAt(j) == '1') {
					counter++;
				}
			}
			redundancy += counter % 2 == 0 ? "0" : "1";
		}
		
		return input + redundancy;
	}
	
	/*
	 * Pomoćna funkcija za računanje binomnog koeficijenta
	 * potrebnog za određivanje je li kod perfektan
	 * */
	private static long binomialCoefficient(int n, int k) {
		long N = 1;
		long NmK = 1;
		long K = 1;
		for(int i = 1; i <= n; ++i) { N *= i; }
		for(int i = 1; i <= (n - k); ++i) { NmK *= i; }
		for(int i = 1; i <= k; ++i) { K *= i; }
		
		return N / (NmK * K);
		
	}
	
}
