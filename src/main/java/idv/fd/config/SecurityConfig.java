package idv.fd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.error.AppError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public UnauthEntryPoint unauthEntryPoint() {

        return new UnauthEntryPoint(objectMapper);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthEntryPoint())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    public static class UnauthEntryPoint implements AuthenticationEntryPoint {

        private static final Logger log = LoggerFactory.getLogger(UnauthEntryPoint.class);

        private ObjectMapper objectMapper;

        public UnauthEntryPoint(ObjectMapper objectMapper) {

            this.objectMapper = objectMapper;
        }

        @Override
        public void commence(
                HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
                throws IOException, ServletException {

            log.error(e.getMessage(), e);
            HttpStatus status = HttpStatus.UNAUTHORIZED;

            AppError body = AppError.builder()
                    .status(status.value())
                    .msg(status.getReasonPhrase())
                    .build();

            String json = objectMapper.writeValueAsString(body);

            PrintWriter out = response.getWriter();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(status.value());

            out.print(json);
            out.flush();
        }
    }
}
