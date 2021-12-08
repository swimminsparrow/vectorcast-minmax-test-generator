package vectorcastesterminmaxgen.Configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.Utility.BrowseFileorFolderVCExtension;

/**
 * PATTERN: SINGLETON.
 * This Class contains functions that:
 *  - parses vc_assistant_discover_config.xml to get all the configuration settings
 *  - save configurations to vc_assistant_discover_config.xml 
 * 
 * @author Roberto Caputo
 * @version 0.1a    07/11/2018
 * @version 0.2.2a  10/04/2019  add Generic boolean and string get/set property methods.
 */

public class ConfigFileReader {
    public final static String ENVIRONMENT_DIR_PATH = "ENVIRONMENT_DIR_PATH";
    public final static String TST_FILE_PATH = "TST_FILE_PATH";
    public final static String VECTORCAST_INSTALL_PATH = "VECTORCAST_INSTALL_PATH";
        
    //  PARSER OPTIONS      //
    public final static String PARSER_OPTION_EXPLODE_STRUCT = "PARSER_OPTION_EXPLODE_STRUCT";
    public final static String PARSER_OPTION_DELETE_VOID_FUNCTIONS = "PARSER_OPTION_DELETE_VOID_FUNCTIONS"; 
    
    //  GENERATE OPTIONS    //
    public final static String GENERATE_OPTION_TEST_COMBINATORI = "GENERATE_OPTION_TEST_COMBINATORI";
    public final static String GENERATE_OPTION_EXECUTE_TESTCASES = "EXECUTE_TESTCASES";

        /*GENERATE_OPTION_TEST_COMBINATORI-> se TRUE genera un test per ogni parametro specificato per gli intorni destri e sinistro,
        *se FALSE genera un test INTORNO cumulativo per tutti i parametri            
        */
    //  DEFAULT VALUES      //
    public final static String DEFAULT_VECTORCAST_INSTALL_PATH =  "C:\\VCAST_2018_SP1";    
    
    
    private static ConfigFileReader currentInstance;    
    private Properties toolProps;
    
    private ConfigFileReader(){
        LoggerBoy.getInstance().logEverywhere(this.getClass().getName(), 
                "Loading configuration XML file...");
        toolProps = new Properties ();
        loadToolPropsFromXML();
    }
    
    public static ConfigFileReader getInstance(){
        if (currentInstance == null)
            currentInstance = new ConfigFileReader();
        return currentInstance;
    }
    
    /**
     * This Method stores a boolean for the specfied identifier.
     *
     * @param propertyIdentifier
     * @param booleanValue
     */
    public void storeBooleanProperty(String propertyIdentifier, boolean booleanValue){
        String value = booleanValue ? "TRUE" : "FALSE";
        toolProps.setProperty(propertyIdentifier, value);
        storeToolPropsIntoXML();
            //  Store New Property to XML file  //
    }
    
    public boolean getBooleanProperty(String propertyIdentifier) {
        boolean retValue = false;
        String retValueString = toolProps.getProperty(propertyIdentifier, null);
        if (retValueString != null && retValueString.equals("TRUE")){
            retValue = true;
        }
        return retValue;
    }

    /**
     * This Function stores the new path selected by the user for the specified tag if it exists
     * @param propertyKey
     * @param newPath 
     */
    public void storeNewPath (String propertyKey, File newPath){
        if (newPath != null && newPath.exists())
            setProperty (propertyKey, newPath.getPath());
    }
    /**
     * This Function stores the new path selected by the user for the specified tag if it exists
     * @param propertyKey
     * @param newPath 
     * @param futurePath TRUE: path will be created soon, FALSE instead.
     */
    public void storeNewPath (String propertyKey, File newPath, boolean futurePath){
        if (futurePath)
            setProperty (propertyKey, newPath.getPath());
        else storeNewPath(propertyKey, newPath);
    }
    
