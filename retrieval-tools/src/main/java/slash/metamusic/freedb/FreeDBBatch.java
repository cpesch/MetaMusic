/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * FreeDBBatch batches queries to FreeDB which had
 * been registered with FreeDBRegister.
 *
 * @author Christian Pesch
 * @version $Id: FreeDBBatch.java 914 2006-12-26 20:44:49Z cpesch $
 */

public class FreeDBBatch extends RegisterBase {

    public void batch() throws IOException {
        Set discIds = readDiscIds();
        if (discIds == null) {
            log.fine("No disc ids found");
            return;
        }

        log.fine("Batching queries for disc ids: " + discIds);

        FreeDBClient client = new FreeDBClient();

        Set<DiscId> failed = new HashSet<DiscId>(1);
        for (Object discId1 : discIds) {
            DiscId discId = (DiscId) discId1;
            log.info("Querying disc id: " + discId.getEncodedDiscId());
            CDDBRecord[] records = client.queryDiscId(discId);
            if (records.length == 0)
                failed.add(discId);
            for (CDDBRecord record : records) {
                client.readCDInfo(record);
            }
        }

        log.fine("Clearing registered disc ids");
        writeDiscIds(failed);
    }


    public static void main(String[] args) throws Exception {
        FreeDBBatch batch = new FreeDBBatch();
        batch.batch();
        System.exit(0);
    }
}
