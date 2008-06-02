package nl.vpro.redactie;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.vpro.redactie.actions.*;
import nl.vpro.redactie.handlers.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.StorageException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * De wizardservice voert alle acties uit geinitieerd door een redactielid. Handler objecten worden gebruikt om desbetreffende acties te
 * verwerken. Zo heeft een createNodeActie een createNodeHandler die gebruikt wordt voor de verwerking van de actie.
 *
 * @author Rob Vermeulen
 */
public class WizardService {
    private static Logger log = Logging.getLoggerInstance(WizardService.class);

    /**
     * Verwerkt de acties.
     *
     * @param command
     *            Een container met alle acties.
     * @param cloud
     *            Een ingelogd cloud.
     */
    public ResultContainer processActions(Command command, Cloud cloud, HttpServletRequest request, HttpServletResponse response) {
        ResultContainer resultContainer = new ResultContainer(request, response);
        Map<String, Node> idMap = new HashMap<String, Node>();

        String username = cloud.getUser().getIdentifier();

        if (log.isDebugEnabled()) {
            log.debug("Processing data for user(" + username + "), command(" + command + ")");
        }

        // Een transactie bestaat uit de transactienaam in combinatie met het accountname.
        Transaction transactionalCloud = cloud.getTransaction("dvtwizard");

        CreateNodeHandler createNodeHandler = new CreateNodeHandler(transactionalCloud, resultContainer, idMap);
        UpdateNodeHandler updateNodeHandler = new UpdateNodeHandler(transactionalCloud, resultContainer, idMap);

        DeleteNodeHandler deleteNodeHandler = new DeleteNodeHandler(transactionalCloud, resultContainer, idMap);

        CheckRelationHandler checkRelationHandler = new CheckRelationHandler(transactionalCloud, resultContainer, idMap);
        CreateRelationHandler createRelationHandler = new CreateRelationHandler(transactionalCloud, resultContainer, idMap);

        CreateCheckboxRelationHandler createCheckboxRelationHandler = new CreateCheckboxRelationHandler(transactionalCloud,
                resultContainer, idMap);
        UpdateCheckboxRelationHandler updateCheckboxRelationHandler = new UpdateCheckboxRelationHandler(transactionalCloud,
                resultContainer, idMap);

        PosrelSortHandler posrelSortHandler = new PosrelSortHandler(transactionalCloud, resultContainer, idMap);

        for (CreateNodeAction action : command.getCreateNodeActions().values()) {
            createNodeHandler.process(action);
        }

        if (!resultContainer.containsErrors()) {
            for (UpdateNodeAction action : command.getUpdateNodeActions().values()) {
                updateNodeHandler.process(action);
            }
        }

        if (!resultContainer.containsErrors()) {
            for (DeleteNodeAction action : command.getDeleteNodeActions().values()) {
                deleteNodeHandler.process(action);
            }
        }

        if (!resultContainer.containsErrors()) {
            for (PosrelSortAction action : command.getPosrelSortActions().values()) {
                posrelSortHandler.process(action);
            }
        }


        if (!resultContainer.containsErrors()) {
            for (CheckRelationAction action : command.getCheckRelationActions().values()) {
                checkRelationHandler.process(action);
            }
        }

        if (!resultContainer.containsErrors()) {
            for (CreateRelationAction action : command.getCreateRelationActions().values()) {
                createRelationHandler.process(action);
            }
        }

        if (!resultContainer.containsErrors()) {
            for (CreateCheckboxRelationAction action : command.getCreateCheckboxRelationActions().values()) {
                createCheckboxRelationHandler.process(action);
            }
        }

        if (!resultContainer.containsErrors()) {
            for (UpdateCheckboxRelationAction action : command.getUpdateCheckboxRelationActions().values()) {
                updateCheckboxRelationHandler.process(action);
            }
        }

        // Als er geen errors zijn kan de transactie gecommit worden.
        if (resultContainer.getErrors().isEmpty()) {
            try {
                // TODO weer aanzetten
                // throw new Exception("ff niet werken");
                transactionalCloud.commit();
//              Nu dat alle objecten gecommit zijn even controleren of er een nieuw object is aangemaakt.
                // TODO: this is not ok. what if there are more new nodes than one? It should at least be a list.
                // and the new id is not good for the same reason.
                if (idMap.containsKey("new")) {
                    int number = idMap.get("new").getNumber();
                    resultContainer.setNewObject("" + number);
                }
            } catch (Exception e) {
                writeError(resultContainer, e);
            }


        } else {
            transactionalCloud.cancel();
        }

        return resultContainer;
    }

    /**
     * Converteer de exception naar een voor de redactie bruikbare foutmelding.
     */
    private void writeError(ResultContainer result, Exception exception) {
        if (exception instanceof StorageException) {
            if (exception.getMessage().startsWith("Unique constraint (informix.u121_1189) violated")) {
                FieldError error = new FieldError("url", "Het url veld heeft een waarde die al in de database bestaat.");
                result.getErrors().addElement(error);
            }
        }
    }
}