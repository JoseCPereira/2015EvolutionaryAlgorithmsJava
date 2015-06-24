package com.UMDA;

//////////////////////////////////////////////
//
//This class implements the ECGA solver.
//
//////////////////////////////////////////////


public class UMDASolver{	
	
	public static int                    N;			 			// Population size. Default = 500.
	public static long            fitCalls;						// Number of fitness calls. This is incremented by Problem.computeFitness(...).
	public static Problem          problem;			 			// NOTE: Use Parameter.initializeProblem() to generate an appropriate problem instance.
	
	public static Selection      selection;		 				// NOTE: Use Parameter.initializeSelector() to generate the chosen selector type.
	public static UniModel        uniModel;						// NOTE: Use initializeMPModel() to initialize the chosen bayesian network generator.
	public static IReplacement replacement; 					// NOTE: Use Parameter.initializeReplacement() to generate the chosen replacement type.
	
	
	public UMDASolver(String paramFile){
		UMDAParameter.initializeParameters(paramFile);  		// Initialize and validate hBOA parameters
		problem     = UMDAParameter.initializeProblem();		// Design Pattern Strategy
		selection   = UMDAParameter.initializeSelection(N);
		uniModel 	= UMDAParameter.initializeUniModel(N);
		replacement = UMDAParameter.initializeReplacement();
		UMDAPress.initializePress();					
	}
		
	public int solve(int nRun){
		int nGen = 0;																 	// Initialize number of generations.
		fitCalls = 0;																 	// Initialize number of fitness calls.		
		Population currentPopulation = new RandomPopulation(N);						 	// Initial random population. Responsible for computing individual fitnesses and statistics.
		
		while(!Stopper.criteria(nGen, currentPopulation)){			
			SelectedSet selectedSet = selection.select(currentPopulation);			 	// 1. SELECTION.
			Individual[] newIndividuals = uniModel.sampleNewIndividuals(selectedSet);	// 2. SAMPLING with UniFrequencies.					
			replacement.replace(currentPopulation, newIndividuals);				 	 	// 3. REPLACEMENT. Responsible for updating information about the best individual.
			
			fitCalls += newIndividuals.length;
			currentPopulation.computeUnivariateFrequencies();
			currentPopulation.computeAvgFitness();					
			nGen++;					
			
			UMDAPress.printCurrentInfo(nGen, currentPopulation);
		}
		UMDAPress.printRunFinalStats(nGen, currentPopulation);
		
		return Stopper.foundOptimum()? 1 : 0;
	}		
}








