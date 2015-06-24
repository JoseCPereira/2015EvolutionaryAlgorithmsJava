package com.UMDA;


class UniModel{
	public int offspringSize;
	
	public UniModel(int offSize){offspringSize = offSize;}	
	
	public Individual[] sampleNewIndividuals(SelectedSet selectedSet){
		Individual[] newIndividuals = new Individual[offspringSize];
		int[] frequencies = selectedSet.getUniFrequencies();
		int NS = selectedSet.getN();
		for(int i = 0; i < offspringSize; i++){
			newIndividuals[i] = new Individual();
			for(int j = 0; j < Problem.n; j++){
				double probJ = ((double)(frequencies[j]))/NS;
				if (UMDA.random.nextDouble() < probJ)
					newIndividuals[i].setAllele(j, '1');
				else
					newIndividuals[i].setAllele(j, '0');
			}
		}
		return newIndividuals;
	}
	
	public String toString(){
		return "UNIVARIATE MARGINAL DISTRIBUTION: offspringSize = " + offspringSize;
	}
	
}