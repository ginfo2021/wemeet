package com.wemeet.dating.model.entity;

import com.wemeet.dating.model.enums.DeleteType;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DeletedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    private DeleteType deleteType;

    private LocalDateTime deletedOn;

    public DeletedUser() {
    }

    public DeletedUser(User user, String userEmail, DeleteType deleteType, LocalDateTime deletedOn) {
        this.user = user;
        this.userEmail = userEmail;
        this.deleteType = deleteType;
        this.deletedOn = deletedOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public DeleteType getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(DeleteType deleteType) {
        this.deleteType = deleteType;
    }

    public LocalDateTime getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(LocalDateTime deletedOn) {
        this.deletedOn = deletedOn;
    }
}
