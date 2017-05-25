package mcuca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;

import mcuca.establecimiento.Establecimiento;
import mcuca.establecimiento.EstablecimientoRepository;
import mcuca.mesa.Mesa;
import mcuca.mesa.MesaRepository;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.Rol;
import mcuca.usuario.Usuario;
import mcuca.usuario.UsuarioService;
import mcuca.zona.Zona;
import mcuca.zona.ZonaRepository;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
public class Application extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private EstablecimientoRepository repoEst;
	
	@Autowired
	private ZonaRepository repoZona;
	
	@Autowired
	private MesaRepository repoMesa;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UsuarioService service) {
		return (args) -> {

			if (service.findAll().size() == 0) {
				//Establecimientos de prueba
				Establecimiento benalup = new Establecimiento("McUCA - Benalup", "Avenida Bahía Blanca, s/n");
				Establecimiento vejer = new Establecimiento("McUCA - Vejer", "Plaza de España, s/n");
				
				repoEst.save(benalup);
				repoEst.save(vejer);
				
				// Guardar algunos usuarios de prueba:	
				Usuario manu = new Usuario("11111111", "Manuel Jesús", "López Jiménez", "manu", Rol.GERENTE);
				manu.setPassword("12");
				service.save(manu);
				
				Usuario toni = new Usuario("22222222", "Antonio", "Ruiz Rondán", "toni", Rol.ENCARGADO);
				toni.setPassword("34");
				toni.setEstablecimiento(benalup);
				service.save(toni);
				
				Usuario andres = new Usuario("33333333", "Andrés", "Martínez Gavira", "andres", Rol.CAMARERO);
				andres.setPassword("56");
				andres.setEstablecimiento(benalup);
				service.save(andres);
				
				Usuario luisfe = new Usuario("44444444", "Luis Fernando", "Pérez Peregrino", "luisfe", Rol.CAMARERO); 
				luisfe.setPassword("78");
				luisfe.setEstablecimiento(vejer);
				service.save(luisfe);

				//Zonas de prueba
				Zona z1b = new Zona("Terraza", 40); z1b.setEstablecimiento(benalup);
				Zona z2b = new Zona("Salón", 30); z2b.setEstablecimiento(benalup);
				
				Zona z1v = new Zona("Terraza", 20); z1v.setEstablecimiento(vejer);
				Zona z2v = new Zona("Sala de Fiestas", 40); z2v.setEstablecimiento(vejer);
				repoZona.save(z1b);
				repoZona.save(z2b);
				repoZona.save(z1v);
				repoZona.save(z2v);
				
				//Mesas
				Mesa m1b = new Mesa("1"); m1b.setZona(z1b);
				Mesa m2b = new Mesa("2"); m2b.setZona(z1b);
				Mesa m3b = new Mesa("3"); m3b.setZona(z2b);
				Mesa m4b = new Mesa("4"); m4b.setZona(z2b);
				
				Mesa mAv = new Mesa("A"); mAv.setZona(z1v);
				Mesa mBv = new Mesa("B"); mBv.setZona(z1v);
				Mesa mCv = new Mesa("C"); mCv.setZona(z2v);
				Mesa mDv = new Mesa("D"); mDv.setZona(z2v);
				
				repoMesa.save(m1b);
				repoMesa.save(m2b);
				repoMesa.save(m3b);
				repoMesa.save(m4b);
				
				repoMesa.save(mAv);
				repoMesa.save(mBv);
				repoMesa.save(mCv);
				repoMesa.save(mDv);
				

				// fetch all users
				log.info("Users found with findAll():");
				log.info("-------------------------------");
				for (Usuario user : service.findAll()) {
					log.info(user.toString());
				}
				log.info("");

				// fetch an individual user by ID
				Usuario user = service.findOne(1L);
				log.info("Usuario found with findOne(1L):");
				log.info("--------------------------------");
				log.info(user.toString());
				log.info("");

				// fetch users by last name
				log.info("Usuario found with findByLastNameStartsWithIgnoreCase('Bauer'):");
				log.info("--------------------------------------------");
				for (Usuario bauer : service.findByApellidosStartsWithIgnoreCase("Bauer")) {
					log.info(bauer.toString());
				}
				log.info("");
			}
		};
	}

	@Configuration
	@EnableGlobalMethodSecurity(securedEnabled = true)
	public static class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

		@Autowired
		private UserDetailsService userDetailsService;

		@Bean
		public PasswordEncoder encoder() {
			return new BCryptPasswordEncoder(11);
		}

		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
			authProvider.setUserDetailsService(userDetailsService);
			authProvider.setPasswordEncoder(encoder());
			return authProvider;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {

			auth.authenticationProvider(authenticationProvider());

			// auth
			// .inMemoryAuthentication()
			// .withUser("admin").password("p").roles("ADMIN", "MANAGER",
			// "USER")
			// .and()
			// .withUser("manager").password("p").roles("MANAGER", "USER")
			// .and()
			// .withUser("user").password("p").roles("USER");

		}

		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return authenticationManager();
		}

		static {
			// Use a custom SecurityContextHolderStrategy
			SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
		}
	}
}
