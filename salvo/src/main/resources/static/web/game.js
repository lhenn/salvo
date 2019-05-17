const app = new Vue({
  el: "#app",
  data: {
    gp: window.location.search.split('=')[1] || null,
    gameViewData: {},
    shipLocations: [],
    playerSalvos: [],
    opponentSalvos: [],
    cols: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
    error: "",
    shipsPlaced:false,
    testShips: [{
        "type": "destroyer",
        "locations": ["A1", "B1", "C1"]
      },
      {
        "type": "patrol boat",
        "locations": ["H5", "H6"]
      }
    ]
  },
  created: function() {
    this.getData();
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
        if  (json.ships) {
        this.getShipLocations();
        this.playerSalvos = this.gameViewData.playerSalvos;
        this.opponentSalvos = this.gameViewData.opponentSalvos;
        }
        console.log(app.gameViewData);
      } else {
        this.error = 'Sorry something went wrong -- please find your game through our home page.'
      }
    },
    saveShips: async function() {
      let json = await fetch('/api/games/players/' + this.gp + '/ships', {
          method: "POST",
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.testShips)
        })
        .then(function(response) {
          return response.json();
        })
        .then((json) => json)
        .catch(function(error) {
          console.log(error);
        })
      console.log(json);
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
      let isSalvo = false;
      let salvos;
      if (player == 'player') {
        salvos = this.playerSalvos;
      } else {
        salvos = this.opponentSalvos;
      }
      for (let i = 0; i < salvos.length; i++) {
        if (salvos[i].locations.includes(cellName)) {
          isSalvo = true;
        }
      }
      return isSalvo;
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
    determineClass: function(row, col) {
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
  }
})
// Still figuring this out:
// Make the DIV element draggable:
dragElement(document.getElementById("aircraft-carrier"));

function dragElement(elmnt) {
  var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;

    // otherwise, move the DIV from anywhere inside the DIV:
    elmnt.onmousedown = dragMouseDown;


  function dragMouseDown(e) {
    e = e || window.event;
    e.preventDefault();
    // get the mouse cursor position at startup:
    pos3 = e.clientX;
    pos4 = e.clientY;
    document.onmouseup = closeDragElement;
    // call a function whenever the cursor moves:
    document.onmousemove = elementDrag;
  }

  function elementDrag(e) {
    e = e || window.event;
    e.preventDefault();
    // calculate the new cursor position:
    pos1 = pos3 - e.clientX;
    pos2 = pos4 - e.clientY;
    pos3 = e.clientX;
    pos4 = e.clientY;
    // set the element's new position:
    elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
    elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
    // highlight td that clicker is over
    document.elementFromPoint(e.clientX, e.clientY).classList.add("draggedOver");
    console.log(e.clientX, e.clientY);
    console.log(document.elementFromPoint(e.clientX, e.clientY));
  }

  function closeDragElement() {
    // stop moving when mouse button is released:
    document.onmouseup = null;
    document.onmousemove = null;
  }
}
