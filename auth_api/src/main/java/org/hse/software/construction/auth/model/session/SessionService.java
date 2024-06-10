package org.hse.software.construction.auth.model.session;

import org.hse.software.construction.auth.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void createSession(User user, String token, Date expires) {
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setExpires(expires);
        sessionRepository.save(session);
    }

}
