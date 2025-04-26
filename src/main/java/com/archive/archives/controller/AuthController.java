package com.archive.archives.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archive.archives.model.Utilisateur;
import com.archive.archives.service.UtilisateurService;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UtilisateurService utilisateurService;



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Utilisateur utilisateur) {
        boolean isAuthenticated = utilisateurService.authenticate(utilisateur.getEmail(), utilisateur.getPassword());
    
        if (isAuthenticated) {
            // ✅ Token simulé (remplace ça par du JWT si tu veux faire propre)
            String fakeToken = UUID.randomUUID().toString();
    
            return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie !",
                "token", fakeToken
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect."));
        }
    }
    


    @PostMapping("/register")
    public String ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        utilisateur.setPassword(utilisateur.getPassword());
        utilisateurService.ajouterUtilisateur(utilisateur);
        return "Utilisateur ajouté avec succès !";
    }
}
