package ru.freelib.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ru.freelib.converter.RoleAttributeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Convert(converter = RoleAttributeConverter.class)
    @Column(nullable = false, length = 20)
    private Role role;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("addedAt DESC")
    private List<UserBookFavorite> favoriteRecords;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Role { ROLE_READER, ROLE_AUTHOR, ROLE_ADMIN }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
