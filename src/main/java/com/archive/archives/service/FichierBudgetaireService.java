package com.archive.archives.service;

import java.util.List;
import java.util.Optional;

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

        fichier.setNomfichier(updatedFichier.getNomfichier());
        fichier.setFichier(updatedFichier.getFichier());
        fichier.setDateReception(updatedFichier.getDateReception());
        fichier.setEtat(updatedFichier.isEtat());
        fichier.setTraiter(updatedFichier.isTraiter());

        return fichierRepo.save(fichier);
    }

    public void deleteFichier(Long id) {
        fichierRepo.deleteById(id);
    }
}
