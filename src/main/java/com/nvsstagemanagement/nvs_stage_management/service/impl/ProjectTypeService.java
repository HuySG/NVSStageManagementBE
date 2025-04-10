package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectType;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectTypeRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectTypeService  implements IProjectTypeService {
    private final ProjectTypeRepository projectTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ProjectTypeDTO> getAllProjectTypes() {
        List<ProjectType> projectTypes = projectTypeRepository.findAll();
        return projectTypes.stream()
                .map(pt -> modelMapper.map(pt, ProjectTypeDTO.class))
                .collect(Collectors.toList());
    }
}
