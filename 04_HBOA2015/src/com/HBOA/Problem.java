package com.HBOA;

///////////////////////////////////////////////////////////////////
// DESIGN PATTERN STRATEGY
// Each particular problem must implement interface IProblem.
// To generate an instance of problem IsingSpin (for example) use
//
//		Problem problem = new Problem(new IsingSpin(), 3);
//
// where the second argument (3 in this case) is the
// string size of an individual.
///////////////////////////////////////////////////////////////////


interface IProblem{
	public float computeFitness(Individual individual);
	public String toString();
}
 
public class Problem{
	private IProblem      problem;
	public static int     n;				// Allele size. No default, it's problem dependent.
	public static float   optimumValue;		// Best fitness. No default, it's problem dependent.
	public static String  problemName;
	private static double sigma;			// Standard deviation of noise. sigma = sigmaK * sqrt(n), where sigmaK in ]0,1]. 
											// See Problem constructor and check '2009UMDA_Pelikan.pdf'.
	
	public Problem(IProblem problem, int stringSize, float optValue, double sigmaK){
		this.problem         = problem;
		Problem.n            = stringSize;
		Problem.optimumValue = optValue;
		Problem.sigma        = Math.sqrt((double)(sigmaK*stringSize));
		Problem.problemName  = (sigma == 0)? problem.toString() : "NOISY-" + problem.toString();
	}
	
	public float computeFitness(Individual individual){
		HBOASolver.fitCalls++;
		return (float)(this.problem.computeFitness(individual) +
				sigma*HBOA.random.nextGaussian());
	}
	
	public static boolean validateXSize(Individual individual){
		return (individual.getIndividual().length == n) ? true : false;
	}
	
	public String toString(){
		return "  Solving: " + problemName +"\n        n: " + n + "\n  Optimum: " + optimumValue;
	}
}


//################## ZERO PROBLEMS ZERO PROBLEMS ZERO PROBLEMS ZERO PROBLEMS ##################

//###### 0 --> ZERO MAX PROBLEM ######
class ZeroMax implements IProblem{
	public float computeFitness(Individual individual){
		int fit = 0;
		for(int i = 0; i < Problem.n; i++)
			if(individual.getAllele(i) == '0')
				fit++;
		return (float)fit;	
	}
	public String toString(){
		return "ZERO MAX";
	}
}


//###### 1 --> ZERO QUADRATIC PROBLEM ######
class ZeroQuadratic implements IProblem{
	public float computeFitness(Individual individual){
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			char a = individual.getAllele(i++);
			char b = individual.getAllele(i++);
			if(a == '1' && b == '1')
				fit += 0.9;
			if(a == '0' && b == '0')
				fit += 1;
		}
		return fit;
	}
	public String toString(){
		return "ZERO QUADRATIC";
	}
}


//###### 2 --> ZERO 3-DECEPTIVE PROBLEM ######
class ZeroThreeDeceptive implements IProblem{
	public float computeFitness(Individual individual){
		int three;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			three  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			switch(three){
				case 0: fit += 1; break;
				case 2: fit += 0.8; break;
				case 3: fit += 0.9; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "ZERO 3-DECEPTIVE";
	}
}


//###### 3 --> ZERO 3-DECEPTIVE BIPOLAR PROBLEM ######
class ZeroThreeDeceptiveBiPolar implements IProblem{	
	public float computeFitness(Individual individual){
		int six;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			six  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six = Math.abs(six - 3);
			switch(six){
				case 0: fit += 0.9; break;
				case 1: fit += 0.8; break;
				case 3: fit += 1; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "ZERO 3-DECEPTIVE BIPOLAR";
	}
}



//###### 4 --> ZERO 3-DECEPTIVE OVERLAPPING PROBLEM ######
class ZeroThreeDeceptiveOverlapping implements IProblem{
	public float computeFitness(Individual individual){
		int three; 
		float fit = 0;
		for(int i = 0; i < Problem.n-1;){ //Empty increment
			three  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i)));
			switch(three){
				case 0: fit += 1; break;
				case 2: fit += 0.8; break;
				case 3: fit += 0.9; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "ZERO 3-DECEPTIVE OVERLAPPING";
	}
}


//###### 5 --> ZERO TRAP-K PROBLEM ######
class ZeroTrapK implements IProblem{
	private int kay;
	public ZeroTrapK(int kay){this.kay = kay;}

