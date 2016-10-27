package bankeralgsim;

import java.util.concurrent.Semaphore;

import javax.management.monitor.Monitor;

public class BankClient implements Runnable{
	
	String[] data;
	Semaphore mutex_R;
	Semaphore mutex_B;
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
	
	BankClient(long credit, int clientnumber, Semaphore mutex_R, Semaphore mutex_B, Banker B){

		//trims off leading whitespace, and separates words by spaces, commas, periods, colons, etc. using regex
		name = "Client no. " + clientnumber; 
		req = (long)(credit * .25);
		
		this.clientnumber = clientnumber;
		this.credit = credit;
		this.mutex_R = mutex_R;
		this.mutex_B = mutex_B;
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

			req = credit-allocated;
		}
		
		if(allocated==credit){
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
