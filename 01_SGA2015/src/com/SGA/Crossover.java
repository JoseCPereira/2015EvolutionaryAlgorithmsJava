package com.SGA;

import java.util.TreeSet;


abstract class Crossover{
	protected double pCrossover;
	abstract public Individual[] cross(Population selectedSet);
}

class NPointCrossover extends Crossover{
	private int nCrossover; 													// Number of cross points. NOTE: nCrossover = 1, 2, ..., n-1
	
	public NPointCrossover(double pCrossover, int nCrossover){
		this.pCrossover = pCrossover;
		this.nCrossover = nCrossover;
	}
	
	public Individual[] cross(Population selectedSet){
		int NS = selectedSet.getN();
		Individual[] newIndividuals = new Individual[NS];
		for(int i = 0; i < NS;i += 2){											// NOTE: SelectedSet size should be even.
			Individual indiv1 = selectedSet.getIndividualCopy(i);
			Individual indiv2 = selectedSet.getIndividualCopy(i+1);
			if(SGA.random.nextDouble() < pCrossover){							// Perform NPointCrossover with probability pCrossover.
				TreeSet<Integer> crossPoints = new TreeSet<Integer>();
				
				do crossPoints.add(SGA.random.nextInt(Problem.n));				// Choose exactly nCrossover cross points.
				while(crossPoints.size() < nCrossover);
				if(nCrossover%2 != 0)											// If the number of cross points is odd then Problem.n is the "last" cross point.
					crossPoints.add(Problem.n);
			
				while(!crossPoints.isEmpty()){									// Perform the swapping for all positions between Kodd - Keven.
					int k1 = crossPoints.pollFirst(),
						k2 = crossPoints.pollFirst();
					for(int k = k1; k < k2; k++){
						char allele = indiv1.getAllele(k);
						indiv1.setAllele(k, indiv2.getAllele(k));
						indiv2.setAllele(k, allele);
					}
				}
			}// END: if
			newIndividuals[i] = indiv1;		
			newIndividuals[i+1] = indiv2;
		}// END: for
		return newIndividuals;
	}// END: cross(...)
	
	public String toString(){
		return "NPOINT-CROSSOVER: pCrossover = " + this.pCrossover + " : nCrossover = " + this.nCrossover;
	}
}

class UniformCrossover extends Crossover{
	private double pSwap;
	
	public UniformCrossover(double pCrossover, double pSwap){
		this.pCrossover = pCrossover;
		this.pSwap = pSwap;
	}
	
	public Individual[] cross(Population selectedSet){
		int NS = selectedSet.getN();
		Individual[] newIndividuals = new Individual[NS];
		for(int i = 0; i < NS-1; i += 2){										// NOTE: SelectedSet size should be even!
			Individual indiv1 = selectedSet.getIndividualCopy(i),
					   indiv2 = selectedSet.getIndividualCopy(i+1);
			if(SGA.random.nextDouble() < pCrossover)
				for(int j = 0; j < Problem.n; j++)	
					if(SGA.random.nextDouble() < pSwap){						// Perform swapping of every position with probability pSwap.
						char allele = indiv1.getAllele(j);
						indiv1.setAllele(j, indiv2.getAllele(j));
						indiv2.setAllele(j, allele);
					}
			newIndividuals[i] = indiv1;
			newIndividuals[i+1] = indiv2;
		}
		return newIndividuals;
	}
	
	public String toString(){
		return "UNIFORM-CROSSOVER: pCrossover = " + this.pCrossover + " : pSwap = " + this.pSwap;
	}
}







