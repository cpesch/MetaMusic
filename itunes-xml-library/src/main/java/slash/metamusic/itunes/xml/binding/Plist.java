//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.12.16 at 11:08:45 PM CET
//


package slash.metamusic.itunes.xml.binding;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * <p>Java class for plist element declaration.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;element name="plist">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;choice>
 *           &lt;element ref="{http://www.w3.org/namespace/}array"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}data"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}date"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}dict"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}real"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}integer"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}string"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}true"/>
 *           &lt;element ref="{http://www.w3.org/namespace/}false"/>
 *         &lt;/choice>
 *         &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "array",
        "data",
        "date",
        "dict",
        "real",
        "integer",
        "string",
        "_true",
        "_false"
})
@XmlRootElement(name = "plist")
public class Plist {

    @XmlElement
    protected Array array;
    @XmlElement
    protected byte[] data;
    @XmlElement
    protected XMLGregorianCalendar date;
    @XmlElement
    protected Dict dict;
    @XmlElement
    protected BigDecimal real;
    @XmlElement
    protected BigInteger integer;
    @XmlElement
    protected String string;
    @XmlElement(name = "true")
    protected Object _true;
    @XmlElement(name = "false")
    protected Object _false;
    @XmlAttribute(required = true)
    protected String version;

    /**
     * Gets the value of the array property.
     *
     * @return possible object is
     *         {@link Array }
     */
    public Array getArray() {
        return array;
    }

    /**
     * Sets the value of the array property.
     *
     * @param value allowed object is
     *              {@link Array }
     */
    public void setArray(Array value) {
        this.array = value;
    }

    /**
     * Gets the value of the data property.
     *
     * @return possible object is
     *         byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value allowed object is
     *              byte[]
     */
    public void setData(byte[] value) {
        this.data = value;
    }

    /**
     * Gets the value of the date property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the dict property.
     *
     * @return possible object is
     *         {@link Dict }
     */
    public Dict getDict() {
        return dict;
    }

    /**
     * Sets the value of the dict property.
     *
     * @param value allowed object is
     *              {@link Dict }
     */
    public void setDict(Dict value) {
        this.dict = value;
    }

    /**
     * Gets the value of the real property.
     *
     * @return possible object is
     *         {@link BigDecimal }
     */
    public BigDecimal getReal() {
        return real;
    }

    /**
     * Sets the value of the real property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setReal(BigDecimal value) {
        this.real = value;
    }

    /**
     * Gets the value of the integer property.
     *
     * @return possible object is
     *         {@link BigInteger }
     */
    public BigInteger getInteger() {
        return integer;
    }

    /**
     * Sets the value of the integer property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setInteger(BigInteger value) {
        this.integer = value;
    }

    /**
     * Gets the value of the string property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getString() {
        return string;
    }

    /**
     * Sets the value of the string property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setString(String value) {
        this.string = value;
    }

    /**
     * Gets the value of the true property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getTrue() {
        return _true;
    }

    /**
     * Sets the value of the true property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setTrue(Object value) {
        this._true = value;
    }

    /**
     * Gets the value of the false property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getFalse() {
        return _false;
    }

    /**
     * Sets the value of the false property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setFalse(Object value) {
        this._false = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
