/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.xml.util;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

import java.util.Map;

public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {
    private Map<String, String> uriToPrefix;

    public NamespacePrefixMapperImpl(Map<String, String> uriToPrefix) {
        this.uriToPrefix = uriToPrefix;
    }

    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return uriToPrefix.get(namespaceUri);
    }

    public String[] getPreDeclaredNamespaceUris() {
        return uriToPrefix.keySet().toArray(new String[uriToPrefix.size()]);
    }
}
