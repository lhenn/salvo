package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gameplayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores;

    private Date gameDate;

    public Game() {
       this.gameDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setGame(this);
        gameplayers.add(gameplayer);
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return gameplayers.stream()
                .map(gameplayer -> gameplayer.getPlayer())
                .collect(Collectors.toList());
    }

    public Set<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    public Set<Score> getScores() { return scores;}

    public void setGameplayers(Set<GamePlayer> gameplayers) {
        this.gameplayers = gameplayers;
    }
}
