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
 * <li>iterations
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: IterationHelper.java 507 2005-05-12 11:30:04Z cpesch $
 */

public class IterationHelper {

    public static <T> Set<T> toSet(Iterator<? extends T> iteration) {
        Set<T> elements = new HashSet<T>(1);
        while (iteration.hasNext()) {
            elements.add(iteration.next());
        }
        return elements;
    }

    public static <T> List<T> toList(Iterator<? extends T> iteration) {
        List<T> elements = new ArrayList<T>(1);
        while (iteration.hasNext()) {
            elements.add(iteration.next());
        }
        return elements;
    }

    public static int size(Iterator iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
}
