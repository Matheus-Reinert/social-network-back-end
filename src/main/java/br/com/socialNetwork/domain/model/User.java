package br.com.socialNetwork.domain.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String lastName;
    @Column
    private String subtitle;
    @Column
    private String aboutMe;
    @Column
    private String email;
    @Column
    private String password;

}
