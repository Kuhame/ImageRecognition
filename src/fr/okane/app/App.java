package fr.okane.app;

import fr.okane.db.ImagesSimilaires;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ImagesSimilaires images = new ImagesSimilaires("069.jpg", "motos", true);
        images.afficherResultats();

    }
}
