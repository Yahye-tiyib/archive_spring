package com.archive.archives.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.archive.archives.model.BoxMensuelle;
import com.archive.archives.service.BoxMensuelleService;

@RestController
@RequestMapping("/api/box")
public class BoxFichierController {

    @Autowired
    private BoxMensuelleService boxMensuelleService;

    // ✅ Récupérer toutes les BoxMensuelle
    @GetMapping("/all")
    public ResponseEntity<List<BoxMensuelle>> getAllBoxes() {
        return ResponseEntity.ok(boxMensuelleService.getAllBoxes());
    }

    // ✅ Ajouter une BoxMensuelle manuellement
    @PostMapping("/ajouter")
    public ResponseEntity<BoxMensuelle> ajouterBoxManuelle(@RequestBody BoxMensuelle box) {
        try {
            BoxMensuelle nouvelleBox = boxMensuelleService.ajouterBoxManuellement(
                box.getMois(),
                box.getNom(),// Ajout du nom ici si nécessaire
                box.getAnnee(),
                box.getDateDebut(),
                box.getDateFin(),
                box.getStatut()
            );
            return ResponseEntity.ok(nouvelleBox);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Gérer automatiquement la box mensuelle (clôturer l'ancienne et créer une nouvelle)
    @PostMapping("/gerer")
    public ResponseEntity<Void> gererBoxMensuelle() {
        try {
            boxMensuelleService.gererBoxesMensuelles();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Modifier une BoxMensuelle existante par son ID
    @PutMapping("/modifier/{id}")
    public ResponseEntity<BoxMensuelle> modifierBoxMensuelle(@PathVariable Long id,
                                                              @RequestBody BoxMensuelle boxModifiee) {
        try {
            BoxMensuelle modifiedBox = boxMensuelleService.modifierBoxMensuelle(id, boxModifiee);
            return ResponseEntity.ok(modifiedBox);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Supprimer une BoxMensuelle par son ID
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> supprimerBoxMensuelle(@PathVariable Long id) {
        try {
            boxMensuelleService.supprimerBoxMensuelle(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
