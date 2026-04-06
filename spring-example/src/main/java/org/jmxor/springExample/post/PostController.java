package org.jmxor.springExample.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostModelAssembler postModelAssembler;
    private final PagedResourcesAssembler<Post> pagedResourcesAssembler;

    public PostController(PostService postService, PostModelAssembler postModelAssembler, PagedResourcesAssembler<Post> pagedResourcesAssembler) {
        this.postService = postService;
        this.postModelAssembler = postModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<Post>> getAll(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = postService.getAll(pageable);
        return pagedResourcesAssembler.toModel(posts, postModelAssembler);
    }

    @PostMapping
    public EntityModel<Post> create(@RequestBody Post post) {
        return postModelAssembler.toModel(postService.create(post));
    }

    @GetMapping("/{id}")
    public EntityModel<Post> getById(@PathVariable UUID id) {
        return postModelAssembler.toModel(postService.getById(id));
    }

    @PostMapping("/{id}/publish")
    public EntityModel<Post> publish(@PathVariable UUID id) {
        return postModelAssembler.toModel(postService.publish(id));
    }
}
