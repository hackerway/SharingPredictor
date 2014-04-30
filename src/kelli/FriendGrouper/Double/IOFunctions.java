package kelli.FriendGrouper.Double;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
@SuppressWarnings({ "deprecation" })
public class IOFunctions {
   private static HashMap<Double, String> uidNames = new HashMap<Double, String>();
   private static HashMap<Double, Boolean> uidInClique = new HashMap<Double, Boolean>();
   public static UndirectedGraph<Double,DefaultEdge> UIDGraph = null;
   //creates the UIDGraph from the original friendship pairs.  Stores here for printing purposes, returns for comparison purposes 
   public static UndirectedGraph<Double, DefaultEdge> createUIDGraph(String inputFile)
   {
      UndirectedGraph<Double, DefaultEdge> g = new SimpleGraph<Double, DefaultEdge>(DefaultEdge.class);
      int linesReadCount = 0;
      try {
    	 DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
    	 String FriendPair = null;
    	 double friend1 = -1;
    	 double friend2 = -1;
    	 int parsingSpace = -1;
    	 // in.available() returns 0 if the file does not have more lines.
    	 while (in.available() != 0) {
    		FriendPair = in.readLine();
    		linesReadCount++;
    		parsingSpace = FriendPair.indexOf(' ');
    		friend1 = Double.parseDouble(FriendPair.substring(0, parsingSpace));
    		FriendPair = FriendPair.substring(parsingSpace+1);
    		parsingSpace = FriendPair.indexOf(' ');
    		if(parsingSpace != -1)
    		   friend2 = Double.parseDouble(FriendPair.substring(0,parsingSpace));
    		else friend2 = Double.parseDouble(FriendPair);
    		g.addVertex(friend1); uidInClique.put(friend1, false);
    		g.addVertex(friend2); uidInClique.put(friend2, false);
    		g.addEdge(friend1, friend2);
    	 }
    	 // dispose all the resources after using them.
    	 in.close();
      } catch (Exception e){
    	  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
      }
      UIDGraph = g;
      return g;
   }
   public static Collection<Set<Integer>> loadCliques(String inCliquesFile){
		  int linesReadCount = 0;
		  Collection<Set<Integer>> returnCollection = new ArrayList<Set<Integer>>();
		  try {
			 DataInputStream in = new DataInputStream(new FileInputStream(inCliquesFile));
			 String inputLine = null;
			 List<Integer> currClique = null;
			 int uid = 0;
			 // in.available() returns 0 if the file does not have more lines.
			 while (in.available() != 0) {
				currClique = new ArrayList<Integer>();
				inputLine = in.readLine();
				linesReadCount++;
				while(!inputLine.contains("*")){
					if(!inputLine.contains("Clique:")){
						uid = Integer.parseInt(inputLine);
						currClique.add(uid);
						if(in.available() != 0) inputLine = in.readLine();
					} else if (in.available() != 0) inputLine = in.readLine();
				}
				Set<Integer> theClique = new HashSet<Integer>(currClique);
				returnCollection.add(theClique);
			 }
			 // dispose all the resources after using them.
			 in.close();
		  } catch (Exception e){
			  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
		  }
		  return returnCollection;
	   }
   private static Collection<Set<String>> alphabetizeCliques(Collection<Set<Double>> networkCliques){
	   Collection<Set<String>> cliques = new ArrayList<Set<String>>();
	   Set<String> cliqueNames;
	   Iterator<Set<Double>> cliqueIter = networkCliques.iterator();
	   Set<Double> currClique;
	   Iterator<Double> uidIter;
	   double currUID;
	   while(cliqueIter.hasNext()){
		   currClique = cliqueIter.next();
		   uidIter = currClique.iterator();
		   cliqueNames = new TreeSet<String>();
		   while(uidIter.hasNext()){
			   currUID = uidIter.next();
			   if(uidNames.containsKey(currUID)){
				   cliqueNames.add(uidNames.get(currUID));
			   }
		   }
		   cliques.add(cliqueNames);
	   }
	   return cliques;
   }
   public static void printCliqueNamesToFile(String outputFile, Collection<Set<Double>> networkCliques){
	  int cliqueCount = 1;
	  try {
		 PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
		 Collection<Set<String>> cliques = alphabetizeCliques(networkCliques);
		 Iterator<Set<String>> collIter = cliques.iterator();
		 Iterator<String> uidIter;
		 Set<String> currClique;
		 String currUID;
		 while (collIter.hasNext()){
			pw.println("Clique: "+cliqueCount);
			currClique = collIter.next();
			pw.println("clique size: "+currClique.size());
			uidIter = currClique.iterator();
			while (uidIter.hasNext()){
			   currUID = uidIter.next();
			   pw.println(currUID);
			}
			pw.println();
			cliqueCount++;
		 }
		 System.out.println("Results can be found in: "+outputFile);
		 pw.close();
	  } catch (Exception e){
		  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
		  System.exit(0);
	  }
   }
   public static void printCliquesToFile(String outputFile, Collection<Set<Double>> cliques){
	  int cliqueCount = 1;
	  try {
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
    	 Iterator<Set<Double>> collIter = cliques.iterator();
    	 Iterator<Double> uidIter;
    	 Set<Double> currClique;
    	 Set<DefaultEdge> edgeSet;
    	 HashSet<Double> friendSet = null;
    	 Double currUID;
    	 int connectionLevel = 0;
    	 int averageConnectionLevel = 0;
    	 HashMap<Double, Boolean> uidNOTinClique = new HashMap<Double, Boolean>(uidInClique);
    	 while (collIter.hasNext()){
    		pw.println("Clique: "+cliqueCount);
     		currClique = collIter.next();
     		pw.println("clique size: "+currClique.size());
    		uidIter = currClique.iterator();
    		while (uidIter.hasNext()){
    		   currUID = uidIter.next();
    		   if(uidNames.containsKey(currUID)){
    			  edgeSet = UIDGraph.edgesOf(currUID);
    			  friendSet = new HashSet<Double>();
    			  for(DefaultEdge edge: edgeSet){
    				 double source = UIDGraph.getEdgeSource(edge);
    				 if(friendSet.contains(source) || currUID == source){
    					 source = UIDGraph.getEdgeTarget(edge);
    				 } 
    				 friendSet.add(source);
    			  }
    			  for (double friend: friendSet){
    				  if (currClique.contains(friend)){
    					  connectionLevel++;
    				  }
    			  }
    			  averageConnectionLevel = averageConnectionLevel+connectionLevel;
    			  pw.println(uidNames.get(currUID)+ "  ~ "+connectionLevel+ " in this clique out of "
    					  +friendSet.size()+ " total mutual friends");
    			  uidNOTinClique.put(currUID, true);
    		   } 
    		   connectionLevel = 0;
    		}
    		pw.println("average connection is "+ averageConnectionLevel/currClique.size());
    		pw.println();
    		averageConnectionLevel = 0;
    		cliqueCount++;
    	 }
    	 pw.println("Friends Not Grouped:");
    	 int coverageCount = 0;
    	 for(double uid: uidNOTinClique.keySet()){
    		 if(uidNOTinClique.get(uid)){
    			coverageCount++;
    		 } else {
    		    if(uidNames.containsKey(uid)) pw.println(uidNames.get(uid));
    		 }
    	 }
    	 System.out.println("Coverage: "+coverageCount+" out of "+uidNOTinClique.size()+ " friends.");
    	 System.out.println("Results can be found in: "+outputFile);
    	 pw.close();
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
    	  System.exit(0);
      }
   }
   public static void fillNames(String inputNames){
		  int linesReadCount = 0;
		  try {
			 DataInputStream in = new DataInputStream(new FileInputStream(inputNames));
			 String friendName = null;
			 double friendUID = -1;
			 int parsingComma = -1;
			 // in.available() returns 0 if the file does not have more lines.
			 while (in.available() != 0) {
				friendName = in.readLine();
				linesReadCount++;
				parsingComma = friendName.indexOf(',');
				friendUID = Double.parseDouble(friendName.substring(0, parsingComma));
				friendName = friendName.substring(parsingComma+2);
				uidNames.put(friendUID, friendName);
			 }
			 in.close();
		  } catch (Exception e){
			 System.out.println("!!! fillNames, line:"+linesReadCount+": "+e.getMessage());
		  }
	   }
}
