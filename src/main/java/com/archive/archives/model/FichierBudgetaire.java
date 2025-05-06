package com.archive.archives.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FichierBudgetaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomfichier;


    private String fichier;

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
    public String getNomfichier() {
      return nomfichier;
    }

    public void setNomfichier(String nomfichier) {
      this.nomfichier = nomfichier;
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

    public String getFichier() {
      return fichier;
    }

    public void setFichier(String fichier) {
      this.fichier = fichier;
    }
}