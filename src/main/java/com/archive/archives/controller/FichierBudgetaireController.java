package com.archive.archives.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

  @PostMapping("/ajouter")
  public ResponseEntity<FichierBudgetaire> ajouterFichier(
      @RequestParam("nomEtablissement") String nomEtablissement,
      @RequestParam("referenceLettre") String referenceLettre,
      @RequestParam("objet") String objet,
      @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
      @RequestParam("traiter") boolean traiter,
      @RequestParam("file") MultipartFile file,
      @RequestParam("boxId") Long boxId) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body(null);
      }

      String uploadDir = "uploads/fichiersBudgeters";
      Files.createDirectories(Paths.get(uploadDir));

      String fileName = file.getOriginalFilename();
      Path filePath = Paths.get(uploadDir, fileName);
      Files.write(filePath, file.getBytes());

      FichierBudgetaire fichier = new FichierBudgetaire();
      fichier.setNomEtablissement(nomEtablissement);
      fichier.setReferenceLettre(referenceLettre);
      fichier.setObjet(objet);
      fichier.setDateReception(dateReception);
      fichier.setTraiter(traiter);
      fichier.setFichier("/uploads/fichiersBudgeters/" + fileName);

      BoxMensuelle box = boxRepo.findById(boxId)
          .orElseThrow(() -> new IllegalStateException("Box non trouvée avec l'ID : " + boxId));
      fichier.setBox(box);

      return ResponseEntity.ok(fichierService.ajouterFichierAvecBoxId(fichier, boxId));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  @PostMapping("/ajouter/box-actuelle")
  public ResponseEntity<FichierBudgetaire> ajouterFichierBoxActuelle(
      @RequestParam("nomEtablissement") String nomEtablissement,
      @RequestParam("referenceLettre") String referenceLettre,
      @RequestParam("objet") String objet,
      @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
      @RequestParam("traiter") boolean traiter,
      @RequestParam("file") MultipartFile file) {
    try {
      String fileName = file.getOriginalFilename();
      Path path = Paths.get("uploads/fichiersBudgeters/" + fileName);
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());

      FichierBudgetaire fichier = new FichierBudgetaire();
      fichier.setNomEtablissement(nomEtablissement);
      fichier.setReferenceLettre(referenceLettre);
      fichier.setObjet(objet);
      fichier.setFichier("/uploads/fichiersBudgeters/" + fileName);
      fichier.setDateReception(dateReception);
      fichier.setTraiter(traiter);

      return ResponseEntity.ok(fichierService.ajouterFichierBoxActuelle(fichier));
    } catch (IOException e) {
      return ResponseEntity.status(500).build();
    }
  }

  @PutMapping("/modifier/{id}")
  public ResponseEntity<FichierBudgetaire> updateFichier(
      @PathVariable Long id,
      @RequestParam("nomEtablissement") String nomEtablissement,
      @RequestParam("referenceLettre") String referenceLettre,
      @RequestParam("objet") String objet,
      @RequestParam("dateReception") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
      @RequestParam("traiter") boolean traiter,
      @RequestParam(value = "file", required = false) MultipartFile file) {
    try {
      FichierBudgetaire existing = fichierService.getFichierById(id);
      if (existing == null)
        return ResponseEntity.notFound().build();

      existing.setNomEtablissement(nomEtablissement);
      existing.setReferenceLettre(referenceLettre);
      existing.setObjet(objet);
      existing.setDateReception(dateReception);
      existing.setTraiter(traiter);

      if (file != null && !file.isEmpty()) {
        String fileName = file.getOriginalFilename();
        Path path = Paths.get("uploads/fichiersBudgeters/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        existing.setFichier("/uploads/fichiersBudgeters/" + fileName);
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

  @GetMapping("/statistiques/{boxId}")
  public Map<String, Long> getStatistiquesByBox(@PathVariable Long boxId) {
    return fichierService.getNombreFichiersByBox(boxId);
  }

  @GetMapping("/box/{boxId}")
  public List<FichierBudgetaire> getFichiersByBox(@PathVariable Long boxId) {
    return fichierService.getFichiersByBox(boxId);
  }
}
