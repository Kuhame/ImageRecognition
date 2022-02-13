package fr.okane.db;

import fr.okane.histogramme.Histogramme;
import fr.okane.utils.Filtre;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Images {
    private final int imageReference;
    private final List<Image> images;
    private final List<double[][]> histogrammes;
    private final List<Double> distances;
    private final TreeMap<Double, Image> resultats;

    public Images(int imageReference, String dossier) {
        this.imageReference = imageReference;
        images = new ArrayList<>();
        histogrammes = new ArrayList<>();
        distances = new ArrayList<>();
        resultats = new TreeMap<>();

        chargerImages(dossier);
        traiterImages();
        calculerDistances();
        stockerResultats();
    }

    private void chargerImages(String dossier) {
        int nbImages = new File("./img/" + dossier + "/").list().length;
        String extension = extension(dossier);

        for (int i = 0; i < nbImages; ++i) {
            String numeroImage = String.format("%03d", i); // 001
            String chemin = "./img/" + dossier + "/" + numeroImage + "." + extension;

            images.add(ImageLoader.exec(chemin));
        }
    }

    private void traiterImages() {
        for (int i = 0; i < images.size(); ++i) {
            Image image = images.get(i);

            images.set(i, Filtre.median(image));
            double[][] h = Histogramme.RGB(image);
            h = Histogramme.discretiser(h);
            h = Histogramme.normaliser(h, image.getXDim() * image.getYDim());

            histogrammes.add(h);
        }
    }

    private void calculerDistances() {
        double[][] histoRef = histogrammes.get(imageReference);

        for (double[][] h : histogrammes) {
            double cumul = 0.0;

            for (int canal = 0; canal < 3; ++canal) { // chaque canal
                for (int i = 0; i < h[0].length; ++i) { // chaque barre
                    cumul += Math.pow((histoRef[canal][i] - h[canal][i]), 2);
                }
            }

            distances.add(Math.sqrt(cumul));
        }
    }

    private void stockerResultats() {
        for (int i = 0; i < distances.size(); ++i) {
            if (i == imageReference) continue;

            resultats.put(distances.get(i), images.get(i));
        }
    }

    public void afficherResultats() {
        int i = 0;
        for (Map.Entry<Double, Image> entry : resultats.entrySet()) {
            Image image = entry.getValue();
            image.setColor(true);
            Viewer2D.exec(image);

            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}*/

            ++i;
            if (i == 10) break;
        }
    }

    private static String extension(String dossier) {
        switch (dossier) {
            case "broad":
                return "png";
            case "motos":
                return "jpg";
            default:
                throw new IllegalArgumentException("Nom de dossier inconnu");
        }
    }
}
