package com.finalmelontoken.fmtauthserver.service;

import com.finalmelontoken.fmtauthserver.domain.AuthKey;
import com.finalmelontoken.fmtauthserver.repository.AuthKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthKeyService {
    private final AuthKeyRepository authKeyRepository;

    public void saveKey(AuthKey authKey) {
        authKeyRepository.save(authKey);
    }

    public boolean isExistKey(String email) {
        return authKeyRepository.existsAuthKeyByEmail(email);
    }

    public void deleteKey(String email) {
        authKeyRepository.deleteAuthKeyByEmail(email);
    }
}