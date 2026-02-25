package com.bugboard26.coreservice.config;

import com.bugboard26.coreservice.constants.CoreConstants;
import com.bugboard26.coreservice.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor JWT.
 * Intercetta ogni richiesta in ingresso, valida il Bearer token e
 * inietta email e ruolo dell'utente come attributi della richiesta.
 * In assenza di un token valido risponde con 401 e blocca la catena.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor
{
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String token = estraiToken(request);

        if (token == null || !jwtService.tokenValido(token))
        {
            log.warn("Errore: Token assente o non valido [{}]", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT assente o non valido");
            return false;
        }

        request.setAttribute(CoreConstants.USER_EMAIL_ATTR, jwtService.estraiUsername(token));
        request.setAttribute(CoreConstants.USER_RUOLO_ATTR, jwtService.estraiRuolo(token));
        return true;
    }

    private String estraiToken(HttpServletRequest request)
    {
        String header = request.getHeader(CoreConstants.AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(CoreConstants.BEARER_PREFIX)) return null;
        return header.substring(CoreConstants.BEARER_PREFIX.length());
    }
}
