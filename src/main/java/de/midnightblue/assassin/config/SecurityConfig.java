package de.midnightblue.assassin.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

  private final MockUsersService mockUsersService;

  public SecurityConfig(MockUsersService mockUsersService) {
    this.mockUsersService = mockUsersService;
  }

  @Bean
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.httpBasic(Customizer.withDefaults());
    http.authorizeHttpRequests(
        m -> m.requestMatchers(r -> r.getMethod().equals("OPTIONS")).permitAll());
    http.authorizeHttpRequests(m -> m.anyRequest().authenticated());
    return http.build();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("POST", "DELETE", "GET", "PUT", "OPTIONS");
  }

  @Bean
  public UserDetailsService users() {
    List<UserDetails> userDetails = new ArrayList<>();

    for (String name : mockUsersService.getUserNames()) {
      UserDetails user =
          User.builder()
              .username(name)
              .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
              .roles("USER")
              .build();
      userDetails.add(user);
    }

    return new InMemoryUserDetailsManager(userDetails);
  }
}
