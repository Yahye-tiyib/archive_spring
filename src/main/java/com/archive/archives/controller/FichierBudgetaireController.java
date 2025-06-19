package com.archive.archives.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.archive.archives.model.BoxMensuelle;
import com.archive.archives.model.FichierBudgetaire;
import com.archive.archives.repository.BoxMensuelleRepository;
import com.archive.archives.service.FichierBudgetaireService;

@RestController
@RequestMapping("/api/fichiers")
public class FichierBudgetaireController {

    @Autowired
    private FichierBudgetaireService fichierService;
    @Autowired
    private BoxMensuelleRepository boxRepo;


    // Ajouter un fichier avec l'ID de la box


    @GetMapping("/all")
    public List<FichierBudgetaire> getAll() {
        return fichierService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichierBudgetaire> getById(@PathVariable Long id) {
        return fichierService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping(value = "/ajouter/avec-box/{boxId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<FichierBudgetaire> ajouterFichierAvecBoxId(
        @RequestParam("nomfichier") String nomfichier,
        @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
        @RequestParam("etat") boolean etat,
        @RequestParam("traiter") boolean traiter,
        @RequestParam("file") MultipartFile file,
        @PathVariable Long boxId
) {
    try {
        // Vérification que le fichier est bien reçu
        if (file.isEmpty()) {
            System.out.println("Aucun fichier reçu.");
            return ResponseEntity.badRequest().body(null);
        }
        
        System.out.println("Nom du fichier reçu : " + file.getOriginalFilename());
        
        // Enregistrer le fichier sur le disque
        String fileName = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/uploads/fichhiers/" + fileName);
        Files.write(path, file.getBytes());

        // Créer et remplir l'objet FichierBudgetaire
        FichierBudgetaire fichier = new FichierBudgetaire();
        fichier.setNomfichier(nomfichier);
        fichier.setDateReception(dateReception);
        fichier.setEtat(etat);
        fichier.setTraiter(traiter);
        fichier.setFichier(path.toString());

        // Récupérer la box et l'associer au fichier
        BoxMensuelle box = boxRepo.findById(boxId)
                .orElseThrow(() -> new IllegalStateException("Box non trouvée avec l'ID : " + boxId));
        fichier.setBox(box);

        // Sauvegarder le fichier et renvoyer la réponse
        return ResponseEntity.ok(fichierService.ajouterFichierAvecBoxId(fichier, boxId));
    } catch (Exception e) {
        System.out.println("Erreur dans le traitement du fichier : " + e.getMessage());
        return ResponseEntity.badRequest().body(null);
    }
}

    

    // Ajouter un fichier à la box actuellement ouverte
    @PostMapping("/ajouter/box-actuelle")
public ResponseEntity<FichierBudgetaire> ajouterFichierBoxActuelle(
        @RequestParam("nomfichier") String nomfichier,
        @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
        @RequestParam("etat") boolean etat,
        @RequestParam("traiter") boolean traiter,
        @RequestParam("file") MultipartFile file) {
    try {
        String fileName = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/uploads/fichhiers/" + fileName);
        Files.write(path, file.getBytes());

        FichierBudgetaire fichier = new FichierBudgetaire();
        fichier.setNomfichier(nomfichier);
        fichier.setFichier(path.toString());
        fichier.setDateReception(dateReception);
        fichier.setEtat(etat);
        fichier.setTraiter(traiter);

        return ResponseEntity.ok(fichierService.ajouterFichierBoxActuelle(fichier));
    } catch (IOException e) {
        return ResponseEntity.status(500).build();
    }
}
    @GetMapping("/statistiques/{boxId}")
    public Map<String, Long> getStatistiquesByBox(@PathVariable Long boxId) {
        return fichierService.getNombreFichiersByBox(boxId);
    }
    @GetMapping("/box/{boxId}")
    public List<FichierBudgetaire> getFichiersByBox(@PathVariable Long boxId) {
        return fichierService.getFichiersByBox(boxId);
    }

    @PutMapping("/modifier/{id}")
public ResponseEntity<FichierBudgetaire> updateFichier(
        @PathVariable Long id,
        @RequestParam("nomfichier") String nomfichier,
        @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
        @RequestParam("etat") boolean etat,
        @RequestParam("traiter") boolean traiter,
        @RequestParam(value = "file", required = false) MultipartFile file) {
    try {
        FichierBudgetaire existing = fichierService.getFichierById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setNomfichier(nomfichier);
        existing.setDateReception(dateReception);
        existing.setEtat(etat);
        existing.setTraiter(traiter);

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/uploads/fichhiers/" + fileName);
            Files.write(path, file.getBytes());
            existing.setFichier(path.toString());
        }

        return ResponseEntity.ok(fichierService.updateFichier(id, existing));
    } catch (IOException e) {
        return ResponseEntity.status(500).build();
    }
}


    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deleteFichier(@PathVariable Long id) {
        try {
            fichierService.deleteFichier(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}
