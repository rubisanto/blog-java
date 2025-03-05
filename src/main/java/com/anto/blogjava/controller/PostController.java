package com.anto.blogjava.controller;

import com.anto.blogjava.domain.dto.PostDto;
import com.anto.blogjava.domain.entity.Post;
import com.anto.blogjava.mapper.PostMapper;
import com.anto.blogjava.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "API de gestion des articles")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping
    @Operation(summary = "Récupérer tous les articles", 
               description = "Retourne la liste de tous les articles")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(postMapper.toDtoList(posts));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un article par son ID", 
               description = "Retourne un article spécifique par son ID")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(post -> ResponseEntity.ok(postMapper.toDto(post)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les articles d'un utilisateur", 
               description = "Retourne tous les articles d'un utilisateur spécifique")
    public ResponseEntity<List<PostDto>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(postMapper.toDtoList(posts));
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Récupérer les articles par nom d'utilisateur", 
               description = "Retourne tous les articles d'un utilisateur par son nom d'utilisateur")
    public ResponseEntity<List<PostDto>> getPostsByUsername(@PathVariable String username) {
        List<Post> posts = postService.getPostsByUsername(username);
        return ResponseEntity.ok(postMapper.toDtoList(posts));
    }
    
    @PostMapping
    @Operation(summary = "Créer un nouvel article", 
               description = "Crée un nouvel article pour un utilisateur spécifique")
    @ApiResponse(responseCode = "201", description = "Article créé avec succès")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post savedPost = postService.createPost(post, postDto.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postMapper.toDto(savedPost));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un article", 
               description = "Met à jour un article existant")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        return postService.updatePost(id, post)
                .map(updatedPost -> ResponseEntity.ok(postMapper.toDto(updatedPost)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un article", 
               description = "Supprime un article existant")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        boolean deleted = postService.deletePost(id);
        if (deleted) {
            return ResponseEntity.ok("Article supprimé avec succès");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 