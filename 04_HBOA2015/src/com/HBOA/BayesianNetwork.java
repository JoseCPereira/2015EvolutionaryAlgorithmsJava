package com.HBOA;

import java.util.ArrayList;
import java.util.HashSet;


@SuppressWarnings("unchecked")   	 														// Supress warnings for creating an Array of ArrayList.  
class BayesianNetwork{           	 														// Check: B. Eckel, "Thinking in Java", 4th ed., MindView Inc., 2006, [pp. 759-761]
	
	public int 				offspringSize; 													// NOTE: The BN is responsible for sampling new individuals.
	private IBayesianMetric bayesianMetric;
	private int 			maxVertexDegree;												// Maximum number of parents per vertex.
	private int       		bestDecisionGraphPos;											// Position of the decision graph (DG) that contains the best score gain.	
	private double 	  		bestScoreGain;													// Best global score.
	
	private DecisionGraph[] 		decisionGraphs = new DecisionGraph[Problem.n];
	private HashSet<Integer>[]    parentList = (HashSet<Integer>[])new HashSet[Problem.n];	// Use this to generate the topological ordering and the CP Tables.
	private HashSet<Integer>[] adjacencyList = (HashSet<Integer>[])new HashSet[Problem.n];	// Use this to insure an acyclic BN.
	private HashSet<Integer>[] 	   splitList = (HashSet<Integer>[])new HashSet[Problem.n]; 	// Use this to choose only the correct splits.   																										
	
	

	private int[] 			   color;	 													// NOTE: color[] is initialized with all zeros, by default.
	private ArrayList<Integer> topSort;  													// Use topological sort to sample the Bayesian Network.
	
	public BayesianNetwork(IBayesianMetric bayesianMetric, int offspringSize, int maxVertexDegree){
		this.bayesianMetric  = bayesianMetric;
		this.offspringSize   = offspringSize;
		this.maxVertexDegree = maxVertexDegree;
	}
	
	
	public DecisionGraph[]   getDecisionGraphs(){return this.decisionGraphs;}
	public DecisionGraph getDecisionGraph(int i){return this.decisionGraphs[i];}
	public int 		   getBestDecisionGraphPos(){return this.bestDecisionGraphPos;}
	public double		  	  getBestScoreGain(){return this.bestScoreGain;}
	
	public void 	setBestDecisionGraphPos(int i){this.bestDecisionGraphPos = i;}
	public void setBestScore(double bestScoreGain){this.bestScoreGain = bestScoreGain;}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// This generator performs a greedy search to generate a bayesian model based on decision graphs,
	// best fitted for the selected set. The model includes a topological sort of the final BN, used for sampling.
	// The greedy algorithm is described in Martin Pelikan, 'Hierarchical Bayesian Optimization Algorithm', pag. 116
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void generateModel(SelectedSet selectedSet){
		this.generateDecisionGraphs(selectedSet);		
		this.topologicalSort();      
	}
	
