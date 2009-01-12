/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.bridge.remote.generator;

import java.io.*;
import java.util.*;

/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id: RMMCI.java,v 1.5 2009-01-12 21:31:54 michiel Exp $
 */
public class RMMCI {

    protected String targetDir = null;
    protected String remoteDir = null;
    protected String rmiDir = null;
    protected String proxyDir = null;
    protected List<Class<?>> objectsToWrap = new ArrayList<Class<?>>();

    public RMMCI(String targetDir) {
        //check if the org/mmbase/bridge/remote dir exists
        remoteDir =  targetDir + "/org/mmbase/bridge/remote/";
        File file = new File(remoteDir);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("directory {" + file.getName() + "} does not contain a sub directory org/mmbase/bridge/remote. this is required for RemoteGenerator to work");
        }

        rmiDir =  remoteDir + "/rmi/";
        file = new File(rmiDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        proxyDir =  remoteDir + "/proxy/";
        file = new File(proxyDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public static boolean needsRemote(java.lang.reflect.Type t) {
        if (t.equals(org.mmbase.util.PublicCloneable.class)) return false;
        return t instanceof Class &&
               ((Class<?>)t).getName().startsWith("org.mmbase") &&
               ((Class<?>)t).isInterface() &&
               (!java.io.Serializable.class.isAssignableFrom(((Class<?>)t)) ||
                org.mmbase.bridge.Cloud.class.equals(t) ||
                org.mmbase.security.UserContext.class.equals(t)
                );
    }


    public void generate(Class<?> c) {
        if (needsRemote(c)) {
           objectsToWrap.add(c);
           new InterfaceGenerator(c).generate(remoteDir);
           new RmiGenerator(c).generate(rmiDir);
           new ProxyGenerator(c).generate(proxyDir);
        }
    }

    public void generateObjectWrapper() {
        new ObjectWrapperGenerator(objectsToWrap).generate(remoteDir);
    }

    public void generateBridgeClasses() throws Exception {
        // Bridge interfaces
        generate(org.mmbase.cache.Cacheable.class);
//        generate(org.mmbase.bridge.BridgeList.class);
        generate(org.mmbase.bridge.Cloud.class);
        generate(org.mmbase.bridge.CloudContext.class);
        generate(org.mmbase.bridge.Descriptor.class);
        generate(org.mmbase.bridge.Field.class);
        generate(org.mmbase.bridge.FieldIterator.class);
        generate(org.mmbase.bridge.FieldList.class);
        generate(org.mmbase.bridge.FieldValue.class);
        generate(org.mmbase.bridge.Module.class);
        generate(org.mmbase.bridge.ModuleIterator.class);
        generate(org.mmbase.bridge.ModuleList.class);
        generate(org.mmbase.bridge.Node.class);
        generate(org.mmbase.bridge.NodeIterator.class);
        generate(org.mmbase.bridge.NodeList.class);
        generate(org.mmbase.bridge.NodeManager.class);
        generate(org.mmbase.bridge.NodeManagerIterator.class);
        generate(org.mmbase.bridge.NodeManagerList.class);
        generate(org.mmbase.bridge.Relation.class);
        generate(org.mmbase.bridge.RelationIterator.class);
        generate(org.mmbase.bridge.RelationList.class);
        generate(org.mmbase.bridge.RelationManager.class);
        generate(org.mmbase.bridge.RelationManagerIterator.class);
        generate(org.mmbase.bridge.RelationManagerList.class);
        generate(org.mmbase.bridge.StringIterator.class);
        generate(org.mmbase.bridge.StringList.class);
        generate(org.mmbase.bridge.Transaction.class);
        generate(org.mmbase.bridge.Query.class);
        generate(org.mmbase.bridge.NodeQuery.class);

        // storage interfaces
        generate(org.mmbase.storage.search.Constraint.class);
        generate(org.mmbase.storage.search.Step.class);
        generate(org.mmbase.storage.search.RelationStep.class);
        generate(org.mmbase.storage.search.StepField.class);
        generate(org.mmbase.storage.search.AggregatedField.class);
        generate(org.mmbase.storage.search.SortOrder.class);
        generate(org.mmbase.storage.search.FieldNullConstraint.class);
        generate(org.mmbase.storage.search.CompareFieldsConstraint.class);
        generate(org.mmbase.storage.search.FieldValueConstraint.class);
        generate(org.mmbase.storage.search.FieldValueInConstraint.class);
        generate(org.mmbase.storage.search.FieldValueInQueryConstraint.class);
        generate(org.mmbase.storage.search.FieldValueBetweenConstraint.class);
        generate(org.mmbase.storage.search.FieldConstraint.class);
        generate(org.mmbase.storage.search.LegacyConstraint.class);
        generate(org.mmbase.storage.search.CompositeConstraint.class);
        generate(org.mmbase.storage.search.SearchQuery.class);
        generate(org.mmbase.storage.search.FieldCompareConstraint.class);

        // cache classes
/*
        generate(org.mmbase.cache.CachePolicy.class);
*/
        // datatypes classes
/*
        generate(org.mmbase.datatypes.DataType.class);
        generate(org.mmbase.datatypes.DataType.Restriction.class);
        generate(org.mmbase.datatypes.LengthDataType.class);
        generate(org.mmbase.datatypes.ComparableDataType.class);
        generate(org.mmbase.datatypes.BinaryDataType.class);
        generate(org.mmbase.datatypes.BooleanDataType.class);
        generate(org.mmbase.datatypes.DateTimeDataType.class);
        generate(org.mmbase.datatypes.DoubleDataType.class);
        generate(org.mmbase.datatypes.FloatDataType.class);
        generate(org.mmbase.datatypes.IntegerDataType.class);
        generate(org.mmbase.datatypes.ListDataType.class);
        generate(org.mmbase.datatypes.LongDataType.class);
        generate(org.mmbase.datatypes.NodeDataType.class);
        generate(org.mmbase.datatypes.NumberDataType.class);
        generate(org.mmbase.datatypes.StringDataType.class);
        generate(org.mmbase.datatypes.XmlDataType.class);
*/

        // security classes
        generate(org.mmbase.security.UserContext.class);
/*

        generate(org.mmbase.security.Rank.class);
*/
        generate(org.mmbase.security.AuthenticationData.class);

        // function classes
/*
        generate(org.mmbase.util.functions.ReturnType.class);
        generate(org.mmbase.util.functions.Parameter.class);
        generate(org.mmbase.util.functions.Parameters.class);
*/
        generate(org.mmbase.util.functions.Function.class);
/*
        generate(org.mmbase.util.LocalizedString.class);
*/
        objectsToWrap.add(org.mmbase.bridge.BridgeList.class);
        generateObjectWrapper();
    }

    public static void main(String [] argv) throws Exception{
        if (argv.length != 1) {
            System.err.println("Usage: java org.mmbase.bridge.remote.generator.RMMCI <targetdir>");
            System.exit(1);
        }
        RMMCI generator = new RMMCI(argv[0]);
        generator.generateBridgeClasses();
    }

}

