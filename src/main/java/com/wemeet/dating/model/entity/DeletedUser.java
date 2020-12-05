package com.wemeet.dating.model.entity;

import com.wemeet.dating.model.enums.DeleteType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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

}
