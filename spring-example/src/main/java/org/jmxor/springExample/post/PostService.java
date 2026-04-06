package org.jmxor.springExample.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    Page<Post> getAll(Pageable pageable);
    Post getById(UUID id);
    Post create(Post post);
    Post publish(UUID id);
}
