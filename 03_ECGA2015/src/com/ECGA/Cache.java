package com.ECGA;

class Cache{
	public int 	maxSize;
	public int 	nMergedSets;
	public Subset[] mergedSets;									// NOTE: Try implementing subclass 'MergedSet' instead.
	public    int[] idA, idB;
	public double[] compressions;
	
	private int[] emptyPositions;
	private int   nEmptyPositions;
	
	public Cache(){
		this.maxSize         = Problem.n*(Problem.n-1)/2;
		this.nMergedSets     = 0;
		this.mergedSets      = new Subset[maxSize];
		this.idA 	         = new int[maxSize];
		this.idB 	         = new int[maxSize];
		this.compressions    = new double[maxSize];
//		Arrays.fill(idA, -1);
//		Arrays.fill(idB, -1);
//		Arrays.fill(compressions, Double.NEGATIVE_INFINITY);
		this.emptyPositions  = new int[2*Problem.n];
		this.nEmptyPositions = 0;
	}
	
	public void insertSubset(Subset mergedSet, int minID, int maxID, double compression){
		if(nMergedSets >= maxSize)								// Cache is full!
			return;
		int position;											// The position where 'mergedSet' will be inserted.
		if(this.nEmptyPositions == 0)							// Cache is compacted. 
			position = nMergedSets;								// Insert in last position.
		else{
			position = this.emptyPositions[nEmptyPositions-1];	// Choose the last position to become empty.
			nEmptyPositions--;
		}
		mergedSets[position]   = mergedSet;						// Insert mergedSet and all corresponding information in the choosen position.
		idA[position] 		   = minID;
		idB[position] 		   = maxID;
		compressions[position] = compression;
		nMergedSets++;
	}
	    
	public void removeSubsets(int minID, int maxID, int nSubsets){
		removeEntry(minID);
	    removeEntry(maxID);
	    replaceXbyY(nSubsets, maxID);
	}
	
	
	private void removeEntry(int id){
		for(int i = 0; i < maxSize; i++)
			if(idA[i] == id || idB[i] == id){	
				mergedSets[i]   = null;
				idA[i] 			= -1;
				idB[i] 			= -1;
				compressions[i] = Double.NEGATIVE_INFINITY;
				emptyPositions[nEmptyPositions] = i;
				nEmptyPositions++;
				nMergedSets--;
			}
	}
	
	private void replaceXbyY(int x, int y){
		for(int i = 0; i < maxSize; i++){
			if(idA[i] == x) idA[i] = y;
			if(idB[i] == x) idB[i] = y;
		}
	}
	
	public void compact(){
		int newMaxSize = maxSize - nEmptyPositions;
		int pos		   = maxSize - 1;
		for(int i = 0; i < nEmptyPositions; i++){
			int emptyPos = emptyPositions[i];
			if(emptyPos < newMaxSize){
				while(idA[pos] == -1)						// Find a non-empty entry.
					pos--;
				mergedSets[emptyPos]   = mergedSets[pos];	// Move non-empty entry to one empty slot in the left-most side of the cache.
				idA[emptyPos]		   = idA[pos];
				idB[emptyPos]		   = idB[pos];
				compressions[emptyPos] = compressions[pos];
				pos--;
			}
		}
		nEmptyPositions = 0;								// All other empty positions are now at the right-most side of the cache and can be ignored.
		maxSize 		= newMaxSize;
	}
	
	public String toString(){return "Sets: " + mergedSets + "\nidA: " + idA + "\nidB: " + idB;}
}








