/**
 * DidactorLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package nl.redspider.www.Didactor.definitions;

public class DidactorLocator extends org.apache.axis.client.Service implements nl.redspider.www.Didactor.definitions.Didactor {

    public DidactorLocator() {
    }


    public DidactorLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DidactorLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DidactorPort
    private java.lang.String DidactorPort_address = "http://www.redspider.nl/Didactor";

    public java.lang.String getDidactorPortAddress() {
        return DidactorPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DidactorPortWSDDServiceName = "DidactorPort";

    public java.lang.String getDidactorPortWSDDServiceName() {
        return DidactorPortWSDDServiceName;
    }

    public void setDidactorPortWSDDServiceName(java.lang.String name) {
        DidactorPortWSDDServiceName = name;
    }

    public nl.redspider.www.Didactor.definitions.DidactorPortType getDidactorPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DidactorPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDidactorPort(endpoint);
    }

    public nl.redspider.www.Didactor.definitions.DidactorPortType getDidactorPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            nl.redspider.www.Didactor.definitions.DidactorBindingStub _stub = new nl.redspider.www.Didactor.definitions.DidactorBindingStub(portAddress, this);
            _stub.setPortName(getDidactorPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDidactorPortEndpointAddress(java.lang.String address) {
        DidactorPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (nl.redspider.www.Didactor.definitions.DidactorPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                nl.redspider.www.Didactor.definitions.DidactorBindingStub _stub = new nl.redspider.www.Didactor.definitions.DidactorBindingStub(new java.net.URL(DidactorPort_address), this);
                _stub.setPortName(getDidactorPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("DidactorPort".equals(inputPortName)) {
            return getDidactorPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.redspider.nl/Didactor/definitions/", "Didactor");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.redspider.nl/Didactor/definitions/", "DidactorPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DidactorPort".equals(portName)) {
            setDidactorPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
