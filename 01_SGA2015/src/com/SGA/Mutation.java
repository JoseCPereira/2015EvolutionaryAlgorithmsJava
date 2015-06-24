package com.SGA;

class Mutation{
	private double pMutation;											// Probability of mutation;
	
	public Mutation(double pMutation){this.pMutation = pMutation;}
	
	public double getPMutation(){return pMutation;}
	
	public void mutate(Individual[] newIndividuals){
		if(pMutation > 0)												// Perform mutation only if pMutation > 0.
			for(int i = 0; i < newIndividuals.length; i++)
				for(int j = 0; j < Problem.n; j++)
					if(SGA.random.nextDouble() < pMutation){			// Perform mutation in every position with probability pMutation.
						char allele = newIndividuals[i].getAllele(j);
						if(allele == '0')
							newIndividuals[i].setAllele(j, '1');
						else
							newIndividuals[i].setAllele(j, '0');
					}	
	}
	
	public String toString(){
		return "MUTATION: pMutation = " + this.pMutation;
	}
}