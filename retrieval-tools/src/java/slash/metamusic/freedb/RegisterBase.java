/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;
import slash.metamusic.util.FileCache;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Base class to register CDs and validate queries to FreeDB.
 *
 * @author Christian Pesch
 * @version $Id: RegisterBase.java 743 2006-03-17 13:49:36Z cpesch $
 */

abstract class RegisterBase {
    /**
     * Logging output
     */
    protected static Logger log;

    static final String REGISTER_FILE_NAME = "register.ser";

    private FileCache fileCache = new FileCache();


    public RegisterBase() {
        log = Logger.getLogger(getClass().getName());
    }

    public void setCacheDirectoryName(String cacheDirectoryName) {
        fileCache.setCacheDirectoryName(cacheDirectoryName);
    }

    protected Set<DiscId> readDiscIds() throws IOException {
        return (Set<DiscId>) fileCache.getFileAsObject(REGISTER_FILE_NAME);
    }

    protected void writeDiscIds(Set discIds) throws IOException {
        fileCache.putAsObject(REGISTER_FILE_NAME, discIds);
    }
}
