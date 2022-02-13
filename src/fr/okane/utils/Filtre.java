package fr.okane.utils;

import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;

import java.util.Arrays;

public class Filtre {

    /**
     * Applique un filtre médian sur une image RGB
     *
     * @param image L'image à transformer
     * @return La nouvelle image filtrée
     */
    public static Image median(Image image) {
        int largeurImg = image.getXDim();
        int hauteurImg = image.getYDim();
        ByteImage imgTransforme = new ByteImage(largeurImg, hauteurImg, 1, 1, 3);

        for (int b = 0; b < 3; b++) {
            for (int i = 1; i < largeurImg - 2; i++) {
                for (int j = 1; j < hauteurImg - 2; j++) {
                    int[] tableau = new int[9];
                    tableau[0] = image.getPixelXYBByte(i, j, b);
                    tableau[1] = image.getPixelXYBByte(i + 1, j, b);
                    tableau[2] = image.getPixelXYBByte(i + 1, j + 1, b);
                    tableau[3] = image.getPixelXYBByte(i - 1, j - 1, b);
                    tableau[4] = image.getPixelXYBByte(i - 1, j, b);
                    tableau[5] = image.getPixelXYBByte(i, j - 1, b);
                    tableau[6] = image.getPixelXYBByte(i - 1, j + 1, b);
                    tableau[7] = image.getPixelXYBByte(i + 1, j - 1, b);
                    tableau[8] = image.getPixelXYBByte(i, j + 1, b);

                    Arrays.sort(tableau);
                    imgTransforme.setPixelXYBByte(i, j, b, tableau[4]); // valeur médiane
                }
            }
        }

        return imgTransforme;
    }

}
