package com.ECGA;


class Subset{
	
	private int[] xList; 		 										// List of variables in this Subset.
	private int nFrequencies;	 										// Number of possible frequencies for this Subset. nFrequencies = 2^xList.size().
	private double mComplexity,	 										// Model Complexity
				   cpComplexity; 										// Compressed Population Complexity
	
	public Subset(SelectedSet selectedSet, int Xi){	
		int NS 			  = selectedSet.getN();
		xList 			  = new int[1];							
		xList[0] 		  = Xi;
		nFrequencies 	  = 2;					 						// Initially each frequency table has only two entries
		int[] frequencies = new int[nFrequencies]; 						// NOTE: Lobo uses 'long' instead of 'int'. This is not allowed in Java!
		int uniFreq 	  = selectedSet.getUniFrequencies(Xi);
		frequencies[0]    = NS - uniFreq;
		frequencies[1]    = uniFreq;
		mComplexity 	  = modelComplexity(NS);
		cpComplexity 	  = compressedPopulationComplexity(frequencies, NS); 
	}
	
	public Subset(SelectedSet selectedSet, int[] newXList){	
		int NS 			  = selectedSet.getN();
		this.xList 		  = newXList;									// NOTE: xList must be initialized before calling computeFrequencies().
		nFrequencies 	  = (int)Math.pow(2, xList.length);					
		int[] frequencies = this.computeFrequencies(selectedSet);
		mComplexity		  = modelComplexity(NS);
		cpComplexity 	  = compressedPopulationComplexity(frequencies, NS);	
	}
	
	public int[] 	  getXList(){return xList;}
	public int 		   getSize(){return xList.length;}
	public int getNFrequencies(){return nFrequencies;}
	public double 		 getMC(){return mComplexity;}
	public double	    getCPC(){return cpComplexity;}
	public double 		 getCC(){return mComplexity + cpComplexity;}
	
	public Subset merge(Subset setB, SelectedSet selectedSet){			// NOTE: setC <- setA.merge(setB),  where setA <- this
		int[] xListC = mergeXList(this.xList, setB.getXList());
		if(nFrequencies > selectedSet.getN())
			return null;
		else
			return new Subset(selectedSet, xListC);					
	}
	
	public int[] mergeXList(int[] xListA, int[] xListB){
		int sizeA = xListA.length,
			sizeB = xListB.length,
			sizeC = sizeA + sizeB;
		int[] xListC = new int[sizeC];
		int a = 0, b = 0, c = 0;
		while(c < sizeC){				 								// NOTE: All xLists are sorted in ascendent order
			if(a >= sizeA || b >= sizeB)
				break;
		    if(xListA[a] < xListB[b])
		    	xListC[c++] = xListA[a++];
		    else
		    	xListC[c++] = xListB[b++];
		}
		if(a >= sizeA)
			for( int i = b; i < sizeB; i++ )
				xListC[c++] = xListB[i];
		if(b >= sizeB)
		    for( int i = a; i < sizeA; i++ )
		    	xListC[c++] = xListA[i];
		return xListC;
	}
	
	private double modelComplexity(int NS){
		return Math.log(1+NS)*(nFrequencies-1);
	}
	
	private double compressedPopulationComplexity(int[] frequencies, int NS){
		double entropy = 0;
		for(int j = 0; j < nFrequencies; j++){
			double prob = ((double)(frequencies[j]))/NS;
			if(prob != 0)
				entropy += -prob * Math.log(prob);
		}
		return entropy*NS;
	}
	
	private int[] computeFrequencies(SelectedSet selectedSet){
		int[] frequencies = new int[nFrequencies];
		int NS = selectedSet.getN();
		int xSize = xList.length;
		char[] schema = new char[xSize];
		for(int i = 0; i < NS; i++){
			for(int j = 0; j < xSize; j++)
				schema[j] = selectedSet.getIndividual(i).getAllele(xList[j]);
			int p = encode(schema, xSize);
			frequencies[p]++;
		}
		return frequencies;
	}
	
	private int encode(char[] schema, int xSize){
		int result = 0;
		int powerof2 = 1;
		for(int j = xSize-1; j >= 0; j--){
			if(schema[j] == '1')
				result += powerof2;
			powerof2 *= 2;
		}
		return result;
	}
	
	/* NOTE: This is never used.
	private int[] decode(int code){
		int nBits = 1;
		int pow2 = 2;
		while(code > pow2){
			nBits++;
			pow2 *= 2;
		}
		int[] result = new int[nBits];
		int i = nBits-1;
		while(code != 0 && i >= 0){
			result[i] = code%2;
			code /= 2;
			i--;
		}
		return result;
	}
	*/
	
	public String toString(){
		String str = "xList: [";
		for(int i = 0; i < xList.length; i++)
			str += xList[i] + ",";						// Print the xList array.
		str = str.substring(0, str.length()-1);			// Remove the last comma.
		str += "] ; MC = " + mComplexity + " ; CPC = " + cpComplexity;
		return str + "\n";
	}
}









