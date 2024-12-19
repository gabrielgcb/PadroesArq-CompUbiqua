package br.com.agendesaude.api.domain.service;

import br.com.agendesaude.api.domain.dto.LocationDto;
import br.com.agendesaude.api.domain.enums.UserType;
import br.com.agendesaude.api.domain.model.Address;
import br.com.agendesaude.api.domain.model.Location;
import br.com.agendesaude.api.domain.model.Media;
import br.com.agendesaude.api.domain.model.User;
import br.com.agendesaude.api.domain.repository.AddressRepository;
import br.com.agendesaude.api.domain.repository.LocationRepository;
import br.com.agendesaude.api.domain.repository.MediaRepository;
import br.com.agendesaude.api.domain.repository.UserRepository;
import br.com.agendesaude.api.infra.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private UserRepository userRepository;

  @Transactional
  public LocationDto createLocation(LocationDto locationDto) {

    Address address = locationDto.getUser().getAddress();
    if (address != null) {
      address = addressRepository.save(address);
    } else {
      throw new CustomException("Null address");
    }

    Media thumbnail = locationDto.getThumbnail();
    if (thumbnail != null) {
      thumbnail = mediaRepository.save(thumbnail);
    }

    User user = locationDto.getUser();
    user.setType(UserType.LOCATION);
    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    user.setAddress(address);
    User savedUser = userRepository.save(user);

    Location location = locationDto.mapDtoToEntity();
    location.setUser(savedUser);
    location.setThumbnail(thumbnail);

    location = locationRepository.save(location);

    return location.mapEntityToDto();
  }


  @Transactional(readOnly = true)
  public LocationDto getLocationById(Long id) {
    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Location not found"));
    LocationDto locationDto = new LocationDto();
    locationDto.setId(location.getId());
    locationDto.setName(location.getName());
    locationDto.setUser(location.getUser());
    locationDto.setThumbnail(location.getThumbnail());
    locationDto.setCreatedAt(location.getCreatedAt());
    locationDto.setUpdatedAt(location.getUpdatedAt());
    return locationDto;
  }

  @Transactional
  public Page<LocationDto> findAllLocations(Pageable pageable) {
    return locationRepository.findAllLocations(pageable)
        .map(Location::mapEntityToDto);
  }

  @Transactional
  public void updateLocation(Long id, LocationDto locationDto) {
    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Location not found"));

    location.setName(locationDto.getName());
    location.setThumbnail(locationDto.getThumbnail());
    locationRepository.save(location);
  }

//    @Transactional
//    public void deleteLocation(Long id) {
//        Location location = locationRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
//
//        if (location.getConsultations() != null && !location.getConsultations().isEmpty()) {
//            throw new IllegalStateException("Cannot delete location with associated consultations");
//        }
//
//        locationRepository.delete(location);
//    }
}
