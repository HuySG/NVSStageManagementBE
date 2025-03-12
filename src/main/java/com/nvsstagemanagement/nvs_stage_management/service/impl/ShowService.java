package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.show.DepartmentShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowTaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentShowRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.ShowRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IShowService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService implements IShowService {
    private final ShowRepository showRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentShowRepository departmentProjectRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Override
    public List<ShowDTO> getAllShow() {
        List<Show> shows = showRepository.findAll();
        return shows.stream()
                .map(project -> modelMapper.map(project, ShowDTO.class)).toList();
    }

    @Override
    public ShowDTO createShow(ShowDTO showDTO) {
        Show createdShow = modelMapper.map(showDTO, Show.class);
        showRepository.save(createdShow);
        return modelMapper.map(createdShow, ShowDTO.class);

    }

    @Override
    public DepartmentShowDTO assignDepartmentToShow(DepartmentShowDTO departmentShowDTO) {
        Department department = departmentRepository.findById(departmentShowDTO.getDepartmentID())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Show show = showRepository.findById(departmentShowDTO.getProjectID())
                .orElseThrow(() -> new RuntimeException("show not found"));

        DepartmentShowId departmentShowId = new DepartmentShowId(departmentShowDTO.getDepartmentID(), departmentShowDTO.getProjectID());

        if (departmentProjectRepository.existsById(departmentShowId)) {
            throw new RuntimeException("Department is already assigned to this show!");
        }

        DepartmentShow departmentShow = new DepartmentShow();
        departmentShow.setId(departmentShowId);
        departmentShow.setDepartment(department);
        departmentShow.setShow(show);

        departmentProjectRepository.save(departmentShow);

        DepartmentShowDTO responseDTO = new DepartmentShowDTO();
        responseDTO.setDepartmentID(departmentShowId.getDepartmentId());
        responseDTO.setProjectID(departmentShowDTO.getProjectID());
        return responseDTO;
    }

    @Override
    public List<ShowTaskDTO> getAllShowWithTasks() {
        List<Show> shows = showRepository.findAllWithTasks();
        return shows.stream()
                .map(project -> {
                    ShowTaskDTO dto = modelMapper.map(project, ShowTaskDTO.class);
                    if (project.getTasks() != null) {
                        dto.setTasks(
                                project.getTasks().stream()
                                        .map(task -> modelMapper.map(task, TaskDTO.class))
                                        .collect(Collectors.toList())
                        );
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ShowDTO> getShowWithUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        List<Show> shows = showRepository.findShowByUserId(userId);

        return shows.stream().map(project -> {
            ShowDTO dto = modelMapper.map(project, ShowDTO.class);
            dto.setDepartment(user.getDepartment().getName());
            return dto;
        }).collect(Collectors.toList());
    }
}
