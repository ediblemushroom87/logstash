package org.logstash.log;

/**
 * Created by tal on 7/21/16.
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyString;
import org.jruby.RubyBoolean;
import org.jruby.RubyArray;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Arity;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.Library;
import java.io.IOException;


public class JrubyLoggerExtLibrary implements Library {

    public void load(Ruby runtime, boolean wrap) throws IOException {
        RubyModule module = runtime.defineModule("LogStash");

        RubyClass clazz = runtime.defineClassUnder("Logger", runtime.getObject(), new ObjectAllocator() {
            public IRubyObject allocate(Ruby runtime, RubyClass rubyClass) {
                return new RubyLogger(runtime, rubyClass);
            }
        }, module);
    }


    @JRubyClass(name = "Logger", parent = "Object")
    public static class RubyLogger extends RubyObject {
        private Logger logger;

        public RubyLogger(Ruby runtime, RubyClass klass) {
            super(runtime, klass);
        }

        public RubyLogger(Ruby runtime) {
            this(runtime, runtime.getModule("LogStash").getClass("Logger"));
        }

        public RubyLogger(Ruby runtime, Logger logger) {
            this(runtime);
            this.logger = logger;
        }

        public static RubyLogger newRubyEvent(Ruby runtime, Logger logger) {
            return new RubyLogger(runtime, logger);
        }

        // def initialize(klazz)
        @JRubyMethod(name = "initialize", optional = 1)
        public IRubyObject ruby_initialize(ThreadContext context, IRubyObject[] args)
        {
            args = Arity.scanArgs(context.runtime, args, 0, 1);
            IRubyObject data = args[0];

            if (data == null || data.isNil()) {
                this.logger = LogManager.getLogger(data.toString());
            } else {
                throw context.runtime.newTypeError("wrong argument type " + data.getMetaClass() + " (expected Hash)");
            }

            return context.nil;
        }

        @JRubyMethod(name = "error")
        public IRubyObject ruby_error(ThreadContext context, IRubyObject[] args)
        {
            args = Arity.scanArgs(context.runtime, args, 1, 0);
            logger.error(args[0]);
            return context.nil;
        }

        @JRubyMethod(name = "trace")
        public IRubyObject ruby_uncancel(ThreadContext context)
        {
            return RubyBoolean.createFalseClass(context.runtime);
        }

        @JRubyMethod(name = "info")
        public IRubyObject ruby_cancelled(ThreadContext context)
        {
            return RubyBoolean.newBoolean(context.runtime, false);
        }

        @JRubyMethod(name = "warn", required = 1)
        public IRubyObject ruby_includes(ThreadContext context, RubyString reference)
        {
            return RubyBoolean.newBoolean(context.runtime, false);
        }
    }
}