	public float computeFitness(Individual individual){
		int k;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			k = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			for(int j = 1; j < kay; j++)
				k += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			if(k == 0)
				fit += kay;
			else
				fit += k - 1;
		}
		return fit;
	}
	public String toString(){
		return "ZERO TRAP-" + kay;
	}
}


//###### 6 --> ZERO UNIFORM 6-BLOCKS PROBLEM ######
class ZeroUniformSixBlocks implements IProblem{		
	public float computeFitness(Individual individual){
		for(int i = 0; i < Problem.n;){  //Empty increment
			char c = individual.getAllele(i++);
			for(int j = 1; j < 6; j++)
				if(individual.getAllele(i++) != c)
					return (float)0;
		}
		return (float)1;
	}
	public String toString(){
		return "ZERO UNIFORM6-BLOCKS";
	}
}



//################## ONE PROBLEMS ONE PROBLEMS ONE PROBLEMS ONE PROBLEMS ##################

//###### 10 --> ONEMAX PROBLEM ######
class OneMax implements IProblem{	
	public float computeFitness(Individual individual){
		int fit = 0;
		for(int i = 0; i < Problem.n; i++)
			if(individual.getAllele(i) == '1')
				fit++;
		return (float)fit;
	}
	public String toString(){
		return "ONEMAX";
	}
}


// ###### 11 --> QUADRATIC PROBLEM ######
class Quadratic implements IProblem{
	public float computeFitness(Individual individual){
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			char a = individual.getAllele(i++);
			char b = individual.getAllele(i++);
			if(a == '1' && b == '1')
				fit += 1;
			if(a == '0' && b == '0')
				fit += 0.9;
		}
		return fit;
	}
	public String toString(){
		return "QUADRATIC";
	}
}


// ###### 12 --> 3-DECEPTIVE PROBLEM ######
class ThreeDeceptive implements IProblem{
	public float computeFitness(Individual individual){
		int three;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			three  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			switch(three){
				case 0: fit += 0.9; break;
				case 1: fit += 0.8; break;
				case 3: fit += 1; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "3-DECEPTIVE";
	}
}


// ###### 13 --> 3-DECEPTIVE BIPOLAR PROBLEM ######
class ThreeDeceptiveBiPolar implements IProblem{
	public float computeFitness(Individual individual){
		int six;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			six  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			six = Math.abs(six - 3);
			switch(six){
				case 0: fit += 0.9; break;
				case 1: fit += 0.8; break;
				case 3: fit += 1; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "3-DECEPTIVE BIPOLAR";
	}
}


// ###### 14 --> 3-DECEPTIVE OVERLAPPING PROBLEM ######
class ThreeDeceptiveOverlapping implements IProblem{
	public float computeFitness(Individual individual){
		int three; 
		float fit = 0;
		for(int i = 0; i < Problem.n-1;){ //Empty increment
			three  = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			three += Integer.parseInt(String.valueOf(individual.getAllele(i)));
			switch(three){
				case 0: fit += 0.9; break;
				case 1: fit += 0.8; break;
				case 3: fit += 1; break;
				default: break;
			}
		}
		return fit;
	}
	public String toString(){
		return "3-DECEPTIVE OVERLAPPING";
	}
}


//###### 15 --> TRAP-K PROBLEM ######
class TrapK implements IProblem{
	private int kay;
	public TrapK(int kay){this.kay = kay;}

