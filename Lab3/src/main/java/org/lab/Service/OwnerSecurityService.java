package org.lab.Service;

import org.lab.Model.User;
import org.lab.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class OwnerSecurityService {

    private final UserRepository userRepository;

    public OwnerSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long ownerId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getOwner() != null && user.getOwner().getId().equals(ownerId);
    }
}