package vectorcastesterminmaxgen.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;

/**
 * This Class instiates a progress dialog on the main thread UI.
 *
 * @author Roberto Caputo
 */
public class UIProgressDialogThread {

    private UIProgressDialog vProgressDialog;

    /**
     * 
     * @param header header text of the dialog
     * @param progressBarSize -1 if you want indeterminate spinning
     * @param indeterminateSpinning
     */
    public UIProgressDialogThread(String header, int progressBarSize, boolean indeterminateSpinning) {
        Platform.runLater(() -> {
            vProgressDialog = new UIProgressDialog();
            vProgressDialog.build(header, progressBarSize, indeterminateSpinning);
            vProgressDialog.show();
        });
    }

    public void updateDialog(String content) {
        Platform.runLater(() -> {
            vProgressDialog.updateDialog(content);
        });
    }

    public void closeDialog() {
        Platform.runLater(() -> {
            vProgressDialog.setResult(ButtonType.CANCEL);
            vProgressDialog.close();
        });
    }
}
