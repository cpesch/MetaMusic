/**
 * SOTDResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package slash.metamusic.lyricwiki;

public class SOTDResult  implements java.io.Serializable {
    private java.lang.String artist;

    private java.lang.String song;

    private java.lang.String nominatedBy;

    private java.lang.String reason;

    private java.lang.String lyrics;

    public SOTDResult() {
    }

    public SOTDResult(
           java.lang.String artist,
           java.lang.String song,
           java.lang.String nominatedBy,
           java.lang.String reason,
           java.lang.String lyrics) {
           this.artist = artist;
           this.song = song;
           this.nominatedBy = nominatedBy;
           this.reason = reason;
           this.lyrics = lyrics;
    }


    /**
     * Gets the artist value for this SOTDResult.
     * 
     * @return artist
     */
    public java.lang.String getArtist() {
        return artist;
    }


    /**
     * Sets the artist value for this SOTDResult.
     * 
     * @param artist
     */
    public void setArtist(java.lang.String artist) {
        this.artist = artist;
    }


    /**
     * Gets the song value for this SOTDResult.
     * 
     * @return song
     */
    public java.lang.String getSong() {
        return song;
    }


    /**
     * Sets the song value for this SOTDResult.
     * 
     * @param song
     */
    public void setSong(java.lang.String song) {
        this.song = song;
    }


    /**
     * Gets the nominatedBy value for this SOTDResult.
     * 
     * @return nominatedBy
     */
    public java.lang.String getNominatedBy() {
        return nominatedBy;
    }


    /**
     * Sets the nominatedBy value for this SOTDResult.
     * 
     * @param nominatedBy
     */
    public void setNominatedBy(java.lang.String nominatedBy) {
        this.nominatedBy = nominatedBy;
    }


    /**
     * Gets the reason value for this SOTDResult.
     * 
     * @return reason
     */
    public java.lang.String getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this SOTDResult.
     * 
     * @param reason
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }


    /**
     * Gets the lyrics value for this SOTDResult.
     * 
     * @return lyrics
     */
    public java.lang.String getLyrics() {
        return lyrics;
    }


    /**
     * Sets the lyrics value for this SOTDResult.
     * 
     * @param lyrics
     */
    public void setLyrics(java.lang.String lyrics) {
        this.lyrics = lyrics;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SOTDResult)) return false;
        SOTDResult other = (SOTDResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.artist==null && other.getArtist()==null) || 
             (this.artist!=null &&
              this.artist.equals(other.getArtist()))) &&
            ((this.song==null && other.getSong()==null) || 
             (this.song!=null &&
              this.song.equals(other.getSong()))) &&
            ((this.nominatedBy==null && other.getNominatedBy()==null) || 
             (this.nominatedBy!=null &&
              this.nominatedBy.equals(other.getNominatedBy()))) &&
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason()))) &&
            ((this.lyrics==null && other.getLyrics()==null) || 
             (this.lyrics!=null &&
              this.lyrics.equals(other.getLyrics())));
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
        if (getArtist() != null) {
            _hashCode += getArtist().hashCode();
        }
        if (getSong() != null) {
            _hashCode += getSong().hashCode();
        }
        if (getNominatedBy() != null) {
            _hashCode += getNominatedBy().hashCode();
        }
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        if (getLyrics() != null) {
            _hashCode += getLyrics().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SOTDResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:LyricWiki", "SOTDResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("artist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "artist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("song");
        elemField.setXmlName(new javax.xml.namespace.QName("", "song"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nominatedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nominatedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lyrics");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lyrics"));
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
