package fr.okane.db;

import fr.okane.histogramme.Histogramme;
import fr.okane.utils.Filtre;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.*;
import java.util.*;

public class ImagesSimilaires {
    private final boolean rgb;
    private final List<Image> images;

    private final List<double[][]> histogrammes;
    private final List<Double> distances;
    private final TreeMap<Double, Image> resultats;
    private int indexImageRef;

    public ImagesSimilaires(String imageReference, String dossier, boolean rgb) {
        images = new ArrayList<>();
        histogrammes = new ArrayList<>();
        distances = new ArrayList<>();
        resultats = new TreeMap<>();

        this.rgb = rgb;

        chargerImages(dossier, imageReference);
        calculerSignatures(dossier);
        calculerDistances();
        stockerResultats();
    }

    private void chargerImages(String dossier, String imageReference) {
        File dir = new File("./img/" + dossier + "/");

        File[] files = dir.listFiles();

        int i = 0;
        for (File file : files) {
            if (file.getName().equals(imageReference)) {
                indexImageRef = i;
            }
            ++i;

            images.add(ImageLoader.exec(file.getPath()));
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
                    double[][] h = rgb ? Histogramme.RGB(image) : Histogramme.HSV(image);

                    if (rgb) {
                        h = Histogramme.discretiser(h);
                        h = Histogramme.discretiser(h);
                        h = Histogramme.discretiser(h);
                        h = Histogramme.discretiser(h);
                        h = Histogramme.discretiser(h);
                        h = Histogramme.discretiser(h);
                    }

                    h = Histogramme.normaliser(h, image.getXDim() * image.getYDim());

                    histogrammes.add(h);

                    // Ajouter au fichier
                    for (int canal = 0; canal < 3; ++canal) {
                        StringBuilder histogrammeString = new StringBuilder();

                        int taille = h[canal].length;
                        for (int k = 0; k < taille; ++k) {
                            histogrammeString.append(h[canal][k]).append(" ");
                        }
                        histogrammeString.append("\n");
                        w.write(histogrammeString.toString());
                    }
                }
                w.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void calculerDistances() {
        double[][] histoRef = histogrammes.get(indexImageRef);

        for (double[][] h : histogrammes) {
            double cumul = 0.0;

            for (int canal = 0; canal < 3; ++canal) { // chaque canal
                int taille = h[canal].length;
                for (int i = 0; i < taille; ++i) { // chaque barre
                    cumul += Math.pow((histoRef[canal][i] - h[canal][i]), 2);
                }
            }

            distances.add(Math.sqrt(cumul));
        }
    }

    private void stockerResultats() {
        for (int i = 0; i < distances.size(); ++i) {
            if (i == indexImageRef) continue; // ne stocke pas l'image de référence dans les résultats (distance O)

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
}
