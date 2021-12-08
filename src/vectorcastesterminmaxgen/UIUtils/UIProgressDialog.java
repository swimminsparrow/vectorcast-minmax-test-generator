package vectorcastesterminmaxgen.UIUtils;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Roberto Caputo
 */
public class UIProgressDialog extends Dialog {

    private UIProgressBar vDialogProgressBar;
    private Label vDialogLabel;

    /**
     * This Function builds a dialog with a progressbar and a label.
     *
     * @param header
     * @param iterations number of iterations for the progress bar.
     * @param indeterminateSpinning
     */
    public void build(String header, int iterations, boolean indeterminateSpinning) {
        // Create the custom dialog.
        this.setTitle("Working...");
        this.setHeaderText(header);
        this.setWidth(400);

        VBox vBox = new VBox();
        vDialogLabel = new Label();
        vDialogLabel.setMinWidth(350);

        if (!indeterminateSpinning) {
            //  Determinate //
            vDialogProgressBar = new UIProgressBar(iterations);
            vDialogProgressBar.setProgress(0);
        } else {
            //  Indeterminate   //
            vDialogProgressBar = new UIProgressBar();
            vDialogProgressBar.setIndeterminateSpinning(true);
        }

        vDialogProgressBar.setMinWidth(350);

        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(vDialogLabel);
        hBox1.setAlignment(Pos.CENTER);
        HBox hBox2 = new HBox();
        hBox2.getChildren().add(vDialogProgressBar);
        hBox2.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(hBox1, hBox2);
        vBox.fillWidthProperty();
        vBox.setSpacing(10);

        this.getDialogPane().setContent(vBox);

        this.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL
        );

    }

    public void updateDialog(String content) {
        this.vDialogLabel.setText(content);
        if(!this.vDialogProgressBar.isIndeterminate())
            this.vDialogProgressBar.increment();
    }
}
