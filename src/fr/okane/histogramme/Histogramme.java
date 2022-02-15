package fr.okane.histogramme;

import fr.unistra.pelican.Image;

import java.util.Arrays;

public class Histogramme {
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
    public static double[][] RGB(Image image) {
        double[][] h = new double[3][256];

        for (int canal = 0; canal < 3; ++canal) {
            double[] histCanal = histogramme(image, canal);
            System.arraycopy(histCanal, 0, h[canal], 0, 256);
            //HistogramTools.plotHistogram(h[canal]);
        }

        return h;
    }

    /*public static double[][] discretiser(double[][] h) {
        double[][] nv = new double[3][10];

        for (int canal = 0; canal < 3; ++canal) {
            for (int i = 0; i < 10; ++i) {
                int cumul = 0;
                for (int j = (i * 25); j < (25 + i * 25); ++j) {
                    cumul += h[canal][j];
                }
                nv[canal][i] = cumul;
            }
            //HistogramTools.plotHistogram(nv[canal]);
        }

        return nv;
    }*/

    /**
     * Discrétise l'histogramme d'une image RGB en le divisant par 2
     *
     * @param h L'histogramme à discrétiser
     * @return Le nouvel histogramme discrétisé
     */
    public static double[][] discretiser(double[][] h) {
        int nvTaille = h[0].length/2;
        double[][] nv = new double[3][nvTaille];

        for (int canal = 0; canal < 3; ++canal) {
            for (int i = 0; i < nvTaille; ++i) {
                int cumul = 0;
                for (int j = (i * 2); j < (2 + i * 2); ++j) {
                    cumul += h[canal][j];
                }
                nv[canal][i] = cumul;
            }
            //HistogramTools.plotHistogram(nv[canal]);
        }

        return nv;
    }

    /**
     * Normalise un histogramme RGB de quantités de pixels en proportions
     *
     * @param h        L'histogramme à normaliser
     * @param nbPixels Le nombre de pixels de l'image
     * @return Le nouvel histogramme normalisé
     */
    public static double[][] normaliser(double[][] h, int nbPixels) {
        double[][] nv = new double[h.length][h[0].length];

        for (int canal = 0; canal < 3; ++canal) {
            for (int i = 0; i < h[0].length; ++i) {
                nv[canal][i] = h[canal][i] / nbPixels;
            }
            //HistogramTools.plotHistogram(nv[canal]);
        }

        return nv;
    }
}
