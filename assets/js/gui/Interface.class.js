var UserInterface = function() {
	this.game = Game.instance;
};

UserInterface.prototype.render = function(context) {
	context.fillStyle = "rgba(0,0,0,0.5)";
	// boost rect border
	context.fillRect(6, 6, 404, 40);
	// health rect border
	context.fillRect(6, 56, 404, 40);

	context.fillStyle = "#ffffff";
	context.fillText("Boost", 15, 32);
	context.fillText("Health", 15, 83);
	// boost rect in
	context.fillRect(150, 10, 256*this.game.player.boost/this.game.player.maxBoost, 32);

	// health rect in
	context.fillStyle = "#ff0000";
	context.fillRect(150, 60, 256*this.game.player.health/this.game.player.maxHealth, 32);
};

var DebugInterface = function() {
	this.game = Game.instance;
};

DebugInterface.prototype.render = function(context) {
	context.fillStyle = "rgba(0,0,0,0.5)";
	context.fillRect(6, 6, 404, 90);

	context.fillStyle = "#ffffff";
	context.fillText("FPS "+this.game.renderer.fps, 15, 32);
	context.fillText("Key "+this.game.inputHandler.lastPressed, 15, 83);

	context.fillText("Pos.x "+(this.game.player.position.x/32>>0), 220, 32);
	context.fillText("Pos.y "+(this.game.player.levelY), 220, 83);
};

var DeathScreen = function() {
	this.game = Game.instance;
	var deathMessage = this.game.player.getDeathMessage();

	var width = 700;
	var height = 64;
	var position = { x: (this.game.canvas.width-width) >> 1, y: (this.game.canvas.height-height) >> 1 };

	this.render = function(context) {
		context.fillStyle = "rgba(0,0,0,0.5)";
		context.fillRect(position.x, position.y, width, height);
		context.fillStyle = "#ffffff";
		context.fillText(deathMessage, (context.canvas.width-context.measureText(deathMessage).width) >> 1, position.y+40);
	};
};

var Button = function(title, action, pos_x, pos_y, width, height) {
	this.title = title;
	this.onClick = action;
	this.position = { x: pos_x, y: pos_y };
	this.dim = { w: width, h: height };
	this.disabled = false;
};

Button.prototype.render = function(context) {
	var inputHandler = Game.instance.inputHandler;
	var mouse = inputHandler.mousePos;

	var offset = 0;
	if(this.disabled) {
		context.fillStyle = "rgba(25,25,25,0.8)";
	} else {
		if(mouse.y >= this.position.y && mouse.y <= this.position.y+this.dim.h && mouse.x >= this.position.x && mouse.x <= this.position.x+this.dim.w) {
			context.fillStyle = "rgba(0,0,0,0.9)";
			if(inputHandler.isLeftClicking()) {
				offset = 2;
			}

			if(inputHandler.hasLeftClicked()) {
				if(typeof this.onClick === 'function')
					this.onClick();
			}
		} else
			context.fillStyle = "rgba(0,0,0,0.7)";
	}

	context.fillRect(this.position.x+offset, this.position.y+offset, this.dim.w, this.dim.h);
	context.fillStyle = "#ffffff";
	context.fillText(this.title, this.position.x + ((this.dim.w - context.measureText(this.title).width) >> 1) + offset, this.position.y+(this.dim.h >> 1)+10+offset);
};

var MainMenu = function() {
	var game = Game.instance;

	var buttonWidth = 500;
	var buttonHeight = 64;

	var posX = (game.canvas.width-buttonWidth) >> 1;

	this.buttonLength = 3;

	this.buttons = [];
	this.buttons.push(new Button('Resume', function() {
		game.togglePause();
	}, posX, 100, buttonWidth, buttonHeight));

	this.buttons.push(new Button('Options', function() {
		game.setMenu(new ConfigMenu());
	}, posX, 200, buttonWidth, buttonHeight));

	this.buttons.push(new Button('Exit', function() {
		game.stop();
	}, posX, 300, buttonWidth, buttonHeight));
};

MainMenu.prototype.render = function(context) {
	for(var i = 0; i < this.buttonLength; i++) {
		this.buttons[i].render(context);
	}
};

var ConfigMenu = function() {
	var game = Game.instance;

	var buttonWidth = 500;
	var buttonHeight = 64;

	var posX = (game.canvas.width-buttonWidth) >> 1;

	this.buttons = [];
	this.buttons.push(new Button('Key configuration', function() {
		game.setMenu(new KeyConfigMenu());
	}, posX, 100, buttonWidth, buttonHeight));

	this.buttons.push(new Button('Video settings', function() {

	}, posX, 200, buttonWidth, buttonHeight));

	this.buttons.push(new Button('Sound settings', function() {

	}, posX, 300, buttonWidth, buttonHeight));

	this.buttons.push(new Button('Back', function() {
		game.setMenu(new MainMenu());
	}, posX, 400, buttonWidth, buttonHeight));
};

ConfigMenu.prototype.render = function(context) {
	for(var i = 0; i < this.buttons.length; i++) {
		this.buttons[i].render(context);
	}
};

var KeyConfigMenu = function() {
	var game = Game.instance;

	var buttonWidth = 300;
	var buttonHeight = 50;

	var posX = (game.canvas.width-buttonWidth) >> 2;

	this.buttons = [];
	this.buttons.push(new Button('Back', function() {
		game.setMenu(new ConfigMenu());
	}, posX, 100, buttonWidth, buttonHeight));

	var keyNames = new KeyMap();
	var keyMap = game.keyConfig.getMap();
	var j = 0, k = 0;
	for(var i = 0; i < keyMap.length; i++) {
		var key = keyMap[i];

		if(40+75*(j+2) > game.canvas.height-250) {
			j = 0;
			k++;
		}

		this.buttons.push(new Button(key.getName()+': '+keyNames.getKeyName(key.getCode()), function() {
		}, posX+k*350, 40+75*(j+2), buttonWidth, buttonHeight));
		j++;
	}
};

KeyConfigMenu.prototype.render = function(context) {
	for(var i = 0; i < this.buttons.length; i++) {
		this.buttons[i].render(context);
	}
};