    @Deprecated //use storeBooleanProperty
    public String getVECTORCAST_INSTALL_PATH(){
        String retValue = toolProps.getProperty(VECTORCAST_INSTALL_PATH, null);
        if (retValue == null){
            // if null set the default value    //
            retValue = DEFAULT_VECTORCAST_INSTALL_PATH;
            toolProps.setProperty(VECTORCAST_INSTALL_PATH, retValue);
        }
            
        return retValue;
    }
    public void setVECTORCAST_INSTALL_PATH(String propertyContent){
        if (propertyContent.endsWith("\\"))
            propertyContent = propertyContent.substring(0, propertyContent.length()-1);
        toolProps.setProperty(VECTORCAST_INSTALL_PATH, propertyContent);
        storeToolPropsIntoXML();
            //  Store New Property to XML file  //
    }
    public String getENVIRONMENT_DIR_PATH(){
        String retValue = null;
        String envPathSaved = toolProps.getProperty(ENVIRONMENT_DIR_PATH, null);
        if (envPathSaved != null  && BrowseFileorFolderVCExtension.verifyIfEnvironmentFolder(new File (envPathSaved)))
            retValue = toolProps.getProperty(ENVIRONMENT_DIR_PATH, null);
        
        return retValue;
    }
    
    @Deprecated //use storeBooleanProperty
    public void setENVIRONMENT_DIR_PATH(String propertyContent){
        toolProps.setProperty(ENVIRONMENT_DIR_PATH, propertyContent);
        storeToolPropsIntoXML();
            //  Store New Property to XML file  //
    }
    @Deprecated //use storeBooleanProperty
    public String getTST_FILE_PATH(){
        return toolProps.getProperty(TST_FILE_PATH, null);
    }
    @Deprecated //use storeBooleanProperty
    public void setTST_FILE_PATH(String propertyContent){
        toolProps.setProperty(TST_FILE_PATH, propertyContent);
        storeToolPropsIntoXML();
            //  Store New Property to XML file  //
    }    
    /**
     * This Function returns a Property Specified by propertyKey param.
     * @param propertyKey Property Key to get
     * @return Property String content specified by propertyKey param
     */
    private String getProperty (String propertyKey){
        return toolProps.getProperty(propertyKey, null);
    }
    
    /**
     * This Function set a Property Specified by propertyKey param with the Content passed.
     * @param propertyKey Property Key to get
     * @param propertyContent Property String Content to set in Property
     */
    private void setProperty (String propertyKey, String propertyContent){
        toolProps.setProperty(propertyKey, propertyContent);
        storeToolPropsIntoXML();
            //  Store New Property to XML file  //
    }
    
    /**
     * This Function loads all Properties from XML Configuration File if xmlFile exists.
     */
    private void loadToolPropsFromXML() {
        File xmlFile = new File(Constants.CONFIG_XML_FILE_PATH);
        if (xmlFile.exists()) {
            try {
                toolProps.loadFromXML(new FileInputStream(xmlFile));
            } catch (FileNotFoundException ex) {
                LoggerBoy.logError(ConfigFileReader.class.getName(), ex.getMessage());
            } catch (IOException ex) {
                LoggerBoy.logError(ConfigFileReader.class.getName(), ex.getMessage());
            }
        }
    }
    
    /**
     * This Function stores all Properties into XML Configuration File
     */
    private synchronized void storeToolPropsIntoXML(){
        try {
            if (!new File(Constants.CONFIG_XML_FILE_PATH).exists()){
                    //  Create empty file if not exists //
                new File(Constants.CONFIG_XML_FILE_PATH).createNewFile();
            }
            else {
                //  If XMl file already exists reinitialize it  //
                new File(Constants.CONFIG_XML_FILE_PATH).delete();            
                new File(Constants.CONFIG_XML_FILE_PATH).createNewFile();
            }
            toolProps.storeToXML(new FileOutputStream(Constants.CONFIG_XML_FILE_PATH), "XML Configuration File containing"
                    + " main settings for VectorCast Assistant Discover");
        } catch (FileNotFoundException ex) {
            LoggerBoy.logError(ConfigFileReader.class.getName(), ex.getMessage());
        } catch (IOException ex) {
            LoggerBoy.logError(ConfigFileReader.class.getName(), ex.getMessage());
        }
    }   
}
