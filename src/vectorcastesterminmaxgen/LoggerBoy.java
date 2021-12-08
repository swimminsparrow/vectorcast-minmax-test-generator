package vectorcastesterminmaxgen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;

/**
 * Pattern: SINGLETON.
 * @author Roberto Caputo
 * @version 0.1 
 * Logger Created 
 * @version 0.2
 * Class passed instead of classname.
 */
public class LoggerBoy {
    private static LoggerBoy logger = null;
    private static TextArea vTextAreaLogger;
    private static enum LogType {
        DEBUG,//0
        INFO,//1
        ERROR//2
    };
    
    private LoggerBoy (){}
    
    public static synchronized LoggerBoy getInstance(){
        if (logger == null)
            logger = new LoggerBoy ();
        return logger;
    }
    
    public void setLoggerTextArea (TextArea vTextAreaLogger){
        this.vTextAreaLogger = vTextAreaLogger;
    }
    
    public static void logEverywhere(Class callingClass, String message) {
        Platform.runLater(() -> {
            //  Refresh UI  //
            if (!message.isEmpty()) {
                uselog4j(callingClass.getSimpleName(), message, LogType.INFO);
                //  Get current date time   //
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String dateTimeFormatted = now.format(formatter);
                String messageFormatted = dateTimeFormatted + " - " + message + "\n";
                //  If string is not empty  //
                vTextAreaLogger.appendText(messageFormatted);
                
            }
        });
    }
    
    public static void logEverywhere(String className, String message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //  Refresh UI  //
                if (!message.isEmpty()) {
                    uselog4j(className, message, LogType.INFO);        
                    //  Get current date time   //
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String dateTimeFormatted = now.format(formatter);
                    String messageFormatted = dateTimeFormatted + " - " + message + "\n";
                    //  If string is not empty  //
                    vTextAreaLogger.appendText(messageFormatted);

                }
            }
        });
    }
    
    public static void logSilently(String className, String message){
        uselog4j(className, message, LogType.DEBUG);        
    }
    
    @Deprecated
    public static void logError (String className, String message){
        Platform.runLater(() -> {
            //  Refresh UI  //
            if (!message.isEmpty()) {
                //  Get current date time   //
                uselog4j(className, message, LogType.ERROR);        
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot continue due to:");
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }
    
    public static void logError(Class callingClass, String message) {
        Platform.runLater(() -> {
            //  Refresh UI  //
            if (!message.isEmpty()) {
                //  Get current date time   //
                uselog4j(callingClass.getSimpleName(), message, LogType.ERROR);
                UIDialogs.showErrorDialog(callingClass, "Ops...", message);
            }
        });
    }
    
    public static void logError(Class callingClass, Exception e) {
        logError(callingClass, e.getLocalizedMessage());
    }
    
    private static void uselog4j(String className, String message, LogType logType) {
        Logger logger = LogManager.getLogger(className);
        
        switch(logType){
            case INFO:
                logger.info(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            default:
                logger.debug(message);
                break;
        }
    }
}
