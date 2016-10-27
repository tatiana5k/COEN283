package bankeralgsim;

import java.util.ArrayList;

public class Banker {
	
	long[] E;
	long[] A;
	long[][] C;
	long[][] R;
	
	boolean safe;
	
	Banker(){
		//default constructor
	}
	
	//do I need a mutex on the banker?
	
	Banker (long[] E, long[] A, long[][] C, long[][] R){
		this.E = E;
		this.A = A;
		this.C = C;
		this.R = R;
		safe = false;
	}
	
	synchronized void requestResource(long amount, int clientnumber){
		System.out.println(Thread.currentThread().getName() + " request for " + amount + " received.");
		//check if resource grant would cause unsafe state
		while((A[0]-amount) < (R[0][clientnumber-1] - (C[0][clientnumber-1] + amount))){
			//if yes, make the calling thread wait
			try{
				System.out.println(Thread.currentThread().getName() + " must wait.");
				wait();
			} 
			catch(InterruptedException e){
	            e.printStackTrace();
	        }
		}	
		
		System.out.println(Thread.currentThread().getName() + " request for " + amount + " has been granted.");
		C[0][clientnumber-1] = C[0][clientnumber-1] + amount;
		A[0] = A[0]-amount;
		System.out.println("Available Funds in Bank: " + A[0]);
		
		notify();

	}
	synchronized void returnResource(long amount, int clientnumber){
		C[0][clientnumber-1] = C[0][clientnumber-1] - amount;
		A[0] = A[0]+amount;
		System.out.println(Thread.currentThread().getName() + " funds returned.");
		System.out.println("Available Funds in Bank: " + A[0]);
		notify();
	}
}
