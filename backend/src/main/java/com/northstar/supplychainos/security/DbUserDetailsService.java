package com.northstar.supplychainos.security;

import com.northstar.supplychainos.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public DbUserDetailsService(UserRepository users) { this.users = users; }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = users.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    if (!user.isActive()) throw new UsernameNotFoundException("User disabled: " + email);
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPasswordHash(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
  }
}
