package matrixCalculator.actions;

import javafx.scene.canvas.Canvas;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.Box;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Code by:
 * https://stackoverflow.com/questions/25027060/running-swing-application-in-javafx
 */
public class LatexCanvas extends Canvas {

    private FXGraphics2D g2;

    private Box box;

    private TeXIcon icon;

    public LatexCanvas(String matrix) {
        this.g2 = new FXGraphics2D(this.getGraphicsContext2D());

        // create a formula
        TeXFormula formula = new TeXFormula(matrix);

        // render the formla to an icon of the same size as the formula.
        this.icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

        // Redraw canvas when size changes.
        this.widthProperty().addListener(evt -> this.draw());
        this.heightProperty().addListener(evt -> this.draw());
    }

    private void draw() {
        double width = this.getWidth();
        double height = this.getHeight();
        this.getGraphicsContext2D().clearRect(0, 0, width, height);

        // ideally it should be possible to draw directly to the FXGraphics2D
        // instance without creating an image first...but this does not generate
        // good output
        //this.icon.paintIcon(new JLabel(), g2, 50, 50);

        // now create an actual image of the rendered equation
        BufferedImage image = new BufferedImage(this.icon.getIconWidth(), this.icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D gg = image.createGraphics();

        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, this.icon.getIconWidth(), this.icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        this.icon.paintIcon(jl, gg, 0, 0);
        // at this point the image is created, you could also save it with ImageIO

        this.g2.drawImage(image, 0, 0, null);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return this.getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return this.getHeight();
    }

}
