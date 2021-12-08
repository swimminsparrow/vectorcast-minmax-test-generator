package vectorcastesterminmaxgen.Views.Help;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import vectorcastesterminmaxgen.LoggerBoy;
import vectorcastesterminmaxgen.MenuHelp;

/**
 * This Controller handle Help View Class
 *
 * @author Roberto Caputo
 */
public class FXMLHelpController implements Initializable {

    @FXML
    private AnchorPane vMainAnchorPane;

    @FXML
    private TreeView<String> vHelpTree;

    @FXML
    private WebView vHelpContentWebView;

    /**
     * Initializes the controller class.
     */
    
    ArrayList <TreeItemExtended> treeItems = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      
        setVHelpContentWebViewContent("0.html");
        initializevHelpTree();
    }    
    
    private void initializevHelpTree() {
        
        treeItems.add(new TreeItemExtended("User Manual", 0, "0.html")); 
        treeItems.add(new TreeItemExtended("1. Configurazione", 0, "1_1.html")); 
            treeItems.add(new TreeItemExtended("1.1 Prerequisiti", 1, "1_1.html"));
            treeItems.add(new TreeItemExtended("1.2 Impostazioni Tool", 1, "1_2.html"));
            treeItems.add(new TreeItemExtended("1.3 Import/Export DB parametri", 1, "1_3.html"));
            treeItems.add(new TreeItemExtended("1.4 Reset Configurazione", 1, "1_4.html"));
        treeItems.add(new TreeItemExtended("2. Importare Parametri", 0, "2.html"));
        treeItems.add(new TreeItemExtended("3. Configurare i Parametri", 0, "3.html"));
        treeItems.add(new TreeItemExtended("4. Generazione dei Test", 0, "4.html"));
        treeItems.add(new TreeItemExtended("5. Verificare i Risultati", 0, "5.html"));

        for (TreeItemExtended treeItem : treeItems) {
            if (treeItems.indexOf(treeItem) > 0) {
                int fatherIndex = treeItem.father;
                TreeItem fatherItem = treeItems.get(fatherIndex);
                fatherItem.getChildren().addAll(treeItem);
            } else {
                treeItem.setExpanded(true);
            }
        }
        
        vHelpTree.setRoot(treeItems.get(0));
        
        vHelpTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue,
                    Object newValue) {

                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                LoggerBoy.logSilently(this.getClass().getSimpleName(), "Selected item: " + selectedItem.getValue());
                int selectedIndex = vHelpTree.getSelectionModel().getSelectedIndex();
                setVHelpContentWebViewContent(treeItems.get(selectedIndex).htmlFileName);
                
                if (selectedIndex == 1)
                    vHelpTree.getSelectionModel().select(selectedIndex + 1);
            }
        });
        
    }
    
    /**
     * This Function load an html file into the webview with support for images.
     * @param htmlFilePath 
     */
    private void setVHelpContentWebViewContent(String htmlFilePath) {
//        try {
        String htmlContent = new String();
        ClassLoader classLoader = MenuHelp.class.getClassLoader();
        InputStream inputStream
                = classLoader.getResourceAsStream(htmlFilePath);
        //    Convert input stream to string
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        htmlContent = s.hasNext() ? s.next() : "";

        //File htmlFile = null;
        //FileUtils.copyInputStreamToFile(inputStream, htmlFile);
        vHelpContentWebView.getEngine().loadContent(htmlContent);
//        } catch (IOException ex) {
//            LoggerBoy.logError(FXMLHelpController.class.getName(),ex.getLocalizedMessage());
//        }
    }
    
    /**
     * This class extends treeitem object:
     *
     */
    private class TreeItemExtended extends TreeItem<String> {
        public int father;
        public String htmlFileName;
        
        /**
         * 
         * @param value TreeItem Title
         * @param father Index of father level
         * @param htmlFileName Name of the html file
         */
        public TreeItemExtended(final String value, int father, String htmlFileName){
            super(value);  
            this.father = father;
            this.htmlFileName = htmlFileName;
        }
    }
}
