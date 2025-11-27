package com.project.eume.config.security.oauth2;

import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.enums.LoginPlatform;
import com.project.eume.domain.service.EumeUserRegisterService;
import com.project.eume.domain.service.EumeUserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final EumeUserSearchService eumeUserSearchService;
    private final EumeUserRegisterService eumeUserRegisterService;

    @Override
    @Transactional
    public UserPrincipal loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 로그인 진행 시 키가 되는 필드 값 (PK)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 서비스 구분 코드 ex) naver, kakao
        String providerCode = userRequest.getClientRegistration().getRegistrationId();

        // 소셜 쪽에서 받은 값들을 Map 형태로 받음
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name = UUID.randomUUID().toString().substring(10); // 임시 이름
        LoginPlatform loginPlatform;

        switch (providerCode) {
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = (String) kakaoAccount.get("email");
                loginPlatform = LoginPlatform.KAKAO;
            }
            case "google" -> {
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                loginPlatform = LoginPlatform.GOOGLE;
            }
            case "naver" -> {
                Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");
                email = (String) naverAccount.get("email");
                name = (String) naverAccount.get("name");
                loginPlatform = LoginPlatform.NAVER;
            }
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인 플랫폼입니다");
        }
        EumeUser user = findOrSave(email, name, loginPlatform);

        return new UserPrincipal(user, attributes, userNameAttributeName);
    }

    private EumeUser findOrSave(String email, String name, LoginPlatform loginPlatform){
        return eumeUserSearchService.findByEmailOrOptional(email)
            .orElseGet(() -> eumeUserRegisterService.toEntityAndSave(email, name, loginPlatform));
    }
}
