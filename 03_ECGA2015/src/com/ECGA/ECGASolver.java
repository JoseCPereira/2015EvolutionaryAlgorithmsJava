package com.ECGA;

//////////////////////////////////////////////
//
// This class implements the ECGA solver.
//
//////////////////////////////////////////////


class ECGASolver{	
	
	public static int                    N;			 			// Population size. Default = 500.
	public static long            fitCalls;						// Number of fitness calls. This is incremented by Problem.computeFitness(...).
	public static Problem          problem;			 			// NOTE: Use Parameter.initializeProblem() to generate an appropriate problem instance.
	
	public static Selection      selection;		 				// NOTE: Use Parameter.initializeSelector() to generate the chosen selector type.
	public static MPModel          mPModel;						// NOTE: Use initializeMPModel() to initialize the chosen bayesian network generator.
	public static IReplacement replacement; 					// NOTE: Use Parameter.initializeReplacement() to generate the chosen replacement type.
	
	
	public ECGASolver(String paramFile){
		ECGAParameter.initializeParameters(paramFile);  		// Initialize and validate hBOA parameters
		problem     = ECGAParameter.initializeProblem();		// Design Pattern Strategy
		selection   = ECGAParameter.initializeSelection(N);
		mPModel 	= ECGAParameter.initializeMPModel();
		replacement = ECGAParameter.initializeReplacement();
		ECGAPress.initializePress();					
	}
	
	public int solve(int nRun){
		int nGen = 0;																 // Initialize number of generations.
		fitCalls = 0;																 // Initialize number of fitness calls.		
		Population currentPopulation = new RandomPopulation(N);						 // Initial random population. Responsible for computing individual fitnesses and statistics.
		
		while(!Stopper.criteria(nGen, currentPopulation)){			
			SelectedSet selectedSet = selection.select(currentPopulation);			 // 1. SELECTION.
			mPModel.generateModel(selectedSet);		 								 // 2. GENERATE BAYESIAN NETWORK.			
			Individual[] newIndividuals = mPModel.sampleNewIndividuals(selectedSet); // 3. SAMPLING.		
			replacement.replace(currentPopulation, newIndividuals);				 	 // 4. REPLACEMENT. Responsible for updating information about the best individual.
			
			fitCalls += newIndividuals.length;
			currentPopulation.computeUnivariateFrequencies();
			currentPopulation.computeAvgFitness();					
			nGen++;					
			
			ECGAPress.printCurrentInfo(nGen, currentPopulation);
		}
		ECGAPress.printRunFinalStats(nGen, currentPopulation);
		
		return Stopper.foundOptimum()? 1 : 0;
	}
}








