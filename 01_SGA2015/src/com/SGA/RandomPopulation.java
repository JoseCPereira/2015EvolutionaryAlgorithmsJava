package com.SGA;


public class RandomPopulation extends Population{

	public RandomPopulation(int otherN){
		super(otherN);								// Generate empty population of size EASolver.N
		for(int i = 0; i < N; i++)	
			for(int j = 0; j < Problem.n; j++)
				if (SGA.random.nextDouble() < 0.5)
					individuals[i].setAllele(j, '1');
				else
					individuals[i].setAllele(j, '0');
		this.computeFitnessValues();
		this.computeUnivariateFrequencies();
	}

}// END: class HBOARandomPopulation