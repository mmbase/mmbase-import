package nl.didactor.component.redspider.dataobjects;

/**
 * ParticipantResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

public class ParticipantResponse  implements java.io.Serializable
{
    public static String sResultCodeSuccess = "success";
    public static String sResultCodeWarning = "warning";
    public static String sResultCodeError   = "error";

    private java.lang.String externid;
    private java.lang.String action;
    private java.lang.String resultcode;
    private java.lang.String message;

    public ParticipantResponse() {
    }

    public ParticipantResponse(
           java.lang.String externid,
           java.lang.String action,
           java.lang.String resultcode,
           java.lang.String message) {
           this.externid = externid;
           this.action = action;
           this.resultcode = resultcode;
           this.message = message;
    }


    /**
     * Gets the externid value for this ParticipantResponse.
     *
     * @return externid
     */
    public java.lang.String getExternid() {
        return externid;
    }


    /**
     * Sets the externid value for this ParticipantResponse.
     *
     * @param externid
     */
    public void setExternid(java.lang.String externid) {
        this.externid = externid;
    }


    /**
     * Gets the action value for this ParticipantResponse.
     *
     * @return action
     */
    public java.lang.String getAction() {
        return action;
    }


    /**
     * Sets the action value for this ParticipantResponse.
     *
     * @param action
     */
    public void setAction(java.lang.String action) {
        this.action = action;
    }


    /**
     * Gets the resultcode value for this ParticipantResponse.
     *
     * @return resultcode
     */
    public java.lang.String getResultcode() {
        return resultcode;
    }


    /**
     * Sets the resultcode value for this ParticipantResponse.
     *
     * @param resultcode
     */
    public void setResultcode(java.lang.String resultcode) {
        this.resultcode = resultcode;
    }


    /**
     * Gets the message value for this ParticipantResponse.
     *
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this ParticipantResponse.
     *
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ParticipantResponse)) return false;
        ParticipantResponse other = (ParticipantResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.externid==null && other.getExternid()==null) ||
             (this.externid!=null &&
              this.externid.equals(other.getExternid()))) &&
            ((this.action==null && other.getAction()==null) ||
             (this.action!=null &&
              this.action.equals(other.getAction()))) &&
            ((this.resultcode==null && other.getResultcode()==null) ||
             (this.resultcode!=null &&
              this.resultcode.equals(other.getResultcode()))) &&
            ((this.message==null && other.getMessage()==null) ||
             (this.message!=null &&
              this.message.equals(other.getMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getExternid() != null) {
            _hashCode += getExternid().hashCode();
        }
        if (getAction() != null) {
            _hashCode += getAction().hashCode();
        }
        if (getResultcode() != null) {
            _hashCode += getResultcode().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("", ">ParticipantResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("action");
        elemField.setXmlName(new javax.xml.namespace.QName("", "action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
