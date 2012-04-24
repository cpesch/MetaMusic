/**
 * AlbumData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package slash.metamusic.lyricwiki;

public class AlbumData  implements java.io.Serializable {
    private java.lang.String album;

    private int year;

    private java.lang.String amazonLink;

    private java.lang.String imgUrl;

    private java.lang.String url;

    private java.lang.String[] songs;

    public AlbumData() {
    }

    public AlbumData(
           java.lang.String album,
           int year,
           java.lang.String amazonLink,
           java.lang.String imgUrl,
           java.lang.String url,
           java.lang.String[] songs) {
           this.album = album;
           this.year = year;
           this.amazonLink = amazonLink;
           this.imgUrl = imgUrl;
           this.url = url;
           this.songs = songs;
    }


    /**
     * Gets the album value for this AlbumData.
     * 
     * @return album
     */
    public java.lang.String getAlbum() {
        return album;
    }


    /**
     * Sets the album value for this AlbumData.
     * 
     * @param album
     */
    public void setAlbum(java.lang.String album) {
        this.album = album;
    }


    /**
     * Gets the year value for this AlbumData.
     * 
     * @return year
     */
    public int getYear() {
        return year;
    }


    /**
     * Sets the year value for this AlbumData.
     * 
     * @param year
     */
    public void setYear(int year) {
        this.year = year;
    }


    /**
     * Gets the amazonLink value for this AlbumData.
     * 
     * @return amazonLink
     */
    public java.lang.String getAmazonLink() {
        return amazonLink;
    }


    /**
     * Sets the amazonLink value for this AlbumData.
     * 
     * @param amazonLink
     */
    public void setAmazonLink(java.lang.String amazonLink) {
        this.amazonLink = amazonLink;
    }


    /**
     * Gets the imgUrl value for this AlbumData.
     * 
     * @return imgUrl
     */
    public java.lang.String getImgUrl() {
        return imgUrl;
    }


    /**
     * Sets the imgUrl value for this AlbumData.
     * 
     * @param imgUrl
     */
    public void setImgUrl(java.lang.String imgUrl) {
        this.imgUrl = imgUrl;
    }


    /**
     * Gets the url value for this AlbumData.
     * 
     * @return url
     */
    public java.lang.String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this AlbumData.
     * 
     * @param url
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }


    /**
     * Gets the songs value for this AlbumData.
     * 
     * @return songs
     */
    public java.lang.String[] getSongs() {
        return songs;
    }


    /**
     * Sets the songs value for this AlbumData.
     * 
     * @param songs
     */
    public void setSongs(java.lang.String[] songs) {
        this.songs = songs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AlbumData)) return false;
        AlbumData other = (AlbumData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.album==null && other.getAlbum()==null) || 
             (this.album!=null &&
              this.album.equals(other.getAlbum()))) &&
            this.year == other.getYear() &&
            ((this.amazonLink==null && other.getAmazonLink()==null) || 
             (this.amazonLink!=null &&
              this.amazonLink.equals(other.getAmazonLink()))) &&
            ((this.imgUrl==null && other.getImgUrl()==null) || 
             (this.imgUrl!=null &&
              this.imgUrl.equals(other.getImgUrl()))) &&
            ((this.url==null && other.getUrl()==null) || 
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.songs==null && other.getSongs()==null) || 
             (this.songs!=null &&
              java.util.Arrays.equals(this.songs, other.getSongs())));
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
        if (getAlbum() != null) {
            _hashCode += getAlbum().hashCode();
        }
        _hashCode += getYear();
        if (getAmazonLink() != null) {
            _hashCode += getAmazonLink().hashCode();
        }
        if (getImgUrl() != null) {
            _hashCode += getImgUrl().hashCode();
        }
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getSongs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSongs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSongs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AlbumData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:LyricWiki", "AlbumData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("album");
        elemField.setXmlName(new javax.xml.namespace.QName("", "album"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("year");
        elemField.setXmlName(new javax.xml.namespace.QName("", "year"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amazonLink");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amazonLink"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imgUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "imgUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("songs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "songs"));
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
