package com.SGA;


//////////////////////////////////////////////
//
//This class implements the SGA solver.
//
//////////////////////////////////////////////

class SGASolver{
	public static int                    N;								// Population size. Default = 500.
	public static long            fitCalls;								// Number of fitness calls. This is incremented by Problem.computeFitness(...).
	public static Problem   	   problem;	 							// NOTE: Use Parameter.initializeProblem() to generate an appropriate problem instance.
	
	public static Selection 	 selection;		 						// NOTE: Use Parameter.initializeSelector()    to generate the chosen selector type.
	public static Crossover 	 crossover;								// NOTE: Use Parameter.initializeCrossover()   to generate the chosen crossover type.
	public static Mutation 	      mutation;								// NOTE: Use Parameter.initializeMutation()    to generate the chosen mutation type.
	public static IReplacement replacement; 							// NOTE: Use Parameter.initializeReplacement() to generate the chosen replacement type.
	
	
	public SGASolver(String paramFile){		
		SGAParameter.initializeParameters(paramFile);  					// Initialize and validate SGA parameters
		problem     = SGAParameter.initializeProblem();					// Design Pattern Strategy
		selection   = SGAParameter.initializeSelection(N);				// Design Pattern Strategy
		crossover   = SGAParameter.initializeCrossover();
		mutation    = SGAParameter.initializeMutation();
		replacement = SGAParameter.initializeReplacement();
		SGAPress.initializePress();
	}
	
	public int solve(int nRun){
		int nGen = 0;															// Initialize number of generations.
	    fitCalls = 0;											  				// Initialize number of fitness calls. This is incremented by Problem.computeFitness(...);		
		Population currentPopulation = new RandomPopulation(N);	  				// Initial random population. Fitness and statistics automatically computed.
		
		while(!Stopper.criteria(nGen, currentPopulation)){			
			SelectedSet selectedSet = selection.select(currentPopulation);		// 1. SELECTION.
			Individual[] newIndividuals = crossover.cross(selectedSet);			// 2. CROSSOVER.
			mutation.mutate(newIndividuals);									// 3. MUTATION.	 
			replacement.replace(currentPopulation, newIndividuals);				// 4. REPLACEMENT. NOTE: This function is responsible for updating the information about the best individual. 
			
			fitCalls += newIndividuals.length;
			currentPopulation.computeUnivariateFrequencies();
			currentPopulation.computeAvgFitness();					
			nGen++;					
			
			SGAPress.printCurrentInfo(nGen, currentPopulation);
		}
		SGAPress.printRunFinalStats(nGen, currentPopulation);
		
		return Stopper.foundOptimum()? 1 : 0;
	}
}







