/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JaxbUtils {
    public static DatatypeFactory DATATYPE_FACTORY = newDataTypeFactory();

    public static JAXBContext newContext(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Marshaller newMarshaller(JAXBContext context, String... uriToPrefix) {
        try {
            Marshaller result = context.createMarshaller();
            result.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            result.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl(map(uriToPrefix)));
            return result;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Unmarshaller newUnmarshaller(JAXBContext context) {
        try {
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> map(String... keyValue) {
        HashMap<String, String> result = new LinkedHashMap<String, String>();
        for (int i = 0; i < keyValue.length; i += 2) result.put(keyValue[i], keyValue[i + 1]);
        return result;
    }

    private static DatatypeFactory newDataTypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException();
        }
    }

    public static String getResource(Class<?> c, String name) {
        try {
            return c.getResource(name).toURI().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
