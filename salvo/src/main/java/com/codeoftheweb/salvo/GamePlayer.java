package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    Player player;

    @OneToMany(mappedBy="gameplayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gameplayer", fetch=FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    Date date;

    public GamePlayer() { }

    public GamePlayer(Game game, Player player) {
        this.date = new Date();
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addShip(Ship ship){
        this.ships.add(ship);
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    // search through all instances of Score. Return the instance where the game
    // and player match this gameplayer.
    public Score getScore() {
        return game.getScores()
                .stream()
                .filter(score -> score.getPlayer().getId() == player.getId())
                .findFirst()
                .orElse(null);
    }

}
