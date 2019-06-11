const app = new Vue({
  el: "#app",
  data: {
    gp: window.location.search.split('=')[1] || null,
    gameViewData: {},
    gameState: "",
    playerTurnFeedback:"test-says what player just did",
    opponentTurnFeedback:"test-says what opp just did",
    shipLocations: [],
    playerSalvos: [],
    opponentSalvos: [],
    cols: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
    error: "",
    ships: [{
        "type": "Aircraft Carrier",
        "locations": [],
        "length": 5
      },
      {
        "type": "Battleship",
        "locations": [],
        "length": 4
      },
      {
        "type": "Submarine",
        "locations": [],
        "length": 3
      },
      {
        "type": "Destroyer",
        "locations": [],
        "length": 3
      },
      {
        "type": "Patrol Boat",
        "locations": [],
        "length": 2
      }
    ],
    xOrientation: true,
    selectedShip: "",
    shipShadow: [],
    illegalPlacement: true,
    salvoShadow: [],
    currentSalvo: {
      'locations': []
    },
    placingSalvo: true,
    playerStats: [],
    opponentStats: []
  },
  created: function() {
    this.getData();

    setInterval(function(){
      this.getData();
    }.bind(this), 5000);
  },
  methods: {
    getData: async function() {
      if (this.gp != null) {
        let apiRequest = "/api/game_view/" + this.gp;
        let json = await fetch(apiRequest, {
            method: "GET"
          })
          .then(function(response) {
            return response.json();
          })
          .then((json) => json)
          .catch(function(error) {
            console.error(error);
          })
        if (json.error) this.error = "You are not authorized to see this page."
        this.gameViewData = json;
        this.gameState = this.gameViewData.gameState;
        if (json.ships) {
          this.shipsPlaced = true;
          this.getShipLocations();
          this.playerSalvos = this.gameViewData.playerSalvos;
          this.opponentSalvos = this.gameViewData.opponentSalvos;
          this.playerStats = this.getShipStats('player');
          this.opponentStats = this.getShipStats('opponent');
        }
      } else {
        this.error = 'Sorry something went wrong -- please find your game through our home page.'
      }
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
          window.location.replace("/web/games.html")
        })
        .catch(error => console.log(error))
    },
    saveShips: async function() {
      let json = await fetch('/api/games/players/' + this.gp + '/ships', {
          method: "POST",
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.trimShipJSON())
        })
        .then(function(response) {
          return response.json();
        })
        .then((json) => json)
        .catch(function(error) {
          console.log(error);
        })
      console.log(json);
      if (!json.error) this.getData()
    },
    saveSalvos: async function() {
      let json = await fetch('/api/games/players/' + this.gp + '/salvos', {
          method: "POST",
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.currentSalvo)
        })
        .then(function(response) {
          return response.json();
        })
        .then((json) => json)
        .catch(function(error) {
          console.log(error);
        })
      console.log(json);
      if (!json.error) {
        this.currentSalvo.locations = [];
        this.getData()
      }
    },
    getShipLocations: function() {
      let ships = app.gameViewData.ships;
      let locations = [];
      for (let i = 0; i < ships.length; i++) {
        locations = locations.concat(ships[i].locations);
      }
      app.shipLocations = locations;
    },
    checkIfShip: function(row, col) {
      cellName = "" + row + col;
      if (this.shipLocations.includes(cellName)) {
        return true;
      } else {
        return false;
      }
    },
    checkIfSalvo: function(player, row, col) {
      let cellName = "" + row + col;
      let salvos;
      if (player == 'player') {
        salvos = this.playerSalvos;
      } else {
        salvos = this.opponentSalvos;
      }
      for (let i = 0; i < salvos.length; i++) {
        if (salvos[i].locations.includes(cellName)) {
          return true;
        }
      }
      return false;
    },
    getSalvoTurn: function(player, row, col) {
      cellName = "" + row + col;
      let salvos;
      if (player == 'player') {
        salvos = this.playerSalvos;
      } else {
        salvos = this.opponentSalvos;
      }

      for (let i = 0; i < salvos.length; i++) {
        if (salvos[i].locations.includes(cellName)) {
          return salvos[i].turn + "";
        }
      }
    },
    checkIfHit: function(player, row, col){
      let cellName = "" + row + col;
      let hits;
      if(player == 'player') hits = this.gameViewData.playerHits;
      if(player == 'opponent') hits = this.gameViewData.opponentHits;
      for(let i =0; i<hits.length; i++){
        if(hits[i].location == cellName) {
          return true;
        }
      }
      return false;
    },
    determineShipTableClass: function(row, col) {
      let cellName = "" + row + col;
      if (this.checkIfShip(row, col)) {
        if (this.checkIfSalvo('opponent', row, col)) {
          return "hit";
        } else {
          return "ship";
        }
      } else {
        if (this.checkIfSalvo('opponent', row, col)) {
          return "salvo"
        } else {
          return "background"
        }
      }
    },
    determineShipPlacingClass: function(row, col) {
      let cellName = "" + row + col;
      for (let i = 0; i < this.ships.length; i++) {
        if (this.ships[i].locations.includes(cellName)) {
          return "placed-ship";
        }
      }
      if (this.shipShadow.includes(cellName)) {
        for (let i = 0; i < this.ships.length; i++) {
          for (let j = 0; j < this.shipShadow.length; j++) {
            if (this.ships[i].locations.includes(this.shipShadow[j])) {
              this.illegalPlacement = true;
              return "illegal-ship-shadow";
            }
          }
        }
        this.illegalPlacement = false;
        return "ship-shadow";
      }
      return "background";
    },
    getShadowLocation: function(row, col) {
      for (let i = 0; i < this.ships.length; i++) {
        if (this.selectedShip.type == this.ships[i].type &&
          this.ships[i].locations.length == 0) {
          if (this.xOrientation) this.getHorizontalLocation(row, col);
          if (!this.xOrientation) this.getVerticalLocation(row, col);
        }
      }
    },
    getHorizontalLocation: function(row, col) {
      this.shipShadow = [];
      thresholdCol = 10 - this.selectedShip.length + 1;
      for (let i = 0; i < this.selectedShip.length; i++) {
        if (col <= thresholdCol) this.shipShadow.push(row + (col + i));
        else this.shipShadow.push(row + (thresholdCol + i));
      }
    },
    getVerticalLocation: function(row, col) {
      this.shipShadow = [];
      thresholdRow = 74 - this.selectedShip.length + 1;
      for (let i = 0; i < this.selectedShip.length; i++) {
        if (row.charCodeAt(0) <= thresholdRow) this.shipShadow.push(String.fromCharCode(row.charCodeAt(0) + i) + col);
        else this.shipShadow.push(String.fromCharCode(thresholdRow + i) + col);
      }
    },
    selectShip: function(ship) {
      if(this.selectedShip != ship) this.selectedShip = ship;
      else {
        this.clearShipLocations(ship);
        this.resetShadow();
      }
    },
    placeShip: function() {
      if (!this.illegalPlacement) {
        for (let i = 0; i < this.ships.length; i++) {
          if (this.ships[i].type == this.selectedShip.type) {
            this.ships[i].locations = this.shipShadow;
          }
        }
      } else {
        alert("Make sure your ships don't overlap!");
      }
    },
    clearShipLocations: function(ship) {
      for (let i = 0; i < this.ships.length; i++) {
        if (this.ships[i].type == ship.type) this.ships[i].locations = [];
      }
    },
    resetShadow: function() {
      if (this.shipShadow.length > 0) {
        row = this.shipShadow[0].substring(0, 1);
        col = parseInt(this.shipShadow[0].substring(1, 2));
        this.getShadowLocation(row, col);
        console.log(row, col);
      }
    },
    resetShadowOrientation: function() {
      this.xOrientation = !this.xOrientation;
      this.resetShadow();
    },
    checkIfShipsPlaced: function() {
      for (let i = 0; i < this.ships.length; i++) {
        if (this.ships[i].locations.length == 0) return false;
      }
      return true;
    },
    trimShipJSON: function() {
      trimmedShips = [];
      for (let i = 0; i < this.ships.length; i++) {
        trimmedShips.push({
          "type": this.ships[i].type,
          "locations": this.ships[i].locations
        })
      }
      return trimmedShips;
    },
    getSalvoShadow: function(row, col) {
      this.salvoShadow = [];
      if(this.gameState == 'fire salvo' || this.gameState == 'one more chance'){
      this.salvoShadow.push(row + col);
      }
    },
    determineSalvoTableClass: function(row, col) {
      let cellName = "" + row + col;
      if (this.salvoShadow.includes(cellName) &&
        this.currentSalvo.locations.length < 5) {
        if (this.checkIfSalvo('player', row, col)) {
          this.illegalPlacement = true;
          return "stupid-salvo";
        }
        this.illegalPlacement = false;
        return 'salvo-shadow';
      }
      if(this.checkIfHit('opponent', row, col)){
        return "hit";
      }
      if (this.checkIfSalvo('player', row, col)) {
        return "salvo"
      }
      if (this.currentSalvo.locations.includes(cellName)) {
        return "current-salvo";
      }
      return 'background';
    },
    shootSalvo: function(row, col) {
      let cellName = "" + row + col;
      if (!this.illegalPlacement) {
        if (this.currentSalvo.locations.length <= 5) {
          if (this.currentSalvo.locations.includes(cellName)) {
            console.log("current salvo locations: ", this.currentSalvo.locations)
            let index = this.currentSalvo.locations.indexOf(cellName);
            console.log("index: ", index);
            this.currentSalvo.locations.splice(index, 1);
            console.log("after splice: ", this.currentSalvo.locations)
          } else {
            this.currentSalvo.locations.push(this.salvoShadow[0]);
          }
        }
      } else {alert("stupid choice!")}
    },
    checkIfSalvoComplete: function() {
      if (this.currentSalvo.locations.length == 5) {
        return true;
      }
      return false;
    },
    getShipStats: function(player) {
      let shipStats = [];
      for(let i = 0 ; i<this.ships.length; i++){
        shipStats.push({
          'type': this.ships[i].type,
          'length': this.ships[i].length,
          'hits': this.countShipHits(this.ships[i].type, player)
        })
      }
      return shipStats;
    },
    countShipHits: function(shipType, player) {
      let count = 0;
      let hits;
      if(player == 'player') hits = this.gameViewData.playerHits;
      if(player == 'opponent') hits = this.gameViewData.opponentHits;
      for(let i=0; i<hits.length; i++) {
        if( hits[i].type == shipType ) count += 1;
      }
      return count;
    },
    getOpponentTurnHits: function() {
      turnHits = 0
      if(this.gameViewData.opponentSalvos.length > 0) {
        let lastTurn = Math.max(...this.gameViewData.opponentSalvos.map(s =>s.turn));
        let lastSalvo = this.gameViewData.opponentSalvos.find(function(salvo) {
          return salvo.turn == lastTurn;
        });
        for(let i = 0; i<lastSalvo.locations.length; i++){
          if(this.gameViewData.playerHits.map(h => h.location).includes(lastSalvo.locations[i]))
            turnHits+=1;
        }
      }
      console.log("opponent turn hits: ", turnHits);
      return turnHits;
    },
    getPlayerTurnHits: function() {
      turnHits = 0;
      if(this.gameViewData.playerSalvos.length > 0) {
        let lastTurn = Math.max(...this.gameViewData.playerSalvos.map(s =>s.turn));
        let lastSalvo = this.gameViewData.playerSalvos.find(function(salvo) {
          return salvo.turn == lastTurn;
        });
        for(let i = 0; i<lastSalvo.locations.length; i++){
          if(this.gameViewData.opponentHits.map(h => h.location).includes(lastSalvo.locations[i]))
            turnHits+=1;
        }
      }
      return turnHits;
    }
  }
})
