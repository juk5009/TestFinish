package shop.mtcoding.blog.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.model.UserRepository;
import shop.mtcoding.blog.util.Script;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    private final HttpSession session;

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/loginForm";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(String username, String password) {
        User principal = userRepository.findByUsernameAndPassword(username, password);
        if (principal == null) {
            return Script.back("로그인 실패");
        }

        session.setAttribute("principal", principal);
        return Script.href("/board/list");
    }

    @PostMapping("/join")
    @ResponseBody
    public String join(String username, String password, String email) {
        int result = userRepository.insert(username, password, email);
        if (result != 1) {
            return Script.back("회원가입실패");
        }
        return Script.href("회원가입완료", "/loginForom");

    }

    @GetMapping({ "/", "/loginForm" })
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm(Model model) {
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            return "redirect:/loginForm";
        }

        User user = userRepository.findById(principal.getId());
        model.addAttribute("user", user);
        return "user/updateForm";
    }

    @PostMapping("/user/{id}/update")
    @ResponseBody
    public String update(@PathVariable int id, String password, String email) {
        // 1. 유효성 검사
        if (password == null || password.isEmpty()) {
            return Script.back("password가 입력되지 않았습니다.");
        }
        if (email == null || email.isEmpty()) {
            return Script.back("email이 입력되지 않았습니다.");
        }

        // 2. 인증 체크
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            return Script.back("로그인을 먼저 하세요.");
        }

        // 3. 권한체크
        if (principal.getId() != id) {
            return Script.back("수정 권한이 없습니다.");
        }

        // 4. 회원정보 수정
        int result = userRepository.updateById(id, password, email);
        if (result != 1) {
            return Script.back("회원정보수정 실패");
        }

        // 5. 세션 동기화
        User user = userRepository.findById(id);
        session.setAttribute("principal", user);
        return Script.href("회원정보수정 완료", "/user/updateForm");
    }
}
