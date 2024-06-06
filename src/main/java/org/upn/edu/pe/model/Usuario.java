package org.upn.edu.pe.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idUsuario;
	@Column(name="dni",nullable = false)
	private int DNI;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private String apellido;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fechaNacimiento",nullable = false)
	private Date fechaNacimiento;
	@Column(nullable = false)
	private String telefono; 
	@Column(nullable = false)
	private String correo_electro;
	@Column(nullable = false)
	private String contrase;
	@Column(nullable = false)
	private String direccion;
	
	
	public Usuario() {}

	
	

	public Usuario(int idUsuario, int dNI, String nombre, String apellido, Date fechaNacimiento, String telefono,
			String correo_electro, String contrase, String direccion) {
		super();
		this.idUsuario = idUsuario;
		DNI = dNI;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaNacimiento = fechaNacimiento;
		this.telefono = telefono;
		this.correo_electro = correo_electro;
		this.contrase = contrase;
		this.direccion = direccion;
	}




	public int getIdUsuario() {
		return idUsuario;
	}


	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}


	public int getDNI() {
		return DNI;
	}


	public void setDNI(int dNI) {
		DNI = dNI;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getApellido() {
		return apellido;
	}


	public void setApellido(String apellido) {
		this.apellido = apellido;
	}


	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}


	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}


	public String getTelefono() {
		return telefono;
	}


	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public String getCorreo_electro() {
		return correo_electro;
	}


	public void setCorreo_electro(String correo_electro) {
		this.correo_electro = correo_electro;
	}


	public String getContrase() {
		return contrase;
	}


	public void setContrase(String contrase) {
		this.contrase = contrase;
	}


	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

}
