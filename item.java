import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="items")
@Getter @Setter
public class Item {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String location;
    private String contact;
    private String status = "LOST";
    private String imagePath;
    private String qrCodePath;
    private Double rewardAmount;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime reportedAt = LocalDateTime.now();

    @ManyToOne
    private User owner;

    public Item() {}
}
