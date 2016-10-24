package multibufferedprinter;

import java.util.concurrent.*;

public class BufPrinter implements Runnable {
	StringBuilder Buffer2;
	
	Semaphore isfullBuf2;
	Semaphore isemptyBuf2;
	
	int n;
	int numrecords;
		
	BufPrinter(){
		//default constructor
	}
	
	/*
	 * Constructor BufPrinter Object with all the proper pointers
	 * to buffers and Semaphores.
	 */
	BufPrinter(StringBuilder Buffer2, 
			   Semaphore isfullBuf2, Semaphore isemptyBuf2, 
			   int numrecords){
		
		this.Buffer2 = Buffer2;
		this.isfullBuf2 = isfullBuf2;
		this.isemptyBuf2 = isemptyBuf2;
		this.numrecords = numrecords;
		n = 0;
		System.out.println("Created Printer");

	}
	
	public void run(){
		while(n<numrecords){
			try{
				//acquires access to Buffer2 for read
				isfullBuf2.acquire();
				
				System.out.println("Printing from Buffer 2...");
				System.out.println();
				System.out.println("_______________________________________");
				System.out.println(Buffer2);
				System.out.println("_______________________________________");
				System.out.println();
				Buffer2.delete(0, Buffer2.length()); //clear buffer
				
				n++;
				
				//release Buffer2 for next write
				isemptyBuf2.release();
			}
			catch(InterruptedException e){
				System.out.println("Failed in BufPrinter");
				e.printStackTrace();
			}
		}
	}

}
