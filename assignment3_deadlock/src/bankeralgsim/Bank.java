package bankeralgsim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Bank {
	
	public static void main ( String[] args ) throws IOException {
		
		Semaphore mutex_B = new Semaphore(1);
		Semaphore mutex_R = new Semaphore(0);
		
		int numresources = 1;	//number of resources in the bank
		
		Scanner sc_console = new Scanner(System.in);
		PrintStream stdout = System.out;
		
		System.out.print("Enter quantity of bank resource: ");
		long resource = sc_console.nextInt();	
		
		//create matrices
		long[] E = new long[numresources];	//Resource matrix
		long[] A = new long[numresources];	//Available matrix

		for(int i=0;i<numresources;i++){
			//can implement user input here for resource amounts
			//using one resource hard coded for now.
			E[i] = resource; 
			A[i] = resource;		
		}

		System.out.print("Enter number of clients: ");
		int numclients = sc_console.nextInt();		

		long[][] C = new long[numresources][numclients];		//Current Allocations matrix, ArrayList is dynamic
		long[][] R = new long[numresources][numclients];
	
		System.out.println("Enter each client's credit limit:");
		for(int i=1; i<numclients+1; i++){
			System.out.print("Client no. " + i + ": ");
			C[0][i-1] = 0;
			R[0][i-1] = sc_console.nextInt();
			while(R[0][i-1]>E[0]){
				//guarantees safe state
				System.out.println("Illegal entry. No safe state possible.");
				System.out.println("Credit limit cannot exceed maximum bank resource.");
				System.out.print("Please enter a valid credit limit for Client no. " + i + " or -1 to quit: ");
				R[0][i-1] = sc_console.nextInt();
				if (R[0][i-1] < 0){
					System.out.println("Simulation Terminated.");
					System.exit(0);
				}
			}
		}
		PrintStream log = new PrintStream(new FileOutputStream("LogFile.txt"));
		//System.setOut(log);
		
		System.out.println("The clients' allocated credit lines are:");
		for(int i=1; i<numclients+1;i++){
			System.out.println("Client no. " + i + ": " + R[0][i-1]);
		}
		System.out.println();

		Banker B = new Banker(E,A,C,R);

		Thread currentthread;
		BankClient currentclient;
		String currentline;
		
		Thread[] threads = new Thread[numclients];
		System.setOut(stdout);
		
		for(int clientnumber=1; clientnumber<numclients+1; clientnumber++){

			//creates the thread, constructor calls start()
			currentclient = new BankClient(R[0][clientnumber-1], clientnumber, mutex_R, mutex_B, B);
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
		
		//System.setOut(log);
		
		for(int clientnumber=1; clientnumber<numclients+1; clientnumber++){
			
			threads[clientnumber-1].start(); //calls BankClient run()
		}
		for(int clientnumber=1; clientnumber<numclients+1;clientnumber++){
			try{
				threads[clientnumber-1].join();
			}catch (InterruptedException e){
		        e.printStackTrace(); //error handling
		    }
		}
	
		System.setOut(stdout);
		System.out.println("Bank Simulation Complete.");
	}
	
}
