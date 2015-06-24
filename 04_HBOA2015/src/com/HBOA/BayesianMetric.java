package com.HBOA;

import java.lang.Math;
import java.util.ArrayList;


abstract class IBayesianMetric {
	protected int NS;
	private static double logBase2 = ((double)1)/Math.log(2);
	
	public double computeScoreGain(int mZero, int mOne, int m00, int m01, int m10, int m11){
		double l0 = computeLeafGain(m00, m10); 
		double l1 = computeLeafGain(m01, m11);
		double l = computeLeafGain(mZero, mOne);
		return logBase2*(l0 + l1 - l - 0.5*Math.log(NS));
	}
	
	abstract protected double computeLeafGain(int m, int mX);
}

class BDMetric extends IBayesianMetric{
	// Precomputed logarithm list -> 0, ln(1), ln(2),..., ln(NS+1).
	// Use this list to compute the score gain for each leaf.
	private static ArrayList<Double> preSumLogs = new ArrayList<Double>();		// Precomputed logarithm list -> 0, ln(1), ln(2),..., ln(NS+1).
																				// Use this list to compute the score gain for each leaf.
	
	BDMetric(int NS){
		this.NS = NS;
		setPreSumLogs();
	}
	
	public void setPreSumLogs(){												// Precompute the logarithm list -> 0, ln(1), ln(1)+ln(2),..., ln(1)+ln(2)+...+ln(NS+1).
		int preSize = preSumLogs.size();		
		double preSumLog;
		if(preSize == 0)														// If this is the first time computing SumLogs, add a first element, just to align the position with the last logarithm.
			preSumLogs.add(preSize++, (double)0);	
		preSumLog = preSumLogs.get(preSize-1);									// Get the last computed SumLog.
		for(int i = preSize; i <= this.NS+1; i++){								// Compute only the new SumLogs.
			preSumLog += Math.log(i);
			preSumLogs.add(preSumLog);
		}
	}
	
	protected double computeLeafGain(int m0, int m1){
		return preSumLogs.get(m0) + preSumLogs.get(m1) - preSumLogs.get(m0+m1+1);
	}
	
	public String toString(){
		return "BAYESIAN-DIRICHELET METRIC";
	}
}


class BICMetric extends IBayesianMetric{
	
	// DEPRECATED: public BICMetric(int NS){this.NS = NS;
	
	protected double computeLeafGain(int m, int mX){
		if(m == mX || mX == 0) return 0; 										// NOTE: The case m = 0 is always included because 0 ² mX ² m, and so m = 0 => mX = 0. The reverse is not necessarily true.
		double freq = ((double)mX)/((double)m); 								// NOTE: The return value 0 is limit-based! Is this correct, even for m = mX = 0 ?
		return (double)((m-mX)*Math.log(1-freq) + mX*Math.log(freq));
	}
	
	public String toString(){
		return "BAYESIAN INFORMATION CRITERION";
	}
}


