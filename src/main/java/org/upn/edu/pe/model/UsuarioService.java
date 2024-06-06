package org.upn.edu.pe.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upn.edu.pe.repository.IUserRepository;

@Service
public class UsuarioService {
	 @Autowired
	    private IUserRepository usuarioRepository;

	    public Usuario findByDNIAndContrase(int DNI, String contrase) {
	        return usuarioRepository.findByDNIAndContrase(DNI, contrase);
	    }

}
