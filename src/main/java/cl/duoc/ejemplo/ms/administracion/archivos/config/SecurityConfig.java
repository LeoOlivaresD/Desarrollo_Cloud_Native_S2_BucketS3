package cl.duoc.ejemplo.ms.administracion.archivos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
   public SecurityConfig() {
   }
   //Aca decimos que cualquier llamada http que llegue debe autorizarse y autenticarse y para ello usaremos el oauth2 con un servidor de recursos el cual validare un jtw que llegara ( en nuestro caso azure)
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).authorizeHttpRequests((authorize) -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorize.anyRequest()).authenticated();
        }).oauth2ResourceServer((oauth2) -> {
            oauth2.jwt(Customizer.withDefaults());
        });
        return (SecurityFilterChain)http.build();
    }
}