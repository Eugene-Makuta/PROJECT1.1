import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body){
        if(userRepo.findByEmail(body.get("email")).isPresent())
            return ResponseEntity.badRequest().body("Email exists");
        
        User u = new User(body.get("name"), body.get("email"), 
                          encoder.encode(body.get("password")), 
                          body.getOrDefault("role", "ROLE_USER"));
        userRepo.save(u);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        User u = userRepo.findByEmail(body.get("email")).orElse(null);
        
        if(u==null || !encoder.matches(body.get("password"), u.getPassword()))
            return ResponseEntity.status(401).body("Invalid credentials");
        
        String token = JwtUtil.generateToken(u);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", u.getName());
        return ResponseEntity.ok(response);
    }
}
