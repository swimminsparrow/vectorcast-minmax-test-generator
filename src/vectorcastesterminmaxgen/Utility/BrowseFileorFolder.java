package vectorcastesterminmaxgen.Utility;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javafx.event.Event;
import javafx.scene.control.Control;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import vectorcastesterminmaxgen.LoggerBoy;

/**
 *
 * @author Roberto Caputo
 * @version 1 28/02/2019
 * 
 */
public class BrowseFileorFolder {
    
    /**
     * This Function simply open a folder in system explorer. (Attention: Only-Windows tested!).
     * @param folderPath : path to the folder to open
     */
    public static void openFolder(String folderPath) {
        if (folderPath != null && !folderPath.isEmpty()) {
            File dir = new File(folderPath);
            if (dir.isDirectory()) {
                try {
                    Desktop.getDesktop().open(dir);
                } catch (IOException e) {
                    LoggerBoy.logError(BrowseFileorFolder.class.getSimpleName(), "Error opening Path: " + e.getLocalizedMessage());
                }
            }
        }
    }
    
    public static Collection <File> getListOfFilesInFolder (File folder, String [] allowedExtensions){
        Collection <File> listOfFiles = new ArrayList();
        
        if (verifyIfFolderExists(folder))
            listOfFiles = FileUtils.listFiles(folder, allowedExtensions, true);
        
        return listOfFiles;
    }
    
    
    /**
     * This Function simply verifies if a PATH exists.
     * @param folderOrFile Folder or File to check if exists
     * @return TRUE: if exists, FALSE: if not
     */
    public static boolean verifyIfFolderExists(File folderOrFile){
        if (folderOrFile != null && folderOrFile.exists())
           return true;
        else {
            String path = folderOrFile==null ? "null" : folderOrFile.getPath();
            LoggerBoy.logError(BrowseFileorFolder.class.getSimpleName(), "Selected Path is not correct " + path);
            return false;
        }
    }
    
    /**
     * This Function shows a browse dialog for selecting a folder.
     * @param event Event related to action
     * @param initialDirectory Folder to show at the launch
     * @return
     */
    public static File browseFolderDialog(Event event, File initialDirectory){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        
        if (initialDirectory.exists())
            directoryChooser.setInitialDirectory(initialDirectory);
                //  Set Initial Directory if it exists  //
        
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (verifyIfFolderExists(selectedDirectory))
            return selectedDirectory;
        else return null;
        
    }

    
//     /**
//     * This Function shows a browse dialog for selecting a tst File
//     * @param event Event related to action
//     * @param initialDirectory Folder to show at the launch
//     * @return
//     */
//    public static File browseTSTFileDialog(Event event, File initialDirectory){
//        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
//        
//        FileChooser FileChooser = new FileChooser();
//                
//        FileChooser.ExtensionFilter tstExtensionFilter =
//            new FileChooser.ExtensionFilter(
//                    "TST - Environment TestCase Script (.tst)", "*.tst");
//        
//        FileChooser.getExtensionFilters().add(tstExtensionFilter);
//        
//        File selectedFile = FileChooser.showOpenDialog(stage);
//        return selectedFile;
//    }
}