	public float computeFitness(Individual individual){
		int k;
		float fit = 0;
		for(int i = 0; i < Problem.n;){ //Empty increment
			k = Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			for(int j = 1; j < kay; j++)
				k += Integer.parseInt(String.valueOf(individual.getAllele(i++)));
			if(k < kay)
				fit += kay-1 - k;
			else
				fit += kay;
		}
		return fit;
	}
	public String toString(){
		return "TRAP-" + kay;
	}
}


// ###### 16 --> UNIFORM 6-BLOCKS PROBLEM ######
class UniformSixBlocks implements IProblem{
	public float computeFitness(Individual individual){
		for(int i = 0; i < Problem.n;){  // Empty increment
			char c = individual.getAllele(i++);
			for(int j = 1; j < 6; j++)
				if(individual.getAllele(i++) != c)
					return (float)0;
		}
		return (float)1;
	}
	public String toString(){
		return "UNIFORM 6-BLOCKS";
	}
}


//################## OTHER PROBLEMS OTHER PROBLEMS OTHER PROBLEMS OTHER PROBLEMS ##################

// ###### 21 --> HIERARCHICAL TRAP ONE PROBLEM ######
// Check Pelikan, Martin, "Hierarchical Bayesian Optimization Algorithm", pp. 88 - 103
// NOTES: k = 3; 
//		  All lower levels => flow = fhigh = 1;
//		    	 Top level => flow = .9; fhigh = 1;
// 		  Consider implementation with general values of k, flow, fhigh.
//////////////////////////////////////////////////////////////////////////////////////

class HierarchicalTrapOne implements IProblem{
	
	public float computeFitness(Individual individual){
		float fitness = 0;
		int three = 99;									  				// Bogus value. Just to initialize.
		char[] levelString = individual.copyIndividual(); 
		int levelSize = Problem.n;						
		float levelFit;
		
		float flow = 1,													// Parameters for trap at all levels except the top one.
			  fhigh = 1;
		float topFlow = (float)0.9,										// Parameters for topTrap
			  topFhigh = 1;
		
		while(levelSize > 3){											// All levels except the top one.
			levelFit = 0;												// Fitness contribution of each level.
			for(int i = 0; i < levelSize;){	 							// Empty increment
				three  = Integer.parseInt(String.valueOf(levelString[i++]));
				three  += Integer.parseInt(String.valueOf(levelString[i++]));
				three  += Integer.parseInt(String.valueOf(levelString[i++]));
				if(three == 3){							 
					levelFit += fhigh;									// fhigh = 1.
					levelString[i/3-1] = '1';							// '111' -> 1. NOTE: rewriting over the leftmost part of levelString. 
				}
				else if(three == 0){
					levelFit += flow;									// flow = 1.
					levelString[i/3-1] = '0';							// '000' -> 0. NOTE: rewriting over the leftmost part of levelString.
				}
				else if(three < 3){
					levelFit += flow - ((float)three)*flow/((float)2);	// flow - u*flow/(k-1).
					levelString[i/3-1] = '8';							// anything else -> 8 (NULL symbol). NOTE: rewriting over the leftmost part of levelString.
				}
				else if(three > 3){
					// levelFit += 0									// NULL symbol present.
					levelString[i/3-1] = '8';							// anything else -> 8 (NULL symbol). NOTE: rewriting over the leftmost part of levelString.
				}
			}
			levelSize /= 3;							 					// Next levelSize. Each 3-string is collapsed in to a single symbol.
			fitness += levelFit*Problem.n/levelSize; 					// Each level contribution is multiplied by the factor: 3^level = stringSize/(next levelSize). 
		}
		
		levelFit = 0;													// We are at the top level. No need for mapping. Use topTrap as the contribution function.
		for(int i = 0; i < 3;){	
			three  = Integer.parseInt(String.valueOf(levelString[i++]));
			three  += Integer.parseInt(String.valueOf(levelString[i++]));
			three  += Integer.parseInt(String.valueOf(levelString[i++]));
		}
		if(three == 3)						 
			levelFit += topFhigh;										// fhigh = 1.
		else if(three == 0)
			levelFit += topFlow;										// flow = .9.
		else if(three < 3)
			levelFit += topFlow - ((float)three)*topFlow/((float)2);	// flow - u*flow/(k-1).
																		// else NULL symbol present, do nothing.
		fitness += levelFit*Problem.n;									// At the top level the contribution factor is
		return fitness; 												// 3^nLevels = stringSize.
	}																	// END: computeFitness(...)
	public String toString(){
		return "HIERARCHICAL TRAP ONE";
	}
} 																		// END: HierarchicalTrapOne


//###### 22 --> HIERARCHICAL TRAP TWO PROBLEM ######
//Check Pelikan, Martin, "Hierarchical Bayesian Optimization Algorithm", pp. 88 - 103
//NOTES: k = 3; 
//		  All lower levels => flow = 1 + 0.1/l; fhigh = 1;
//		    	 Top level => flow = .9; fhigh = 1;
//		  Consider implementation with general values of k, flow, fhigh.
//////////////////////////////////////////////////////////////////////////////////////

class HierarchicalTrapTwo implements IProblem{
	
