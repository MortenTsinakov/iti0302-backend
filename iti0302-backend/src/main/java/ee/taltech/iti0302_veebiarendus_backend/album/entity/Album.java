package ee.taltech.iti0302_veebiarendus_backend.album.entity;

import ee.taltech.iti0302_veebiarendus_backend.review.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Table(name = "albums")
@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String artist;
    @Column(name = "image_url")
    private String imageUrl;
    @OneToMany(mappedBy = "album")
    private List<Track> trackList;
    @OneToMany(mappedBy = "album")
    private List<Review> reviews;
}
