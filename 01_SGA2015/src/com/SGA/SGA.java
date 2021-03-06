package com.SGA;

import java.util.Random;


public class SGA{
	
	private static String paramFile;
	public static int         nRuns;					// Number of runs to perform with the same problem.
	public static int	   nSuccess = 0;	 		 	// Number of successful runs;
	
	public static Random     random = new Random();		// Random global object. Use this to generate any random sequence in any file.	
	
	
	public static void main(String[] args){
		//rand.setSeed(654321);						 	// Fixing the seed will also fix the random generator.
		 											 	// sequence generated by Random class. This is for testing only!
		paramFile = args[0];						 	// Check Run->RunConfigurations...-> Arguments to set the Parameter File name.  
		SGASolver sGA = new SGASolver(paramFile); 	 	// Initialize the hBOA engine and Problem 'problem'.
		SGAPress.printInitialInfo();
		for(int r = 0; r < SGA.nRuns; r++){			 	// Perform 'nRuns' for the same problem. Each run creates its own output file.
			SGAPress.printRunInitialInfo(r);
			nSuccess += sGA.solve(r);					// Solve Problem 'problem'.
			SGAPress.printRunFinalInfo(r);
		}
		SGAPress.printFinalInfo();
	}
}


