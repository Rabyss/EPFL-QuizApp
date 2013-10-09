package epfl.sweng.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private final static boolean DEBUG_ENABLED = true;
    private final static String ENDLINE = "</ul></body></html>";
    private final static int RAF_PADDING = ENDLINE.length();

    public static void log(String message) {
        if (DEBUG_ENABLED) {
            String strLogDir = "logs/";
            File logDir = new File(strLogDir);
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            String strLogIndex = strLogDir + "log_index.html";
            File logIndex = new File(strLogIndex);
            try {
                if (!logIndex.exists()) {
                    logIndex.createNewFile();
                    printIndexBase(logIndex, "Log Index");
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat f = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                String day = f.format(calendar.getTime());
                String strDayLogDir = strLogDir + day + "/";
                File dayLogDir = new File(strDayLogDir);
                if (!dayLogDir.exists()) {
                    dayLogDir.mkdir();
                }
                String strDayLogIndex = strDayLogDir + "index" + day + ".html";
                File dayLogIndex = new File(strDayLogIndex);
                if (!dayLogIndex.exists()) {
                    dayLogIndex.createNewFile();
                    printIndexBase(dayLogIndex, "Day Index");
                }
                String strCurLog = strDayLogDir
                        + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                        + calendar.get(Calendar.MINUTE) + ":"
                        + calendar.get(Calendar.SECOND) + ":"
                        + calendar.get(Calendar.MILLISECOND);
                File curLog = new File(strCurLog);
                PrintWriter logger = new PrintWriter(new FileOutputStream(
                        curLog, true));
                logger.append(message);
                logger.flush();
                printLink(dayLogIndex, curLog, "");
                printLink(logIndex, dayLogIndex, day + "/");
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printLink(File source, File destination,
            String subFolder) throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(source, "rw");
        raf.seek(raf.length() - RAF_PADDING);
        raf.writeBytes("<li><a href=\"" + subFolder + destination.getName()
                + "\">" + destination.getName() + "</a></li>\n" + ENDLINE);
        raf.close();
    }

    private static void printIndexBase(File indexFile, String title) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(indexFile, true));
        pw.append("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n"
                + "<meta charset=\"utf-8\">" + "<title>" + title + "</title>\n"
                + "</head>\n" + "<body>\n" + "<ul>\n" + ENDLINE);
        pw.flush();
        pw.close();
    }

    public static void main(String[] args) {
        Logger.log("test");
    }

}
