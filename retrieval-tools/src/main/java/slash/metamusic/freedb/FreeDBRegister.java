/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;
import slash.metamusic.util.OperationSystem;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * FreeDBRegister registers CDs for validate queries to
 * FreeDB by FreeDBBatch.
 *
 * @author Christian Pesch
 * @version $Id: FreeDBRegister.java 959 2007-03-11 08:21:11Z cpesch $
 */

public class FreeDBRegister extends RegisterBase {
    private File device;


    public File getDevice() {
        return device;
    }

    public void setDevice(File device) {
        this.device = device;
    }


    public void register() throws IOException {
        log.info("Reading disc id from " + getDevice().getAbsolutePath());
        DiscId discId = new DiscId(getDevice());
        log.info("Disc id is " + discId);

        FreeDBClient client = new FreeDBClient();
        if (client.isDiscIdCached(discId)) {
            log.info("Found cached result for disc id:");
            CDDBRecord[] records = client.queryDiscId(discId);
            for (int i = 0; i < records.length; i++) {
                CDDBRecord record = records[i];
                log.info(i + ". record: " + record);
                CDDBEntry entry = client.readCDInfo(record);
                log.info(i + ". entry: " + entry);
            }
        }

        Set<DiscId> discIds = readDiscIds();
        if (discIds == null)
            discIds = new HashSet<DiscId>(1);
        log.info("Read " + discIds.size() + " registered disc ids");
        if (log.isLoggable(Level.FINER))
            log.finer("Read: " + discIds);
        discIds.add(discId);
        writeDiscIds(discIds);
        log.info("Wrote " + discIds.size() + " registered disc ids");
        if (log.isLoggable(Level.FINER))
            log.finer("Wrote: " + discIds);
    }


    public static void main(String[] args) throws Exception {
        String device = args.length == 0 ? OperationSystem.getDefaultDeviceName() : args[0];
        FreeDBRegister register = new FreeDBRegister();
        register.setDevice(new File(device));
        register.register();
        System.exit(0);
    }
}
