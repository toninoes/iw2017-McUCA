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

import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.Rol;
import mcuca.usuario.Usuario;
import mcuca.usuario.UsuarioService;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UsuarioService service) {
		return (args) -> {

			if (service.findAll().size() == 0) {
				// Guardar algunos usuarios de prueba: user: manu; pass:12
				service.save(new Usuario("12", "Manuel Jesús", "López Jiménez", "manu", Rol.GERENTE));
				service.save(new Usuario("34", "Antonio", "Ruiz Rondán", "toni", Rol.ENCARGADO));
				service.save(new Usuario("56", "Andrés", "Martínez Gavira", "andres", Rol.CAMARERO));
				service.save(new Usuario("78", "Luis Fernando", "Pérez Peregrino", "luisfe", Rol.CAMARERO));

				//Usuario root = new Usuario("root", "root");
				//root.setPassword("root");
				//service.save(root);

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
