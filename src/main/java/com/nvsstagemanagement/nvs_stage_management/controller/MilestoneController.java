package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.milestone.CreateMilestoneDTO;
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
    public ResponseEntity<?> createMilestone(@RequestBody CreateMilestoneDTO createMilestoneDTO) {
        try {
            MilestoneDTO created = milestoneService.createMilestone(createMilestoneDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + ex.getMessage());
        }
    }

    @GetMapping("/{milestoneID}")
    public ResponseEntity<?> getMilestone(@PathVariable String milestoneID) {
        try {
            MilestoneDTO milestoneDTO = milestoneService.getMilestone(milestoneID);
            return ResponseEntity.ok(milestoneDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + ex.getMessage());
        }
    }

    @PutMapping("/{milestoneID}")
    public ResponseEntity<?> updateMilestone(@PathVariable String milestoneID,
                                             @RequestBody MilestoneDTO milestoneDTO) {
        try {
            MilestoneDTO updated = milestoneService.updateMilestone(milestoneID, milestoneDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{milestoneID}")
    public ResponseEntity<?> deleteMilestone(@PathVariable String milestoneID) {
        try {
            milestoneService.deleteMilestone(milestoneID);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + ex.getMessage());
        }
    }

    @GetMapping("/project/{projectID}")
    public ResponseEntity<?> getMilestonesByProject(@PathVariable String projectID) {
        try {
            List<MilestoneDTO> list = milestoneService.getMilestonesByProject(projectID);
            return ResponseEntity.ok(list);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + ex.getMessage());
        }
    }
}
