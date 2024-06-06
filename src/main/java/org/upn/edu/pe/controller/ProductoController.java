package org.upn.edu.pe.controller;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.upn.edu.pe.model.Detalle;
import org.upn.edu.pe.model.Producto;
import org.upn.edu.pe.model.Usuario;
import org.upn.edu.pe.model.UsuarioService;
import org.upn.edu.pe.model.Venta;
import org.upn.edu.pe.repository.IDetalleRepository;
import org.upn.edu.pe.repository.IProductoRepository;
import org.upn.edu.pe.repository.IUserRepository;
import org.upn.edu.pe.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"carrito","total","subtotal","costoEnvio","descuento"})
public class ProductoController {
	
	// Inicializacion del objeto carrito
	@ModelAttribute("carrito")
	public List<Detalle> getCarrito(){
		return new ArrayList<>();
	}
	
	// Inicializacion del objeto total
	@ModelAttribute("total")
	public double getTotal() {
		return 0.0;
	}
	
	// Inicializacion del objeto subtotal
	@ModelAttribute("subtotal")
	public double getSubtotal() {
		return 0.0;
	}
		
	// Inicializacion del objeto costoEnvio
		
	@ModelAttribute("descuento")
	public double getDescuento() {
		return 0.0;
	}
	
	// Inicializacion del objeto costoEnvio
	
	@ModelAttribute("costoEnvio")
	public double getCostoEnvio() {
		return 0.0;
	}
	
	// Declaracion e Inicializacion de objetos para el control del carrito de compras
	@Autowired
	private IProductoRepository productoRepository;
	
	@Autowired
	private IVentaRepository ventaRepository;
	
	@Autowired
	private IDetalleRepository detalleRepository;
	
	// Método para visualizar los productos a vender
	@GetMapping("/index")						// localhost:9090/index
	public String listado(Model model) {
		List<Producto> lista = new ArrayList<>();
		lista = productoRepository.findAll();	// Recuperar las filas de la tabla productos
		model.addAttribute("productos", lista);
		return "index";
	}
	
	// Método para agregar productos al carrito
	@GetMapping("/agregar/{idProducto}")
	public String agregar(Model model, @PathVariable(name = "idProducto", required = true) int idProducto) {
	    Producto p = productoRepository.findById(idProducto).orElse(null);
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double subtotal = 0.0; // Inicializamos el subtotal
	    boolean existe = false;
	    Detalle detalle = new Detalle();

	    if (p != null) {
	        detalle.setProducto(p);
	        detalle.setCantidad(1);
	        detalle.setSubtotal(detalle.getProducto().getPrecio() * detalle.getCantidad());
	    }

	    // Si el carrito está vacío
	    if (carrito.size() == 0) {
	        carrito.add(detalle);
	    } else {
	        for (Detalle d : carrito) {
	            if (d.getProducto().getIdProducto() == p.getIdProducto()) {
	                d.setCantidad(d.getCantidad() + 1);
	                d.setSubtotal(d.getProducto().getPrecio() * d.getCantidad());
	                existe = true;
	            }
	        }
	        if (!existe) carrito.add(detalle);
	    }

	    // Calculando la suma de sub-totales
	    for (Detalle d : carrito) subtotal += d.getSubtotal();

	    // Calcula el costo de envío (5% del subtotal)
	    double costoEnvio = 0;

	    // Aplicar descuento si el subtotal supera los 2000
	    double descuento = 0;
	    if (subtotal > 2000) {
	        descuento = 0;
	    }

	    // Calcula el total (subtotal - descuento + costo de envío)
	    double total = subtotal - descuento + costoEnvio;

	    // Guardar valores en la sesión y pasarlos a la vista
	    model.addAttribute("subtotal", subtotal);
	    model.addAttribute("total", total);
	    model.addAttribute("descuento", descuento); // Agrega el descuento al modelo
	    model.addAttribute("carrito", carrito);
	    model.addAttribute("costoEnvio", costoEnvio);

	    return "redirect:/index";
	}
	
	
	// Método para calcular el descuento y el costo de envío
	@PostMapping("/actualizarCarrito")
	public String calcularDescuentoYCostoEnvio(Model model) {
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double subtotal = 0.0;

	    // Calcula el subtotal
	    for (Detalle d : carrito) {
	        subtotal += d.getSubtotal();
	    }

	    // Calcula el costo de envío (5% del subtotal)
	    double costoEnvio = 0.05 * subtotal;

	    // Aplica descuento si el subtotal supera los 2000
	    double descuento = 0;
	    if (subtotal > 2000) {
	        descuento = 20;
	    }

	    // Actualiza los valores en la sesión
	    model.addAttribute("subtotal", subtotal);
	    model.addAttribute("costoEnvio", costoEnvio);
	    model.addAttribute("descuento", descuento);

	    // Calcula el total (subtotal - descuento + costo de envío)
	    double total = subtotal - descuento + costoEnvio;
	    model.addAttribute("total", total);

	    return "redirect:/carrito"; // Redirige a la vista del carrito después de actualizar los valores
	}


