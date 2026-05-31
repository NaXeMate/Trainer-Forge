package dev.trainerforge.security.core;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.repository.TrainerRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final TrainerRepository trainerRepo;

    public UserDetailsServiceImpl(TrainerRepository trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Trainer trainer = trainerRepo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Trainer not found with username: " + username));

        return new User(
                trainer.getUsername(),
                trainer.getPasswordHash(),
                Collections.emptyList()
        );
    }

}
