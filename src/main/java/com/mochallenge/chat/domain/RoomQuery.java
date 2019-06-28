package com.mochallenge.chat.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "room_queries")
@Table(name = "rooms")
@Immutable
public class RoomQuery {
    @Id
    private String roomId;

    @Column(nullable = false, unique = true)
    private String roomName;

    @Column
    private String creatorId;

    @Column
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="users_to_rooms",
            joinColumns = @JoinColumn(name="room_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private List<User> users;

}