	public float computeFitness(Individual individual){
		float fitness = 0;
		int three = 99;									  				// Bogus value. Just to initialize.
		char[] levelString = individual.copyIndividual(); 
		int levelSize = Problem.n;
		int nLevels = (int)(Math.log(levelSize)/Math.log(3));
		float levelFit;
		
		float flow = (float)1 + (float)0.1/(float)nLevels,				// Parameters for trap at all levels except the top one.
			  fhigh = 1;
		float topFlow = (float)0.9,										// Parameters for topTrap
			  topFhigh = 1;
				
		while(levelSize > 3){											// All levels except the top one.
			levelFit = 0;												// Fitness contribution of each level.
			for(int i = 0; i < levelSize;){	 							// Empty increment
				three  = Integer.parseInt(String.valueOf(levelString[i++]));
				three  += Integer.parseInt(String.valueOf(levelString[i++]));
				three  += Integer.parseInt(String.valueOf(levelString[i++]));
				if(three == 3){							 
					levelFit += fhigh;									// fhigh = 1.
					levelString[i/3-1] = '1';							// '111' -> 1. NOTE: rewriting over the leftmost part of levelString. 
				}
				else if(three == 0){
					levelFit += flow;									// flow = 1 + 0.1/l.
					levelString[i/3-1] = '0';							// '000' -> 0. NOTE: rewriting over the leftmost part of levelString.
				}
				else if(three < 3){
					levelFit += flow - ((float)three)*flow/((float)2);	// flow - u*flow/(k-1).
					levelString[i/3-1] = '8';							// anything else -> 8 (NULL symbol). NOTE: rewriting over the leftmost part of levelString.
				}
				else if(three > 3){
					// levelFit += 0									// NULL symbol present.
					levelString[i/3-1] = '8';							// anything else -> 8 (NULL symbol). NOTE: rewriting over the leftmost part of levelString.
				}
			}
			levelSize /= 3;							 					// Next levelSize. Each 3-string is collapsed in to a single symbol.
			fitness += levelFit*Problem.n/levelSize; 					// Each level contribution is multiplied by the factor: 3^level = stringSize/(next levelSize). 
		}
		
		levelFit = 0;													// We are at the top level. No need for mapping. Use topTrap as the contribution function.
		for(int i = 0; i < 3;){	
			three  = Integer.parseInt(String.valueOf(levelString[i++]));
			three  += Integer.parseInt(String.valueOf(levelString[i++]));
			three  += Integer.parseInt(String.valueOf(levelString[i++]));
		}
		if(three == 3)						 
			levelFit += topFhigh;										// topFhigh = 1.
		else if(three == 0)
			levelFit += topFlow;										// topFlow = .9.
		else if(three < 3)
			levelFit += topFlow - ((float)three)*topFlow/((float)2);	// flow - u*flow/(k-1).
																		// else NULL symbol present, do nothing.
		fitness += levelFit*Problem.n;									// At the top level the contribution factor is
		return fitness; 												// 3^nLevels = stringSize.
	}																	// END: computeFitness(...)
	public String toString(){
		return "HIERARCHICAL TRAP TWO";
	}
} 																		// END: HierarchicalTrapTwo



//###### 30 --> ISING SPIN GLASSES PROBLEM ######
class IsingSpin implements IProblem{
	public float computeFitness(Individual individual){
		float fit = 0; 
		// TODO
		return fit;
	}
	public String toString(){
		return "ISING SPIN GLASSES";
	}
}























