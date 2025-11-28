package com.project.eume.config.security;

import com.project.eume.config.security.oauth2.AdminPrincipal;
import com.project.eume.domain.entity.EumeAdmin;
import com.project.eume.domain.service.AdminSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminDetailsServiceImpl implements UserDetailsService {
	private final AdminSearchService adminSearchService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EumeAdmin user = adminSearchService.findByAdminLoginId(username);
		return new AdminPrincipal(user);
	}
}
