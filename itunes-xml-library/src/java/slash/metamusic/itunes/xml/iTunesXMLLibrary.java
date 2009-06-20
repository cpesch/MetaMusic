/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.xml;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.xml.sax.ContentHandler;
import slash.metamusic.itunes.xml.binding.Array;
import slash.metamusic.itunes.xml.binding.Dict;
import slash.metamusic.itunes.xml.binding.ObjectFactory;
import slash.metamusic.itunes.xml.util.JaxbUtils;

import javax.xml.bind.*;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class iTunesXMLLibrary {
    public static final JAXBContext CONTEXT = JaxbUtils.newContext(ObjectFactory.class);

    public static final Source[] SCHEMAS = {
            new StreamSource(JaxbUtils.getResource(iTunesXMLLibrary.class, "schemas/xml.xsd")),
    };

    public static Unmarshaller newUnmarshaller() {
        return JaxbUtils.newUnmarshaller(CONTEXT);
    }

    public static Marshaller newMarshaller() {
        return JaxbUtils.newMarshaller(CONTEXT
                /* "http://www.apple.com/DTDs/PropertyList-1.0.dtd", "pl", */
                /* "http://www.w3.org/2001/XMLSchema-instance", "xsi" */
        );
    }

    public static <T> void marshal(String uri, String localName, Class<T> c, T o, OutputStream out) throws JAXBException {
        // work around iTunes bug: xml declaration has to exist and declare UTF-8, but content is encoded with systems local encoding
        try {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("ISO8859-1"));
        } catch (IOException e) {
            throw new JAXBException("Could not write xml declaration: " + e.getMessage(), e);
        }
        Marshaller marshaller = newMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, System.getProperty("file.encoding"));
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(new JAXBElement<T>(new QName(uri, localName), c, o), out);
    }

    public static <T> void marshal(String uri, String localName, Class<T> c, T o, ContentHandler out) throws JAXBException {
        newMarshaller().marshal(new JAXBElement<T>(new QName(uri, localName), c, o), out);
    }

    public static <T> Object unmarshal(Class<T> c, Source source) throws JAXBException {
        return newUnmarshaller().unmarshal(source, c).getValue();
    }

    public static Map<String, Object> dictToMap(Dict dict) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Object> objects = dict.getKeyAndArrayOrData();
        for (int i = 0; i < objects.size(); i += 2) {
            JAXBElement key = (JAXBElement) objects.get(i);
            Object value = objects.get(i + 1);
            if (value instanceof JAXBElement)
                value = ((JAXBElement) value).getValue();
            result.put(key.getValue().toString(), value);
        }
        return result;
    }

    public static List<Object> arrayToList(Array array) {
        List<Object> result = new ArrayList<Object>();
        if (array != null)
            result.addAll(array.getArrayOrDataOrDate());
        return result;
    }

    protected static class Dictionary {
        private Dict delegate;

        public Dictionary(Dict value) {
            this.delegate = value;
        }

        protected Dict getDelegate() {
            return delegate;
        }

        public Object get(String key) {
            List<Object> elements = delegate.getKeyAndArrayOrData();
            for (int i = 0; i < elements.size(); i += 2) {
                JAXBElement keyElement = (JAXBElement) elements.get(i);
                String foundKey = (String) keyElement.getValue();
                if (key.equals(foundKey)) {
                    Object value = elements.get(i + 1);
                    if (value instanceof JAXBElement)
                        return ((JAXBElement) value).getValue();
                    else if (value instanceof Array)
                        return ((Array) value).getArrayOrDataOrDate();
                    else
                        throw new IllegalArgumentException("Type " + value.getClass() + " not supported");
                }
            }
            return null;
        }

        public void put(String key, Object value) {
            List<Object> elements = this.delegate.getKeyAndArrayOrData();
            for (int i = 0; i < elements.size(); i += 2) {
                JAXBElement keyElement = (JAXBElement) elements.get(i);
                String foundKey = (String) keyElement.getValue();
                if (key.equals(foundKey)) {
                    JAXBElement valueElement = (JAXBElement) elements.get(i + 1);
                    valueElement.setValue(value);
                    return;
                }
            }
            ObjectFactory objectFactory = new ObjectFactory();
            elements.add(objectFactory.createKey(key));
            if (value instanceof BigInteger)
                elements.add(objectFactory.createInteger((BigInteger) value));
            else if (value instanceof XMLGregorianCalendar)
                elements.add(objectFactory.createDate((XMLGregorianCalendar) value));
            else
                throw new IllegalArgumentException("Type " + value + " not supported");
        }

        public String toString() {
            List<Object> elements = delegate.getKeyAndArrayOrData();
            return super.toString() + " " + elements;
        }
    }

    public static class Track extends Dictionary {
        private Dict parent;
        private JAXBElement key;

        public Track(Dict parent, JAXBElement key, Dict value) {
            super(value);
            this.parent = parent;
            this.key = key;
        }

        public int getId() {
            BigInteger rating = (BigInteger) get("Track ID");
            return rating != null ? rating.intValue() : null;
        }

        public Integer getRating() {
            BigInteger rating = (BigInteger) get("Rating");
            return rating != null ? rating.intValue() : null;
        }

        public void setRating(int rating) {
            put("Rating", new BigInteger(Integer.toString(rating)));
        }

        public Integer getPlayCount() {
            BigInteger playCount = (BigInteger) get("Play Count");
            return playCount != null ? playCount.intValue() : null;
        }

        public void setPlayCount(int playCount) {
            put("Play Count", new BigInteger(Integer.toString(playCount)));
        }

        public Calendar getPlayTime() {
            XMLGregorianCalendar playDate = (XMLGregorianCalendar) get("Play Date UTC");
            if (playDate == null)
                return null;
            Calendar calendar = playDate.toGregorianCalendar();
            // convert UTC to default time zone
            calendar.setTimeZone(TimeZone.getDefault());
            return calendar;
        }

        public void setPlayTime(Calendar playTime) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(playTime.getTime());
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            // convert default time zone time to UTC
            int offset = TimeZone.getDefault().getOffset(playTime.getTimeInMillis());
            calendar.add(Calendar.MILLISECOND, offset);
            put("Play Date UTC", new XMLGregorianCalendarImpl(calendar));
            put("Play Date", new BigInteger(Long.toString(calendar.getTimeInMillis())));
        }

        public Calendar getModificationTime() {
            XMLGregorianCalendar modificationDate = (XMLGregorianCalendar) get("Date Modified");
            if (modificationDate == null)
                return null;
            return modificationDate.toGregorianCalendar();
        }

        public Calendar getSynchronizationTime() {
            XMLGregorianCalendar synchronizationDate = (XMLGregorianCalendar) get("Date Synced");
            if (synchronizationDate == null)
                return null;
            return synchronizationDate.toGregorianCalendar();
        }

        public void setSynchronizationTime(Calendar synchronizationTime) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(synchronizationTime.getTime());
            put("Date Synced", new XMLGregorianCalendarImpl(calendar));
        }

        public URL getLocation() {
            String location = (String) get("Location");
            try {
                if (location != null) {
                    location = location.replaceAll("\\+", "%2B");
                    location = URLDecoder.decode(location, "UTF-8");
                    return new URL(location);
                }
            } catch (IOException e) {
                // intentionally left empty
            }
            return null;
        }

        public boolean delete() {
            List<Object> data = parent.getKeyAndArrayOrData();
            return data.remove(key) && data.remove(getDelegate());
        }
    }

    public static class Playlist extends Dictionary {

        public Playlist(Dict value) {
            super(value);
        }

        public String getName() {
            return (String) get("Name");
        }

        public int delete(Set<Track> accessibleTracks) {
            List<Object> items = (List<Object>) get("Playlist Items");
            if (items == null)
                return -1;
            List<Object> removedItems = new ArrayList<Object>();
            for (Object item : items) {
                Dict dict = (Dict) item;
                Map<String, Object> map = dictToMap(dict);
                int trackId = ((BigInteger) map.get("Track ID")).intValue();
                if (findTrackById(accessibleTracks, trackId) == null) {
                    removedItems.add(item);
                }
            }
            for (Object removedItem : removedItems) {
                items.remove(removedItem);
            }
            return removedItems.size();
        }

        private Track findTrackById(Set<Track> tracks, int trackId) {
            for (Track track : tracks) {
                if (track.getId() == trackId)
                    return track;
            }
            return null;
        }
    }
}
