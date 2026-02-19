import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired private ItemRepository itemRepo;

    @GetMapping("/all-items")
    public List<Item> getAllItems(){
        return itemRepo.findAll();
    }
}
