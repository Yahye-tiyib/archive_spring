package com.archive.archives.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class BoxMensuelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mois;       // 1 à 12
    private int annee;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    public enum StatutBox { CLOTUREE,OUVERTE }
    private StatutBox statut;

    private String nom;
    @JsonIgnore
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    private List<FichierBudgetaire> fichiers = new ArrayList<>();

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public StatutBox getStatut() {
        return statut;
    }

    public void setStatut(StatutBox statut) {
        this.statut = statut;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<FichierBudgetaire> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<FichierBudgetaire> fichiers) {
        this.fichiers = fichiers;
    }
}
