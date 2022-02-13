package fr.okane.db;

import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Images {
    private final int imageRequete;
    private final List<Image> images;

    public Images(int imageRequete, String dossier) {
        this.imageRequete = imageRequete;
        images = new ArrayList<>();

        int nbImages = new File("./img/" + dossier + "/").list().length;
        String extension = extension(dossier);

        for (int i = 0; i < nbImages; ++i) {
            if (i == imageRequete) continue;

            String numeroImage = String.format("%03d", i); // 001
            String chemin = "./img/" + dossier + "/" + numeroImage + "." + extension;

            images.add(ImageLoader.exec(chemin));
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
