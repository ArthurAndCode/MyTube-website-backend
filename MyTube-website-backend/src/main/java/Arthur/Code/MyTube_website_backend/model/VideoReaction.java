package Arthur.Code.MyTube_website_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "videoReaction")
public class VideoReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private Boolean liked;

    @Column(nullable = false)
    private Boolean disliked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}

