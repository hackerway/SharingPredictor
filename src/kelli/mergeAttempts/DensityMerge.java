package kelli.mergeAttempts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class DensityMerge {
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static Boolean mergeHappened = true;
   private static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, Set<Integer>> friendLists = new HashMap<Integer, Set<Integer>>();
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   static double OMEGA;
   public static void doMerges(Collection<Set<Integer>> Cliques, 
		   UndirectedGraph<Integer, DefaultEdge> graph, double omega){
	  origGraph = graph;
	  OMEGA = omega;
	  long passStart, elapsedTime;
	  float elapsedTimeMin;
	  int passNumber = 1;
	  while(mergeHappened){
		 mergeHappened = false;
		 toRemoveClique = new HashMap<Integer, Boolean>();
		 for(int i = 0; i < Cliques.size(); i++)
			toRemoveClique.put(i, false);
		 passStart = System.currentTimeMillis();
		 combineCliques(Cliques);
		 for(Set<Integer> clique: mergedCliques){
			Cliques.remove(clique);
		 }
		 mergedCliques = new ArrayList<Set<Integer>>(); 
		 elapsedTime = System.currentTimeMillis() - passStart;
		 elapsedTimeMin = elapsedTime/(60*1000F);
		 System.out.println("pass "+passNumber+" cliques.size: "+Cliques.size()+"   pass time: "+elapsedTimeMin);
		 passNumber++;
	  }
	  System.out.println("final cliques.size: "+Cliques.size());
   }
   private static void combineCliques(Collection<Set<Integer>> Cliques){
	  int iterationCount = 0; int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: Cliques){
		 if(!toRemoveClique.get(iterationCount) ){
			innerIter = 0;
			for (Set<Integer> c2: Cliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){
					 if(c1.size() >= c2.size()) 
						mergeHappened = mergeCliques(c1, c2, innerIter);
					 else
						firstOfCliquePairMerged = mergeCliques(c2, c1, iterationCount); 
					 //possible problem...if c1 merged into c5, i shouldn't be able to try c6 into c1
				  }
				  if(firstOfCliquePairMerged) {
					 firstOfCliquePairMerged = false;
					 mergeHappened = true;
					 break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
   private static boolean mergeCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  Set<Integer> mergedClique = new HashSet<Integer>(c1);
	  Set<Integer> difference = new HashSet<Integer>();
	  for (int uid: c2){
		 if(!mergedClique.contains(uid)){
			difference.add(uid);
		 }
	  }
	  double density = findDensity(c1,difference);
	  if(density >= OMEGA){   //maybe need to have the same req as LCMA ...newAD >= .95AD
		 for(int uid: difference){
			c1.add(uid);
		 }
		 mergedCliques.add(c2); 
		 toRemoveClique.put(smallerCliqueNumber, true); 
		 return true;
	  }
	  else return false;
   }
   private static double findDensity(Set<Integer> clique, Set<Integer> diff){
	  Iterator<Integer> uidIter = clique.iterator();
	  int currUID;
	  Set<DefaultEdge> edgeSet;
	  Set<DefaultEdge> cliqueEdges = new HashSet<DefaultEdge>();
	  Set<Integer> friendSet = null;
	  double edgeCount = 0.0;
	  while(uidIter.hasNext()){
		 currUID = uidIter.next();
		 edgeSet = origGraph.edgesOf(currUID);
		 if(!friendLists.containsKey(currUID)){
			friendSet = findFriendLists(currUID);
		 } else friendSet = friendLists.get(currUID);  //get the friends of each person in DIFF
		 for(DefaultEdge edge: edgeSet){
			 if(clique.contains(origGraph.getEdgeSource(edge))){
				 if(clique.contains(origGraph.getEdgeTarget(edge)))
					cliqueEdges.add(edge);
				 else if (diff.contains(origGraph.getEdgeTarget(edge))) cliqueEdges.add(edge);
			 } else if (diff.contains(origGraph.getEdgeSource(edge))){
				 if(clique.contains(origGraph.getEdgeTarget(edge)))
					cliqueEdges.add(edge);
				 else if (diff.contains(origGraph.getEdgeTarget(edge))) cliqueEdges.add(edge);
			 }
		 }
	  }
	  uidIter = diff.iterator();
	  while(uidIter.hasNext()){
		 currUID = uidIter.next();		
		 edgeSet = origGraph.edgesOf(currUID);
		 if(!friendLists.containsKey(currUID)){
			friendSet = findFriendLists(currUID);
		 } else friendSet = friendLists.get(currUID);  //get the friends of each person in DIFF
		 for(DefaultEdge edge: edgeSet){
			 if(clique.contains(origGraph.getEdgeSource(edge))){
				 if(clique.contains(origGraph.getEdgeTarget(edge)))
					cliqueEdges.add(edge);
				 else if (diff.contains(origGraph.getEdgeTarget(edge))) cliqueEdges.add(edge);
			 } else if (diff.contains(origGraph.getEdgeSource(edge))){
				 if(clique.contains(origGraph.getEdgeTarget(edge)))
					cliqueEdges.add(edge);
				 else if (diff.contains(origGraph.getEdgeTarget(edge))) cliqueEdges.add(edge);
			 }
		 }
	  }
	  int vSetSize = clique.size()+diff.size();
	  double densityNum = 2*cliqueEdges.size();
	  double densityDem = (vSetSize*(vSetSize-1));
	  return densityNum/densityDem;
   }
   private static Set<Integer> findFriendLists(int id){
	  Set<Integer> friendList = new HashSet<Integer>();
	  Set<DefaultEdge> edgeSet = origGraph.edgesOf(id);
	  for(DefaultEdge edge: edgeSet){
		 int source = origGraph.getEdgeSource(edge);
		 if(friendList.contains(source)|| id == source) source = origGraph.getEdgeTarget(edge);
		 friendList.add(source);
	  }
	  return friendList;
   }


}
