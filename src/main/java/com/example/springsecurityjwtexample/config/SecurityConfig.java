package com.example.springsecurityjwtexample.config;

import com.example.springsecurityjwtexample.filter.JwtFilter;
import com.example.springsecurityjwtexample.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//securityConfig aktif edilir. Bu anatasyon, spring security config anlamına gelir.
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    //Spring kullanıcıyı veritabanından (veya başka bir kaynaktan almasını UserDetailsService interfacesine sahiptir.)
    //UserDetailsService nesnesi, kullanıcıyı veritabanından yüklemek için auth manager tarafından kullanılır.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                //İstekleri yetkilendirme yapılandırması
                .authorizeRequests()
                //"auth" servisine erişen herkese izin verir
                .antMatchers("/api/login").permitAll()
                //admin alanına erişmeye çalışıyorsanız yönetici olmalısınız(burada ayrıca kimlik doğrulaması gerekir)
                //antMatchers daki URL'kimliği doğrulanmış herhangi bir istemciye bağlar
                .anyRequest().authenticated()
                .and()
                //stateles sessionu kullandığımzdan emin olun. session kullanıcının durumunu tutmayacak
                //session yönetimini devre dışı bırakması için eklenir
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // ***
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //bu yapıyı eklememizin sebebi restController'da AuthticationManager nesnesini inject ederken hata alamız
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
