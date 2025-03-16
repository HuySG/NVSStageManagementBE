package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.location.CreateLocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.location.LocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.location.UpdateLocationDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.LocationStatus;
import com.nvsstagemanagement.nvs_stage_management.model.Location;
import com.nvsstagemanagement.nvs_stage_management.repository.LocationRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ILocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService implements ILocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<LocationDTO> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(location -> modelMapper.map(location, LocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO getLocationById(String locationID) {
        Location location = locationRepository.findById(locationID)
                .orElseThrow(() -> new RuntimeException("Location not found: " + locationID));
        return modelMapper.map(location, LocationDTO.class);
    }

    @Override
    public LocationDTO createLocation(CreateLocationDTO createLocationDTO) {

        if (createLocationDTO.getLocationID() == null || createLocationDTO.getLocationID().trim().isEmpty()) {
            createLocationDTO.setLocationID(UUID.randomUUID().toString());
        }
        Location location = modelMapper.map(createLocationDTO, Location.class);
        location.setStatus(LocationStatus.AVAILABLE.toString());

        Location saved = locationRepository.save(location);

        return modelMapper.map(saved, LocationDTO.class);
    }

    @Override
    public LocationDTO updateLocation(String locationID, UpdateLocationDTO updateLocationDTO) {

        Location existing = locationRepository.findById(locationID)
                .orElseThrow(() -> new RuntimeException("Location not found: " + locationID));

        if (updateLocationDTO.getLocationName() != null && !updateLocationDTO.getLocationName().trim().isEmpty()) {
            existing.setLocationName(updateLocationDTO.getLocationName());
        }
        if (updateLocationDTO.getStatus() != null) {
            existing.setStatus(updateLocationDTO.getStatus().toString());
        }

        Location updated = locationRepository.save(existing);
        return modelMapper.map(updated, LocationDTO.class);
    }

    @Override
    public void deleteLocation(String locationID) {
        Location existing = locationRepository.findById(locationID)
                .orElseThrow(() -> new RuntimeException("Location not found: " + locationID));
        locationRepository.delete(existing);
    }
}
