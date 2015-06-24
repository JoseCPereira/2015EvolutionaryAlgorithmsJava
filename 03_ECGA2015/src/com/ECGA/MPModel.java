package com.ECGA;



class MPModel{
	public static int offspringSize;									// NOTE: The MPM is responsible for sampling new individuals.
		
	private Subset[]  subsets; 
	private int		  maxSubsets;
	private int       nSubsets;
	private Cache     cache;
	
	
	public MPModel(int offSize){
		offspringSize = offSize;
		this.maxSubsets    = Problem.n;									// NOTE: initializeMPM(...) is responsible for initializing the MPM structure and cache.
	}		
	
	public int  getOffspringSize(){return offspringSize;}
	public Subset[]   getSubsets(){return subsets;}
	public Subset getSubset(int i){return subsets[i];}
	public int 		    getNSets(){return nSubsets;}
	public Cache 	    getCache(){return cache;}
	
	
	public void generateModel(SelectedSet selectedSet){
		initializeMPM(selectedSet);
		
		while(true){
			int bestCompressionID  = -1;
			double bestCompression = 0;
			for(int i = 0; i < cache.nMergedSets; i++){					// Find the mergedSet that improves the MPM the most.
				double compression = cache.compressions[i];
				if(bestCompression < compression){
					bestCompression   = compression;
					bestCompressionID = i;
				}
			}
			
			if(bestCompressionID == -1)									
				return;													// The MPModel can no longer be improved. Just return.
			
		    int id1   = cache.idA[bestCompressionID],					// 1. Retrieve from the cache all the information about the best subset.
				id2   = cache.idB[bestCompressionID];					//	 1.1 Enforce ascending order for the IDs.
			int minID = Math.min(id1, id2),								// NOTE: 'minID' is the ID for the new merged set in 'subsets'. 
				maxID = Math.max(id1, id2);								// NOTE: 'maxID' is the ID of the set that is going to be replaced by the last subset in the MPM.	 
			
			Subset newSet       = cache.mergedSets[bestCompressionID];	// 2. Update the MPM.
			subsets[minID] 		= newSet;								//	 2.1. Replace setA with the new merged set.
			subsets[maxID] 		= subsets[nSubsets-1];					// 	 2.2. Replace setB with the last subset in the MPM. NOTE: At this point the cache is not up-to-date, check items 1 and 3.
			subsets[nSubsets-1] = null;									//   2.3. Delete last subset, it's just a copy.
			nSubsets--;													//   2.4. 1 newSet - 2 oldSets = -1 subset.
			
			cache.removeSubsets(minID, maxID, nSubsets);				// 3. Update Cache. Remove from the cache any subsets composed by setA or setB and replace all ID's equal to 'nSubsets' by 'maxID'. 
			
			for(int i = 0; i < nSubsets; i++)							// 4. Compute the new subsets and insert them in the cache.
				if(i != minID){											// NOTE: 'minID' is now the ID for the new merged set in 'subsets'.
					Subset setI = subsets[i],
						   mergeSet = setI.merge(newSet, selectedSet);
					if(mergeSet != null){								// Assert that the new merge set is not too big.
						double compI = setI.getCC(),
							   compJ = newSet.getCC(),
							  compIJ = mergeSet.getCC(),
						 compression = compI + compJ - compIJ;
						int newMinID = Math.min(i, minID),		 
							newMaxID = Math.max(i, minID);		
						cache.insertSubset(mergeSet, newMinID, newMaxID, compression);
					}
				}
			
			cache.compact();											// 5. Compact the cache by moving all entries to the left-most side.
			
		}// END: Cache loop.
	}// END: generateModel(...)
	
	public void initializeMPM(SelectedSet selectedSet){
		this.subsets = new Subset[maxSubsets];							// 0. Initialize MPM structure.	
		for(int i = 0; i < maxSubsets; i++)								// 1. Initial MPM is [0][1][2]...[Problem.n-1].
			subsets[i] = new Subset(selectedSet, i);					// NOTE: The Subset constructor is responsible for computing the partial complexities of each set.
		this.nSubsets = maxSubsets;										// NOTE: Initially the MPM is at full capacity.
		this.cache    = new Cache();									// 2. Initialize the Cache.				 
		for(int i = 0; i < Problem.n-1; i++)							// Compute the new subsets and insert them in the cache. 
			for(int j = i+1; j < Problem.n; j++){						// NOTE: Insertion in the cache is in descending order of compression.
				Subset setI = subsets[i],
					   setJ = subsets[j],
					   mergeSet = setI.merge(setJ, selectedSet);
				if(mergeSet.getNFrequencies() <= selectedSet.getN()){
					double compI  	   = setI.getCC(),
						   compJ  	   = setJ.getCC(),
					       compIJ 	   = mergeSet.getCC(),
					       compression = compI + compJ - compIJ;				
					cache.insertSubset(mergeSet, i, j, compression);
				}
			}
	}
	
	public Individual[] sampleNewIndividuals(SelectedSet selectedSet){
		Individual[] newIndividuals = new Individual[offspringSize];
		for(int i = 0; i < offspringSize; i++)
			newIndividuals[i] = new Individual();
		for(int i = 0; i < nSubsets; i++){
			int [] xList = subsets[i].getXList();
			for(int j = 0; j < offspringSize; j++){
				int pick = ECGA.random.nextInt(offspringSize);			// NOTE: Implement makeShuffle(offspringSize) to ensure non-replacement
				Individual pickIndiv = selectedSet.getIndividual(pick);
				for(int l = 0; l < xList.length; l++){
					int locus = xList[l];
					char allele = pickIndiv.getAllele(locus);
					newIndividuals[j].setAllele(locus, allele);
				}
			}
		}
		return newIndividuals;
	}
		
	public String toString(){
		return "Subsets: " + subsets + "\nCache: " + cache; 
	}
}









	

