package br.com.agendesaude.api.domain.service;

import br.com.agendesaude.api.domain.dto.LocationDto;
import br.com.agendesaude.api.domain.dto.UserDto;
import br.com.agendesaude.api.domain.enums.AccessLevelType;
import br.com.agendesaude.api.domain.enums.UserType;
import br.com.agendesaude.api.domain.model.Address;
import br.com.agendesaude.api.domain.model.Location;
import br.com.agendesaude.api.domain.model.Media;
import br.com.agendesaude.api.domain.model.User;
import br.com.agendesaude.api.domain.repository.AddressRepository;
import br.com.agendesaude.api.domain.repository.LocationRepository;
import br.com.agendesaude.api.domain.repository.MediaRepository;
import br.com.agendesaude.api.domain.repository.UserRepository;
import br.com.agendesaude.api.infra.exception.BadRequestException;
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

  @Autowired
  private UserService userService;

  private static void verifyUserLocation(User user, Location location) {
    if (location.getUser().getId() != user.getId()) {
      throw new BadRequestException("Location does not match the logged-in user");
    }
  }

  @Transactional
  public Page<LocationDto> findAllLocations(Pageable pageable) {
    return locationRepository.findAllLocations(pageable)
        .map(Location::mapEntityToDto);
  }

  @Transactional
  public LocationDto findByUserId(Long userId) {
    Location location = locationRepository.findByUserId(userId);
    if (location.getId() == null) {
      throw new BadRequestException("Location not found.");
    }
    return location.mapEntityToDto();
  }

  @Transactional
  public LocationDto createLocation(LocationDto locationDto) {

    userService.verifyTaxIdAndEmailExists(locationDto.getUser());

    Address address = locationDto.getUser().getAddress();
    if (address != null) {
      address = addressRepository.save(address);
    } else {
      throw new BadRequestException("Null address");
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

    if (location.getUser().getId() != null
        && location.getUser().getAddress() != null && location.getUser().getAddress().getId() != null
        && location.getUser().getEmail() != null && !location.getUser().getEmail().isEmpty()
        && location.getUser().getTaxId() != null && !location.getUser().getTaxId().isEmpty()
        && location.getUser().getPhone() != null && !location.getUser().getPhone().isEmpty()

        && location.getThumbnail() != null && locationDto.getThumbnail().getId() != null

        && location.getName() != null && !location.getName().isEmpty()) {
      location.getUser().setAccessLevel(AccessLevelType.FULL);
    }

    location = locationRepository.save(location);

    return location.mapEntityToDto();
  }

  @Transactional(readOnly = true)
  public LocationDto getLocationById(Long id, User user) {
    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Location not found"));

    verifyUserLocation(user, location);

    LocationDto locationDto = new LocationDto();
    locationDto.setId(location.getId());
    locationDto.setName(location.getName());
    locationDto.setUser(location.getUser());
    locationDto.setThumbnail(location.getThumbnail());
    locationDto.setCreatedAt(location.getCreatedAt());
    locationDto.setUpdatedAt(location.getUpdatedAt());
    return locationDto;
  }

  //    @Transactional
//    public void deleteLocation(Long id) {
//        Location location = locationRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Location not found"));
//
//        if (location.getConsultations() != null && !location.getConsultations().isEmpty()) {
//            throw new IllegalStateException("Cannot delete location with associated consultations");
//        }
//
//        locationRepository.delete(location);
//    }

  //HELPER METHODS

  @Transactional
  public LocationDto updateLocation(LocationDto locationDto, User user) {
    Location existingLocation = locationRepository.findById(locationDto.getId())
        .orElseThrow(() -> new BadRequestException("Location not found"));

    verifyUserLocation(user, existingLocation);

    existingLocation.setName(
        locationDto.getName() != null ? locationDto.getName() : existingLocation.getName());

    if (locationDto.getThumbnail() != null) {
      Media thumbnail = mediaRepository.save(locationDto.getThumbnail());
      existingLocation.setThumbnail(thumbnail);
    }

    User existingUser = existingLocation.getUser();
    UserDto userDto = locationDto.getUser().mapEntityToDto();

    if (userDto.getAddress() != null) {
      Address address = userDto.getAddress();
      existingUser.setAddress(addressRepository.save(address));
    }

    if (userDto.getPhone() != null) {
      existingUser.setPhone(userDto.getPhone());
    }

    if (locationDto.getUser().getPassword() != null) {
      existingLocation.getUser().setPassword(new BCryptPasswordEncoder().encode(locationDto.getUser().getPassword()));
    }

    if (locationDto.getUser().getEmail() != null) {
      existingLocation.getUser().setEmail(locationDto.getUser().getEmail());
    }

    if (locationDto.getUser().getTaxId() != null) {
      existingLocation.getUser().setTaxId(locationDto.getUser().getTaxId());
    }

    userRepository.save(existingUser);

    if (existingLocation.getUser().getId() != null
        && existingLocation.getUser().getAddress() != null && existingLocation.getUser().getAddress().getId() != null
        && existingLocation.getUser().getEmail() != null && !existingLocation.getUser().getEmail().isEmpty()
        && existingLocation.getUser().getTaxId() != null && !existingLocation.getUser().getTaxId().isEmpty()
        && existingLocation.getUser().getPhone() != null && !existingLocation.getUser().getPhone().isEmpty()

        && existingLocation.getThumbnail() != null && existingLocation.getThumbnail().getId() != null

        && existingLocation.getName() != null && !existingLocation.getName().isEmpty()) {
      existingLocation.getUser().setAccessLevel(AccessLevelType.FULL);
    }

    existingLocation = locationRepository.save(existingLocation);

    return existingLocation.mapEntityToDto();
  }

}
