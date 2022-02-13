package fr.okane.histogram;

import fr.okane.utils.HistogramTools;
import fr.unistra.pelican.Image;

import java.io.IOException;
import java.util.Arrays;

public class Histogram {
    /**
     * Construit et affiche l'histogramme d'une image à niveaux de gris
     *
     * @param image L'image dont on veut construire l'histogramme
     */
    public static double[] histogramme(Image image, int canal) {
        double[] histogramme = new double[256];
        Arrays.fill(histogramme, 0);

        int largeur = image.getXDim();
        int hauteur = image.getYDim();

        for (int x = 0; x < largeur; ++x) {
            for (int y = 0; y < hauteur; ++y) {
                ++histogramme[image.getPixelXYBByte(x, y, canal)];
            }
        }

        //HistogramTools.plotHistogram(h);
        return histogramme;
    }

    /**
     * Construit et affiche l'histogramme d'une image RGB
     *
     * @param image L'image RGB dont on veut construire l'histogramme
     */
    public static double[][] RGB(Image image) throws IOException {
        double[][] h = new double[3][256];

        for (int canal = 0; canal < 3; ++canal) {
            double[] histCanal = histogramme(image, canal);
            System.arraycopy(histCanal, 0, h[canal], 0, 256);
            HistogramTools.plotHistogram(h[canal]);
        }

        return h;
    }
}
