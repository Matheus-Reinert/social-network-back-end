package br.com.socialNetwork.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "comments")
public class Comment implements Reaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "NUMERIC(19,0)")
    private Long id;
    @Column(name = "comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "commentParent_id")
    private Comment commentParent;
    @Column(name = "dateTime")
    private LocalDateTime dateTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(name = "likes")
    private double likes;

    @PrePersist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }

    @Override
    public void like() {
        this.likes += 1;
    }
}
