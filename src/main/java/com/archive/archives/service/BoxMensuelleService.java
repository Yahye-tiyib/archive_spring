package com.archive.archives.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.archive.archives.model.BoxMensuelle;
import com.archive.archives.repository.BoxMensuelleRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class BoxMensuelleService {

    @Autowired
    private BoxMensuelleRepository boxMensuelleRepository;

    // Récupérer toutes les BoxMensuelles
    public List<BoxMensuelle> getAllBoxes() {
      return boxMensuelleRepository.findAll(Sort.by(Sort.Direction.DESC, "annee", "mois"));
  }
  

    // Méthode appelée automatiquement chaque 1er du mois pour gérer les BoxMensuelles
    public void gererBoxesMensuelles() {
        YearMonth moisActuel = YearMonth.now();
        LocalDate debut = moisActuel.atDay(1);  // 1er du mois
        LocalDate fin = moisActuel.atEndOfMonth();  // Fin du mois

        // Clôturer toutes les box ouvertes
        boxMensuelleRepository.findByStatut(BoxMensuelle.StatutBox.OUVERTE)
            .forEach(box -> {
                box.setStatut(BoxMensuelle.StatutBox.CLOTUREE);
                boxMensuelleRepository.save(box);
            });

        // Créer la nouvelle box si elle n'existe pas pour le mois en cours
        Optional<BoxMensuelle> existingBox = boxMensuelleRepository.findByMoisAndAnnee(
                moisActuel.getMonthValue(), moisActuel.getYear());

        if (existingBox.isEmpty()) {
            BoxMensuelle newBox = new BoxMensuelle();
            newBox.setMois(moisActuel.getMonthValue());
            newBox.setAnnee(moisActuel.getYear());
            newBox.setDateDebut(debut);
            newBox.setDateFin(fin);
            newBox.setStatut(BoxMensuelle.StatutBox.OUVERTE);
            newBox.setNom("Box-" + moisActuel.getMonthValue() + "-" + moisActuel.getYear());

            boxMensuelleRepository.save(newBox);
        }
    }

    // Ajouter une BoxMensuelle manuellement
    public BoxMensuelle ajouterBoxManuellement(int mois,String nom, int annee, LocalDate dateDebut, LocalDate dateFin, BoxMensuelle.StatutBox statut) {
        Optional<BoxMensuelle> existingBox = boxMensuelleRepository.findByMoisAndAnnee(mois, annee);
        if (existingBox.isPresent()) {
            throw new IllegalStateException("Une box existe déjà pour ce mois et cette année.");
        }

        BoxMensuelle box = new BoxMensuelle();
        box.setMois(mois);
        box.setAnnee(annee);
        box.setDateDebut(dateDebut);
        box.setDateFin(dateFin);
        box.setStatut(statut);
        box.setNom("Box-" + mois + "-" + annee);

        return boxMensuelleRepository.save(box);
    }

    // Obtenir une BoxMensuelle par mois et année
    public BoxMensuelle getBoxMensuelleByMoisAndAnnee(int mois, int annee) {
        Optional<BoxMensuelle> box = boxMensuelleRepository.findByMoisAndAnnee(mois, annee);
        return box.orElse(null);
    }

    public BoxMensuelle modifierBoxMensuelle(Long id, BoxMensuelle boxModifiee) {
        Optional<BoxMensuelle> existingBox = boxMensuelleRepository.findById(id);
        if (!existingBox.isPresent()) {
            throw new IllegalStateException("Aucune box trouvée pour cet identifiant.");
        }

        BoxMensuelle box = existingBox.get();

        // Mettre à jour le mois et l'année si modifiés
        if (boxModifiee.getMois() != box.getMois()) {
            box.setMois(boxModifiee.getMois());
        }

        if (boxModifiee.getAnnee() != box.getAnnee()) {
            box.setAnnee(boxModifiee.getAnnee());
        }

        // Mettre à jour les autres propriétés de la box
        box.setDateDebut(boxModifiee.getDateDebut());
        box.setDateFin(boxModifiee.getDateFin());
        box.setStatut(boxModifiee.getStatut());
        box.setNom(boxModifiee.getNom());

        return boxMensuelleRepository.save(box);
    }

    public void supprimerBoxMensuelle(Long id) {
        Optional<BoxMensuelle> existingBox = boxMensuelleRepository.findById(id);
        if (!existingBox.isPresent()) {
            throw new IllegalStateException("Aucune box trouvée pour cet identifiant.");
        }

        boxMensuelleRepository.delete(existingBox.get());
    }
}
