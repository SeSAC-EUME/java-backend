package com.project.eume.config.security;

import com.project.eume.config.security.oauth2.UserPrincipal;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.service.EumeUserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final EumeUserSearchService eumeUserSearchService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EumeUser user = eumeUserSearchService.findByEmail(username);
		return new UserPrincipal(user);
	}
}
