package com.SGA;

public class Individual {
	private char[] individual = new char[Problem.n];
	
	public Individual(){}															// Default constructor	
	public Individual(Individual johnDoe){individual = johnDoe.getIndividual();}	// Non shallow copy of an Individual	
	public Individual(char[] individual){this.individual = individual;}				// Shallow copy of an Individual
	
	public char  getAllele(int j){return individual[j];}
	public char[] getIndividual(){return individual;}
	
	public void setAllele(int j, char c){individual[j] = c;}
	
	public float computeFitness(){return SGASolver.problem.computeFitness(this);}
	
	public char[] copyIndividual(){
		char[] copy = new char[Problem.n];
		for(int i = 0; i < Problem.n; i++){
			char c = individual[i];
			copy[i] = c;
		}
		return copy;
	}	
		
	public int distance(Individual johnDoe){										// Hamming Distance
		char[] thatIndividual = johnDoe.getIndividual();
		int dist = 0;
		for(int i = 0; i < Problem.n; i++)
			if(this.individual[i] != thatIndividual[i])
				dist++;
		return dist;
	}
	
	public boolean isZero(){
		for(int i = 0; i < Problem.n; i++)
			if(individual[i] != '0')
				return false;
		return true;
	}
	
	public boolean isOne(){
		for(int i = 0; i < Problem.n; i++)
			if(individual[i] != '1')
				return false;
		return true;
	}
	
	public String toString(){
		String str = "";
		for(int i = 0; i < Problem.n; i++)
			str += individual[i];
		return str;
	}
}
