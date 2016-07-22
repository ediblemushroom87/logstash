package org.logstash.log;

import org.jruby.Ruby;
import org.jruby.runtime.load.BasicLibraryService;

import java.io.IOException;

public class JrubyLoggerExtService implements BasicLibraryService {
    public boolean basicLoad(final Ruby runtime)
            throws IOException
    {
        new JrubyLoggerExtLibrary().load(runtime, false);
        return true;
    }
}

