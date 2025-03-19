package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;

    @PostMapping
    public ResponseEntity<MilestoneDTO> createMilestone(@RequestBody MilestoneDTO milestoneDTO) {
        MilestoneDTO created = milestoneService.createMilestone(milestoneDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{milestoneID}")
    public ResponseEntity<MilestoneDTO> getMilestone(@PathVariable String milestoneID) {
        MilestoneDTO milestoneDTO = milestoneService.getMilestone(milestoneID);
        return ResponseEntity.ok(milestoneDTO);
    }

    @PutMapping("/{milestoneID}")
    public ResponseEntity<MilestoneDTO> updateMilestone(@PathVariable String milestoneID,
                                                        @RequestBody MilestoneDTO milestoneDTO) {
        MilestoneDTO updated = milestoneService.updateMilestone(milestoneID, milestoneDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{milestoneID}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable String milestoneID) {
        milestoneService.deleteMilestone(milestoneID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectID}")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByProject(@PathVariable String projectID) {
        List<MilestoneDTO> list = milestoneService.getMilestonesByProject(projectID);
        return ResponseEntity.ok(list);
    }
}
