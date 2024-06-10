package org.hse.software.construction.tickets.config;

import org.hse.software.construction.tickets.model.station.Station;
import org.hse.software.construction.tickets.model.user.User;
import org.hse.software.construction.tickets.model.station.StationRepository;
import org.hse.software.construction.tickets.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;

    public DataInitializer(StationRepository stationRepository, UserRepository userRepository) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) {
        stationRepository.save(new Station(null, "Central Station"));
        stationRepository.save(new Station(null, "North Station"));
        stationRepository.save(new Station(null, "East Station"));
        stationRepository.save(new Station(null, "West Station"));
        stationRepository.save(new Station(null, "South Station"));

        userRepository.save(new User(1, "user", "user@example.com", "Password123!"));
        userRepository.save(new User(2, "jane_smith", "jane.smith@example.com", "Securepass456!"));
        userRepository.save(new User(3, "mike_jones", "mike.jones@example.com", "Mysecret789!"));
        userRepository.save(new User(4, "alice_williams", "alice.williams@example.com", "Passalice001!"));
        userRepository.save(new User(5, "bob_brown", "bob.brown@example.com", "Bobpass002!"));
        userRepository.save(new User(6, "john_doe", "john.doe@example.com", "Password123!"));
    }
}
