package com.example.op3.services;

import com.example.op3.models.Role;
import com.example.op3.models.User;
import com.example.op3.repositories.RoleRepository;
import com.example.op3.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void add(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresentOrElse(x -> {
        }, () -> {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            roleRepository.findByName("ROLE_USER").ifPresentOrElse(x -> {
                    },
                    () -> roleRepository.save(new Role("USER"))
            );
            user.setRoles(Collections.singletonList(new Role("USER", 1L)));
            userRepository.save(user);
        });
    }

    public Optional<User> get(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles())))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found" + username));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

}
