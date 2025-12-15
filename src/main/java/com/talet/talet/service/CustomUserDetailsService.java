package com.talet.talet.service;

import com.talet.talet.entity.Member;
import com.talet.talet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Member member = null;

        if (memberRepository.existsByIdentifier(identifier)) {
            member = memberRepository.findByIdentifier(identifier);
        }

        if (member == null) {
            throw new UsernameNotFoundException("User not found: " + identifier);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + member.getRole());

        return new User(
                member.getIdentifier(),
                "",
                Collections.singleton(authority)
        );
    }

}
