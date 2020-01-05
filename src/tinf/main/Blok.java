package tinf.main;

import java.util.ArrayList;


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
	 * Konstruktor uzima generirajucu matricu, te iz nje izracuna sve kodne rijeci
	 * zbrajanjem svake kodne rijeci sa svakom, ponavljajuci to jos m - 1 put nad
	 * novonastalim kodnim rijecima. u codeWords listu dodaje samo one koje vec u njoj
	 * ne postoje.
	 * 
	 * */
	public Blok(ArrayList<String> genMatrix) {
		
		int m = genMatrix.size();						// broj redaka nam je potreban za iteriranje
		int n = genMatrix.get(0).length();				// n također, kao i za ostale metode klase koji koriste n
		
		
		// ovi exceptioni se ne bi trebali dogoditi posto se vec u mainu osigurava da se preda generirajuca matrica
		if (n <= m) { throw new IllegalArgumentException("Matrica nije generirajuća"); }
		for(int i = 0; i < m; ++i) {
			if(genMatrix.get(i).length() != n) { throw new IllegalArgumentException("Redovi su razlicitih duljina."); }
		}
		
		// spremanje privatnih varijabli potrebnih za kasnije izracune
		this.n = n;
		this.genMatrix = genMatrix;
		
		/* iz generirajuce matrice odmah izvuci sve kodne rijeci */
		ArrayList<String> codeWords = new ArrayList<String>(genMatrix);
		ArrayList<String> tempCodeWords = new ArrayList<String>();
		
		// zbrajamo svaku kodnu rijec sa svakom, i to m puta kako bismo ih dobili sve
		for(int h = 0; h < m; ++h) {
			codeWords.addAll(tempCodeWords);
			tempCodeWords.clear();
			for(int i = 0; i < codeWords.size() - 1; ++i) {
				for(int j = i + 1; j < codeWords.size(); ++j) {
					String word = "";
					for(int k = 0; k < n; ++k) {
						word += codeWords.get(i).charAt(k) == codeWords.get(j).charAt(k) ? "0" : "1";
					}
					if(!tempCodeWords.contains(word) && !codeWords.contains(word)) { tempCodeWords.add(word); }
				}
			}
		}
		
		// dodavanje 0-vektora ako ga nema
		String zeroVector = "";
		for(int i = 0; i < codeWords.get(0).length(); ++i) { zeroVector += "0"; }
		if(!codeWords.contains(zeroVector)) { codeWords.add(zeroVector); }
		
		this.codeWords = codeWords;
	}
	
	/*
	 * Metoda vraca duljinu kodne rijeci
	 * */
	public int calculateN() {
		return n;
	}
	
	/*
	 * Vraca listu kodih rijeci
	 * */
	public ArrayList<String> getCodeWords() {
		return this.codeWords;
	}
	
	/*
	 * Vraca k blok koda, koji je samo broj redaka generirajuce matrice
	 * */
	public int calculateK() {
		return genMatrix.size();
	}
	
	
	/*
	 * Racuna brzinu koda, sto je samo k / n
	 * */
	public float calculateSpeed() {
		float k = (float) calculateK();
		return k / (float) n;
	}
	
	/*
	 * Vraca udaljenost kodnih rijeci; za svake 2 kodne rijeci pogleda njihovu udaljenost i vraca najmanju
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
	 * Vraca koliko gresaka kod moze detektirati; (1. zadatak, ne koristi se)
	 * */
	public int calculateErrorDetection() {
		if(d == -1) { this.calculateDistance(); }
		return d - 1;
	}
	
	
	/*
	 * Vraca koliko gresaka kod moze ispraviti; (1. zadatak, ne koristi se)
	 * */
	public int calculateErrorCorrection() {
		if(d == -1) { this.calculateDistance(); }
		return (d - 1) / 2;
	}
	
	/*
	 * Vraca je li kod perfektan, (1. zadatak, ne koristi se)
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
	 * 
	 * Metoda zbraja svaku kodnu rijec sa svakom i gleda postoji li
	 * novonastala rijec u kodu, ako ne, kod nije linearan
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
				if(!codeWords.contains(newString)) { System.out.println(newString); return false; }
			}
		}
		
		return true;
	}
	
	/*
	 * Provjerava je li generirajuca matrica standardna
	 * 
	 * m - broj redaka
	 * Funkcija gleda nalazi li se u pocetnom m x m dijelu matrice jedinicna matrica
	 * */
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
	
	
	/*
	 * Vraca standardnu matricu, tj. prvo provjeri je li genMatrix standardna,
	 * ako nije, izracuna standardnu metodama zamijenjivanja redaka stupaca i 
	 * dodavanjem jednog retka drugome
	 * */
	public ArrayList<String> getStandardMatrix() {
		if(isGenMatrixStandard()) {
            return genMatrix;
        }
        int col = 0;
        int pamti = -1;
        int brojgotovih = -1;

        //broj redova genMatrix.size()   matrix.length
        //broj stupaca n                 matrix[0].length

        ArrayList<String> stdMatrix = new ArrayList<>();
        int[][] matrix = new int[genMatrix.size()][n];

        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){

                matrix[i][j]= genMatrix.get(i).charAt(j) -48;
            }
        }

        while (col < matrix.length) {
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][col] == 1 && row > brojgotovih) {
                    pamti = row;
                    break;
                }
            }
            if (pamti == -1) {
                int row2 = col;
                for (int column = 0; column < matrix[0].length; column++) {
                    if (matrix[row2][column] == 1) {
                        swapCols(matrix, row2, column);
                        break;
                    }
                }
            } else {

                for (int row = 0; row < matrix.length; row++) {
                    if (matrix[row][col] == 1 && row != pamti) {
                        for (int k = 0; k < matrix[0].length; k++) {
                            matrix[row][k] = matrix[row][k] ^ matrix[pamti][k];
                        }
                    }
                }

                swapRows(matrix, pamti, col);
                pamti = -1;
                col++;
                brojgotovih++;
            }
        }

        for(int i=0; i<matrix.length; i++){
            StringBuilder s = new StringBuilder();
            for(int j=0; j<matrix[0].length; j++){
                s.append(matrix[i][j]);
            }
            stdMatrix.add(s.toString());
            s.delete(0, matrix[0].length-1);
        }

        return stdMatrix;
	}
	
	
	/*
	 * Kodira input pomocu standardne generirajuce matrice,
	 * tj. vraca input + zalihost koja se dobije mnozenjem inputa
	 * sa desnom stranom generirajuce matrice
	 * */
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
			
			for(int i = 0; i < m; ++i) {
				if(input.charAt(i) == '1' && rightMatrix.get(i).charAt(k) == '1') {
					counter++;
				}
			}
			redundancy += counter % 2 == 0 ? "0" : "1";
		}
		
		return input + redundancy;
	}
	
	/*
    Zamijenjuje dva retka u matrici
     */
    public static void swapRows(int[][] matrix, int a, int b) {
        int[] temp = matrix[a];
        matrix[a] = matrix[b];
        matrix[b] = temp;
    }

    /*
    Zamjenjuje dva stupca u matrici
     */
    public static void swapCols(int[][] matrix, int a, int b) {
        int temp;
        for (int i = 0; i < matrix.length; i++) {
            temp = matrix[i][a];
            matrix[i][a] = matrix[i][b];
            matrix[i][b] = temp;
        }
    }
	
	
	/*
	 * Pomoćna funkcija za računanje binomnog koeficijenta
	 * potrebnog za određivanje je li kod perfektan
	 * (1. zadatak, ne koristi se)
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