	private void generateDecisionGraphs(SelectedSet selectedSet){		
		this.initializeBN(selectedSet);			 											// Refresh the BN. Compute and store in decreasing order the first score gains, corresponding to adding a first edge to the empty BN.		
		while(bestScoreGain > 0){			 			 									// Search for the best split and store all the necessary information to effectively perform it.
			DecisionGraph bestDecisionGraph = decisionGraphs[bestDecisionGraphPos];
			int bestLeafPos  = bestDecisionGraph.getBestLeafPos();
			int bestSplitPos = bestDecisionGraph.getBestLeaf().getBestSplit();				// Split Variable k. We are adding the edge (Xk)-->(Xi) to the BN.			
			if(parentList[bestDecisionGraphPos].size() >= maxVertexDegree)  				// Do NOT perform this split because Xi has reached the maximum number of parents. 
				bestDecisionGraph.setBestLeafScoreGain(Double.NEGATIVE_INFINITY);			// Ensure that this decision graph will no longer allow splits.
			else{ 
				if(!(splitList[bestDecisionGraphPos].contains(bestSplitPos))){				// Do NOT perform this split because it creates a cycle in the BN.
					Leaf bestLeaf = bestDecisionGraph.getLeaf(bestLeafPos);
					bestLeaf.setScoreGain(bestSplitPos, -1);
					bestLeaf.resetBestSplit(splitList[bestDecisionGraphPos]);
				}
				else{																		// Perform the best split.
					performBestSplit(bestDecisionGraphPos, bestLeafPos, bestSplitPos);		// Update the BN and remove non-valid splits.
					computeNewLeafScores(selectedSet, bestDecisionGraphPos, bestLeafPos);					
				}	
				decisionGraphs[bestDecisionGraphPos].updateBestLeaf();
			}
			updateScoreGain();
		}																					// END: while(bestScoreGain > 0)
	} 																						// END: generateDecisionGraphs(...)
																						
	
	private void initializeBN(SelectedSet selectedSet){
		int NS = selectedSet.getN();
		this.bestScoreGain = Double.NEGATIVE_INFINITY;
		Individual[] individuals = selectedSet.getIndividuals();
		for(int i = 0; i < Problem.n; i++){									// Refresh the Bayesian Network structure.
			parentList[i]    = new HashSet<Integer>(Problem.n);				// Initial Capacity = stringSize; 	Default Load Factor = 0.75    
			adjacencyList[i] = new HashSet<Integer>(Problem.n);				// Initial Capacity = stringSize; 	Default Load Factor = 0.75   
			splitList[i]     = new HashSet<Integer>(2*Problem.n);			// Initial Capacity = 2*stringSize; Default Load Factor = 0.75        			 
			for(int n = 0; n < Problem.n; n++)
				if(n != i)
					splitList[i].add(n);
			
			int mOne  = selectedSet.getUniFrequencies(i);					// Construct the initial single leaf decision graphs.
			int mZero = NS - mOne;
			Leaf newLeaf = new Leaf(0,	-1, mZero, mOne);	 				// There is only a single leaf per variable, at depth 0, with side -1.		
			if(mZero > 0 && mOne > 0){						 				// If mZero = 0 or mOne = 0 there is no need to try any split with this leaf.				
				for(int j = 0; j < NS; j++){
					char alleleJI = individuals[j].getAllele(i);				// Value of Xi in individual j.
					for(int s: splitList[i]){
						char alleleS = individuals[j].getAllele(s);			// Value of Xs in individual j.
						if(alleleJI == '0'){
							if(alleleS == '0')
								newLeaf.addPossibleSplitFrequency(0, s);	// m00[s]++;
							else
								newLeaf.addPossibleSplitFrequency(1, s);	// m01[s]++;
						}
						else{
							if(alleleS == '0')
								newLeaf.addPossibleSplitFrequency(2, s); 	// m10[s]++;
							else	
								newLeaf.addPossibleSplitFrequency(3, s); 	// m11[s]++;
						}
					}
				}
				for(int s: splitList[i]){
					int m00 = newLeaf.getPossibleSplitFrequency(0,s);
					int m01 = newLeaf.getPossibleSplitFrequency(1,s);
					int m10 = newLeaf.getPossibleSplitFrequency(2,s);
					int m11 = newLeaf.getPossibleSplitFrequency(3,s);
					double scoreGain = bayesianMetric.computeScoreGain(mZero, mOne, m00, m01, m10, m11);
					newLeaf.setScoreGain(s, scoreGain);
					newLeaf.updateBestSplit(s, scoreGain);					// Responsible for updating the value of the best split score gain in this leaf.
				}
			}																// END: if(mZero > 0 ...)																			 
			decisionGraphs[i] = new DecisionGraph(newLeaf);	 				// Initially each graph as a single leaf and there are n-1 possible splits.
			decisionGraphs[i].updateBestLeaf();
			double scoreGain = decisionGraphs[i].getBestLeafScoreGain();
			if(scoreGain > this.bestScoreGain){								// Update the value of the global best score gain and position (DG).
				this.bestDecisionGraphPos = i;
				this.bestScoreGain	      = scoreGain;
			}
		}																	// END: for(int i = 0 ...)		
	} 																		// END: initializeBN(...)
	
	
	
	private void performBestSplit(int i, int j, int k){	 
		parentList[i].add(k);					 							// Xk is a parent of Xi.
		adjacencyList[k].add(i);				 							// Xi is a child of Xk.					
		HashSet<Integer> descendants = new HashSet<Integer>(Problem.n);		// Initial Capacity = stringSize; Default Load Factor = 0.75
		HashSet<Integer>  ascendants = new HashSet<Integer>(Problem.n);		// Initial Capacity = stringSize; Default Load Factor = 0.75
		getDescendants(descendants, i);										// NOTE: The list of descendants includes variable Xi itself. 
		getAscendants(ascendants, k);										// NOTE: The list of ascendants includes variable Xk itself.
		for(int asc: ascendants)											// No child of the child of the child of ... of Xi can be a parent of Xk.
			for(int desc: descendants)						 				// No child of the child of the child of ... of Xi can be a parent of the parent of the parent of ... of Xk.
				splitList[asc].remove(desc);				 				// Xdesc can no longer be a parent of Xasc. Remove splits to avoid cycles.
		decisionGraphs[i].splitBestLeaf(j, k);					 			// Effectively perform the best split.
	}																		// END: performBestSplit(...)
	
	
	
