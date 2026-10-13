package com.demo.metier;

import com.demo.dao.IDao;

public class MetierImpl implements IMetier {
    private IDao dao;

    public void setDao(IDao dao) {
        this.dao = dao;
    }


    @Override
    public double calcul() {
        if (dao == null) {
            throw new IllegalStateException("Le DAO n'a pas été injecté.");
        }

        double valeurBase = dao.getValue();

        // Logique métier : double la valeur
        double resultat = valeurBase * 3;

        System.out.println("[METIER] Calcul effectué. Résultat : " + resultat);
        return resultat;
    }
}
