/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * FreeDBValidate validates the cached query results to FreeDB.
 *
 * @author Christian Pesch
 * @version $Id: FreeDBValidate.java 959 2007-03-11 08:21:11Z cpesch $
 */

public class FreeDBValidate extends RegisterBase {

    public void validate() throws IOException {
        FreeDBClient client = new FreeDBClient();
        FreeDBCache freeDBCache = new FreeDBCache();
        for (DiscId discId : freeDBCache.getCachedDiscIds()) {
            log.info("Querying disc id: " + discId.getEncodedDiscId());

            client.setUseCache(true);
            CDDBRecord[] oldRecords = client.queryDiscId(discId);
            Arrays.sort(oldRecords, new CDDBRecordComparator());

            client.setUseCache(false);
            CDDBRecord[] newRecords = client.queryDiscId(discId);
            Arrays.sort(newRecords, new CDDBRecordComparator());

            // log.fine("Old records: " + ArrayHelper.printArrayToString(oldRecords));
            // log.fine("New records: " + ArrayHelper.printArrayToString(newRecords));

            for (int i = 0; i < oldRecords.length; i++) {
                CDDBRecord oldRecord = oldRecords[i];
                if (i >= newRecords.length)
                    break;

                CDDBRecord newRecord = newRecords[i];
                if (!oldRecord.equals(newRecord))
                    log.info("New record " + newRecord + " changed from " + oldRecord);
            }
        }
    }

    protected class CDDBRecordComparator implements Comparator<CDDBRecord> {
        public int compare(CDDBRecord r1, CDDBRecord r2) {
            int result = r1.getDiscId().compareTo(r2.getDiscId());
            if (result == 0)
                result = r1.getCategory().compareTo(r2.getCategory());
            return result;
        }
    }


    public static void main(String[] args) throws Exception {
        FreeDBValidate validate = new FreeDBValidate();
        validate.validate();
        System.exit(0);
    }
}
