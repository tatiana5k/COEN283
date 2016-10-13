package multibufferedprinter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class SrcLoader implements Runnable {
	String recordname;
	Scanner input;
	File sourcefile; 
	StringBuilder Buffer1;
	Semaphore isfullBuf1;
	Semaphore isemptyBuf1;
	String[] sourcename;
	int n;
	int numrecords;
	
	SrcLoader(){
		//default constructor
	}
	SrcLoader(String[] sourcename, StringBuilder Buffer1, Semaphore isfullBuf1, Semaphore isemptyBuf1, int numrecords) {
	
		sourcefile = null; 
		this.Buffer1 = Buffer1;
		this.isfullBuf1 = isfullBuf1;
		this.isemptyBuf1 = isemptyBuf1;
		this.sourcename = sourcename;
		this.numrecords = numrecords;
		n = 0;

		System.out.println("Created SrcReader");

	}
	
	public void run(){
		while(n<numrecords){
			
			try{
				isemptyBuf1.acquire();
				
				/*
				 * Loads the source file. Code recycled from assignment 1.
				 */		
				try{ //in case the file is not found.
					sourcefile = new File(sourcename[n]);
					input = new Scanner(sourcefile);
				} catch (FileNotFoundException e) {
					System.out.println("Source file nt found. Program terminated.");
			        e.printStackTrace();
			    }
				
				Buffer1.append(input.useDelimiter("\\Z").next());

				System.out.println();
				System.out.println("Loaded Record "+ sourcename[n] + " to Buffer 1");
				
				n++;
				
				isfullBuf1.release();
				
			}
			catch(InterruptedException e){
				System.out.println("Failed in SrcReader");
				e.printStackTrace();
			
				System.out.println("Waiting to Load");
			}	
		}
	}
}
