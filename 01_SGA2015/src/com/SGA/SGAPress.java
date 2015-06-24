package com.SGA;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SGAPress{
	
	private static String         testFileName;							// This file stores all the information that is also printed in the console during an entire run.
	private static FileWriter     fstreamTest;
	private static BufferedWriter testFileOut;
	
	private static String         testFileNameStats;					// This file stores only the statistics necessary to generate graphics.
	private static BufferedWriter testFileOutStats;
	
	
	public static void initializePress(){
		testFileName      = "SGA_N" + SGASolver.N + "_"  + Problem.problemName + "_n" + Problem.n + ".txt";
		testFileNameStats = "SGA-STATS_N" + SGASolver.N + "_"  + Problem.problemName + "_n" + Problem.n + ".txt";
		try{
			fstreamTest 	 = new FileWriter(testFileName);			// 'true' => Append to file.
			testFileOut 	 = new BufferedWriter(fstreamTest);
			
			fstreamTest		 = new FileWriter(testFileNameStats);		// 'true' => Append to file.
			testFileOutStats = new BufferedWriter(fstreamTest);
		}catch(Exception e){System.err.println("ERROR: " + e.getMessage());}
	}
	
	
	private static void printString(String str){						// NOTE: Use this method to print simultaneous in 
		System.out.println(str);										//	 	 the console and in the testFileOut.
		try{	
			testFileOut.write("\n" + str);
		}catch(Exception e){System.err.println("ERROR: " + e.getMessage());} 
	}
	
	private static void printStats(String str){							// NOTE: Use this method to print in the STATS file.										//	 	 the console and in the testFileOut.
		try{	
			testFileOutStats.write(str + "\n");
		}catch(Exception e){System.err.println("ERROR: " + e.getMessage());} 
	}
	
	public static void printInitialInfo(){
		String str = "############ - SGA - ############ - SGA - ############ - SGA - ############" + 
					 "\n#" +
					 "\n#   SGA:" +
					 "\n#     ->  Number of Runs = " + SGA.nRuns                  +
					 "\n#     -> Population Size = " + SGASolver.N                +
					 "\n#     ->       Selection = " + SGASolver.selection        +      
					 "\n#     ->       Crossover = " + SGASolver.crossover        +
					 "\n#     ->        Mutation = " + SGASolver.mutation         +
					 "\n#     ->     Replacement = " + SGASolver.replacement      +
					 "\n#" +
					 "\n#   PROBLEM:" +
					 "\n#     ->            Name = " + Problem.problemName        +
					 "\n#     ->     String size = " + Problem.n                  +
					 "\n#" +
					 "\n#   STOPPER:" + 
					 "\n#     ->  maxGenerations = " + Stopper.maxNGen            +
					 "\n#     ->     maxFitCalls = " + Stopper.maxFitCalls        +					 
					 "\n#     -> allFitnessEqual = " + Stopper.allFitnessEqual    +
					 "\n#     ->         epsilon = " + Stopper.epsilon            +
					 "\n#     ->      maxOptimal = " + Stopper.maxOptimal         +
					 "\n#     ->    foundBestFit = " + Stopper.foundBestFit       +
					 "\n#     ->       foundOnes = " + Stopper.foundOnes          +
					 "\n#" +
					 "\n############################################################################\n";
		printString(str);
		
		String stats = "Generation      Pop. Size      Fitness Calls      Avg. Fitness      BestFitness";
		printStats(stats);
	}
	
	
	public static void printRunInitialInfo(int r){
		printString("\n##### START RUN " + (r+1) + "/" + SGA.nRuns + " #####"     + 
			 	      "##### START RUN " + (r+1) + "/" + SGA.nRuns + " #####"     +
			 	      "##### START RUN " + (r+1) + "/" + SGA.nRuns + " #####\n\n" +
			 	      "Generation      Pop. Size      Fitness Calls      Avg. Fitness      BestFitnessSoFar");
	}
	
	
	public static void printCurrentInfo(int nGen, Population population){		
		if(nGen%30 == 0)
			printString("Generation      Pop. Size      Fitness Calls      Avg. Fitness      BestFitnessSoFar");			
		printString(String.format("%6d %16d %16d %18.2f %18.2f",
									nGen, SGASolver.N, SGASolver.fitCalls, population.getAvgFit(), population.getBestFit())	
					);
	}
	
	
	public static void printRunFinalInfo(int r){
		String str = "\n############################################################################" +
	                 "\n#"                                                       +
					 "\n#               Success: " + Stopper.foundOptimum()      + 
					 "\n#  Current Success Rate: " + SGA.nSuccess + "/" + (r+1) +					 
					 "\n# Current Fitness Calls: " + SGASolver.fitCalls         +
					 "\n#"                                                       +
					 "\n######## END RUN " + (r+1) + "/" + SGA.nRuns + " #####"    + 
					      "##### END RUN " + (r+1) + "/" + SGA.nRuns + " #####"    +
					      "##### END RUN " + (r+1) + "/" + SGA.nRuns + " ########\n\n\n";
		printString(str);
	}
	
	public static void printRunFinalStats(int nGen, Population population){
		printStats(String.format("%6d %16d %16d %18.2f %18.2f",
									nGen, SGASolver.N, SGASolver.fitCalls, population.getAvgFit(), population.getBestFit())
					);
	}
	
	public static void printFinalInfo(){
		printString("\nSUCCESS RATE = " + SGA.nSuccess + "/" + SGA.nRuns + "\n");
		closeTestFiles();
	}
	
	private static void closeTestFiles(){		
		try{testFileOut.close();}
		catch(Exception e){System.err.println("ERROR: " + e.getMessage());}
		try{testFileOutStats.close();}
		catch(Exception e){System.err.println("ERROR: " + e.getMessage());}
	}		
	
}













