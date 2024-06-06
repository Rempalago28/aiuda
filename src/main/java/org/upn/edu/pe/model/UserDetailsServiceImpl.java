package org.upn.edu.pe.model;

import org.hibernate.engine.internal.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.upn.edu.pe.repository.IUserRepository;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
	 @Autowired
	    private IUserRepository userRepository;

	  @Override
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        try {
	            int DNI = Integer.parseInt(username); // Convertir el DNI de String a int
	            Usuario usuario = userRepository.findByDni(DNI);
	            if (usuario == null) {
	                throw new UsernameNotFoundException("Usuario no encontrado con DNI: " + DNI);
	            }
	            return User.builder()
	                    .username(String.valueOf(usuario.getDNI()))
	                    .password(usuario.getContrase()) // Asegúrate de que la contraseña esté encriptada si usas BCrypt
	                    .roles("USER")
	                    .build();
	        } catch (NumberFormatException e) {
	            throw new UsernameNotFoundException("El DNI debe ser un número entero.");
	        }
	    }
}
