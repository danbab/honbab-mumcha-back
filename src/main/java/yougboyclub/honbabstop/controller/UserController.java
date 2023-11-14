package yougboyclub.honbabstop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yougboyclub.honbabstop.domain.User;
import yougboyclub.honbabstop.domain.UserInfo;
import yougboyclub.honbabstop.dto.RequestEmailCodeVerificationDto;
import yougboyclub.honbabstop.dto.RequestUserDto;
import yougboyclub.honbabstop.dto.RequestUserEmailDto;
import yougboyclub.honbabstop.service.UserService;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    //이메일 인증코드 발송
    //BindingResult: 스프링 프레임워크에서 사용되는 유효성 검사(validation) 결과를 수신하고 오류 메시지를 처리하는 인터페이스
    @PostMapping("/emails/authenticationRequest")
    public ResponseEntity<String> sendMessage(@RequestBody @Validated RequestUserEmailDto toEmailDto, BindingResult bindingResult) {

        //입력받은 이메일이 빈칸이 있거나 이메일형식이 아닐 시, 에러 반환.
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 형식이 맞지 않습니다.");
        }

        String toEmail = toEmailDto.getEmail();
        userService.sendCodeToEmail(toEmail);

        return ResponseEntity.status(HttpStatus.OK).body("인증코드가 발송되었습니다.");
    }

    //인증코드 인증 확인
    @PostMapping("/emails/codeChecked")
    public ResponseEntity<String> verificationEmailCode(@RequestBody RequestEmailCodeVerificationDto verificationDto) {
        userService.verifiedCode(verificationDto.getEmail(), verificationDto.getAuthCode());

        return ResponseEntity.status(HttpStatus.OK).body("인증이 완료되었습니다."); //200
    }

    //회원가입
    @PostMapping("/new")
    public ResponseEntity<String> join(@RequestBody @Validated RequestUserDto dto, BindingResult bindingResult) {
        System.out.println("dto = " + dto);
        //입력받은 정보 중 필수정보나 형식에 맞지 않을 시, 에러 반환.
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입에 필요한 정보가 부족하거나 형식이 틀렸습니다. 다시 확인해주세요.");
        }
        userService.save(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공하였습니다.");
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user, HttpSession session) {
        System.out.println("나는 실행되었도다" + user.getEmail() + user.getPassword());
        System.out.println("일단 세션 확인부터" + session);
        try {
            User loginUser = userService.findByEmail(user.getEmail()); //로그인 창에서 입력한 이메일로 db에 저장된 회원 정보를 가져옴.
            System.out.println("loginUser:: " + loginUser);
            //입력받은 비밀번호와 데이터베이스에서 조회한 유저의 암호화된 비밀번호를 비교
            //passwordEncoder.matches(): 입력받은 비밀번호를 암호화하여 해시화한 값과 DB에서 조회한 유저의 암호화된 비밀번호를 비교
            if (loginUser != null && passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
                // 로그인 성공
                UserInfo userInfo = new UserInfo(loginUser.getName(), loginUser.getEmail());
                return ResponseEntity.ok(userInfo);
            } else {
                // 사용자가 존재하지 않거나 비밀번호가 일치하지 않음
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            // 예외 발생 시
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println("세션에 담긴 유저" + user);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //email로 회원정보 가져오기
    @GetMapping
    public ResponseEntity<User> findUser(@RequestParam String email) {
        User findUser = userService.findByEmail(email);
        return ResponseEntity.ok(findUser);
    }
}