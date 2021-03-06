package com.revengemission.sso.oauth2.server.controller;

import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SignInAndUpController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/signIn")
    public String signIn() {
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(HttpServletRequest request,
                         @RequestParam(value = "error", required = false) String error,
                         Model model) {
        if (StringUtils.isNotEmpty(error)) {
            model.addAttribute("error", error);
        }
        return "signUp";
    }

    @PostMapping("/signUp")
    public String handleSignUp(HttpServletRequest request,
                               Model model,
                               @RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "confirmPassword") String confirmPassword) {

        if (StringUtils.isAnyEmpty(username, password, confirmPassword) || !StringUtils.equals(password, confirmPassword)) {
            return "redirect:/signUp?error=parameters";
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setRole(RoleEnum.ROLE_USER.name());
        userAccount.setUsername(username);
        userAccount.setPassword(passwordEncoder.encode(password));
        try {
            userAccount = userAccountService.create(userAccount);
        } catch (AlreadyExistsException e) {
            if (log.isErrorEnabled()) {
                log.error("create user exception", e);
            }
            return "redirect:/signUp?error=Already exists";
        }
        return "redirect:/?success=signUp";
    }
}
