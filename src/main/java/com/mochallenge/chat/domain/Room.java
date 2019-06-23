package com.mochallenge.chat.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.GenericGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(nullable = false, updatable = false, unique = true)
    private String roomId;

    @Column(nullable = false, unique = true)
    private String roomName;

    @Column
    private String creatorId;

    @Column
    private LocalDate createdAt;

    @ElementCollection
    @CollectionTable(name = "users_to_rooms", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "user_id")
    private List<String> users;

    public Room() {
        this.createdAt = LocalDate.now();
    }

}
