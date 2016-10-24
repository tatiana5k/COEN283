package bankeralgsim;

import java.util.ArrayList;

public class Banker {
	
	long[] E;
	long[] A;
	ArrayList C;
	ArrayList R;
	
	Banker(){
		//default constructor
	}
	
	//do I need a mutex on the banker?
	
	Banker (long[] E, long[] A, ArrayList C, ArrayList R){
		this.E = E;
		this.A = A;
		this.C = C;
		this.R = R;	
	}
	
	synchronized boolean requestResource(long amount){
		System.out.println(Thread.currentThread().getName() + " request received.");
		//check if resource grant would cause unsafe state
		while(amount>A[0]){
			
			//error handling
			if(amount>E[0]){
				System.out.println("Resource requested is greater than maximum allowed)");
			//	Thread.currentThread().stop();
			}
			
			//if yes, make the calling thread wait
			try{
				System.out.println(Thread.currentThread().getName() + " must wait.");
				wait();
			} 
			catch(InterruptedException e){
	            e.printStackTrace();
	        }
		}	
		
		C.add(Thread.currentThread());
		A[0] = A[0]-amount;
		System.out.println("Available Funds in Bank: " + A[0]);
			
		return true;

	}
	synchronized void returnResource(long amount){
		C.remove(Thread.currentThread());
		A[0] = A[0]+amount;
		System.out.println(Thread.currentThread().getName() + " funds released.");
		notify();
	}
}
