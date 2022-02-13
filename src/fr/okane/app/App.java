package fr.okane.app;

import fr.okane.db.Images;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        /*Image image = NoiseTools.addNoise(ImageLoader.exec("./img/tmp/eiffel.jpg"), 0.05);
        Viewer2D.exec(image);
        image = ImageFilter.median(image);
        afficherRGB(image);*/

        //Image image = ImageLoader.exec("./img/tmp/sobRGB.png");
        //double[][] h = Histogramme.RGB(image);
        //Histogramme.discretiser(h);
        //Histogramme.normaliser(h, image.getXDim() * image.getYDim());

        Images images = new Images(1, "motos");
    }

    public static void afficherRGB(Image image) {
        image.setColor(true);
        Viewer2D.exec(image);
    }
}
