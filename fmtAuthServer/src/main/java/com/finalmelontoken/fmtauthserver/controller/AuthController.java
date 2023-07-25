package com.finalmelontoken.fmtauthserver.controller;

import com.finalmelontoken.fmtauthserver.domain.AuthKey;
import com.finalmelontoken.fmtauthserver.domain.Mail;
import com.finalmelontoken.fmtauthserver.domain.User;
import com.finalmelontoken.fmtauthserver.domain.req.JoinRequest;
import com.finalmelontoken.fmtauthserver.domain.res.ResponseDto;
import com.finalmelontoken.fmtauthserver.service.AuthKeyService;
import com.finalmelontoken.fmtauthserver.service.MailService;
import com.finalmelontoken.fmtauthserver.service.UserService;
import com.finalmelontoken.fmtauthserver.util.MailUtil;
import com.finalmelontoken.fmtauthserver.util.RandomStringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final MailService mailService;
    private final AuthKeyService authKeyService;
    private final RandomStringUtil randomStringUtil;
    private final MailUtil mailUtil;

    // TODO: login구현
//    @PostMapping("login")
//    public ResponseEntity<?> loginPage() {
//        System.out.println("asdads");
//        return new ResponseEntity("localhost:8080/oauth2/authorization/google", HttpStatus.OK);
//    }

    @GetMapping("auth-result")
    public String success(Authentication auth, Model model) {
        if (auth == null) {
            return "join/fail";
        }
        User user = userService.getUserByLoginId(auth.getName());
        if (user == null) {
            return "join/fail";
        }
        String key = randomStringUtil.generateRandomString(6);
        AuthKey authKey = AuthKey.builder()
                .authKey(key)
                .createdTime(Instant.now())
                .email(user.getEmail())
                .build();

        if (mailUtil.isExpiredMail(authKey.getCreatedTime()))
            authKeyService.deleteKey(user.getEmail());

        if (!authKeyService.isExistKey(user.getEmail())) {
            authKeyService.saveKey(authKey);
            mailService.sendSimpleMessage(Mail.builder()
                    .id(1L)
                    .email(user.getEmail())
                    .name("정말 대소고 학생이세요??? - 이메일 인증 코드입니당")
                    .message(String.format("%s님 안녕하세요\n 인증 코드: %s", user.getNickname(), key))
                    .build());
            model.addAttribute("msg", "이메일을 전송했어요!");
        } else {
            model.addAttribute("msg", "이미 전송을 완료했어요");
        }

        model.addAttribute("req", user);

        return "join/success";
    }

    @PostMapping("/join")
    public ResponseEntity<?> signUp(@RequestBody JoinRequest joinRequest) {
        return new ResponseEntity<>(
                ResponseDto.builder()
                        .status(200)
                        .data(userService.register(joinRequest))
                        .build()
                , HttpStatus.OK
        );
    }
//
//    @PostMapping("/in")
//    public ResponseEntity<?> signIn(@RequestBody User user) {
//        return new ResponseEntity<>(
//                ResponseDto.builder()
//                        .status(200)
//                        .data(userService.login(user))
//                        .build()
//                , HttpStatus.OK
//        );
//    }


}
