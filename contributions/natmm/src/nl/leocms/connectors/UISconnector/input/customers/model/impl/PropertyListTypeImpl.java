//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.17 at 08:40:55 PM MSD 
//


package nl.leocms.connectors.UISconnector.input.customers.model.impl;

public class PropertyListTypeImpl implements nl.leocms.connectors.UISconnector.input.customers.model.PropertyListType, com.sun.xml.bind.JAXBObject, nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.UnmarshallableObject, nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.XMLSerializable, nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.ValidatableObject
{

    protected com.sun.xml.bind.util.ListImpl _Property;
    public final static java.lang.Class version = (nl.leocms.connectors.UISconnector.input.customers.model.impl.JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static java.lang.Class PRIMARY_INTERFACE_CLASS() {
        return (nl.leocms.connectors.UISconnector.input.customers.model.PropertyListType.class);
    }

    protected com.sun.xml.bind.util.ListImpl _getProperty() {
        if (_Property == null) {
            _Property = new com.sun.xml.bind.util.ListImpl(new java.util.ArrayList());
        }
        return _Property;
    }

    public java.util.List getProperty() {
        return _getProperty();
    }

    public nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.UnmarshallingEventHandler createUnmarshaller(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.UnmarshallingContext context) {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListTypeImpl.Unmarshaller(context);
    }

    public void serializeBody(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx1 = 0;
        final int len1 = ((_Property == null)? 0 :_Property.size());
        while (idx1 != len1) {
            if (_Property.get(idx1) instanceof javax.xml.bind.Element) {
                context.childAsBody(((com.sun.xml.bind.JAXBObject) _Property.get(idx1 ++)), "Property");
            } else {
                context.startElement("", "property");
                int idx_0 = idx1;
                context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Property.get(idx_0 ++)), "Property");
                context.endNamespaceDecls();
                int idx_1 = idx1;
                context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Property.get(idx_1 ++)), "Property");
                context.endAttributes();
                context.childAsBody(((com.sun.xml.bind.JAXBObject) _Property.get(idx1 ++)), "Property");
                context.endElement();
            }
        }
    }

    public void serializeAttributes(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx1 = 0;
        final int len1 = ((_Property == null)? 0 :_Property.size());
        while (idx1 != len1) {
            if (_Property.get(idx1) instanceof javax.xml.bind.Element) {
                context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Property.get(idx1 ++)), "Property");
            } else {
                idx1 += 1;
            }
        }
    }

    public void serializeURIs(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx1 = 0;
        final int len1 = ((_Property == null)? 0 :_Property.size());
        while (idx1 != len1) {
            if (_Property.get(idx1) instanceof javax.xml.bind.Element) {
                context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Property.get(idx1 ++)), "Property");
            } else {
                idx1 += 1;
            }
        }
    }

    public java.lang.Class getPrimaryInterface() {
        return (nl.leocms.connectors.UISconnector.input.customers.model.PropertyListType.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        if (schemaFragment == null) {
            schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize((
 "\u00ac\u00ed\u0000\u0005sr\u0000 com.sun.msv.grammar.OneOrMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.s"
+"un.msv.grammar.UnaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\u0003expt\u0000 Lcom/sun/msv/gram"
+"mar/Expression;xr\u0000\u001ecom.sun.msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0002"
+"L\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000"
+"~\u0000\u0002xpppsr\u0000\u001dcom.sun.msv.grammar.ChoiceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.s"
+"un.msv.grammar.BinaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1q\u0000~\u0000\u0002L\u0000\u0004exp2q\u0000~\u0000\u0002x"
+"q\u0000~\u0000\u0003ppsr\u0000\'com.sun.msv.grammar.trex.ElementPattern\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000"
+"\u0001L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000\u001ecom.sun.m"
+"sv.grammar.ElementExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002Z\u0000\u001aignoreUndeclaredAttribute"
+"sL\u0000\fcontentModelq\u0000~\u0000\u0002xq\u0000~\u0000\u0003pp\u0000sq\u0000~\u0000\u0006ppsq\u0000~\u0000\u0000sr\u0000\u0011java.lang.Bo"
+"olean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000psr\u0000 com.sun.msv.grammar.Attribut"
+"eExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClassq\u0000~\u0000\nxq\u0000~\u0000\u0003q\u0000~\u0000\u0010psr\u00002c"
+"om.sun.msv.grammar.Expression$AnyStringExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000"
+"xq\u0000~\u0000\u0003sq\u0000~\u0000\u000f\u0001psr\u0000 com.sun.msv.grammar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000"
+"\u0000xr\u0000\u001dcom.sun.msv.grammar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.m"
+"sv.grammar.Expression$EpsilonExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003q\u0000~\u0000"
+"\u0015psr\u0000#com.sun.msv.grammar.SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocal"
+"Namet\u0000\u0012Ljava/lang/String;L\u0000\fnamespaceURIq\u0000~\u0000\u001cxq\u0000~\u0000\u0017t\u0000@nl.leo"
+"cms.connectors.UISconnector.input.customers.model.Propertyt\u0000"
+"+http://java.sun.com/jaxb/xjc/dummy-elementssq\u0000~\u0000\tpp\u0000sr\u0000\u001fcom"
+".sun.msv.grammar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0007ppsq\u0000~\u0000\tpp\u0000sq\u0000~"
+"\u0000\u0006ppsq\u0000~\u0000\u0000q\u0000~\u0000\u0010psq\u0000~\u0000\u0011q\u0000~\u0000\u0010pq\u0000~\u0000\u0014q\u0000~\u0000\u0018q\u0000~\u0000\u001asq\u0000~\u0000\u001bt\u0000Dnl.leocm"
+"s.connectors.UISconnector.input.customers.model.PropertyType"
+"q\u0000~\u0000\u001fsq\u0000~\u0000\u0006ppsq\u0000~\u0000\u0011q\u0000~\u0000\u0010psr\u0000\u001bcom.sun.msv.grammar.DataExp\u0000\u0000\u0000\u0000"
+"\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0002"
+"L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0003ppsr\u0000\"com.sun.m"
+"sv.datatype.xsd.QnameType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype"
+".xsd.BuiltinAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xs"
+"d.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSData"
+"typeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\u001cL\u0000\btypeNameq\u0000~\u0000\u001cL\u0000\nwh"
+"iteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt"
+"\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0005QNamesr\u00005com.sun.msv.da"
+"tatype.xsd.WhiteSpaceProcessor$Collapse\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,com.su"
+"n.msv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.s"
+"un.msv.grammar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003"
+"ppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000"
+"\u001cL\u0000\fnamespaceURIq\u0000~\u0000\u001cxpq\u0000~\u00006q\u0000~\u00005sq\u0000~\u0000\u001bt\u0000\u0004typet\u0000)http://www."
+"w3.org/2001/XMLSchema-instanceq\u0000~\u0000\u001asq\u0000~\u0000\u001bt\u0000\bpropertyt\u0000\u0000sr\u0000\"c"
+"om.sun.msv.grammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lc"
+"om/sun/msv/grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.m"
+"sv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0003\u0000\u0003I\u0000\u0005countB\u0000\rst"
+"reamVersionL\u0000\u0006parentt\u0000$Lcom/sun/msv/grammar/ExpressionPool;x"
+"p\u0000\u0000\u0000\b\u0001pq\u0000~\u0000\u000eq\u0000~\u0000%q\u0000~\u0000\rq\u0000~\u0000$q\u0000~\u0000\"q\u0000~\u0000\u0005q\u0000~\u0000)q\u0000~\u0000\bx"));
        }
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.AbstractUnmarshallingEventHandlerImpl
    {


        public Unmarshaller(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.UnmarshallingContext context) {
            super(context, "----");
        }

        protected Unmarshaller(nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.UnmarshallingContext context, int startState) {
            this(context);
            state = startState;
        }

        public java.lang.Object owner() {
            return nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListTypeImpl.this;
        }

        public void enterElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  1 :
                        if (("propertyId" == ___local)&&("" == ___uri)) {
                            _getProperty().add(((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyTypeImpl) spawnChildFromEnterElement((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyTypeImpl.class), 2, ___uri, ___local, ___qname, __atts)));
                            return ;
                        }
                        break;
                    case  0 :
                        if (("property" == ___local)&&("" == ___uri)) {
                            _getProperty().add(((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl) spawnChildFromEnterElement((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl.class), 3, ___uri, ___local, ___qname, __atts)));
                            return ;
                        }
                        if (("property" == ___local)&&("" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 1;
                            return ;
                        }
                        break;
                    case  3 :
                        if (("property" == ___local)&&("" == ___uri)) {
                            _getProperty().add(((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl) spawnChildFromEnterElement((nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl.class), 3, ___uri, ___local, ___qname, __atts)));
                            return ;
                        }
                        if (("property" == ___local)&&("" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 1;
                            return ;
                        }
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                }
                super.enterElement(___uri, ___local, ___qname, __atts);
                break;
            }
        }

        public void leaveElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                    case  2 :
                        if (("property" == ___local)&&("" == ___uri)) {
                            context.popAttributes();
                            state = 3;
                            return ;
                        }
                        break;
                }
                super.leaveElement(___uri, ___local, ___qname);
                break;
            }
        }

        public void enterAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromEnterAttribute(___uri, ___local, ___qname);
                        return ;
                }
                super.enterAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void leaveAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromLeaveAttribute(___uri, ___local, ___qname);
                        return ;
                }
                super.leaveAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void handleText(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                try {
                    switch (state) {
                        case  3 :
                            revertToParentFromText(value);
                            return ;
                    }
                } catch (java.lang.RuntimeException e) {
                    handleUnexpectedTextException(value, e);
                }
                break;
            }
        }

    }

}
