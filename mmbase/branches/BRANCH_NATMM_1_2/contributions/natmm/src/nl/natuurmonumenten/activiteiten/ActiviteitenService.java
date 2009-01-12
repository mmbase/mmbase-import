package nl.natuurmonumenten.activiteiten;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.log4j.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

/**
 * WebService voor de Centrale Activiteiten Database (CAD)
 * 
 * @author rob
 *
 */
public class ActiviteitenService {
    private static Logger logger = Logger.getLogger(ActiviteitenService.class);

    public String getVersion() {
        return "0.1";
    }

    public Provincie[] getProvincies() {
        logger.debug("getProvincies");
        Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
        NodeManager manager = cloud.getNodeManager("provincies");
        NodeList list = manager.getList(null, null, null);
        List provincies = new ArrayList();
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            provincies.add(createProvincie(node));
        }
        return (Provincie[]) provincies.toArray(new Provincie[provincies.size()]);
//        if (true) throw new IllegalArgumentException("dummy");
//        Provincie[] provincies = new Provincie[0];
//        return provincies;
    }

    private Provincie createProvincie(Node node) {
        Provincie provincie = new Provincie();
        provincie.setId(node.getStringValue("number"));
        // titel is verplicht in database maar naam wordt in de code gebruikt
        String naam = node.getStringValue("naam");
        if (!isEmpty(naam)) {
            provincie.setNaam(naam);
        } else {
            String titel = node.getStringValue("titel");
            provincie.setNaam(titel);
        }
        String omschrijving = node.getStringValue("omschrijving");
        if (!isEmpty(omschrijving)) {
            provincie.setOmschrijving(omschrijving);
        }
        return provincie;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
