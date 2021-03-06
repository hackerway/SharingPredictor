package recommendation.groups.evolution.composed.listmaker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.evolution.GroupPredictionList;


public class JaccardCoefficientPredictionListMaker<V> extends PredictionListMaker<V> {

	@Override
	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<GroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold){
		
		GroupPredictionList<V> predictionList = new GroupPredictionList<V>(oldGroup);
		
		for(Set<V> recommenderEngineResult: unusedRecommenderEngineResults){
						
			GroupAndPredictionPair<V> currPair = new GroupAndPredictionPair<V>(oldGroup, recommenderEngineResult);
			if(usedPairings.contains(currPair)) continue;
			
			Set<V> existingPredictionMembers = new HashSet<V>(recommenderEngineResult);
			existingPredictionMembers.removeAll(newIndividuals);
			
			Set<V> intersection = getIntersection(oldGroup, recommenderEngineResult);
			if(intersection.size() == 0) continue;
			Set<V> oldMinusPrediction = getSubtraction(oldGroup, intersection);
			Set<V> predictionMinusOld = getSubtraction(recommenderEngineResult, intersection);
			
			
			//Get the values for q, s, and r
			int q = intersection.size();
			int r = oldMinusPrediction.size();
			int s = predictionMinusOld.size();
			
			//Compute the similarity and dissimilarity
			double sim = computeJaccardianCoefficient(q, r, s);
			double dissim = 1 - sim;
			if(dissim <= threshold){
				predictionList.addPrediction(recommenderEngineResult);
			}
		}
		
		return predictionList;
	}
	
	@Override
	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup, String oldGroupName, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<GroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold, Map<Set<V>, String> predictionNames){
		
		GroupPredictionList<V> predictionList = new GroupPredictionList<V>(oldGroup, oldGroupName, predictionNames);
		
		for(Set<V> recommenderEngineResult: unusedRecommenderEngineResults){
			
			GroupAndPredictionPair<V> currPair = new GroupAndPredictionPair<V>(oldGroup, recommenderEngineResult);
			if(usedPairings.contains(currPair)) continue;
			
			Set<V> existingPredictionMembers = new HashSet<V>(recommenderEngineResult);
			existingPredictionMembers.removeAll(newIndividuals);
			
			Set<V> intersection = getIntersection(oldGroup, recommenderEngineResult);
			if(intersection.size() == 0) continue;
			Set<V> oldMinusPrediction = getSubtraction(oldGroup, intersection);
			Set<V> predictionMinusOld = getSubtraction(recommenderEngineResult, intersection);
			
			
			//Get the values for q, s, and r
			int q = intersection.size();
			int r = oldMinusPrediction.size();
			int s = predictionMinusOld.size();
			
			//Compute the similarity and dissimilarity
			double sim = computeJaccardianCoefficient(q, r, s);
			double dissim = 1 - sim;
			if(dissim <= threshold){
				predictionList.addPrediction(recommenderEngineResult);
			}
		}
		
		return predictionList;
	}
	
	private Set<V> getIntersection(Set<V> a, Set<V> b){
		if(a.size() > b.size()){
			return getIntersection(b, a);
		}
		
		Set<V> intersection = new TreeSet<V>(a);
		intersection.retainAll(b);
		
		return intersection;
	}
	
	private Set<V> getSubtraction(Set<V> a, Set<V> intersection){
		Set<V> subtraction = new TreeSet<V>(a);
		subtraction.removeAll(intersection);
		
		return subtraction;
	}
	
	private double computeJaccardianCoefficient(double q, double r, double s){
		return q/(q+r+s);
	}

}
