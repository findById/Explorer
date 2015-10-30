package org.cn.log;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Created by chenning on 2015/10/14.
 */
public class Log {

    private Log() {
    }

    public static void d(String tag, String messages) {

    }

    public static void e(String tag, String messages) {

    }

    private void log() {

    }

    private void writer(Level level, String messages) {
    }

    public static String getStack(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    class Level implements Serializable {

        /**
         * The SEVERE level provides severe failure messages.
         */
        Level SEVERE = new Level("SEVERE", 400);
        /**
         * The DEBUG level provides debug messages.
         */
        Level DEBUG = new Level("DEBUG", 300);
        /**
         * The WARNING level provides warnings.
         */
        Level WARNING = new Level("WARNING", 200);
        /**
         * The INFO level provides informative messages.
         */
        Level INFO = new Level("INFO", 100);

        public Level(String name, int level) {

        }
    }

    class LogWriterImpl {
    }

}
