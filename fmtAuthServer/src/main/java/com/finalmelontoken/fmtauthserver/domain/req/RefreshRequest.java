package com.finalmelontoken.fmtauthserver.domain.req;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RefreshRequest {
    private String clientKey;
}