	// Compute scores for new leaf0 and leaf1, and add them to scoreList.
	private void computeNewLeafScores(SelectedSet selectedSet, int i, int j){
		int NS = selectedSet.getN();
		for(int a = 0; a < NS; a++){
			Individual individual = selectedSet.getIndividual(a);
			IGraph iterator = decisionGraphs[i].getGraph();
			while(!(iterator instanceof Leaf)){								// Iterator is a variable because we are still traversing the DG.
				int x = ((Variable)iterator).getVariable();
				char alleleX = individual.getAllele(x);
				if(alleleX == '0')
					iterator = ((Variable)iterator).getZero();
				else
					iterator = ((Variable)iterator).getOne();
			}
			int itrPosition = decisionGraphs[i].getLeafs().indexOf((Leaf)iterator);
			if(itrPosition == j || itrPosition == j+1){						// We've reached one of the new leafs.
				int mZero = ((Leaf)iterator).getMZero();
				int mOne = ((Leaf)iterator).getMOne();
				if(mZero > 0 && mOne > 0){		   							// It's still "interesting" to split.
					char alleleI = individual.getAllele(i);					// Value of Xi in individual a.
					for(int split: splitList[i]){
						char alleleS = individual.getAllele(split); 		// Value of Xsplit in individual a.
						if(alleleI == '0'){
							if(alleleS == '0')
								((Leaf)iterator).addPossibleSplitFrequency(0,split); 	// m00[split]++;
							else
								((Leaf)iterator).addPossibleSplitFrequency(1,split); 	// m01[split]++;  
						}
						else{
							if(alleleS == '0')
								((Leaf)iterator).addPossibleSplitFrequency(2,split); 	// m10[split]++;
							else
								((Leaf)iterator).addPossibleSplitFrequency(3,split); 	// m11[split]++;						
						}
					}
				}
			}
		}
		for(int a = 0; a <= 1; a++){
			Leaf newLeaf = decisionGraphs[i].getLeaf(j+a); 								// The two new leafs are at positions 'j' and 'j+1'. 
 			int mZero = newLeaf.getMZero();
			int mOne = newLeaf.getMOne();
			if(mZero > 0 && mOne > 0)				
				for(int s: splitList[i]){
					int m00 = newLeaf.getPossibleSplitFrequency(0,s);
					int m01 = newLeaf.getPossibleSplitFrequency(1,s);
					int m10 = newLeaf.getPossibleSplitFrequency(2,s);
					int m11 = newLeaf.getPossibleSplitFrequency(3,s);
					double scoreGain = bayesianMetric.computeScoreGain(mZero, mOne, m00, m01, m10, m11);
					newLeaf.setScoreGain(s, scoreGain);
					newLeaf.updateBestSplit(s, scoreGain);								// Responsible for updating the value of the best split score gain in this leaf.
				}			
		}																				// END: for(int a = 0 ...)
	} 																					// END: computeNewLeafScores(...)
	
	
	private void updateScoreGain(){
		this.bestScoreGain = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < Problem.n; i++){
			double scoreGain = decisionGraphs[i].getBestLeafScoreGain();
			if(scoreGain > bestScoreGain){
				this.bestDecisionGraphPos = i;
				this.bestScoreGain		  = scoreGain;
			}
		}
	}
	
	private void getDescendants(HashSet<Integer> descendants, int i){
		boolean notVisited = descendants.add(i);
		if(notVisited)
			for(int desc: adjacencyList[i])
				getDescendants(descendants, desc);		
	}
	
	private void getAscendants(HashSet<Integer> ascendants, int k){
		boolean notVisited = ascendants.add(k);
		if(notVisited)
			for(int asc: parentList[k])
				getAscendants(ascendants, asc);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// The topological sort of the BayesianNetwork is stored in the array 'topSort'.
	// Since our BN has a single connected component the topologicalSort() starts with X0, 
	// the first variable, and uses  'WHITE = 0'    'GRAY = 1'. 
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void topologicalSort(){
		color = new int[Problem.n];								// NOTE: color[] is initialized with all zeros, by default.
		topSort = new ArrayList<Integer>();
		for(int Xi = 0; Xi < Problem.n; Xi++)
			if(color[Xi] == 0)  
				DFSVisit(Xi);
	}
	
	private void DFSVisit(int Xi){
		color[Xi] = 1;      									// Xi is now GRAY.
		for(int child: adjacencyList[Xi])
			if(color[child] == 0)        
				DFSVisit(child);
		topSort.add(Xi);   										// NOTE: Topological sort is in ASCENDENT order of finishing time.
	}
	
	public Individual[] sampleNewIndividuals(){
		Individual[] newIndividuals = new Individual[offspringSize];
		for(int i = 0; i < offspringSize; i++)
			newIndividuals[i] = this.generateInstance();
		return newIndividuals;
	}	
	
	private Individual generateInstance(){
		char[] indiv = new char[Problem.n];
		for(int i = Problem.n-1; i >= 0; i--)  					// NOTE: Topological sort is in ASCENDENT order of finishing time.
			decisionGraphs[topSort.get(i)].generateAllele(indiv, topSort.get(i));
		return new Individual(indiv);
	}
	
	public String toString(){
		return bayesianMetric + " : maxVertexDegree: " + maxVertexDegree;
	}
	
//	public String toString(){
//		String str = "Xi    pList          aList             splitList\n";
//		String pList = "";
//		String aList = "";
//		String sList = "";
//		for(int i = 0; i < Problem.n; i++){
//			for(int p: parentList[i])
//				pList += p + "|";
//			for(int a: adjacencyList[i])
//				aList += a + "|";
//			for(int s: splitList[i])
//				sList += s + "|";
//			str += i + "    " + pList + "              " + aList + "                       " + sList + "\n";
//			pList = "";
//			aList = "";
//			sList = "";			
//		}
//		return str;
//	}	
}// End of BayesianNetwork.	


	
