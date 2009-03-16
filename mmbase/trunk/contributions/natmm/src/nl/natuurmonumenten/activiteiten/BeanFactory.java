package nl.natuurmonumenten.activiteiten;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

/**
 * Factory om value objects van mmbase nodes voor de activiteiten webservice te maken
 * 
 */
class BeanFactory {
    
    private static Logger logger = Logger.getLogger(BeanFactory.class);

    public Provincie createProvincie(Node node) {
        Provincie bean = new Provincie();
        bean.setId(node.getStringValue("number"));
        // titel is verplicht in database maar naam wordt in de code gebruikt
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            bean.setNaam(naam);
        } else {
            String titel = node.getStringValue("titel");
            bean.setNaam(titel);
        }
        String omschrijving = node.getStringValue("omschrijving");
        if (!isEmpty(omschrijving)) {
            bean.setOmschrijving(omschrijving);
        }
        return bean;
    }

    public EventType createEventType(Node node) {
        EventType bean = new EventType();
        bean.setId(node.getStringValue("number"));
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            bean.setNaam(naam);
        }
        return bean;
    }

    public MediaType createMediaType(Node node) {
        MediaType bean = new MediaType();
        bean.setId(node.getStringValue("number"));
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            bean.setNaam(naam);
        }
        return bean;
    }

    public DeelnemersCategorie createDeelnemersCategorie(Node node) {
        DeelnemersCategorie bean = new DeelnemersCategorie();
        bean.setId(node.getStringValue("number"));
        bean.setAantalPlaatsenPerDeelnemer(node.getIntValue("aantal_per_deelnemer"));
        bean.setGroepsExcursie(node.getBooleanValue("groepsactiviteit"));
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            bean.setNaam(naam);
        }
        return bean;
    }

    public Natuurgebied createNatuurgebied(Node node) {
        Natuurgebied bean = new Natuurgebied();
        bean.setId(node.getStringValue("number"));
        // titel is verplicht in database maar naam wordt in de code gebruikt
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            bean.setNaam(naam);
        } else {
            String titel = node.getStringValue("titel");
            bean.setNaam(titel);
        }
        return bean;
    }

    public Vertrekpunt createVertrekpunt(Node node) {
        Vertrekpunt bean = new Vertrekpunt();
        bean.setId(node.getStringValue("number"));
        String titel = node.getStringValue("titel");
        if (!isEmpty(titel)) {
            bean.setTitel(titel);
        }
        String intro = node.getStringValue("intro");
        if (!isEmpty(intro)) {
            bean.setIntro(intro);
        }
        String tekst = node.getStringValue("tekst");
        if (!isEmpty(tekst)) {
            bean.setOmschrijving(tekst);
        }
        return bean;
    }

    public Event createEvent(Node node) {
        Event bean = new Event();
        String eventNumber = node.getStringValue("number");
        bean.setId(eventNumber);
        bean.setTitel(node.getStringValue("titel"));
        bean.setAanvangstijd(toDate(node.getLongValue("begindatum")));
        bean.setEindtijd(toDate(node.getLongValue("einddatum")));
        String omschrijving = node.getStringValue("omschrijving");
        if (!isEmpty(omschrijving)) {
            bean.setOmschrijving(omschrijving);
        }
        Cloud cloud = node.getCloud();
        String parentNumber = nl.leocms.evenementen.Evenement.findParentNumber(eventNumber);
        Node imageNode = ActiviteitenHelper.getFoto(cloud, parentNumber);
        if (imageNode != null) {
            bean.setFoto(createFoto(imageNode));
        }
        Node parentEvent = cloud.getNode(parentNumber);
        boolean volgeboekt = nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent, node);
        boolean aanmeldingGesloten = nl.leocms.evenementen.Evenement.subscriptionClosed(parentEvent, node);
        bean.setVolgeboekt(volgeboekt);
        bean.setAanmeldingGesloten(aanmeldingGesloten);
        return bean;
    }
    
    public Foto createFoto(Node node) {
        Foto foto = new Foto();
        foto.setId(node.getStringValue("number"));
        foto.setTitel(node.getStringValue("titel"));
        foto.setMimeType(node.getStringValue("itype"));
        foto.setData(node.getByteValue("handle"));
        return foto;
    }

    public EventDetails createEventDetails(Node node) {
        EventDetails bean = new EventDetails();
        bean.setId(node.getStringValue("number"));
        bean.setTitel(node.getStringValue("titel"));
        String omschrijving = node.getStringValue("omschrijving");
        if (!isEmpty(omschrijving)) {
            bean.setKorteOmschrijving(omschrijving);
        }
        String tekst = node.getStringValue("tekst");
        if (!isEmpty(tekst)) {
            bean.setVolledigeOmschrijving(tekst);
        }
        
        bean.setAanvangstijd(toDate(node.getLongValue("begindatum")));
        bean.setEindtijd(toDate(node.getLongValue("einddatum")));
        NodeList eventTypeNodeList = node.getRelatedNodes("evenement_type");
        bean.setEventTypeId(createLijst(eventTypeNodeList));
        NodeList natuurgebiedenNodeList = node.getRelatedNodes("natuurgebieden");
        bean.setNatuurgebiedId(createLijst(natuurgebiedenNodeList));
        NodeList extraInfoNodeList = node.getRelatedNodes("extra_info");
        bean.setExtraInfo(createLijst(extraInfoNodeList));
        NodeList vertrekpuntenNodeList = node.getRelatedNodes("vertrekpunten");
        bean.setVertrekpuntId(createLijst(vertrekpuntenNodeList));
        
        NodeList deelnemersCategorieNodeList = node.getRelatedNodes("deelnemers_categorie", "posrel", "both"); // posrel
        bean.setKosten(createKosten(node, deelnemersCategorieNodeList));
        
        Cloud cloud = node.getCloud();
        String eventNumber = node.getStringValue("number");
        
        NodeList medewerkerList = cloud.getList(eventNumber, "evenement,readmore,medewerkers", "medewerkers.email", null, null, null, "destination", true);
        if (medewerkerList != null && !medewerkerList.isEmpty()) {
            Node medewerkerNode= medewerkerList.getNode(0);
             String email = medewerkerNode.getStringValue("medewerkers.email");
             if (!isEmpty(email)) {
                 bean.setContactPersoon("email");
             }
        }
        
        Node imageNode = ActiviteitenHelper.getFoto(cloud, eventNumber);
        if (imageNode != null) {
            bean.setFoto(createFoto(imageNode));
        }
        Set childEvents = ActiviteitenHelper.getChildEvents(cloud, eventNumber);
        List data = new ArrayList();
        if (childEvents.isEmpty()) {
            bean.setEenmaligEvent(true);
            // no childs put info of parent in EventData
            EventData eventData = createEventData(node, node);
            data.add(eventData);
        } else {
            for (Iterator iter = childEvents.iterator(); iter.hasNext();) {
                String childNumber = (String) iter.next();
                Node childNode = cloud.getNode(childNumber); 
                EventData eventData = createEventData(node, childNode);
                data.add(eventData);
            }
            bean.setEenmaligEvent(false);
        }
        bean.setEventData((EventData[])data.toArray(new EventData[data.size()]));
        return bean;
    }
    
    private EventData createEventData(Node parentNode, Node childNode) {
        EventData bean = new EventData();
        String eventNumber = childNode.getStringValue("number");
        bean.setId(eventNumber);
        boolean volgeboekt = nl.leocms.evenementen.Evenement.isFullyBooked(parentNode, childNode);
        bean.setVolgeboekt(volgeboekt);
        boolean aanmeldingGesloten = nl.leocms.evenementen.Evenement.subscriptionClosed(parentNode, childNode);
        bean.setAanmeldingGesloten(aanmeldingGesloten);
        int aantalBeschikbarePlaatsen = ActiviteitenHelper.getAantalBeschikbarePlaatsen(parentNode, childNode);
        bean.setAantalPlaatsenBeschikbaar(aantalBeschikbarePlaatsen);
        bean.setAanvangstijd(toDate(childNode.getLongValue("begindatum")));
        bean.setEindtijd(toDate(childNode.getLongValue("einddatum")));
        boolean canceled = childNode.getBooleanValue("iscanceled");
        bean.setGeannuleerd(canceled);
        return bean;
    }
    
    private String[] createLijst(NodeList nodeList) {
        List list = new ArrayList();
        for (NodeIterator iter = nodeList.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            list.add(node.getStringValue("number"));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    private Kosten[] createKosten(Node event, NodeList nodeList) {
        List list = new ArrayList();
        for (NodeIterator iter = nodeList.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            logger.debug("Kosten node: " + node.getNumber());
            Kosten kosten = new Kosten();
            kosten.setDeelnemersCategorieId(node.getStringValue("number"));

            // de kosten staan in posrel.pos!!, rijp voor http://thedailywtf.com/
            NodeManager manager = node.getCloud().getNodeManager("posrel");
            String snumber = event.getStringValue("number");
            String dnumber = node.getStringValue("number");
            String constraint = "dnumber=" + dnumber + " and snumber=" + snumber;
            NodeList relations = manager.getList(constraint, null, null);
            if (relations != null && relations.size() > 0) {
                Node relation = relations.getNode(0);
                logger.debug("posrel.pos: " + relation.getStringValue("pos"));
                kosten.setKostenInCenten(relation.getIntValue("pos"));
            } else {
                kosten.setKostenInCenten(-1);
            }
            list.add(kosten);
        }
        return (Kosten[]) list.toArray(new Kosten[list.size()]);
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    private Date toDate(long cadDatum) {
        Date date = new Date();
        date.setTime(cadDatum * 1000L);
        return date;
    }

}
