package bankeralgsim;

import java.util.concurrent.Semaphore;

import javax.management.monitor.Monitor;

public class BankClient implements Runnable{
	
	String[] data;
	Semaphore mutex_R;
	Semaphore mutex_B;
	Monitor monitor_B;
	Banker B;
	
	
	BankClient(){
		//default constructor
	}
	
	BankClient(String lineinput, Semaphore mutex_R, Semaphore mutex_B, Banker B){
		/*
		 * Creates the thread with the passed partition, initialize local variables including frequency map.
		 */

		//trims off leading whitespace, and separates words by spaces, commas, periods, colons, etc. using regex
		data = lineinput.trim().split("[\\n\\r\\f\\t\\v\\s,.:;()-+*$?!/\"]+"); 
		
		this.mutex_R = mutex_R;
		this.mutex_B = mutex_B;
		this.B = B;
		//setName(data[0]);

		System.out.println(data[0] + " has been created.");

	}
	
	public void run() {
		
		/*
		 * Banker Algorithm runs
		 */
		
		//System.out.println(data[0] + " is running");
		
	//	try{
			
			//obtains access before entering critical section
			
		//	mutex_B.acquire();
			
			if(B.requestResource(Long.valueOf(data[1]).longValue())){
				System.out.println(Thread.currentThread().getName() + " received funds.");
				try {
				    Thread.sleep(1000);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				System.out.println(data[0] + " successfully completed. Returning funds...");
				B.returnResource(Long.valueOf(data[1]).longValue());
			}
			else{
				System.out.println("Failed in BankClient");
			}
		
		//	mutex_B.release();
	/*	}
		catch(InterruptedException e){
			System.out.println("Failed in Thread " + data[0]);
			e.printStackTrace();
		} */

	}


	String getName(){
		return data[0];
	}
	
}
