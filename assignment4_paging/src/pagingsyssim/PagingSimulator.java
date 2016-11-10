package pagingsyssim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class PagingSimulator {

	public static void main ( String[] args ) throws IOException {
		
		/*
		 * Some code reused from previous COEN283 projects.
		 */
	
		/*
		 *  Receives input for number of frames.
		 *  Creates the page.
		 */
		Scanner sc_console = new Scanner(System.in);
		System.out.print("Enter number of page frames, must be an integer > 0: ");
		int numframes = sc_console.nextInt();
		System.out.println();
		Semaphore mutex = new Semaphore(1);
		
		PrintStream stdout = System.out;

		/*
		 * Loads the source file
		 */
		Scanner input = null;
		File sourcefile = null;
		
		String sourcename = new String("input.txt");
		System.out.println("Source file name = " + sourcename);
		
		try{ 
			sourcefile = new File(sourcename);
			input = new Scanner(new File(sourcename)).useDelimiter("[\\n\\r\\f\\t\\v\\s,.:;()-+*$?!/\"]+");
			//input = new Scanner(new File(sourcename)).useDelimiter(",\\s*");
		} catch (FileNotFoundException e) { //in case the file is not found.
			System.out.println("Source file not found. Program terminated.");
	        e.printStackTrace();
	    }
		
	    List<String> holder = new ArrayList<String>();
	    String pagenum;

	    while (input.hasNext()) {
	      // find next line
	      pagenum = input.next();
	      holder.add(pagenum);
	    }
	    System.out.println("Number of Frames: " + numframes);
	    System.out.println("Number of Pages: " + holder.size());

	    String[] sequencest = holder.toArray(new String[holder.size()]);
	    int[] sequence = new int[sequencest.length];
	    
	    //check if worked?
	  /*  for (String mystrings : sequencest) {
	        System.out.println(mystrings);
	    }*/
	    
	    for(int i=0; i<sequencest.length;i++){
	    	sequence[i] = Integer.parseInt(sequencest[i]);
	    }
		
		int[][] frames = new int[3][numframes]; //two columns, one to hold the page info and the other to hold the counter
		//initialize frames
		for( int i=0; i<numframes; i++){
			//frames[i] = null;
			frames[0][i] = 0; //page number
			frames[1][i] = 0b0; //reference bit
			frames[2][i] = 0b00000000; //counters init to 0
		}
		
		System.out.println("Running Simulation...");
		
		//run simulation
		PgSim mysim = new PgSim(sequence,frames, numframes, mutex);
		PgClk myclk = new PgClk(frames,numframes,mutex);
		Thread mysimthread = new Thread(mysim);
		Thread myclkthread = new Thread(myclk);
		myclkthread.start();
		mysimthread.start();
		try{
			mysimthread.join();
			
		}catch (InterruptedException e){
	        e.printStackTrace(); //error handling
	    }
		
		myclk.stopTicking();
		try{
			myclkthread.join();
			
		}catch (InterruptedException e){
	        e.printStackTrace(); //error handling
	    }
		
		System.out.println("Simulation Complete.");
		System.out.println("Total Number of Page Faults: " + mysim.getFaults());
		System.out.println("[End]");
	}
}
