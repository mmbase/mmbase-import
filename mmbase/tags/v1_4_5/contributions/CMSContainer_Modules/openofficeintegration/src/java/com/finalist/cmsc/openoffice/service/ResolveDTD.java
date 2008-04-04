package com.finalist.cmsc.openoffice.service;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import java.io.StringReader;

public class ResolveDTD implements EntityResolver {
    public InputSource resolveEntity (String publicId, String systemId)
    {
        if (systemId.endsWith(".dtd")){
            StringReader stringInput =
                new StringReader(" ");
            return new InputSource(stringInput);
        }
        else{
            return null;// default behavior
        }
    }
}
