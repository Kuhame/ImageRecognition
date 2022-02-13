package fr.okane.app;

import fr.okane.utils.ImageFilter;
import fr.okane.utils.NoiseTools;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

public class App {
    public static void main(String[] args) {
        Image image = NoiseTools.addNoise(ImageLoader.exec("./img/tmp/eiffel.jpg"), 0.05);
        Viewer2D.exec(image);
        image = ImageFilter.median(image);
        afficherRGB(image);
    }

    public static void afficherRGB(Image image) {
        image.setColor(true);
        Viewer2D.exec(image);
    }
}
