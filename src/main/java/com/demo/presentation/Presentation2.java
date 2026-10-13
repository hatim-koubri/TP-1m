package com.demo.presentation;

import com.demo.metier.IMetier;
import com.demo.dao.IDao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class Presentation2 {

    public static void main(String[] args) {
        try {
            // 1. Charger les noms de classes depuis le fichier de configuration
            String[] classNames = loadClassNames("config.txt");

            // 2. Instancier le DAO
            IDao dao = (IDao) instantiateClass(classNames[0]);
            System.out.println("[CONTAINER] DAO instancié : " + dao.getClass().getName());

            // 3. Instancier le Métier
            IMetier metier = (IMetier) instantiateClass(classNames[1]);
            System.out.println("[CONTAINER] Métier instancié : " + metier.getClass().getName());

            // 4. Injecter le DAO dans le Métier via la réflexion
            injectDependency(metier, "setDao", dao);

            // 5. Exécuter le calcul
            double resultat = metier.calcul();
            System.out.println("Résultats = " + resultat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lit les noms de classes depuis un fichier présent dans le classpath.
     */
    private static String[] loadClassNames(String fileName) throws IOException {
        String[] classNames = new String[2];

        try (InputStream is = Presentation2.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException(fileName + " introuvable dans le classpath");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            for (int i = 0; i < 2; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("config.txt doit contenir 2 lignes (DAO puis Métier)");
                }
                classNames[i] = line.trim();
            }
        }
        return classNames;
    }

    /**
     * Instancie une classe à partir de son nom complet.
     */
    private static Object instantiateClass(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        return constructor.newInstance();
    }

    /**
     * Injecte une dépendance dans un objet via une méthode setter.
     */
    private static void injectDependency(Object target, String setterName, Object dependency) throws Exception {
        for (Method m : target.getClass().getMethods()) {
            if (m.getName().equals(setterName)
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0].isAssignableFrom(dependency.getClass())) {
                m.invoke(target, dependency);
                return;
            }
        }
        throw new NoSuchMethodException("Aucun setter compatible " + setterName + " dans "
                + target.getClass().getName());
    }
}