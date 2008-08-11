package nl.vpro.redactie;

import java.util.HashMap;
import java.util.Map;

import nl.vpro.redactie.actions.CheckRelationAction;
import nl.vpro.redactie.actions.CreateCheckboxRelationAction;
import nl.vpro.redactie.actions.CreateNodeAction;
import nl.vpro.redactie.actions.CreateRelationAction;
import nl.vpro.redactie.actions.CreateTrackAction;
import nl.vpro.redactie.actions.DeleteNodeAction;
import nl.vpro.redactie.actions.PosrelSortAction;
import nl.vpro.redactie.actions.UpdateCheckboxRelationAction;
import nl.vpro.redactie.actions.UpdateNodeAction;
import nl.vpro.redactie.actions.UpdateTrackAction;

import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Als een redactielid het formulier submit ontstaan er een aantal acties. Dit Command
 * object is een container voor al die acties.
 *
 * Rob Vermeulen (VPRO)
 */
public class Command {
	private Map<String,CreateNodeAction> createNodeActions = new HashMap<String,CreateNodeAction>();
	private Map<String,UpdateNodeAction> updateNodeActions = new HashMap<String,UpdateNodeAction>();

	private Map<String,DeleteNodeAction> deleteNodeActions = new HashMap<String,DeleteNodeAction>();

	private Map<String, PosrelSortAction> posrelSortActions = new HashMap<String,PosrelSortAction>();

	private Map<String,CreateTrackAction> createTrackActions = new HashMap<String,CreateTrackAction>();
	private Map<String,UpdateTrackAction> updateTrackActions = new HashMap<String,UpdateTrackAction>();

	private Map<String,CheckRelationAction> checkRelationActions = new HashMap<String,CheckRelationAction>();
	private Map<String,CreateRelationAction> createRelationActions = new HashMap<String,CreateRelationAction>();

	private Map<String,CreateCheckboxRelationAction> createCheckboxRelationActions = new HashMap<String,CreateCheckboxRelationAction>();
	private Map<String,UpdateCheckboxRelationAction> updateCheckboxRelationActions = new HashMap<String,UpdateCheckboxRelationAction>();

	@SuppressWarnings("unchecked") // suppress conversion warning in the constructor, no objects in list yet
	public Command() {
		createNodeActions = MapUtils.lazyMap(createNodeActions, FactoryUtils.instantiateFactory(CreateNodeAction.class));
		updateNodeActions = MapUtils.lazyMap(updateNodeActions, FactoryUtils.instantiateFactory(UpdateNodeAction.class));

		deleteNodeActions = MapUtils.lazyMap(deleteNodeActions, FactoryUtils.instantiateFactory(DeleteNodeAction.class));

        posrelSortActions = MapUtils.lazyMap(posrelSortActions, FactoryUtils.instantiateFactory(PosrelSortAction.class));

		createTrackActions = MapUtils.lazyMap(createTrackActions, FactoryUtils.instantiateFactory(CreateTrackAction.class));
		updateTrackActions = MapUtils.lazyMap(updateTrackActions, FactoryUtils.instantiateFactory(UpdateTrackAction.class));

		checkRelationActions = MapUtils.lazyMap(checkRelationActions, FactoryUtils.instantiateFactory(CheckRelationAction.class));
		createRelationActions = MapUtils.lazyMap(createRelationActions, FactoryUtils.instantiateFactory(CreateRelationAction.class));

		createCheckboxRelationActions = MapUtils.lazyMap(createCheckboxRelationActions, FactoryUtils.instantiateFactory(CreateCheckboxRelationAction.class));
		updateCheckboxRelationActions = MapUtils.lazyMap(updateCheckboxRelationActions, FactoryUtils.instantiateFactory(UpdateCheckboxRelationAction.class));
	}

	public Map<String, CheckRelationAction> getCheckRelationActions() {
		return checkRelationActions;
	}



	public Map<String, UpdateNodeAction> getUpdateNodeActions() {
		return updateNodeActions;
	}



	public Map<String, CreateNodeAction> getCreateNodeActions() {
		return createNodeActions;
	}



	public Map<String, CreateRelationAction> getCreateRelationActions() {
		return createRelationActions;
	}



	public Map<String, CreateTrackAction> getCreateTrackActions() {
		return createTrackActions;
	}



	public Map<String, UpdateTrackAction> getUpdateTrackActions() {
		return updateTrackActions;
	}



	public Map<String, CreateCheckboxRelationAction> getCreateCheckboxRelationActions() {
		return createCheckboxRelationActions;
	}



	public Map<String, UpdateCheckboxRelationAction> getUpdateCheckboxRelationActions() {
		return updateCheckboxRelationActions;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

    public Map<String, DeleteNodeAction> getDeleteNodeActions() {
        return deleteNodeActions;
    }



    public Map<String, PosrelSortAction> getPosrelSortActions() {
        return posrelSortActions;
    }



}