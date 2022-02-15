package fr.okane.db;

import fr.okane.histogramme.Histogramme;
import fr.okane.utils.Filtre;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class ImagesSimilaires {
    private final int imageReference;
    private final List<Image> images;
    private final List<double[][]> histogrammes;
    private final List<Double> distances;
    private final TreeMap<Double, Image> resultats;

    public ImagesSimilaires(int imageReference, String dossier) {
        this.imageReference = imageReference;
        images = new ArrayList<>();
        histogrammes = new ArrayList<>();
        distances = new ArrayList<>();
        resultats = new TreeMap<>();

        chargerImages(dossier);
        calculerSignatures(dossier);
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

    private void calculerSignatures(String dossier) {
        /*
        Chercher le fichier dossier.txt
        - S'il existe, comparer images.size() avec le nombre de lignes du fichier dossier.txt
        - S'il n'existe pas, calculer les signatures, créer le fichier et ajouter les signatures dedans
         */

        String path = "./cache/" + dossier + ".txt";

        try {
            if (new File(path).exists()) {
                BufferedReader tmp = new BufferedReader(new FileReader(path));
                int nbBarres = tmp.readLine().split(" ").length;
                tmp.close();

                // Parser

                BufferedReader r = new BufferedReader(new FileReader(path));

                int t = 0;
                while (t < images.size()) {
                    double[][] h = new double[3][nbBarres];

                    for (int i = 0; i < 3; ++i) { // 3 lignes (3 canaux) par image
                        String ligne = r.readLine();
                        String[] hist1dStr = ligne.split(" ");
                        double[] hist1d = Arrays.stream(hist1dStr).mapToDouble(Double::parseDouble).toArray();
                        System.arraycopy(hist1d, 0, h[i], 0, hist1d.length);
                    }
                    histogrammes.add(h);
                    System.out.println(h[0].length);
                    ++t;
                }

            } else {
                // Calculer les signatures et ajouter au fichier

                new File(path).createNewFile();

                // Flux vers le fichier texte
                FileWriter w = new FileWriter(path);

                for (int i = 0; i < images.size(); ++i) {
                    Image image = images.get(i);

                    images.set(i, Filtre.median(image));
                    double[][] h = Histogramme.RGB(image);

                    h = Histogramme.discretiser(h);
                    h = Histogramme.discretiser(h);
                    h = Histogramme.discretiser(h);
                    h = Histogramme.discretiser(h);
                    h = Histogramme.discretiser(h);
                    h = Histogramme.discretiser(h);
                    h = Histogramme.normaliser(h, image.getXDim() * image.getYDim());

                    histogrammes.add(h);

                    // Ajouter au fichier
                    for (int canal = 0; canal < 3; ++canal) {
                        StringBuilder histogrammeString = new StringBuilder();

                        for (int k = 0; k < h[0].length; ++k) {
                            histogrammeString.append(h[canal][k]).append(" ");
                        }
                        histogrammeString.append("\n");
                        w.write(histogrammeString.toString());
                    }
                }
                w.close();
            }
        } catch (IOException ignored) {}
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
