package com.anto.blogjava.controller;

import com.anto.blogjava.domain.dto.UserDto;
import com.anto.blogjava.domain.dto.PasswordChangeDto;
import com.anto.blogjava.mapper.UserMapper;
import com.anto.blogjava.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(summary = "Récupérer tous les utilisateurs", 
               description = "Retourne la liste de tous les utilisateurs sans leurs mots de passe")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var users = userService.getAllUsers();
        
        var userDtos = userMapper.toDtoList(users);
        
        return ResponseEntity.ok(userDtos);
    }
    
    @PostMapping
    @Operation(summary = "Créer un nouvel utilisateur",
               description = "Crée un nouvel utilisateur avec un nom d'utilisateur et un email uniques")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou nom d'utilisateur/email déjà utilisé")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
        }
        
        if (userService.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }
        
        var user = userMapper.toEntity(userDto);
        var savedUser = userService.createUser(user);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userMapper.toDto(savedUser));
    }
    
    @DeleteMapping
    @Operation(summary = "Supprimer un utilisateur",
               description = "Supprime un utilisateur en vérifiant son nom d'utilisateur et son email")
    @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé ou informations incorrectes")
    public ResponseEntity<String> deleteUser(@RequestParam String username, @RequestParam String email) {
        boolean deleted = userService.deleteUserByUsernameAndEmail(username, email);
        
        if (deleted) {
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } else {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Utilisateur non trouvé ou informations incorrectes");
        }
    }

    @PutMapping("/password")
    @Operation(summary = "Changer le mot de passe",
               description = "Change le mot de passe d'un utilisateur après vérification de l'ancien mot de passe")
    @ApiResponse(responseCode = "200", description = "Mot de passe modifié avec succès")
    @ApiResponse(responseCode = "401", description = "Nom d'utilisateur ou ancien mot de passe incorrect")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        boolean changed = userService.changePassword(
            passwordChangeDto.getUsername(),
            passwordChangeDto.getOldPassword(),
            passwordChangeDto.getNewPassword()
        );
        
        if (changed) {
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        } else {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Nom d'utilisateur ou ancien mot de passe incorrect");
        }
    }
} 