package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;

import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gameplayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores;

    public Player() { }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setPlayer(this);
        gameplayers.add(gameplayer);
    }

    public Set<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    public List<Game> getGames() {
        return gameplayers.stream()
                .map(gameplayer -> gameplayer.getGame())
                .collect(Collectors.toList());
    }


    public void setGameplayers(Set<GamePlayer> gameplayers) {
        this.gameplayers = gameplayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
