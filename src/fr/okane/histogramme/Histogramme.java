package fr.okane.histogramme;

import fr.okane.utils.HistogramTools;
import fr.unistra.pelican.Image;

import java.io.IOException;
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

    public static double[] histogrammeHSV(Image image, int canal) {
        // Canaux
        // H : 0 (360 valeurs)
        // S : 1 (100 valeurs en %)
        // V : 2 (100 valeurs en %)
        int taille = canal == 0 ? 360 + 1 : 100 + 1;
        double[] histogramme = new double[taille];
        Arrays.fill(histogramme, 0);

        int largeur = image.getXDim();
        int hauteur = image.getYDim();

        for (int x = 0; x < largeur; ++x) {
            for (int y = 0; y < hauteur; ++y) {
                int valeur = RGBtoHSV(image, x, y, canal);
                /*String s;
                if (canal == 0)
                    s = "h";
                else if (canal == 1)
                    s = "s";
                else
                    s = "v";
                System.out.println(s + " : " + valeur);*/
                ++histogramme[valeur];
            }
        }

        return histogramme;
    }

    public static int RGBtoHSV(Image image, int x, int y, int canal) {
        int r = image.getPixelXYBByte(x, y, 0);
        int g = image.getPixelXYBByte(x, y, 1);
        int b = image.getPixelXYBByte(x, y, 2);

        int max = Math.max(Math.max(r, g), b);
        int min = Math.min(Math.min(r, g), b);

        switch (canal) {
            case 0: // H
                int h = (int) Math.toDegrees(Math.acos((r - g*.5 - b*.5) / (Math.sqrt(r*r + g*g + b*b - r*g - r*b - g*b))));
                return g >= b ? h : 360 - h;
            case 1: // S
                if (max == 0)
                    return 0;
                return (int) ((1.0 - (double) min/max) * 100.0);
            case 2: // V
                return (int) (((double) max / 255.0) * 100.0);
            default:
                throw new IllegalArgumentException("Canal inconnu");
        }
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

    public static double[][] HSV(Image image) throws IOException {
        double[][] histo = new double[3][];
        histo[0] = new double[360];
        histo[1] = new double[100];
        histo[2] = new double[100];

        for (int canal = 0; canal < 3; ++canal) {
            double[] histCanal = histogrammeHSV(image, canal);
            System.arraycopy(histCanal, 0, histo[canal], 0, canal == 0 ? 360 : 100);
            //HistogramTools.plotHistogram(histo[canal]);
        }

        return histo;
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
        double[][] nv = new double[3][];
        nv[0] = new double[h[0].length/2];
        nv[1] = new double[h[1].length/2];
        nv[2] = new double[h[2].length/2];

        for (int canal = 0; canal < 3; ++canal) {
            for (int i = 0; i < nv[canal].length; ++i) {
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
            int taille = h[canal].length;
            for (int i = 0; i < taille; ++i) {
                nv[canal][i] = h[canal][i] / nbPixels;
            }
            //HistogramTools.plotHistogram(nv[canal]);
        }

        return nv;
    }
}
