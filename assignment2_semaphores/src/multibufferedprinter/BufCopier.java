package multibufferedprinter;

import java.util.concurrent.*;

public class BufCopier implements Runnable{
	StringBuilder Buffer1;
	StringBuilder Buffer2;
	
	Semaphore isfullBuf1;
	Semaphore isfullBuf2;
	Semaphore isemptyBuf1;
	Semaphore isemptyBuf2;
	
	int n;
	int numrecords;
	
	BufCopier(){
		//default constructor
	}
	
	
	/*
	 * Constructs BufCopier Object with all the proper pointers
	 * to buffers and Semaphores.
	 */
	BufCopier(StringBuilder Buffer1, StringBuilder Buffer2, 
			  Semaphore isfullBuf1, Semaphore isemptyBuf1, 
			  Semaphore isfullBuf2, Semaphore isemptyBuf2, 
			  int numrecords){
	
		this.Buffer1 = Buffer1;
		this.Buffer2 = Buffer2;
		this.isfullBuf1 = isfullBuf1;
		this.isfullBuf2 = isfullBuf2;
		this.isemptyBuf1 = isemptyBuf1;
		this.isemptyBuf2 = isemptyBuf2;
		this.numrecords = numrecords;
		n=0;
		
		System.out.println("Created BufCopier");
	
	}
	
	public void run(){
		
		while(n<numrecords){
			try{
			
				//obtains access before entering critical section
				isfullBuf1.acquire();
				isemptyBuf2.acquire();
				
				//copies contents of Buffer1 to Buffer2
				Buffer2.append(Buffer1.toString());
				Buffer1.delete(0, Buffer1.length());
		
				System.out.println("Copying from Buffer 1 to Buffer 2");
				
				n++;
				
				//releases for write to Buffer1 and print of Buffer2
				isemptyBuf1.release();
				isfullBuf2.release();
				
			}
			catch(InterruptedException e){
				System.out.println("Failed in BufCopier");
				e.printStackTrace();
			}
			
		}
	}
}
