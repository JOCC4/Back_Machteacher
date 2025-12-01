package MachTeacher.MachTeacher.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserIdHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest sreq) {
            String userId = sreq.getServletRequest().getParameter("userId");
            if (userId != null && !userId.isBlank()) {
                return () -> userId;
            }
        }
        
        return () -> java.util.UUID.randomUUID().toString();
    }
}
