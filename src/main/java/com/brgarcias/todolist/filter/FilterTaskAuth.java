package com.brgarcias.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brgarcias.todolist.user.IUserRepository;
import com.brgarcias.todolist.user.UserModel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    private static final String BASIC_PREFIX = "Basic ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith(BASIC_PREFIX)) {
            try {
                String base64Credentials = authorization.substring(BASIC_PREFIX.length()).trim();
                byte[] authDecoded = Base64.getDecoder().decode(base64Credentials);
                String authString = new String(authDecoded);
                String[] credentials = authString.split(":");

                if (credentials != null && credentials.length == 2) {
                    String username = credentials[0];
                    String password = credentials[1];

                    UserModel user = this.userRepository.findByUsername(username);

                    if (user == null) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    } else {
                        Result passwordVerificationResult = BCrypt.verifyer().verify(password.toCharArray(),
                                user.getPassword());

                        if (passwordVerificationResult.verified) {
                            filterChain.doFilter(request, response);
                        } else {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                    }
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization Header");
            }
        }
        filterChain.doFilter(request, response);
    }

}
