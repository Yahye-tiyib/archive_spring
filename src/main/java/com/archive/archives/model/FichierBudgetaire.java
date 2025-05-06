package com.archive.archives.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FichierBudgetaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nomEtablissement; 
    private String fichier; // Remplacé ici

    private String referenceLettre;   // Nouveau
    private String objet;             // Nouveau

  

    private LocalDate dateReception;

    private boolean traiter = false; 
  
    @ManyToOne
    @JoinColumn(name = "box_id")
    private BoxMensuelle box;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    public String getReferenceLettre() {
        return referenceLettre;
    }

    public void setReferenceLettre(String referenceLettre) {
        this.referenceLettre = referenceLettre;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
    }

    public boolean isTraiter() {
        return traiter;
    }

    public void setTraiter(boolean traiter) {
        this.traiter = traiter;
    }

    public BoxMensuelle getBox() {
        return box;
    }

    public void setBox(BoxMensuelle box) {
        this.box = box;
    }
}
