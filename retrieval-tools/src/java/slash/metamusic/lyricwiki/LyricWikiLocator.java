/**
 * LyricWikiLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package slash.metamusic.lyricwiki;

public class LyricWikiLocator extends org.apache.axis.client.Service implements slash.metamusic.lyricwiki.LyricWiki {

    public LyricWikiLocator() {
    }


    public LyricWikiLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LyricWikiLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LyricWikiPort
    private java.lang.String LyricWikiPort_address = "http://lyrics.wikia.com/server.php";

    public java.lang.String getLyricWikiPortAddress() {
        return LyricWikiPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LyricWikiPortWSDDServiceName = "LyricWikiPort";

    public java.lang.String getLyricWikiPortWSDDServiceName() {
        return LyricWikiPortWSDDServiceName;
    }

    public void setLyricWikiPortWSDDServiceName(java.lang.String name) {
        LyricWikiPortWSDDServiceName = name;
    }

    public slash.metamusic.lyricwiki.LyricWikiPortType getLyricWikiPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LyricWikiPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLyricWikiPort(endpoint);
    }

    public slash.metamusic.lyricwiki.LyricWikiPortType getLyricWikiPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            slash.metamusic.lyricwiki.LyricWikiBindingStub _stub = new slash.metamusic.lyricwiki.LyricWikiBindingStub(portAddress, this);
            _stub.setPortName(getLyricWikiPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLyricWikiPortEndpointAddress(java.lang.String address) {
        LyricWikiPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (slash.metamusic.lyricwiki.LyricWikiPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                slash.metamusic.lyricwiki.LyricWikiBindingStub _stub = new slash.metamusic.lyricwiki.LyricWikiBindingStub(new java.net.URL(LyricWikiPort_address), this);
                _stub.setPortName(getLyricWikiPortWSDDServiceName());
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
        if ("LyricWikiPort".equals(inputPortName)) {
            return getLyricWikiPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:LyricWiki", "LyricWiki");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:LyricWiki", "LyricWikiPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LyricWikiPort".equals(portName)) {
            setLyricWikiPortEndpointAddress(address);
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
