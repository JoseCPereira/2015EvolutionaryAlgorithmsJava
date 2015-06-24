package com.HBOA;

import java.util.ArrayList;
import java.util.HashSet;


interface IGraph{
	public void setParent(Variable parent, int side);	
}

class Variable implements IGraph{
	private int variable;
	private IGraph zero, one;
	
	
	public Variable(int x){variable = x;}  								// Default constructor
	public Variable(int x, IGraph zero, IGraph one){
		variable = x;
		if(zero instanceof Leaf)          								// A variable is responsible for setParent()  
			zero.setParent(this,0);       								// to both of its leaves.
		if(one instanceof Leaf)
			one.setParent(this,1);
		this.zero = zero;
		this.one = one;
	}
	
	public int getVariable(){return variable;}
	public IGraph getZero(){return zero;}
	public IGraph getOne(){return one;}
	public void setZero(IGraph zero){this.zero = zero;}
	public void setOne(IGraph one){this.one = one;}
	public void setParent(Variable parent, int side){} 					// No need to store a variable's parent.
		
	public String toString(){return "(X" + variable + " ("+ zero + ")" + " (" + one +"))";}
}

class Leaf implements IGraph{
	private int depth, 
				side,
				mZero, mOne;
	private Variable parent;
	private int[][]  possibleSplitFrequencies = new int[4][Problem.n]; 	// NOTE: 0 -> m00; 1 -> m01; 2 -> m10; 3 -> m11
	private double[] scoreGains = new double[Problem.n];
	private int		 bestSplit;
	private double   bestSplitScoreGain;
												 				  	  								 
	public Leaf(){}  // Default constructor
	public Leaf(int depth, int side, int mZero, int mOne){	 
		this.depth = depth;									
		this.side  = side;
		this.mZero = mZero;
		this.mOne  = mOne;
		this.bestSplitScoreGain = Double.NEGATIVE_INFINITY;
	}

	public int 									  getDepth(){return depth;}
	public int 									   getSide(){return side;}
	public int 									  getMZero(){return mZero;}
	public int 									   getMOne(){return mOne;}
	public int[][] 			   getPossibleSplitFrequencies(){return possibleSplitFrequencies;}
	public int getPossibleSplitFrequency(int row, int split){return possibleSplitFrequencies[row][split];}
	public Variable 							 getParent(){return parent;}
	public double[] 						 getScoreGains(){return scoreGains;}
	public double 						 getScoreGain(int k){return scoreGains[k];}
	public int								  getBestSplit(){return bestSplit;}
	public double 					 getBestSplitScoreGain(){return bestSplitScoreGain;}

	public void			  addPossibleSplitFrequency(int x, int y){this.possibleSplitFrequencies[x][y]++;}
	public void setPossibleSplitFrequency(int x, int y, int freq){this.possibleSplitFrequencies[x][y] = freq;}
	public void 			setScoreGain(int k, double scoreGain){this.scoreGains[k] = scoreGain;}
	public void 							  setBestSplit(int k){this.bestSplit = k;}
	public void 		  setBestSplitScoreGain(double bestScore){this.bestSplitScoreGain = bestScore;}
	
	public void setParent(Variable parent, int side){
		this.parent = parent;
		this.side = side;
	}
	
	public void updateBestSplit(int k, double scoreGain){				// Responsible for updating the value of the best split score gain in this leaf.
		if(scoreGain > bestSplitScoreGain){		
			this.bestSplit 			= k;
			this.bestSplitScoreGain = scoreGain;
		}
	}
	
	public void resetBestSplit(HashSet<Integer> splitList){
		this.bestSplitScoreGain = Double.NEGATIVE_INFINITY;
		for(int s: splitList){
			double scoreGain = scoreGains[s];
			if(scoreGain > bestSplitScoreGain){
				this.bestSplit 			= s;
				this.bestSplitScoreGain = scoreGain;
			}
		}
	}
			
	public String toString(){
		String str = "";
		for(int i = 0; i < Problem.n; i++)
			str += scoreGains[i] + ",";										// Print the scoreOrders array.
		str = str.substring(0, str.length()-1);  							// Remove the last comma.
		return "[d = " + depth + "; s = " + side + "; m0 = " + mZero + 
				"; m1= " + mOne + "]" + "; scoreGains=[" + str + "]";
	}
}


