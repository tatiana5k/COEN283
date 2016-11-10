package pagingsyssim;

import java.util.concurrent.Semaphore;

public class PgClk implements Runnable {
	int[][] frames;
	int numframes;
	Semaphore mutex;
	int clockticksz;
	boolean keepticking;
	
	PgClk(){
		//default constructor
	}
	
	PgClk(int[][] frames, int numframes, Semaphore mutex){
		this.frames = frames;
		this.numframes = numframes;
		this.mutex = mutex;
		clockticksz = 3;
		keepticking=true;
		//run();
	}
	
	public void run(){
		System.out.println("Running PgClk.");
		while(keepticking){
			//System.out.println("Tick");
			
			try{
				Thread.sleep(clockticksz); //wait some amount of time for the frames to be loaded
				mutex.acquire();
				//System.out.println("Clk acquired mutex");
				//update the counters every clock tick
				for(int i=0; i<numframes; i++){
					frames[2][i] = frames[2][i]/2; //shift counter right by 1
					if(frames[1][i]==1){ //If the reference bit of the page is set
						frames[2][i] = frames[2][i] + 0b10000000; //Set the most significant bit
						frames[1][i] = 0b0; //Clear reference bit
					}
				}
				mutex.release();
				//System.out.println("Clk released mutex");
			}
			catch(InterruptedException e){
				System.out.println("Failed in PgSim");
				e.printStackTrace();
			}
			
		}
		
	}
	public void stopTicking(){
		this.keepticking = false;
	}
}
