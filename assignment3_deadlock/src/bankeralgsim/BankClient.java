package bankeralgsim;

import javax.management.monitor.Monitor;

public class BankClient implements Runnable{
	
	String[] data;
	Monitor monitor_B;
	Banker B;
	String name;
	long req;
	long credit;
	long allocated;
	int clientnumber;
	
	BankClient(){
		//default constructor
	}
	
	BankClient(long credit, int clientnumber, Banker B){

		//initialize variables
		name = "Client no. " + clientnumber; 
		req = (long)(credit * .25); //first request will be 25% of the client's credit line
		
		this.clientnumber = clientnumber;
		this.credit = credit;
		this.B = B;

		System.out.println( name + " has been created.");
	}
	
	public void run() {
		

		while(allocated<credit){
			//make request
			B.requestResource(req, clientnumber);
			allocated = allocated + req;
			try {
				//simulate doing lots of stuff
			    Thread.sleep(1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			//second request will be the remainder of the credit allowed
			req = credit-allocated;
		}
		
		if(allocated==credit){
			//threads are done when they've used up all their credit
			System.out.println(name + " successfully completed. Returning funds (" + allocated + ")...");
			B.returnResource(allocated, clientnumber);
		}	
		else System.out.println("Something went wrong in client");

	}

	String getName(){
		return name;
	}
	
	int getClientNumber(){
		return clientnumber;
	}
	
}