class DecisionGraph{
	private IGraph graph;
	private ArrayList<Leaf> leafs = new ArrayList<Leaf>();
	
	private int    bestLeafPos;												// Position of the leaf that contains the best score gain for this decision graph.
	private double bestLeafScoreGain;										// Best score gain among all possible splits for this decision graph.
	
	public IGraph 			  getGraph(){return graph;}
	public ArrayList<Leaf> 	  getLeafs(){return leafs;}
	public Leaf 	   	  getLeaf(int j){return leafs.get(j);}
	public Leaf			   getBestLeaf(){return leafs.get(bestLeafPos);}
	public int 			getBestLeafPos(){return bestLeafPos;}
	public double getBestLeafScoreGain(){return bestLeafScoreGain;}
	
	public void 					 setBestLeafPos(int j){this.bestLeafPos = j;}
	public void setBestLeafScoreGain(double bestScoreGain){this.bestLeafScoreGain = bestScoreGain;}
	
	public void updateBestLeaf(){
		bestLeafScoreGain = Double.NEGATIVE_INFINITY;						// Reset the value of best leaf score gain.
		for(int j = 0; j < leafs.size(); j++){								// Recompute the value of best leaf score gain.
			double scoreGain = leafs.get(j).getBestSplitScoreGain();
			if(scoreGain > bestLeafScoreGain){
				this.bestLeafPos 	   = j;
				this.bestLeafScoreGain = scoreGain;
			}
		}
	}
	
	public DecisionGraph(){}         										// Empty constructor.
	public DecisionGraph(Leaf leaf){  										// A DecisionGraph always starts with a single 
		graph = leaf;                 										// leaf and it evolves with each splitLeaf().
		leafs.add(leaf);
//		bestLeafPos = 0;													// Initially each graph as a single leaf, so that's the one with the best split.
//		bestLeafScoreGain = leaf.getBestSplitScoreGain();					// The best score gain was already computed by initializeBN(...)@BayesianNetWork.java.
	}

	public void splitBestLeaf(int j, int split){
		Leaf oldLeaf = leafs.get(j);
		int depth = oldLeaf.getDepth() + 1;
		int m00 = oldLeaf.getPossibleSplitFrequency(0, split);
		int m01 = oldLeaf.getPossibleSplitFrequency(1, split);
		int m10 = oldLeaf.getPossibleSplitFrequency(2, split);
		int m11 = oldLeaf.getPossibleSplitFrequency(3, split);
		Leaf leaf0 = new Leaf(depth, 0, m00, m10);
		Leaf leaf1 = new Leaf(depth, 1, m01, m11);
		IGraph newSplit = new Variable(split,leaf0,leaf1);					// NOTE: The Variable constructor is responsible for setting the leaf's parent.
		if(oldLeaf.getParent() == null) 						
			graph = newSplit;
		else if(oldLeaf.getSide() == 0)
			oldLeaf.getParent().setZero(newSplit);
			else
				oldLeaf.getParent().setOne(newSplit);
		leafs.set(j, leaf0);    											// Replace old leaf with leaf zero.
		leafs.add(j+1,leaf1);   											// Leaf one is added next to leaf zero. 
	}	
	
	public void generateAllele(char[] indiv, int Xi){
		double prob = getCondProb(indiv);
		indiv[Xi] = (HBOA.random.nextDouble() <= prob) ? '1' : '0';
	}
	
	private double getCondProb(char[] indiv){
		IGraph iterator = graph;
		while(iterator instanceof Variable){
			int x = ((Variable)iterator).getVariable();
			if(indiv[x] == '0')
				iterator = ((Variable)iterator).getZero();
			else
				iterator = ((Variable)iterator).getOne();
		}
		int m0 = ((Leaf)iterator).getMZero();
		int m1 = ((Leaf)iterator).getMOne();
		return ((double)m1)/((double)(m0 + m1));
	}

	public String toString(){return graph + "\n";}
}