	// Método para eliminar un producto del carrito
	@GetMapping("/eliminar/{idProducto}")
	public String eliminarProducto(Model model, @PathVariable(name = "idProducto", required = true) int idProducto) {
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double subtotal = 0.0;
	    boolean encontrado = false;

	    // Busca el producto en el carrito y lo elimína
	    for (Detalle detalle : carrito) {
	        if (detalle.getProducto().getIdProducto() == idProducto) {
	            carrito.remove(detalle);
	            encontrado = true;
	            break;
	        }
	    }

	    // Recalcula el subtotal
	    for (Detalle d : carrito) subtotal += d.getSubtotal();

	    // Calcula el costo de envío (5% del subtotal)
	    double costoEnvio = 0;

	    // Aplicar descuento si el subtotal supera los 2000
	    double descuento = 0;
	    if (subtotal > 2000) {
	        descuento = 0;
	    }

	    // Calcula el total (subtotal - descuento + costo de envío)
	    double total = subtotal - descuento + costoEnvio;

	    // Guardar los valores actualizados en la sesión y pasarlos a la vista
	    model.addAttribute("subtotal", subtotal);
	    model.addAttribute("total", total);
	    model.addAttribute("descuento", descuento);
	    model.addAttribute("carrito", carrito);
	    model.addAttribute("costoEnvio", costoEnvio);

	    return "redirect:/carrito";
	}

	
	// Método para visualizar el carrito de compras
	@GetMapping("/carrito")
	public String carrito() {
		return "carrito";
	}
	
	
	// Método para logueo
	@Controller
	public class LoginController {

	    @Autowired
	    private IUserRepository usuarioRepository;
	    
	    @Autowired
	    private UsuarioService usuarioService;

	    @GetMapping("/login")
	    public String login() {
	        return "login";
	    }
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @PostMapping("/login")
	    public String loginUser(@RequestParam("dni") int dni, @RequestParam("contrase") String contrase, HttpSession session, HttpServletResponse response, Model model) {
	        Usuario usuario = usuarioRepository.findByDni(dni);
	        if (usuario != null && passwordEncoder.matches(contrase, usuario.getContrase())) {
	            session.setAttribute("usuario", usuario);
	            
	            // Crear una cookie para la sesión del usuario
	            Cookie cookie = new Cookie("userSession", String.valueOf(usuario.getIdUsuario()));
	            cookie.setMaxAge(60 * 60 * 24); // La cookie durará 1 día
	            cookie.setHttpOnly(true); // Hacer la cookie accesible solo para HTTP (más seguro)
	            cookie.setPath("/"); // La cookie estará disponible en toda la aplicación
	            response.addCookie(cookie);

	            return "redirect:/index";
	        } else {
	            model.addAttribute("error", "DNI o contraseña incorrectos");
	            return "login";
	        }
	    }

	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate();
	        return "redirect:/login";
	    }
	}
	
	// Método para registrar usuario
	
	@Controller
	public class UsuarioController {

	    @Autowired
	    private IUserRepository usuarioRepository;

	  
	    
	    @InitBinder
	    public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	    }
	    public String registrar(Model model) {
	        model.addAttribute("usuario", new Usuario());
	        return "registrar";
	    }
	    
	    @RequestMapping("/registrar")
	    public String showRegistrationForm(Model model) {
	        model.addAttribute("usuario", new Usuario());
	        return "registrar";
	    }
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @PostMapping("/registrar")
	    public String registrarUsuario(@ModelAttribute Usuario usuario) {
	        usuario.setContrase(passwordEncoder.encode(usuario.getContrase()));
	        usuarioRepository.save(usuario);
	        //guardas las cookies
	        //redireccionas a la pagina principal index
	        return "redirect:/login";
	    }
	}
	
	// Método para realizar el pago y registrar la venta
	@GetMapping("/pagar")
	public String realizarPago(Model model) {
	    List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");
	    double total = (double) model.getAttribute("total");

	    // Calcular descuento y costo de envío
	    calcularDescuentoYCostoEnvio(model);

	    // Crea una nueva venta con la fecha actual
	    Venta nuevaVenta = new Venta();
	    nuevaVenta.setMontoTotal(total);
	    nuevaVenta.setFechaRegistro(new Date(0)); // Establece la fecha actual

	    // Registra la venta en la base de datos utilizando el repositorio de ventas
	    ventaRepository.save(nuevaVenta);

	    // Limpia el carrito después de realizar la venta
	    carrito.clear();

	    // Actualiza los valores en la sesión
	    model.addAttribute("carrito", carrito);
	    model.addAttribute("total", 0.0); // Establece el total en cero

	    return "redirect:/index"; // Redirige al índice después de realizar el pago
	}
	
	


	
}
