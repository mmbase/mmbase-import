/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.*;
import org.mmbase.applications.vprowizards.spring.action.Action;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class accepts a number of Action objects and maps them to their names, so the data binder can access them. For
 * each action class that is registered a Map is created, where an instance of
 * the action class is mapped to a string id. This way you can create more than one actions of one type in one request.
 *
 *
 * @author Ernst Bunders
 * @version $Id: BasicCommand.java,v 1.3 2009-04-28 15:40:44 michiel Exp $
 */
public class BasicCommand implements Command {
    private Map<String, Map<String, Action>> actions = new LinkedHashMap<String, Map<String, Action>>();
    private static final Logger log = Logging.getLoggerInstance(BasicCommand.class);

    public Map<String, Map<String, Action>> getActions() {
        return actions;
    }



    public void processActions(HttpServletRequest request,
                               HttpServletResponse response, ResultContainer resultContainer) {
        //we only iterate over the actions until there is an error
        actions:
        for(String actionMapping :  actions.keySet()){
            Map<String, Action> mappedActions = actions.get(actionMapping);
            log.service(String.format("%s actions found for mapping '%s'", ""+mappedActions.size(), actionMapping));
            for (Action action: mappedActions.values()){
                action.process(resultContainer);
                if(resultContainer.hasGlobalErrors() || resultContainer.hasFieldErrors()){
                    break actions;
                }
            }
        }
    }

    public void addAction(String mappingName, Class<? extends Action> actionClass) {
        log.debug("mapping action class '"+actionClass.toString()+"' to '"+mappingName+"'");
        actions.put(mappingName, MapUtils.lazyMap(new HashMap<String, Action>(), new MyFactory(actionClass)));
    }

    /**
     * We have to wrap the Instationation Factory, becouse the LazyMap expects a factory of generic type Action, but
     * the instantiation factory has no differentiation between the type it instantiates and the type it returns. So
     * We create an implicit cast here (bit silly).
     */
    public static final class MyFactory implements Factory<Action>{
        private Factory<? extends Action> wrappedFactory;

        public MyFactory(Class<? extends Action> actualActionClass){
            wrappedFactory = FactoryUtils.instantiateFactory(actualActionClass);
        }

        public Action create() {
            return wrappedFactory.create();
        }
    }



}
