package com.archive.archives.repository;



// import com.example.couratiapp.entites.FichierBudgetaire;
import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archives.model.FichierBudgetaire;

import java.util.List;



public interface FichierBudgetaireRepository extends JpaRepository<FichierBudgetaire, Long> {

    List<FichierBudgetaire> findByBoxId(Long boxId);
    
    List<FichierBudgetaire> findByTraiter(boolean traiter);

    List<FichierBudgetaire> findByEtat(boolean etat);
}
