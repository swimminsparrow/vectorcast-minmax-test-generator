package vectorcastesterminmaxgen.UIUtils;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * This Class offers static methods to handle key shortcuts for the UI.
 * @author Roberto Caputo
 */
public class KeyShortcutHandler {
    
    
    private static KeyCodeCombination copyCTRL_C(){
        return new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
    }
    
    /**
     * This Function set a listener to a ListView object for CTRL+C Key
     * Combination Pressed.
     *
     * @param listView
     */
    public static void set_CTRL_C_ListenerOnListView(ListView<String> listView) {
        listView.setOnKeyPressed(event -> {
            if (copyCTRL_C().match(event)) {
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(listView.getSelectionModel().getSelectedItem());
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }
        });
    }
    
    /**
     * This Function set a listener to a TextField object for CTRL+C Key
     * Combination Pressed.
     *
     * @param textField
     */
    public static void set_CTRL_C_ListenerOnTextView(TextField textField) {
        textField.setOnKeyPressed(event -> {
            if (copyCTRL_C().match(event)) {
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(textField.getText());
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }
        });
    }
}
