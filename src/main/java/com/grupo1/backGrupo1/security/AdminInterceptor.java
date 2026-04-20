package com.grupo1.backGrupo1.security;

import com.grupo1.backGrupo1.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod hm = (HandlerMethod) handler;
        AdminOnly adminOnly = hm.getMethodAnnotation(AdminOnly.class);
        if (adminOnly == null) {
            return true;
        }

        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isBlank()) {
            userIdStr = request.getParameter("userId");
        }

        if (userIdStr == null || userIdStr.isBlank()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operação reservada a administradores: userId não fornecido");
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "userId inválido");
            return false;
        }

        try {
            com.grupo1.backGrupo1.model.User user = userService.findById(userId);
            if (!"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Apenas administradores podem executar esta ação");
                return false;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usuário não encontrado");
            return false;
        }

        return true;
    }
}
