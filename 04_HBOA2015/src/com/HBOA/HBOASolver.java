package com.HBOA;

//////////////////////////////////////////////
//
// This class implements the HBOA solver.
//
//////////////////////////////////////////////


class HBOASolver{	
	
	public static int                           N;	 			// Population size. Default = 500.
	public static long                   fitCalls;				// Number of fitness calls. This is incremented by Problem.computeFitness(...).
	public static Problem                 problem;  			// NOTE: Use Parameter.initializeProblem() to generate an appropriate problem instance.
	
	public static Selection             selection;				// NOTE: Use Parameter.initializeSelector() to generate the chosen selector type.
	public static BayesianNetwork bayesianNetwork;				// NOTE: Use initializeBayesianNetwork() to initialize the chosen bayesian network generator.
	public static IReplacement        replacement; 				// NOTE: Use Parameter.initializeReplacement() to generate the chosen replacement type.
	
	
	public HBOASolver(String paramFile){
		HBOAParameter.initializeParameters(paramFile);  		// Initialize and validate hBOA parameters
		problem         = HBOAParameter.initializeProblem();	// Design Pattern Strategy
		selection       = HBOAParameter.initializeSelection(N);
		bayesianNetwork = HBOAParameter.initializeBayesianNetwork(N);
		replacement     = HBOAParameter.initializeReplacement();
		HBOAPress.initializePress();					
	}
	
	public int solve(int nRun){
		int nGen = 0;																 	// Initialize number of generations.
		fitCalls = 0;																 	// Initialize number of fitness calls.		
		Population currentPopulation = new RandomPopulation(N);						 	// Initial random population. Responsible for computing individual fitnesses and statistics.
		
		while(!Stopper.criteria(nGen, currentPopulation)){			
			SelectedSet selectedSet = selection.select(currentPopulation);				// 1. SELECTION.
			bayesianNetwork.generateModel(selectedSet);		 							// 2. GENERATE BAYESIAN NETWORK.			
			Individual[] newIndividuals = bayesianNetwork.sampleNewIndividuals();		// 3. SAMPLING.		
			replacement.replace(currentPopulation, newIndividuals);				 		// 4. REPLACEMENT. Responsible for updating information about the best individual.
			
			fitCalls += newIndividuals.length;
			currentPopulation.computeUnivariateFrequencies();
			currentPopulation.computeAvgFitness();					
			nGen++;					
			
			HBOAPress.printCurrentInfo(nGen, currentPopulation);
		}
		HBOAPress.printRunFinalStats(nGen, currentPopulation);
		
		return Stopper.foundOptimum()? 1 : 0;
	}
}








