package wordfreqthreads;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



public class WordFreqThread extends Thread {
	
	String[] worddata;
	HashMap<String,Integer> Frequencies;
	
	WordFreqThread(){
		//default constructor
	}
	
	WordFreqThread(String myname, String lineinput){
		/*
		 * Creates the thread with the passed partition, initialize local variables including frequency map.
		 */
		super(myname); 
		//trims off leading whitespace, and separates words by spaces, commas, periods, colons, etc. using regex
		worddata = lineinput.trim().split("[\\n\\r\\f\\t\\v\\s,.:;()-+*$?!/\"]+"); 
		//worddata = lineinput.trim().split("[^\\w\']+"); 
		Frequencies = new HashMap<String, Integer>();	//creates the HashMap to store the frequencies
		System.out.println(myname + " has been created.");
		start(); //calls run()
	}
	
	public void run() {
		
		/*
		 * Build partition lexicon and count instances
		 */
		
		String currentword;
		System.out.println("Running "+ Thread.currentThread().getName());

		//adds words and word counts to the HashMap
		for( int i=0; i<worddata.length; i++){
			currentword = worddata[i].toLowerCase();
			if(!Frequencies.containsKey(currentword)){ 
				//if the word/key does not appear in the HashMap, adds it
				Frequencies.put(currentword,1);
			}
			else{
				//if the word/key does appear in the HashMap, increments the count
				Frequencies.put(currentword, Frequencies.get(currentword)+1);
			}
		}	
	}
	
	public HashMap<String,Integer> getFreqs() {
		//accessor for the HashMap
		return Frequencies;
	}

}
