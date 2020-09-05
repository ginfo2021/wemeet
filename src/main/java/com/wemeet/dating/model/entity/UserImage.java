package com.wemeet.dating.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "imageUrl"})
)
@Entity
public class UserImage {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        @Column(nullable = false)
        private String imageUrl;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
        private User user;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        public UserImage() {

        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public UserImage(String imageUrl, User user) {
                this.imageUrl = imageUrl;
                this.user = user;
        }



        @PrePersist
        private void storeCreatedDate() {
                this.setCreatedAt(LocalDateTime.now());
        }


}
