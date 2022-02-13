package fr.okane.app;

import fr.okane.histogramme.Histogramme;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        /*Image image = NoiseTools.addNoise(ImageLoader.exec("./img/tmp/eiffel.jpg"), 0.05);
        Viewer2D.exec(image);
        image = ImageFilter.median(image);
        afficherRGB(image);*/

        double[][] h = Histogramme.RGB(ImageLoader.exec("./img/tmp/sobRGB.png"));
        Histogramme.discretiser(h);
    }

    public static void afficherRGB(Image image) {
        image.setColor(true);
        Viewer2D.exec(image);
    }
}
