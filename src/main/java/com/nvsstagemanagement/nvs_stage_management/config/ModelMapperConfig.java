package com.nvsstagemanagement.nvs_stage_management.config;

import com.nvsstagemanagement.nvs_stage_management.dto.comment.CommentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Comment;
import com.nvsstagemanagement.nvs_stage_management.model.TaskUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hibernate.Hibernate.map;

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
        modelMapper.addMappings(new PropertyMap<TaskUser, TaskUserDTO>() {
            @Override
            protected void configure() {
                map().setTaskID(source.getId().getTaskId());
            }
        });
        return modelMapper;
    }
}
