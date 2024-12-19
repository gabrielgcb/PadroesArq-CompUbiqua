package br.com.agendesaude.api.domain.repository;

import br.com.agendesaude.api.domain.model.Address;
import br.com.agendesaude.api.domain.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
