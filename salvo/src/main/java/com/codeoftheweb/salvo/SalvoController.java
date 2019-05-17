package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;



    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("Username already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @RequestMapping("/games")
    private Map<String, Object> gamesDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if(authentication != null){
            dto.put("player", playerDTO(getLoggedPlayer(authentication)));
        }
        dto.put("games", getGames());
        return dto;
    }

    @RequestMapping(path="/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if(authentication == null) {
            return new ResponseEntity<>(makeMap("error", "User is unauthorized."), HttpStatus.UNAUTHORIZED);
        } else {
            Player loggedPlayer = getLoggedPlayer(authentication);
            Game newGame = new Game();
            gameRepository.save(newGame);
            GamePlayer newGP = new GamePlayer(newGame, loggedPlayer);
            gamePlayerRepository.save(newGP);

            return new ResponseEntity<>(makeMap("gp",newGP.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path="/game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long id, Authentication authentication) {
        if(authentication == null) {
            return new ResponseEntity<>(makeMap("error", "User is unauthorized."), HttpStatus.UNAUTHORIZED);
        }
        Player loggedPlayer = getLoggedPlayer(authentication);
        Game game = gameRepository.getOne(id);
        if(game == null) {
            return new ResponseEntity<>(makeMap("error", "No such game exists."), HttpStatus.FORBIDDEN);
        }
        if(game.getGameplayers().size()>1){
            return new ResponseEntity<>(makeMap("error","Game has been filled."), HttpStatus.FORBIDDEN);
        }
        GamePlayer newGP = new GamePlayer(game, loggedPlayer);
        gamePlayerRepository.save(newGP);
        return new ResponseEntity<>(makeMap("gp",newGP.getId()), HttpStatus.CREATED);

    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> addShips(@PathVariable Long gamePlayerId,
                                           @RequestBody Set<Ship> ships,
                                           Authentication authentication){

        GamePlayer gameplayer = gamePlayerRepository.getOne(gamePlayerId);
        Player loggedPlayer = getLoggedPlayer(authentication);
        if (gameplayer != null && loggedPlayer != null) {
            if(loggedPlayer.getId() == gameplayer.getPlayer().getId()) {

                if(gameplayer.getShips().size() != 0)
                    return new ResponseEntity<Map<String,Object>>(makeMap("error", "User already has ships."), HttpStatus.FORBIDDEN);

                for (Ship ship : ships) {
                    ship.setGameplayer(gameplayer);
                    shipRepository.save(ship);
                }
                return new ResponseEntity<Map<String,Object>>(makeMap("success", "Ships have been added."), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

    }


    @RequestMapping("/leaderboard")
    private List<Object> getLeaderboard() {
        return playerRepository.findAll()
                .stream()
                .filter(player -> player.getScores() != null)
                .map(player -> playerScoresDTO(player))
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/game_view/{id}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> gameviewDTO(@PathVariable Long id, Authentication authentication) {

        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
        Map<String, Object> dto = new LinkedHashMap<>();
        String opponentUsername;
        List<Object> opponentSalvos;
        if (authentication != null) {
            dto.put("logged-player", playerDTO(getLoggedPlayer(authentication)));
            if(gamePlayer.getPlayer().getId() != getLoggedPlayer(authentication).getId()){
                return new ResponseEntity<>(makeMap("error","User is unauthorized."), HttpStatus.UNAUTHORIZED);
            }
        }
        dto.put("id", gamePlayer.getId());
        dto.put("playerUsername", gamePlayer.getPlayer().getUserName());
        dto.put("game", gameDTO(gamePlayer.getGame()));
        if(gamePlayer.getShips().size() != 0)
        dto.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> shipDTO(ship))
            .collect(Collectors.toList()));
        dto.put("playerSalvos", gamePlayer.getSalvos()
            .stream()
            .map(salvo -> salvoDTO(salvo))
            .collect(Collectors.toList()));
        if(getOpponent(gamePlayer) != null) {
            GamePlayer gamePlayerOpponent = getOpponent(gamePlayer);
            opponentUsername = gamePlayerOpponent.getPlayer().getUserName();
            opponentSalvos = gamePlayerOpponent.getSalvos()
                .stream()
                .map(salvo -> salvoDTO(salvo))
                .collect(Collectors.toList());
        } else {
            opponentUsername = "N/A";
            opponentSalvos = new ArrayList<Object>();
        }
        dto.put("opponentUsername",opponentUsername);
        dto.put("opponentSalvos",opponentSalvos);
        return new ResponseEntity<>(dto,HttpStatus.ACCEPTED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private Player getLoggedPlayer(Authentication authentication) {
        if(authentication != null)
            return playerRepository.findByUserName(authentication.getName());
        return  null;
    }

    private List<Object> getGames() {
        return gameRepository.findAll()
                .stream()
                .map(game -> gameDTO(game))
                .collect(Collectors.toList());
    }

    private GamePlayer getOpponent(GamePlayer gameplayer){
        return gameplayer.getGame().getGameplayers()
                .stream()
                .filter(gp -> gp.getId() != gameplayer.getId())
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> salvoDTO(Salvo salvo){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn",salvo.getTurn());
        dto.put("player",salvo.getGameplayer().getPlayer().getId());
        dto.put("locations",salvo.getLocations());
        return dto;
    }

    private Map<String, Object> shipDTO(Ship ship){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", ship.getId());
        dto.put("type",ship.getType());
        dto.put("locations",ship.getLocations());
        return dto;

    }

    private Map<String, Object> gameDTO (Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getGameDate());
        dto.put("gameplayers", game.getGameplayers()
                .stream()
                .map(gamePlayer -> gamePlayerDTO(gamePlayer))
                .collect(Collectors.toList())
        );
        return dto;
    }

    private Map<String, Object> playerScoresDTO (Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player",player.getUserName());
        dto.put("stats", getStats(player));
        return dto;
    }

    private Map<String, Object> getStats(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        double total = 0;
        double wins = 0;
        double losses = 0;
        double ties = 0;

        Set<GamePlayer> gamePlayers = player.getGameplayers();

        for(GamePlayer gameplayer:gamePlayers) {
            if(gameplayer.getScore() != null) {
                if(gameplayer.getScore().getScore()==1.0){
                    wins = wins + 1;
                    total = total + 1;
                }
                if(gameplayer.getScore().getScore()==0.0){
                    losses = losses + 1;
                }
                if(gameplayer.getScore().getScore()==0.5){
                    ties = ties + 1;
                    total = total + 0.5;
                }
            }
        }
        dto.put("total",total);
        dto.put("wins",wins);
        dto.put("losses",losses);
        dto.put("ties",ties);
        return dto;
    }

    private Map<String, Object> gamePlayerDTO(GamePlayer gameplayer) {
        Map<String, Object> dto =  new LinkedHashMap<>();
        dto.put("id", gameplayer.getId());
        dto.put("score", scoreDTO(gameplayer.getScore()));
        dto.put("player", playerDTO(gameplayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> playerDTO(Player player){
        Map<String, Object> dto =  new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("username", player.getUserName());
        return dto;
    }

    private Map<String, Object> scoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (score != null)
        dto.put("score", score.getScore());
        return dto;
    }

}