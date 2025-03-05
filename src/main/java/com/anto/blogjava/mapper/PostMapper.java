package com.anto.blogjava.mapper;

import com.anto.blogjava.domain.dto.PostDto;
import com.anto.blogjava.domain.entity.Post;
import com.anto.blogjava.domain.entity.User;
import com.anto.blogjava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {
    
    private final UserRepository userRepository;

    public PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .build();
    }

    public Post toEntity(PostDto postDto) {
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return Post.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .user(user)
                .build();
    }

    public List<PostDto> toDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 