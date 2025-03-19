package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.location.CreateLocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.location.LocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.location.UpdateLocationDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }
    @GetMapping("/{locationID}")
    public ResponseEntity<LocationDTO> getLocationById(@RequestParam int locationID) {
        LocationDTO location = locationService.getLocationById(locationID);
        return ResponseEntity.ok(location);
    }
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody CreateLocationDTO createLocationDTO) {
        LocationDTO location = locationService.createLocation(createLocationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }
    @PutMapping("/{locationID}")
    public ResponseEntity<LocationDTO> updateLocation(
            @RequestParam int locationID,
            @Valid @RequestBody UpdateLocationDTO updateLocationDTO
    ) {
        LocationDTO updated = locationService.updateLocation(locationID, updateLocationDTO);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{locationID}")
    public ResponseEntity<Void> deleteLocation(@PathVariable int locationID) {
        locationService.deleteLocation(locationID);
        return ResponseEntity.noContent().build();
    }
}
