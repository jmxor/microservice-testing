package org.jmxor.springExample.post;

import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostModelAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {
    @Override
    public EntityModel<Post> toModel(Post post) {
        EntityModel<Post> orderModel = EntityModel.of(post)
            .add(linkTo(methodOn(PostController.class).getById(post.getId())).withSelfRel());

        if (post.getStatus() == PostStatus.DRAFT) {
            orderModel.add(linkTo(methodOn(PostController.class).publish(post.getId())).withRel("publish"));
        }

        return orderModel;
    }

    @NullMarked
    @Override
    public CollectionModel<EntityModel<Post>> toCollectionModel(Iterable<? extends Post> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
            .add(linkTo(methodOn(PostController.class).getAll(null)).withSelfRel());
    }
}
