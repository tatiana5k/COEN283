package pagingsyssim;

import java.util.concurrent.Semaphore;

public class PgSim implements Runnable {
	
	int[] sequence; 
	int[][] frames;
	int numframes;
	int pagefaults;
	Semaphore mutex;
	
	PgSim(){
		//default constructor
	}
	
	PgSim(int[] sequence, int[][] frames, int numframes, Semaphore mutex){
		this.sequence = sequence;
		this.frames = frames;
		this.numframes = numframes;
		pagefaults = 0;
		this.mutex = mutex;
		
	}
	
	public void run(){
		int current;
		boolean found=false;
		boolean set=false;
		int k = 0;
		int c;
		int n;
		
		System.out.println("Running PgSim. Sequence length: " + sequence.length);
		
		for(int i=0; i<sequence.length; i++){ //for all the input page numbers
			current = sequence[i]; 
			//System.out.println("Current Page = "+ current);
			
			//look for the page in the frames
			for(int j=0;j<numframes;j++){
				if(frames[0][j] != 0){ //if the current frame is not 0 (hasn't been filled yet)
					if(current==frames[0][j]){ //if the page number matches
						//System.out.println("Hit! "+current);
						found = true;  //hit
						//set the reference bit
						frames[1][j] = 0b1;
					}
				}
				
			}
			if(!found){
				//System.out.println("Looking for empty frame in total of " + numframes);
				//look for an empty frame to load the page
				while(k<numframes && set==false){
					if (frames[0][k] == 0){
						frames[0][k] = current;
						set = true;
						//System.out.println("Found empty frame!");
					}
					k++;
					
				}
				//if no empty frame was found, page fault and replace an existing page 
				
				if (!set){
					//System.out.println("Miss! "+current);
					//System.out.println("Didn't find empty frame. Lookin for page to replace...");
					try{
						mutex.acquire();
						//System.out.println("PgSim acquired mutex.");
						pagefaults++;
						//replace a page using aging algorithm
						c = frames[2][0];
						n = 0;
						for(int m=0; m<numframes; m++){ //find smallest counter 
							if(frames[2][m]<c){
								c = frames[2][m];
								n=m;
							}
						}
						//System.out.println(c);
						//System.out.println("Replacing page " + frames[0][n]);
						frames[0][n] = current;
						frames[1][n] = 0b1;
						frames[2][n] = 0b0000000;
						mutex.release();
						//System.out.println("Pgsim released mutex.");
					}
					catch(InterruptedException e){
						System.out.println("Failed in PgSim");
						e.printStackTrace();
					}
				}
				
			}
			set = false; //reset for next iteration
			found = false;
			try{
				Thread.sleep(1);
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public int getFaults(){
		return this.pagefaults;
	}
	

}
