package com.archive.archives.repository;



// import com.example.couratiapp.entites.BoxMensuelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.archive.archives.model.BoxMensuelle;

import java.util.List;
import java.util.Optional;

public interface BoxMensuelleRepository extends JpaRepository<BoxMensuelle, Long> {

    boolean existsByMoisAndAnnee(int mois, int annee);
    List<BoxMensuelle> findByStatut(BoxMensuelle.StatutBox statut);
    List<BoxMensuelle> findByAnnee(int annee);



    @Query("SELECT b FROM BoxMensuelle b WHERE b.mois = :mois AND b.annee = :annee")
    Optional<BoxMensuelle> findByMoisAndAnnee(int mois, int annee);

    @Query("SELECT b FROM BoxMensuelle b WHERE b.dateDebut <= CURRENT_DATE AND b.dateFin >= CURRENT_DATE")
    BoxMensuelle findBoxActuelle();
}

