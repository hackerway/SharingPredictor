package recommendation.recipients.groupbased.interactionrank.scoring;

import java.util.Collection;

import recommendation.general.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;

public class IntersectionGroupCount<V> extends GroupScorer<V> {

	@Override
	public String getName() {
		return "IntersectiongGroupCount";
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {

		Collection<V> intersection = getGroupIntersection(group, currentAction);
		if (intersection.size() > 0) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	public static <V> GroupScorerFactory<V> factory(Class<V> memberTypeClass) {
		
		return new GroupScorerFactory<V>() {

			@Override
			public GroupScorer<V> create(double wOut, double halfLife) {
				return new IntersectionGroupCount<>();
			}
		};
	}

}