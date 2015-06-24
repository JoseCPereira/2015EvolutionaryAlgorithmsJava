package com.HBOA;

import java.util.Arrays;



abstract class Selection{
	public int NS;										// Selection Set size. NOTE: This is initialized and refreshed by Parameter.
	
	public Selection(){}								// Default empty constructor.
	public int getNS(){return NS;}
	abstract SelectedSet select(Population population);
}

class TourWithReplacement extends Selection{
	int tourSize; 										// Default: 2 (binary tournament).
	
	public TourWithReplacement(int NS, int tourSize){	
		this.NS = NS;
		this.tourSize = tourSize;
	}
	
	public SelectedSet select(Population population){
		int N = population.getN();
		int picked, maxPos;
		double maxFit, currentFit;
		SelectedSet selectedSet = new SelectedSet(NS);	// Initialize an empty population of size NS.
		for(int i = 0; i < NS; i++){
			picked = HBOA.random.nextInt(N);			// Random int between 0 and N-1.
			maxFit = population.getFitness(picked);
			maxPos = picked;
			for(int j = 1; j < tourSize; j++){
				picked = HBOA.random.nextInt(N);
				currentFit = population.getFitness(picked);
				if(currentFit > maxFit){
					maxFit = currentFit;
					maxPos = picked;
				}
			}
			selectedSet.setIndividual(i, population.individuals[maxPos], maxFit);
			
		}
		// selectedSet.computeFitnessStatistics();
		selectedSet.computeUnivariateFrequencies();		// NOTE!! Class Subset is responsible for computing all frequencies!
		return selectedSet;
	}// END: select(...)
}// END: class TourWithReplacement.



class TourWithoutReplacement extends Selection{
	int tourSize; 										// Default binary tournament
	
	public TourWithoutReplacement(int NS, int tourSize){	
		this.NS = NS;
		this.tourSize = tourSize;
	}
	
	public SelectedSet select(Population population){
		int N = population.getN();
		SelectedSet selectedSet = new SelectedSet(NS);  // Initialize an empty population.
		int  k = N/tourSize;							// Number of tournaments within each shuffle.
		int ks = NS/k;									// Number of shuffles with exactly 'k' tournaments.
		int rs = NS%k;									// Number of tournaments within the last shuffle. 
		int ls = ks*k;									// Order of the first selection in the last shuffle. We have NS = ls + rs.
		int maxPos = 0;									
		for(int i = 0; i < ks; i++){
			int pos = i*k;								// Position of the next selected individual.
			int[] numbers = shuffle(N);
			for(int j = 0; j < k*tourSize; j += tourSize){
				maxPos = tourSelect(population, selectedSet, numbers, j);
				selectedSet.setIndividual(pos++, population.individuals[maxPos], population.getFitness(maxPos));
			}
		}
		int[] numbers = shuffle(N);
		for(int j = 0; j < rs*tourSize; j += tourSize){
			maxPos = tourSelect(population, selectedSet, numbers, j);
			selectedSet.setIndividual(ls++, population.individuals[maxPos], population.getFitness(maxPos));
		}
		// selectedSet.computeFitnessStatistics();
		selectedSet.computeUnivariateFrequencies();		//NOTE: This is not necessary for SGA!
		return selectedSet;
	}// END: select(..).
	
	private int tourSelect(Population population, SelectedSet selectedSet, int[] numbers, int j){
		int maxPos = numbers[j];
		double maxFit = population.getFitness(maxPos);
		for(int i = j+1; i < j+tourSize; i++){
			int currentPos = numbers[i];
			double currentFit = population.getFitness(currentPos);
			if(currentFit > maxFit){
				maxPos = currentPos;
				maxFit = currentFit;
			}
		}
		return maxPos;
	}
	
	private int[] shuffle(int n){
		int[] numbers = new int[n];
		for(int i = 0; i < n; i++)
			numbers[i] = i;
		for(int i = 1; i < n; i++){
			int r = i + HBOA.random.nextInt(n - i);
			int temp = numbers[i-1];
			numbers[i-1] = numbers[r];
			numbers[r] = temp;
		}
		return numbers;
	}
	
}// END: class TourWithoutReplacement

	
	
class Truncation extends Selection{
	PosFit[] sortedPopulation;
	
	// Use this inner class to sort the current population, in ascending order of fitness.
	class PosFit implements Comparable<Truncation.PosFit>{  
		int position;
		double fitness;
		
		PosFit(int position, double fitness){
			this.position = position;
			this.fitness = fitness;
		}
		
		public int getPosition(){return this.position;}
		public double getFitness(){return this.fitness;}
		public void setPosition(int position){this.position = position;}
		public void setFitness(double fitness){this.fitness = fitness;}
		
		public int compareTo(PosFit posFit){
			if (this.fitness < posFit.fitness)
				return -1;
			if (this.fitness > posFit.fitness)
				return 1;
			return 0;
		}
		
		public String toString(){
			return "(" + position + ";" + fitness + ")";
		}
	}// END: inner class PosFit.
	
	
	public Truncation(int NS){this.NS = NS;}			// Truncation: select the best NS = tau*N individuals.
	
	public SelectedSet select(Population population){
		int N = population.getN();
		sortedPopulation = new PosFit[N];
		for(int i = 0; i < N; i++)
			sortedPopulation[i] = new PosFit(i, population.getFitness(i));
		Arrays.sort(sortedPopulation);					// Sort population in ascending order of fitness.
		SelectedSet selectedSet = new SelectedSet(NS);
		for(int i = 0; i < NS; i++){
			int best = i + N - NS;
			int position = sortedPopulation[best].getPosition();
			double fitness = sortedPopulation[best].getFitness();
			selectedSet.setIndividual(i, population.individuals[position], fitness);
		}
		// selectedSet.computeFitnessStatistics();
		selectedSet.computeUnivariateFrequencies(); 	// NOTE: Class Subset is responsible for computing all frequencies!
		return selectedSet;
	}
}// END: class Truncation.




