package data.preprocess.graphbuilder;

import java.util.Collection;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import data.representation.actionbased.CollaborativeAction;

public abstract class WeightedActionBasedGraphBuilder<Collaborator, Action extends CollaborativeAction<Collaborator>>
		implements ActionBasedGraphBuilder<Collaborator, Action> {

	public abstract SimpleWeightedGraph<Collaborator, DefaultEdge> addActionToWeightedGraph(
			SimpleWeightedGraph<Collaborator, DefaultEdge> graph, Action currentAction,
			Collection<Action> pastActions);

	@Override
	public UndirectedGraph<Collaborator, DefaultEdge> addActionToGraph(
			UndirectedGraph<Collaborator, DefaultEdge> graph,
			Action currentAction,
			Collection<Action> pastActions) {
		if (graph == null || graph instanceof WeightedGraph) {
			return addActionToWeightedGraph((SimpleWeightedGraph<Collaborator, DefaultEdge>) graph,
					currentAction, pastActions);
		}
		return null;
	}

}
