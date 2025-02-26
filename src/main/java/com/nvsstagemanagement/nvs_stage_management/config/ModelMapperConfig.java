package com.nvsstagemanagement.nvs_stage_management.config;

import com.nvsstagemanagement.nvs_stage_management.dto.comment.CommentDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Comment.class, CommentDTO.class)
                .addMapping(comment -> comment.getTask().getTaskID(), CommentDTO::setTaskID)
                .addMapping(comment -> comment.getUser().getId(), CommentDTO::setUserID);
        modelMapper.typeMap(CommentDTO.class, Comment.class)
                .addMappings(mapper -> {
                    mapper.skip(Comment::setTask);
                    mapper.skip(Comment::setUser);
                });
        return modelMapper;
    }
}
