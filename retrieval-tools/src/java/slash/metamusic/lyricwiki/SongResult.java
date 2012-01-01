/**
 * SongResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package slash.metamusic.lyricwiki;

public class SongResult  implements java.io.Serializable {
    private java.lang.String artist;

    private java.lang.String song;

    public SongResult() {
    }

    public SongResult(
           java.lang.String artist,
           java.lang.String song) {
           this.artist = artist;
           this.song = song;
    }


    /**
     * Gets the artist value for this SongResult.
     * 
     * @return artist
     */
    public java.lang.String getArtist() {
        return artist;
    }


    /**
     * Sets the artist value for this SongResult.
     * 
     * @param artist
     */
    public void setArtist(java.lang.String artist) {
        this.artist = artist;
    }


    /**
     * Gets the song value for this SongResult.
     * 
     * @return song
     */
    public java.lang.String getSong() {
        return song;
    }


    /**
     * Sets the song value for this SongResult.
     * 
     * @param song
     */
    public void setSong(java.lang.String song) {
        this.song = song;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SongResult)) return false;
        SongResult other = (SongResult) obj;
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
              this.song.equals(other.getSong())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SongResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:LyricWiki", "SongResult"));
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
