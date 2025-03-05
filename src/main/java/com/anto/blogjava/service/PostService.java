package com.anto.blogjava.service;

import com.anto.blogjava.domain.entity.Post;
import com.anto.blogjava.domain.entity.User;
import com.anto.blogjava.repository.PostRepository;
import com.anto.blogjava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }
    
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }
    
    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByUserUsername(username);
    }
    
    public Post createPost(Post post, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        post.setUser(user);
        return postRepository.save(post);
    }
    
    public Optional<Post> updatePost(Long id, Post postDetails) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    existingPost.setTitle(postDetails.getTitle());
                    existingPost.setContent(postDetails.getContent());
                    return postRepository.save(existingPost);
                });
    }
    
    public boolean deletePost(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    postRepository.delete(post);
                    return true;
                })
                .orElse(false);
    }
    
    public boolean isPostOwner(Long postId, Long userId) {
        return postRepository.findById(postId)
                .map(post -> post.getUser().getId().equals(userId))
                .orElse(false);
    }
} 