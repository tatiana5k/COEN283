package bankeralgsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.management.monitor.Monitor;


public class Bank {
	
	public static void main ( String[] args ) throws IOException {
		
		Semaphore mutex_B = new Semaphore(1);
		Semaphore mutex_R = new Semaphore(0);

		
		int numresources = 1;	//number of resources held by the bank
		
		//create matrices
		long[] E = new long[numresources];	//Resource matrix
		long[] A = new long[numresources];	//Available matrix
		ArrayList C = new ArrayList();		//Current Allocations matrix, ArrayList is dynamic
		ArrayList R = new ArrayList();
		ArrayList Names = new ArrayList();
		
		/* 
		 * initialize matrices
		 */
		
		for(int i=0;i<numresources;i++){
			//can implement user input here for resource amounts
			//using one resource hard coded for now.
			E[i] = 1000; 
			A[i] = 1000;		
		}
		
		Banker B = new Banker(E,A,C,R);
		
		
		
		/* 
		 * Set the source file name.
		 * Note: file must be in the root eclipse directory.
		 */

		String sourcename = new String("Client Requests.txt");
		System.out.println("Client Requests Source file name = " + sourcename);
		
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
		
		//BankClient[] threads; //for keeping track of/accessing threads that will be created
		
		
		/*
		 * Set the number of clients N to the number of lines in the source file
		 */
		Path sourcepath = Paths.get(sourcename);
		List<String> textlines = Files.readAllLines(sourcepath, Charset.defaultCharset()); //extracts lines from text file
		int N = textlines.size(); //number of lines is the number of clients
		
		System.out.println("Number of Clients, N = "+ N);
		
		
		/*
		 * 
		 */
		Thread currentthread;
		BankClient currentclient;
		String currentline;
		//int numthreads = 0;
		
		for(int i=1; i<N+1; i++){
			currentline = textlines.get(i-1); //gets the line to be processed by the child thread
			if(!currentline.isEmpty()){ //ignores empty lines, threads are not created for empty lines
				//creates the thread, constructor calls start()
				currentclient = new BankClient(currentline, mutex_R, mutex_B, B);
				currentthread = new Thread(currentclient,currentclient.getName()); 
			
				R.add(currentthread); 

				currentthread.start(); //calls BankClient run()
				
				//numthreads++; //keeps track of the number of threads
			}
			/*else{
				threads[i-1] = null; //ignores empty line, no thread created.
				numthreads++;
				System.out.println("thread " + i + " was not created and did not run, empty line."); //for completeness
			}*/
		}
		
	/*	for(int i=1; i<N-1; i++){
			((Thread)R.get(i)).start();
		}*/
		
	}

}
