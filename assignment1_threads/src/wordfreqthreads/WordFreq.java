package wordfreqthreads;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class WordFreq {
	
	//Object lock = new Object();
	
	public static void main ( String[] args ) throws IOException {
	
		/* 
		 * Set the source file name.
		 * Note: file must be in the root eclipse directory.
		 */

		String sourcename = new String("The quick brown fox.txt");
		System.out.println("Source file name = " + sourcename);
		
		/*Alternative code if prefer to receive file name input from console.
		  Scanner sc_console = new Scanner(System.in);
		  System.out.println("Enter source .txt file name (ex. MyFile.txt):");
		  String sourcename = sc_console.nextLine(); */
		
		
		/*
		 * Loads the source file.
		 */
		Scanner input;
		File sourcefile = null; 
		
		try{ //in case the file is not found.
			sourcefile = new File(sourcename);
			input = new Scanner(sourcefile);
		} catch (FileNotFoundException e) {
			System.out.println("Source file not found. Program terminated.");
	        e.printStackTrace();
	    }
		
		WordFreqThread[] threads; //for keeping track of/accessing children threads that will be created
		HashMap<String,Integer> TotalFrequencies = new HashMap<String, Integer>(); 
		//where the final word frequencies will be stored
		
		
		/*
		 * Set the number of partitions N to the number of lines in the source file
		 */
		Path sourcepath = Paths.get(sourcename);
		List<String> textlines = Files.readAllLines(sourcepath, Charset.defaultCharset()); //extracts lines from text file
		int N = textlines.size(); //number of lines is the number of segments/partitions
		
		System.out.println("Number of Lines/Segments, N = "+ N);
		
		threads = new WordFreqThread[N]; //there will be at most N children threads
		
		
		/*
		 * Partition the file and create thread for each partition.
		 * Each thread outputs the intermediate frequency count for its partition.
		 */
		WordFreqThread currenthread;
		String currentline;
		int numthreads = 0;
		
		for(int i=1; i<N+1; i++){
			currentline = textlines.get(i-1); //gets the line to be processed by the child thread
			if(!currentline.isEmpty()){ //ignores empty lines, threads are not created for empty lines
				//creates the thread, constructor calls start()
				currenthread = new WordFreqThread("thread " + i, currentline); 
				
				
				threads[i-1] = currenthread; //stores child thread so it can be called later
				numthreads++; //keeps track of the number of threads
			}
			else{
				threads[i-1] = null; //ignores empty line, no thread created.
				numthreads++;
				System.out.println("thread " + i + " was not created and did not run, empty line."); //for completeness
			}
		}
		
		for(int i=1; i<N+1; i++){
			try{ 
				if( !(threads[i-1]==null)){
					threads[i-1].join(); //joins the threads so that parent will wait for them to complete
				}
			} catch (InterruptedException e){
		        e.printStackTrace(); //error handling
		    }
		}
		
		/* Main process waits until all the threads complete then computes the consolidated word-frequency 
		   data based on the individual threads’ output. */
		
		Iterator iterT; //for iterating over the individual thread word count results
		HashMap<String,Integer> ThreadFrequencies;
		int totalwordcount = 0; //for keeping track of the overall word count
		
		for(int i=1; i<N+1; i++){
			if (!(threads[i-1]==null)){ //ignores threads that were not created
				ThreadFrequencies = threads[i-1].getFreqs(); //pulls in data calculated by thread
				
				System.out.println();
				System.out.println("thread "+i);
				printFrequenciesAZ(ThreadFrequencies);
				
				iterT = ThreadFrequencies.entrySet().iterator(); //initializes iterator
				
				while(iterT.hasNext()){ //iterates over the thread HashMap
					Map.Entry tuple = (Map.Entry)iterT.next();
					
					if(!TotalFrequencies.containsKey(tuple.getKey())){ 
						//if the key value does not already exist in the parent HashMap, adds it 
						TotalFrequencies.put((String)tuple.getKey(), (Integer) tuple.getValue());
					}
					else {
						//if the key value does already exist in the parent HashMap, sums its value with the child value 
						TotalFrequencies.put((String)tuple.getKey(), (Integer) tuple.getValue() + TotalFrequencies.get(tuple.getKey()));
					}
					totalwordcount = totalwordcount + (Integer) tuple.getValue(); //keeps track of total word count
				}	
			}
			else{
				System.out.println();
				System.out.println("thread " + i + " was not created and did not run, empty line."); //for completeness
			}
		}	
		
		
		/*
		 * Print result. 
		 */
		System.out.println();
		System.out.println("TOTALS:");
		printFrequenciesAZ(TotalFrequencies);
		
		System.out.println();
		System.out.println("Total Word Count = " + totalwordcount);
	}
	
	/*
	 * Prints HashMap of word frequencies.
	 * input: HashMap
	 * output: none (print to console)
	 */
	public static void printFrequenciesAZ(Map Frequencies){
		TreeMap<String, Integer> SortedFrequencies = new TreeMap(Frequencies);
		Iterator iter = SortedFrequencies.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry tuple = (Map.Entry)iter.next();
			System.out.println(tuple.getKey() + " = " + tuple.getValue());
		}
	}
}
