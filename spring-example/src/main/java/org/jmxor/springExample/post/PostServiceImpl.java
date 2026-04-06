package org.jmxor.springExample.post;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<Post> getAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Post getById(UUID id) {
        return postRepository.findById(id)
            .orElseThrow(() ->new EntityNotFoundException("Post not found"));
    }

    @Override
    public Post create(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post publish(UUID id) {
        Post post = getById(id);

        if (post.getStatus() != PostStatus.DRAFT) {
            throw new IllegalStateException("Post is already published");
        }

        post.setStatus(PostStatus.PUBLISHED);
        return postRepository.save(post);
    }
}
