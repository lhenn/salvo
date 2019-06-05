const app = new Vue({
  el: "#app",
  data: {
    gamesAndPlayerData: {},
    gamesData: {},
    leaderboardData: {},
    leadPlayers: [],
    username: "",
    password: "",
    loggedIn: false,
    error: "none",
    register: false,
    regName: "",
    regPass: "",
    confirmPass: "",
    regFeedback: ""
  },
  created: function() {
    this.getGamesData();
    this.getLeaderBoardData();

  },
  methods: {
    getGamesData: function() {
      fetch('/api/games', {
          method: "GET"
        })
        .then(function(response) {
          return response.json();
        })
        .then(function(json) {
          app.gamesAndPlayerData = json;
          app.gamesData = json.games;
          if (json.player) {
            app.loggedIn = true;
          }
          console.log(json);
        })
        .catch(function(error) {
          console.log(error);
        })
    },
    getLeaderBoardData: function() {
      fetch('/api/leaderboard', {
          method: "GET"
        })
        .then(function(response) {
          return response.json();
        })
        .then(function(json) {
          app.leaderboardData = json;
          app.createLeaderboard();
        })
        .catch(function(error) {
          console.log(error);
        })
    },
    login: function() {
      let body = 'userName=' + app.username + '&password=' + app.password;
      fetch('/api/login', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
          },
          body: body
        })
        .then(function(json) {
          if (json.ok) {
            console.log(json);
            app.getGamesData();
          } else if (!json.ok && app.username == "" && app.password == "") {
            app.error = "blank";
          } else {
            app.error = "error";
          }
        })
        .catch(function(error) {
          console.log(error);
          app.error = 'error';
        })
    },
    logout: function() {
      fetch('/api/logout', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
          }
        })
        .then(function(json) {
          console.log(json);
          location.reload();
        })
        .catch(error => console.log(error))
    },
    createAccount: function() {
      let body = 'userName=' + app.regName + '&password=' + app.regPass;
      fetch('/api/players', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
          },
          body: body
        })
        .then(function(json) {
          if (json.ok) {
            app.regFeedback = "success";
          } else {
            app.regFeedback = "error";
          }
        })
        .catch(error => console.log(error))
    },
    startNewGame: async function() {
      let json = await fetch('/api/games', {
          method: "POST",
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          }
        })
        .then(function(response) {
          return response.json();
        })
        .then((json) => json)
        .catch(function(error) {
          console.error(error);
        })
      console.log(json)
      window.location.replace("game.html?gp=" + json.gp);
      if (json.error) this.error = "You are not authorized to see this page."
    },
    joinGame: async function(game) {
      let gameID = game.id;
      let json = await fetch('/api/game/' + gameID + '/players', {
          method: "POST",
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          }
        })
        .then(function(response) {
          return response.json();
        })
        .then((json) => json)
        .catch(function(error) {
          console.log(error);
        })
      console.log(json);
      // NOT SURE ABOUT ERROR
      if(json.error) this.error = json.error;
      window.location.replace("game.html?gp=" + json.gp);
    },
    createDate: function(date) {
      return date.substring(11, 16) + ", " + date.substring(5, 10);
    },
    getGamePlayers: function(game) {
      let playerArray = [game.gameplayers[0].player.username];
      if (game.gameplayers[1]) {
        playerArray.push(game.gameplayers[1].player.username);
      } else {
        playerArray.push("N/A");
      }
      return playerArray;
    },
    createReturnLink: function(game) {
      if (!app.gamesAndPlayerData.player) {
        return null;
      }
      let n = -1;
      for (let i = 0; i < game.gameplayers.length; i++) {
        if (app.gamesAndPlayerData.player.id === game.gameplayers[i].player.id) {
          n = game.gameplayers[i].id;
        }
      }
      if (n == -1) {
        return null;
      }
      let gameLink = "game.html?gp=" + n;
      return gameLink;
    },
    createLeaderboard: function() {
      $('#leadboard').DataTable({
        pageLength: 5,

        data: app.leaderboardData,
        columns: [{
            data: 'player'
          }, {
            data: 'stats.total'
          }, {
            data: 'stats.wins'
          },
          {
            data: 'stats.losses'
          }, {
            data: 'stats.ties'
          }
        ],
        "columnDefs": [{
            "title": "Player",
            "targets": 0
          },
          {
            "title": "Total Score",
            "targets": 1
          },
          {
            "title": "Won",
            "targets": 2
          },
          {
            "title": "Lost",
            "targets": 3
          },
          {
            "title": "Tied",
            "targets": 4
          },
        ],
        "searching": false,
        "info": false,
        "order": [
          [1, 'dec'],
          [0, 'asc']
        ],
      });
    }
  }
});
