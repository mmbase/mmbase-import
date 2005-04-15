package nl.didactor.util;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

public class GetRelation {
    public static NodeList getRelations(int snum, int dnum, String relationManager, Cloud cloud) {
        RelationManager settingManager = cloud.getRelationManager(relationManager);
        NodeQuery query = settingManager.createQuery();
        StepField snumber = query.getStepField(settingManager.getField("snumber"));
        StepField dnumber = query.getStepField(settingManager.getField("dnumber"));

        // Relation from 'people' to 'components'
        BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(snumber, new Integer(snum));
        BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(dnumber, new Integer(dnum));
        BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint.addChild(constraint1).addChild(constraint2);
        query.setConstraint(constraint);

        return settingManager.getList(query);
    }

	public static NodeList getRelations( int snum, String relationManager, Cloud cloud)
    {
		RelationManager settingManager = cloud.getRelationManager(relationManager);
		NodeQuery query = settingManager.createQuery();
		StepField snumber = query.getStepField(settingManager.getField("snumber"));

		// Relation from 'people' to 'components'
		BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(snumber, new Integer(snum));
		query.setConstraint(constraint);

		return settingManager.getList(query);
	}

}

