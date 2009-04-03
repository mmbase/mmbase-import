package nl.didactor.component.redspider.dataobjects;
/**
 * Participant.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

public class Participant  implements java.io.Serializable {
    private java.lang.String externid;
    private java.lang.String initials;
    private java.lang.String firstname;
    private java.lang.String suffix;
    private java.lang.String lastname;
    private java.lang.String dayofbirth;
    private java.lang.String email;
    private ParticipantClasses classes;
    private java.lang.String address;
    private java.lang.String zipcode;
    private java.lang.String city;
    private java.lang.String country;
    private java.lang.String workgroups_name;
    private java.lang.String roles_name;
    private ParticipantStatusType status;

    public Participant() {
    }

    public Participant(
           java.lang.String externid,
           java.lang.String initials,
           java.lang.String firstname,
           java.lang.String suffix,
           java.lang.String lastname,
           java.lang.String dayofbirth,
           java.lang.String email,
           ParticipantClasses classes,
           java.lang.String address,
           java.lang.String zipcode,
           java.lang.String city,
           java.lang.String country,
           java.lang.String workgroups_name,
           java.lang.String roles_name,
           ParticipantStatusType status) {
           this.externid = externid;
           this.initials = initials;
           this.firstname = firstname;
           this.suffix = suffix;
           this.lastname = lastname;
           this.dayofbirth = dayofbirth;
           this.email = email;
           this.classes = classes;
           this.address = address;
           this.zipcode = zipcode;
           this.city = city;
           this.country = country;
           this.workgroups_name = workgroups_name;
           this.roles_name = roles_name;
           this.status = status;
    }


    /**
     * Gets the externid value for this Participant.
     * 
     * @return externid
     */
    public java.lang.String getExternid() {
        return externid;
    }


    /**
     * Sets the externid value for this Participant.
     * 
     * @param externid
     */
    public void setExternid(java.lang.String externid) {
        this.externid = externid;
    }


    /**
     * Gets the initials value for this Participant.
     * 
     * @return initials
     */
    public java.lang.String getInitials() {
        return initials;
    }


    /**
     * Sets the initials value for this Participant.
     * 
     * @param initials
     */
    public void setInitials(java.lang.String initials) {
        this.initials = initials;
    }


    /**
     * Gets the firstname value for this Participant.
     * 
     * @return firstname
     */
    public java.lang.String getFirstname() {
        return firstname;
    }


    /**
     * Sets the firstname value for this Participant.
     * 
     * @param firstname
     */
    public void setFirstname(java.lang.String firstname) {
        this.firstname = firstname;
    }


    /**
     * Gets the suffix value for this Participant.
     * 
     * @return suffix
     */
    public java.lang.String getSuffix() {
        return suffix;
    }


    /**
     * Sets the suffix value for this Participant.
     * 
     * @param suffix
     */
    public void setSuffix(java.lang.String suffix) {
        this.suffix = suffix;
    }


    /**
     * Gets the lastname value for this Participant.
     * 
     * @return lastname
     */
    public java.lang.String getLastname() {
        return lastname;
    }


    /**
     * Sets the lastname value for this Participant.
     * 
     * @param lastname
     */
    public void setLastname(java.lang.String lastname) {
        this.lastname = lastname;
    }


    /**
     * Gets the dayofbirth value for this Participant.
     * 
     * @return dayofbirth
     */
    public java.lang.String getDayofbirth() {
        return dayofbirth;
    }


    /**
     * Sets the dayofbirth value for this Participant.
     * 
     * @param dayofbirth
     */
    public void setDayofbirth(java.lang.String dayofbirth) {
        this.dayofbirth = dayofbirth;
    }


    /**
     * Gets the email value for this Participant.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this Participant.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the classes value for this Participant.
     * 
     * @return classes
     */
    public ParticipantClasses getClasses() {
        return classes;
    }


    /**
     * Sets the classes value for this Participant.
     * 
     * @param classes
     */
    public void setClasses(ParticipantClasses classes) {
        this.classes = classes;
    }


    /**
     * Gets the address value for this Participant.
     * 
     * @return address
     */
    public java.lang.String getAddress() {
        return address;
    }


    /**
     * Sets the address value for this Participant.
     * 
     * @param address
     */
    public void setAddress(java.lang.String address) {
        this.address = address;
    }


    /**
     * Gets the zipcode value for this Participant.
     * 
     * @return zipcode
     */
    public java.lang.String getZipcode() {
        return zipcode;
    }


    /**
     * Sets the zipcode value for this Participant.
     * 
     * @param zipcode
     */
    public void setZipcode(java.lang.String zipcode) {
        this.zipcode = zipcode;
    }


    /**
     * Gets the city value for this Participant.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this Participant.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the country value for this Participant.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this Participant.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the workgroups_name value for this Participant.
     * 
     * @return workgroups_name
     */
    public java.lang.String getWorkgroups_name() {
        return workgroups_name;
    }


    /**
     * Sets the workgroups_name value for this Participant.
     * 
     * @param workgroups_name
     */
    public void setWorkgroups_name(java.lang.String workgroups_name) {
        this.workgroups_name = workgroups_name;
    }


    /**
     * Gets the roles_name value for this Participant.
     * 
     * @return roles_name
     */
    public java.lang.String getRoles_name() {
        return roles_name;
    }


    /**
     * Sets the roles_name value for this Participant.
     * 
     * @param roles_name
     */
    public void setRoles_name(java.lang.String roles_name) {
        this.roles_name = roles_name;
    }


    /**
     * Gets the status value for this Participant.
     * 
     * @return status
     */
    public ParticipantStatusType getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Participant.
     * 
     * @param status
     */
    public void setStatus(ParticipantStatusType status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Participant)) return false;
        Participant other = (Participant) obj;
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
            ((this.initials==null && other.getInitials()==null) || 
             (this.initials!=null &&
              this.initials.equals(other.getInitials()))) &&
            ((this.firstname==null && other.getFirstname()==null) || 
             (this.firstname!=null &&
              this.firstname.equals(other.getFirstname()))) &&
            ((this.suffix==null && other.getSuffix()==null) || 
             (this.suffix!=null &&
              this.suffix.equals(other.getSuffix()))) &&
            ((this.lastname==null && other.getLastname()==null) || 
             (this.lastname!=null &&
              this.lastname.equals(other.getLastname()))) &&
            ((this.dayofbirth==null && other.getDayofbirth()==null) || 
             (this.dayofbirth!=null &&
              this.dayofbirth.equals(other.getDayofbirth()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.classes==null && other.getClasses()==null) || 
             (this.classes!=null &&
              this.classes.equals(other.getClasses()))) &&
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.zipcode==null && other.getZipcode()==null) || 
             (this.zipcode!=null &&
              this.zipcode.equals(other.getZipcode()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.workgroups_name==null && other.getWorkgroups_name()==null) || 
             (this.workgroups_name!=null &&
              this.workgroups_name.equals(other.getWorkgroups_name()))) &&
            ((this.roles_name==null && other.getRoles_name()==null) || 
             (this.roles_name!=null &&
              this.roles_name.equals(other.getRoles_name()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus())));
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
        if (getInitials() != null) {
            _hashCode += getInitials().hashCode();
        }
        if (getFirstname() != null) {
            _hashCode += getFirstname().hashCode();
        }
        if (getSuffix() != null) {
            _hashCode += getSuffix().hashCode();
        }
        if (getLastname() != null) {
            _hashCode += getLastname().hashCode();
        }
        if (getDayofbirth() != null) {
            _hashCode += getDayofbirth().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getClasses() != null) {
            _hashCode += getClasses().hashCode();
        }
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getZipcode() != null) {
            _hashCode += getZipcode().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getWorkgroups_name() != null) {
            _hashCode += getWorkgroups_name().hashCode();
        }
        if (getRoles_name() != null) {
            _hashCode += getRoles_name().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Participant.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("", ">Participant"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initials");
        elemField.setXmlName(new javax.xml.namespace.QName("", "initials"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suffix");
        elemField.setXmlName(new javax.xml.namespace.QName("", "suffix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayofbirth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dayofbirth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "classes"));
        elemField.setXmlType(new javax.xml.namespace.QName("", ">>Participant>classes"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "zipcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("", "city"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("", "country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workgroups_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "workgroups_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roles_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "roles_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("", "ParticipantStatusType"));
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
