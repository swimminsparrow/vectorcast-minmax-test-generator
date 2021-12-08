package vectorcastesterminmaxgen.Utility;

import java.io.File;
import java.util.ArrayList;
import javafx.event.Event;
import javafx.scene.control.Control;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.UIUtils.UIDialogs;

/**
 *
 * @author Roberto Caputo
 * @version 1   05/07/2018
 * @version 2   17/07/2018  Added verifyIfEnvironmentFolder function.
 * @version 3   24/07/2018
 * @version 4   06/12/2018  Various Fixes
 * @version 5   10/04/2019  getUUTList() added.
 */
public class BrowseFileorFolderVCExtension extends BrowseFileorFolder {
       
    /**
     * This Function scans environment dir to find the main subfolder dir of the env.
     * Shows an error dialog if no sub directory found.
     * @param envDir Environment Main Directory
     * @return Environment Sub Directory
     */
    public static File getEnvironmentSubFolder(File envDir) {
        File envSubDir = null;

        if (envDir != null && envDir.isDirectory()) {
            File[] envSubFolders = envDir.listFiles(File::isDirectory);
            //  Enviroment sub folders in which i have to search for main subfolder    // 

            for (File envSubFolder : envSubFolders) {
                //  search main folder of environment   //

                if (envSubFolder.getName().equals(getEnvironmentNameFromFolder(envDir))
                        && !envSubFolder.getName().endsWith(".BAK")) {
                    /*  environment subfolder must be as the .env file name
                                    AND NOT ending with .BAK
                     */
                    envSubDir = envSubFolder;
                }
            }

            if (envSubDir == null) {
                UIDialogs.showErrorDialog(BrowseFileorFolderVCExtension.class, "Cannot Find a Valid Environment SubFolder in:",
                        envDir.getPath());
            }

        }
        return envSubDir;
    }
    
    /**
     * This Function simply returns environment name after searched .env file.
     * @param envDir Environment Dir
     * @return Environment Name
     */
    public static String getEnvironmentNameFromFolder (File envDir){
        String retValue = new String();
        if (envDir != null && envDir.isDirectory()){
            // IF A VALID FOLDER    //
            File listFiles[] = envDir.listFiles(File::isFile);
            for (File file : listFiles){
                if (FilenameUtils.isExtension(file.getName(), "env"))
                    //  it contain environment project file //
                    retValue = FilenameUtils.getBaseName(file.getName());
            }
        }
        
        return retValue;
    }
    
    /**
     * This Function verifies if the specified path is a correct environment folder containing .vce file.
     * @param envDir Environment File Dir
     * @return TRUE: if Valid Environment Folder, FALSE: instead
     */
    public static boolean verifyIfEnvironmentFolder(File envDir){
        boolean validEnvironment = false;
        String envPath = null;
        
        if (envDir != null)
           envPath = envDir.getPath();
        
        if (envDir !=null && envDir.exists() && envDir.isDirectory()){
            // IF A VALID FOLDER    //
            File listFiles[] = envDir.listFiles(File::isFile);
            for (File file : listFiles){
                if (FilenameUtils.isExtension(file.getName(), "vce"))
                    //  it contain environment project file //
                    validEnvironment = true;
            }
        }
        if (!validEnvironment)
            LoggerBoy.logError(BrowseFileorFolderVCExtension.class.getName(), 
                    "This folder does not contains any valid environment:\n" + envPath);
        
        else LoggerBoy.logEverywhere(BrowseFileorFolderVCExtension.class.getName(), 
                "Valid Environment Selected: " + envPath);
        
        return validEnvironment;
    }
      
     /**
     * This Function shows a browse dialog for selecting a tst File
     * @param event Event related to action
     * @param initialDirectory Folder to show at the launch
     * @return
     */
    public static File browseTSTFileDialog(Event event, File initialDirectory){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        
        FileChooser FileChooser = new FileChooser();
                
        FileChooser.ExtensionFilter tstExtensionFilter =
            new FileChooser.ExtensionFilter(
                    "TST - Environment TestCase Script (.tst)", "*.tst");
        
        FileChooser.getExtensionFilters().add(tstExtensionFilter);
        
        File selectedFile = FileChooser.showOpenDialog(stage);
        return selectedFile;
    }
    
    /**
     * This Function verifies if the passed file is a tst script.
     *
     * @param file
     * @return
     */
    public static boolean isTSTFile(File file) {
        if (file.exists() && FilenameUtils.isExtension(file.getName(), "tst")) {
            return true;
        } else {
            UIDialogs.showErrorDialog(BrowseFileorFolderVCExtension.class, "Error", "The file path you typed is not a valid tst.");
            return false;
        }
    }
    
    /**
     * This Function parses the list of UUTs in a dir.
     *
     * @param srcDir Source Directory to scan
     * @return List of UUTs
     */
    public static ArrayList<String> getUUTList(File srcDir) {
        String[] ext = {"c"};
        ArrayList<String> uutList = new ArrayList();
        ArrayList<File> uutFileList = new ArrayList(FileUtils.listFiles(srcDir, ext, false));
        //  recursive = false to avoid infinite search  //

        LoggerBoy.logEverywhere(BrowseFileorFolderVCExtension.class.getSimpleName(), "Source Dir selected: "
                + srcDir.getPath());

        if (uutFileList.isEmpty()) {
            LoggerBoy.logError(BrowseFileorFolderVCExtension.class.getSimpleName(), "Directory does not contain any .c file");
        } else {
            for (File uutFile : uutFileList) {
                uutList.add(FilenameUtils.getBaseName(uutFile.getPath()));
                LoggerBoy.logEverywhere(BrowseFileorFolderVCExtension.class.getSimpleName(), "Found uut: " + uutList.get(uutList.size() - 1));
            }
        }

        return uutList;
    }
}
