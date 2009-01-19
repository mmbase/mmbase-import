package nl.natuurmonumenten.activiteiten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.log4j.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.RelationList;

/**
 * WebService voor de Centrale Activiteiten Database (CAD)
 * 
 * @author rob
 *
 */
public class ActiviteitenService implements IActiviteitenService {
    // private static Logger logger =
    // Logging.getLoggerInstance(MMBaseActiviteitenService.class);
    private static Logger logger = Logger.getLogger(ActiviteitenService.class);
    BeanFactory beanFactory = new BeanFactory();

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getVersion()
     */
    public String getVersion() {
        return "1";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getProvincies
     * ()
     */
    public Provincie[] getProvincies() {
        logger.debug("getProvincies");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("provincies");
        NodeList list = manager.getList(null, null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createProvincie(node));
        }
        return (Provincie[]) beans.toArray(new Provincie[beans.size()]);
        // if (true) throw new IllegalArgumentException("dit is een test fout");
        // return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getEvents(
     * java.util.Date, java.util.Date, java.lang.String[], java.lang.String,
     * java.lang.String)
     */
    public Event[] getEvents(Date start, Date eind, String[] eventTypeIds, String provincieId, String natuurgebiedenId) {
        logger.debug("getEvents() - eventTypeIds: " + eventTypeIds);
        if (eventTypeIds != null) {
            logger.debug("eventTypeIds: " + Arrays.asList(eventTypeIds));
        }
        if (start == null || eind == null) {
            throw new IllegalArgumentException("Start en/of einddatum ontbreekt");
        }
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        Map eventNodes = ActiviteitenHelper.findEvents(cloud, start, eind, eventTypeIds, provincieId, natuurgebiedenId);
        List beans = new ArrayList();
        for (Iterator iter = eventNodes.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String eventNumber = (String) entry.getValue();
            logger.debug("getting node for: " + eventNumber);
            Node event = cloud.getNode(eventNumber);
            beans.add(beanFactory.createEvent(event));
        }
        logger.debug("beans: " + beans);
        logger.debug("beans.size(): " + beans.size());
        Event[] events = (Event[]) beans.toArray(new Event[beans.size()]);
        logger.debug("events: " + Arrays.asList(events));
        return events;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getEventTypes
     * ()
     */
    public EventType[] getEventTypes() {
        logger.debug("getEventTypes");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("evenement_type");
        // alleen types die op internet getoond worden
        NodeList list = manager.getList("isoninternet=1", null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createEventType(node));
        }
        return (EventType[]) beans.toArray(new EventType[beans.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getMediaTypes
     * ()
     */
    public MediaType[] getMediaTypes() {
        logger.debug("getMediaTypes");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("media");
        NodeList list = manager.getList(null, null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createMediaType(node));
        }
        return (MediaType[]) beans.toArray(new MediaType[beans.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @seenl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#
     * getDeelnemersCategorieen()
     */
    public DeelnemersCategorie[] getDeelnemersCategorieen() {
        logger.debug("getDeelnemersCategorieen");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("deelnemers_categorie");
        NodeList list = manager.getList("naam is not null", null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createDeelnemersCategorie(node));
        }
        return (DeelnemersCategorie[]) beans.toArray(new DeelnemersCategorie[beans.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.natuurmonumenten.activiteiten.ActiviteitenServiceInterf#getNatuurgebieden
     * ()
     */
    public Natuurgebied[] getNatuurgebieden() {
        logger.debug("getNatuurgebieden");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("natuurgebieden");
        NodeList list = manager.getList("naam is not null", null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createNatuurgebied(node));
        }
        return (Natuurgebied[]) beans.toArray(new Natuurgebied[beans.size()]);
    }

    public EventDetails getEventDetails(String id) {
        logger.debug("getEventDetails");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        Node node = cloud.getNode(id);
        if (!"evenement".equals(node.getNodeManager().getName())) {
            logger.debug("Geen evenement: " + id);
            return null;
        }
        return beanFactory.createEventDetails(node);
    }

    public Vertrekpunt[] getVertrekpunten() {
        logger.debug("getNatuurgebieden");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        NodeManager manager = cloud.getNodeManager("vertrekpunten");
        NodeList list = manager.getList(null, null, null);
        List beans = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            beans.add(beanFactory.createVertrekpunt(node));
        }
        return (Vertrekpunt[]) beans.toArray(new Vertrekpunt[beans.size()]);
    }

    public String subscribeEvent(Subscription subscription) {
        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
        Node eventNode = cloud.getNode(subscription.getEvenementId());
        if (eventNode == null) {
            throw new IllegalArgumentException("Evenement id does not exist: " + subscription.getEvenementId());
        }
        NodeManager manager = cloud.getNodeManager("inschrijvingen");
        Node subscriptionNode = manager.createNode();
        subscriptionNode.setLongValue("datum_inschrijving", (new Date()).getTime() / 1000);
        subscriptionNode.setStringValue("source", subscription.getMediaTypeId());
        subscriptionNode.setStringValue("description", subscription.getBijzonderheden());
        subscriptionNode.setStringValue("ticket_office", "website");
        subscriptionNode.commit();
        eventNode.createRelation(subscriptionNode, cloud.getRelationManager("posrel")).commit();
        // *** update inschrijvingen,related,inschrijvings_status
        RelationList relations = subscriptionNode.getRelations("related", "inschrijvings_status");
        for (int r = 0; r < relations.size(); r++) {
            relations.getRelation(r).delete(true);
        }
        String thisStatus = cloud.getNode("site_subscription").getStringValue("number");
        Node statusNode = cloud.getNode(thisStatus);
        if (statusNode != null) {
            subscriptionNode.createRelation(cloud.getNode(thisStatus), cloud.getRelationManager("related")).commit();
        } else {
            logger.error("inschrijvings_status with number " + thisStatus + " does not exist.");
        }
        Aanmelding[] aanmeldingen = subscription.getAanmeldingen();
        if (aanmeldingen != null) {
            for (int i = 0; i < aanmeldingen.length; i++) {
                if (aanmeldingen[i].getAantal() > 0) {
                    logger.debug("aanmelding-deelnemerscatid:" + aanmeldingen[i].getDeelnemersCategorieId());
                    logger.debug("aanmelding-aantal:" + aanmeldingen[i].getAantal());
                    ActiviteitenHelper.createParticipant(cloud, eventNode, subscriptionNode, aanmeldingen[i]
                            .getDeelnemersCategorieId(), aanmeldingen[i].getAantal(), subscription);
                }
            }
        }
        // emails worden niet verstuurd zoals in het origineel, dit hoeft niet
        return  "Aanmelding ontvangen";
    }
}
