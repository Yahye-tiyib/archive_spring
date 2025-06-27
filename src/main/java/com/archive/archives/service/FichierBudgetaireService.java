package com.archive.archives.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archives.model.BoxMensuelle;
import com.archive.archives.model.FichierBudgetaire;
import com.archive.archives.repository.BoxMensuelleRepository;
import com.archive.archives.repository.FichierBudgetaireRepository;

@Service
public class FichierBudgetaireService {

  @Autowired
  private FichierBudgetaireRepository fichierRepo;

  @Autowired
  private BoxMensuelleRepository boxRepo;

  // Ajouter un fichier à une box existante via son ID

  public List<FichierBudgetaire> getAll() {
    return fichierRepo.findAll(Sort.by(Sort.Direction.DESC, "box.nom"));
  }

  public Optional<FichierBudgetaire> getById(Long id) {
    return fichierRepo.findById(id);
  }

  public FichierBudgetaire ajouterFichierAvecBoxId(FichierBudgetaire fichier, Long boxId) {
    BoxMensuelle box = boxRepo.findById(boxId)
        .orElseThrow(() -> new IllegalStateException("Box non trouvée avec l'ID : " + boxId));
    fichier.setBox(box);
    return fichierRepo.save(fichier);
  }

  public FichierBudgetaire getFichierById(Long id) {
    return fichierRepo.findById(id)
        .orElse(null);
  }

  // Ajouter un fichier à la box ouverte (statut OUVERTE)
  public FichierBudgetaire ajouterFichierBoxActuelle(FichierBudgetaire fichier) {
    BoxMensuelle boxOuverte = boxRepo.findByStatut(BoxMensuelle.StatutBox.OUVERTE)
        .stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Aucune box ouverte trouvée."));
    fichier.setBox(boxOuverte);
    return fichierRepo.save(fichier);
  }

  public FichierBudgetaire updateFichier(Long id, FichierBudgetaire updatedFichier) {
    FichierBudgetaire fichier = fichierRepo.findById(id)
        .orElseThrow(() -> new IllegalStateException("Fichier non trouvé"));

    fichier.setNomEtablissement(updatedFichier.getNomEtablissement());
    fichier.setFichier(updatedFichier.getFichier());
    fichier.setDateReception(updatedFichier.getDateReception());
    fichier.setTraiter(updatedFichier.isTraiter());

    fichier.setReferenceLettre(updatedFichier.getReferenceLettre()); // 👈 Nouveau
    fichier.setObjet(updatedFichier.getObjet()); // 👈 Nouveau

    return fichierRepo.save(fichier);
  }

  public void deleteFichier(Long id) {
    fichierRepo.deleteById(id);
  }

  public List<FichierBudgetaire> getFichiersByBox(Long boxId) {
    return fichierRepo.findByBoxId(boxId);
  }

  public Map<String, Long> getNombreFichiersByBox(Long boxId) {
    BoxMensuelle box = boxRepo.findById(boxId)
        .orElseThrow(() -> new IllegalStateException("Box non trouvée avec l'ID : " + boxId));

    List<FichierBudgetaire> fichiers = fichierRepo.findByBox(box);
    if (fichiers == null)
      fichiers = new ArrayList<>(); // sécurité en plus même si normalement Spring Data renvoie jamais null

    long total = fichiers.size();
    long traites = fichiers.stream()
        .filter(f -> f != null && f.isTraiter()) // sécurité au cas où
        .count();
    long nonTraites = total - traites;

    Map<String, Long> statistiques = new HashMap<>();
    statistiques.put("total", total);
    statistiques.put("traites", traites);
    statistiques.put("nonTraites", nonTraites);

    return statistiques;
  }


  public Map<Integer, Long> getNombreFichiersParMoisPourAnnee(int annee) {
    List<BoxMensuelle> boxes = boxRepo.findByAnnee(annee);

    Map<Integer, Long> resultats = new HashMap<>();

    IntStream.rangeClosed(1, 12).forEach(mois -> {
    long total = boxes.stream()
        .filter(box -> box.getMois() == mois)
        .flatMap(box -> box.getFichiers().stream())
        .count();
    resultats.put(mois, total);
    });


    return resultats;
}




public Map<Integer, Map<String, Long>> getTraitementFichiersParMoisPourAnnee(int annee) {
    List<BoxMensuelle> boxes = boxRepo.findByAnnee(annee);

    Map<Integer, Map<String, Long>> resultat = new HashMap<>();

    IntStream.rangeClosed(1, 12).forEach(mois -> {
        long traites = boxes.stream()
            .filter(box -> box.getMois() == mois)
            .flatMap(box -> box.getFichiers().stream())
            .filter(FichierBudgetaire::isTraiter)
            .count();

        long total = boxes.stream()
            .filter(box -> box.getMois() == mois)
            .flatMap(box -> box.getFichiers().stream())
            .count();

        long nonTraites = total - traites;

        Map<String, Long> moisData = new HashMap<>();
        moisData.put("traites", traites);
        moisData.put("nonTraites", nonTraites);

        resultat.put(mois, moisData);
    });

    return resultat;
}


public Map<String, Long> getStatutBoxes() {
    List<BoxMensuelle> boxes = boxRepo.findAll();

    Map<String, Long> resultats = boxes.stream()
        .collect(Collectors.groupingBy(
            box -> box.getStatut().name(), // "OUVERTE" ou "CLOTUREE"
            Collectors.counting()
        ));

    // S'assurer que les deux statuts sont présents dans le résultat, même avec 0
    resultats.putIfAbsent("OUVERTE", 0L);
    resultats.putIfAbsent("CLOTUREE", 0L);

    return resultats;
}


public Map<String, Long> getTop5EtablissementsActifs() {
    List<FichierBudgetaire> fichiers = fichierRepo.findAll();

    return fichiers.stream()
        .filter(f -> f.getNomEtablissement() != null && !f.getNomEtablissement().isBlank())
        .collect(Collectors.groupingBy(
            FichierBudgetaire::getNomEtablissement,
            Collectors.counting()
        ))
        .entrySet()
        .stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(5)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new // pour garder l'ordre
        ));
}

}
