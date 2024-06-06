package org.upn.edu.pe.repository;

import org.upn.edu.pe.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByDNIAndContrase(int DNI, String contrase);
	@Query("SELECT u FROM Usuario u WHERE u.DNI = :DNI")
	Usuario findByDni(int DNI);
}
