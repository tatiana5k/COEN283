package bankeralgsim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Bank {
	
	public static void main ( String[] args ) throws IOException {
		
		int numresources = 1;	//number of resources in the bank
		
		Scanner sc_console = new Scanner(System.in);
		PrintStream stdout = System.out;
		
		System.out.print("Enter quantity of bank resource (credit), numbers only: ");
		long resource = sc_console.nextInt();	
		
		/*
		 * Create matrices
		 */
		long[] E = new long[numresources];	//Resource matrix
		long[] A = new long[numresources];	//Available matrix

		for(int i=0;i<numresources;i++){
			//can implement user input here for resource amounts
			//using one resource hard coded for now.
			E[i] = resource; 
			A[i] = resource;		
		}

		/*
		 * Receives user input for number of clients
		 * and their credit limits.
		 */
		System.out.print("Enter number of clients, must be an integer: ");
		int numclients = sc_console.nextInt();		

		long[][] C = new long[numresources][numclients];	//Current Allocations matrix
		long[][] R = new long[numresources][numclients];	//Max Resource matrix, aka credit lines	
	
		System.out.println("Enter each client's credit limit, must be a number:");
		for(int i=1; i<numclients+1; i++){
			System.out.print("Client no. " + i + ": ");
			C[0][i-1] = 0;
			R[0][i-1] = sc_console.nextInt();
			while(R[0][i-1]>E[0]){
				//guarantees safe state: client can not request credit line greater than the maximum
				System.out.println("Illegal entry. No safe state possible.");
				System.out.println("Credit limit cannot exceed maximum bank resource.");
				System.out.print("Please enter a valid credit limit for Client no. " + i + " or -1 to quit: ");
				R[0][i-1] = sc_console.nextInt();
				if (R[0][i-1] < 0){
					//user has chosen to terminate simulation
					System.out.println("Simulation Terminated.");
					System.exit(0);
				}
			}
		}
		
		System.out.println("Running simulation...");
		
		PrintStream log = new PrintStream(new FileOutputStream("LogFile.txt"));
		System.setOut(log);
		
		System.out.println("Bank total resource (cash): " + E[0]);
		System.out.println();
		
		System.out.println("Clients' allocated credit lines (max resource needs):");
		for(int i=1; i<numclients+1;i++){
			System.out.println("Client no. " + i + ": " + R[0][i-1]);
		}
		System.out.println();

		//Banker logic ensures safe state will be maintained in allocating resources
		Banker B = new Banker(E,A,C,R);

		Thread currentthread;
		BankClient currentclient;
		String currentline;
	
		Thread[] threads = new Thread[numclients]; //for easy access to threads
		
		System.out.println("Creating Clients.");
		for(int clientnumber=1; clientnumber<numclients+1; clientnumber++){

			//creates the thread, constructor calls start()
			currentclient = new BankClient(R[0][clientnumber-1], clientnumber, B);
			currentthread = new Thread(currentclient,currentclient.getName()); 
			//currentthread.start();
			try{
				currentthread.join();
			}catch (InterruptedException e){
		        e.printStackTrace(); //error handling
		    }
			threads[clientnumber-1]=currentthread;
				
		}
		
		System.out.println();
		System.out.println("Running Clients...");
		
		for(int clientnumber=1; clientnumber<numclients+1; clientnumber++){
			threads[clientnumber-1].start(); //calls BankClient run() to run threads
		}
		for(int clientnumber=1; clientnumber<numclients+1;clientnumber++){
			try{
				//ensures Bank main will wait until the Client threads are done to finish
				threads[clientnumber-1].join();
			}catch (InterruptedException e){
		        e.printStackTrace(); //error handling
		    }
		}
		System.out.println();
		System.out.println("Simulation Complete.");
	
		System.setOut(stdout);
		System.out.println("Bank Simulation Complete. See logfile.txt for detailed output.");
	}
	
}
