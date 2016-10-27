package bankeralgsim;

public class Banker {
	
	long[] E;
	long[] A;
	long[][] C;
	long[][] R;
	
	Banker(){
		//default constructor
	}
	
	Banker (long[] E, long[] A, long[][] C, long[][] R){
		//initialize variables
		this.E = E;
		this.A = A;
		this.C = C;
		this.R = R;
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
		
		/*
		 * Only executes if request is granted/state is safe
		 */
		System.out.println(Thread.currentThread().getName() + " request for " + amount + " has been granted.");
		C[0][clientnumber-1] = C[0][clientnumber-1] + amount;
		A[0] = A[0]-amount;
		System.out.println("Available Funds in Bank: " + A[0]);
		notify(); //notifies next waiting thread that it can enter the monitor

	}
	synchronized void returnResource(long amount, int clientnumber){
		//returns funds
		C[0][clientnumber-1] = C[0][clientnumber-1] - amount;
		A[0] = A[0]+amount;
		System.out.println(Thread.currentThread().getName() + " funds returned.");
		System.out.println("Available Funds in Bank: " + A[0]);
		notify();//notifies next waiting thread that it can enter the monitor
	}
}
