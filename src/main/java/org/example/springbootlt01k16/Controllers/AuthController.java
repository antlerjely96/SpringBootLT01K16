package org.example.springbootlt01k16.Controllers;

import org.example.springbootlt01k16.Repositories.AdminRepository;
import org.example.springbootlt01k16.Utils.JwtUtil;
import org.example.springbootlt01k16.entities.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> accountRegister){
        //Lấy email, pwd của admin đang đăng ký
        String email = accountRegister.get("email");
        String password = accountRegister.get("password");

        if(adminRepository.findByEmail(email).isPresent()){
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        Admin newAdmin = new Admin();
        newAdmin.setEmail(email);
        newAdmin.setPassword(passwordEncoder.encode(password));
        adminRepository.save(newAdmin);
        return ResponseEntity.ok("Đăng ký thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> account){
        //Lấy email, pwd của admin đang đăng nhập
        String email = account.get("email");
        String password = account.get("password");

        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        //Kiểm tra tồn tại không
        if(optionalAdmin.isPresent() && passwordEncoder.matches(password, optionalAdmin.get().getPassword())){
            String token = jwtUtil.generateToken(email);

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "token", token
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản sai");
    }

    @GetMapping("/personal")
    public ResponseEntity<?> getCurrentAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader){
        //Kiểm tra header authorization tồn tại không
        if(authHeader == null || authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Lỗi token");
        }

        String token = authHeader.substring(7);

        //Xác thực token
        if(jwtUtil.validateToken(token)){
            String email = jwtUtil.extractEmail(token);
            return ResponseEntity.ok("Xác thực thành công");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất bại");
    }
}
