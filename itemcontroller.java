import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired private ItemRepository itemRepo;
    @Autowired private UserRepository userRepo;

    @PostMapping("/report")
    public ResponseEntity<?> reportItem(@RequestParam String name,
                                        @RequestParam String description,
                                        @RequestParam String location,
                                        @RequestParam String contact,
                                        @RequestParam(required=false) Double rewardAmount,
                                        @RequestParam MultipartFile image,
                                        @RequestHeader("Authorization") String auth) throws IOException {

        User owner = JwtUtil.getUserFromToken(auth, userRepo);
        if(owner == null) return ResponseEntity.status(401).body("Invalid token");

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setLocation(location);
        item.setContact(contact);
        item.setRewardAmount(rewardAmount);
        item.setOwner(owner);

        // Save item first to get ID
        item = itemRepo.save(item);

        // save image
        try {
            String imgPath = "uploads/" + UUID.randomUUID() + "-" + image.getOriginalFilename();
            Files.createDirectories(Paths.get("uploads"));
            Files.copy(image.getInputStream(), Paths.get(imgPath));
            item.setImagePath(imgPath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image");
        }

        // generate QR with itemId
        try {
            String qrPath = "qrcodes/" + UUID.randomUUID() + ".png";
            Files.createDirectories(Paths.get("qrcodes"));
            QRUtil.generateQRCode("ItemID:" + item.getId(), qrPath);
            item.setQrCodePath(qrPath);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate QR code");
        }

        itemRepo.save(item);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<?> getItems(@RequestHeader("Authorization") String auth){
        User owner = JwtUtil.getUserFromToken(auth, userRepo);
        if(owner == null) return ResponseEntity.status(401).body("Invalid token");
        return ResponseEntity.ok(itemRepo.findByOwner(owner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id){
        return itemRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/recover/{id}")
    public ResponseEntity<?> recoverItem(@PathVariable Long id){
        Item item = itemRepo.findById(id).orElse(null);
        if(item == null) return ResponseEntity.notFound().build();
        
        item.setStatus("RECOVERED");
        itemRepo.save(item);
        
        try {
            ZipUtil.createZip(item);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Item recovered and ZIP generated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate ZIP");
        }
    }
}
