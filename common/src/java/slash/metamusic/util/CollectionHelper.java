/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.util.*;

/**
 * Some useful functions for
 * <ul>
 * <li>collections containing collections
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: CollectionHelper.java 203 2004-03-18 14:33:32Z cpesch $
 */

public class CollectionHelper {

    public static boolean containIntersection(Collection first, Collection second) {
        for (Iterator iterator = first.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if (second.contains(o))
                return true;
        }
        return false;
    }

    // --- helper for collections of objects and collections -------------------

    public static boolean equals(Object o1, Object o2) {
        if (o1 instanceof Collection && o2 instanceof Collection) {
            Collection c1 = (Collection) o1;
            Collection c2 = (Collection) o2;
            return containSameObjects(c1, c2);
        }
        return o1.equals(o2);
    }

    public static boolean containSameObjects(Collection c1, Collection c2) {
        if (c1.size() != c2.size())
            return false;

        // quadratic complexity
        for (Iterator iterator1 = c1.iterator(); iterator1.hasNext();) {
            Object o1 = iterator1.next();

            // for simple object equality
            if (c2.contains(o1))
                continue;

            boolean found = false;
            for (Iterator iterator2 = c2.iterator(); iterator2.hasNext();) {
                Object o2 = iterator2.next();

                if (equals(o1, o2)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                return false;
        }
        return true;
    }

    public static boolean containObjectsInSameOrder(List l1, List l2) {
        if (!containSameObjects(l1, l2))
            return false;

        for (int i = 0; i < l1.size(); i++) {
            Object o1 = l1.get(i);
            Object o2 = l2.get(i);
            if (!equals(o1, o2))
                return false;
        }
        return true;
    }

    public static int size(Collection collection) {
        int count = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if (o instanceof Collection)
                count += size((Collection) o);
            else
                count++;
        }
        return count;
    }

    public static void add(Collection collection, Object objectToAdd) {
        if (objectToAdd instanceof Collection)
            addAll(collection, (Collection) objectToAdd);
        else
            collection.add(objectToAdd);
    }

    public static void addAll(Collection collection, Collection collectionToAdd) {
        for (Iterator iterator = collectionToAdd.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            add(collection, o);
        }
    }


    public static List asListOfLists(Object[] objects) {
        List list = new ArrayList(1);
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (!(object instanceof Collection)) {
                object = asListOf(object);
            }
            list.add(object);
        }
        return list;
    }

    public static List asListOf(Object object) {
        return Arrays.asList(new Object[]{object});
    }
}
