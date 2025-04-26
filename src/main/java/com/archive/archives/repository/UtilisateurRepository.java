package com.archive.archives.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archives.model.Utilisateur;



public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
    Optional<Utilisateur> findByEmail(String email);
}
