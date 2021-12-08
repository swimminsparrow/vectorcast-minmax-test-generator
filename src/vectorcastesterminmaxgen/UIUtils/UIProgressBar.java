package vectorcastesterminmaxgen.UIUtils;

import javafx.scene.control.ProgressBar;

/**
 *
 * @author Roberto Caputo
 */
public class UIProgressBar extends ProgressBar {

    private double deltaIncrement;
    private double actualPosition;

    /**
     *
     * @param iterations : number of iterations for the progress bar.
     */
    public UIProgressBar(int iterations) {
        int progressBarLength = 1;
        this.deltaIncrement = ((double) progressBarLength / (double) iterations);
        this.actualPosition = 0;
        this.setProgress(actualPosition);
    }

    public UIProgressBar (){}
    
    public void increment() {
        actualPosition += deltaIncrement;
        this.setProgress(actualPosition);
    }

    /**
     * This Function starts infinite spinning if true on progressbar.
     *
     * @param enableSpinning TRUE: start infinite spinning, FALSE: set progress 100%
     */
    public void setIndeterminateSpinning(boolean enableSpinning) {
        this.setProgress(enableSpinning ? ProgressBar.INDETERMINATE_PROGRESS : 1);
    }

}
