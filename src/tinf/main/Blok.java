package tinf.main;

import java.util.ArrayList;

public class Blok {
	private ArrayList<String> codeWords;
	private int n = -1;
	private int d = -1;
	
	public Blok(ArrayList<String> codeWords) {
		
		/*
		 * Ova se iznimka nikada ne bi trebala dogoditi posto je vec u mainu osigurano da se
		 * ne posalje lista s manje od 2 elementa
		 * 
		 */
		if(codeWords.size() < 2) {
			throw new IllegalArgumentException("Manje od 2 kodne rijeci u blok kodu");
		}
		this.codeWords = codeWords;
	}
	
	public int calculateN() {
		n = codeWords.get(0).length();
		return n;
	}
	
	
	// idk how to calculate this lmao
	public int calculateK() {
		return 0;
	}
	
	public int calculateDistance() {
		if(n == -1) { calculateN(); }
		int m = codeWords.size();
		int d = Integer.MAX_VALUE;
		String[] wordArray = new String[m];
		for(int i = 0; i < m; ++i) { wordArray[i] = codeWords.get(i); }
		int n = wordArray[0].length();
		int i, j;
		for(i = 0; i < (m - 1); ++i) {
			for(j = i + 1; j < m; ++j) {
				int currentD = 0;
				for(int k = 0; k < n; ++k) {
					if(wordArray[i].charAt(k) != wordArray[j].charAt(k)) { ++currentD; }
				}
				if(currentD < d) { d = currentD; }
			}
		}
	
		this.d = d;
		return d;
	}
	
	public int calculateErrorDetection() {
		if(d == -1) { this.calculateDistance(); }
		return d - 1;
	}
	
	public int calculateErrorCorrection() {
		if(d == -1) { this.calculateDistance(); }
		return (d - 1) / 2;
	}
	
	public boolean isCodePerfect() {
		if(n == -1) { calculateN(); }
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
	
	public boolean isCodeLinear() {
		if(n == -1) { calculateN(); }
		int m = codeWords.size();

		String zeroString = "";
		for(int i = 0; i < n; ++i) { zeroString += "0"; }
		if(!codeWords.contains(zeroString)) { return false; }
		
		String[] wordArray = new String[m];
		for(int i = 0; i < m; ++i) { wordArray[i] = codeWords.get(i); }
		int i, j, k;
		for(i = 0; i < (m - 1); ++i) {
			for(j = i + 1; j < m; ++j) {
				String newString = "";
				for(k = 0; k < n; ++k) {
					if(wordArray[i].charAt(k) != wordArray[j].charAt(k)) {
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
