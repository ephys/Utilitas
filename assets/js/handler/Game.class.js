/* GAME CLASS */
var Game = function(canvas) {
	if(Game.instance === null)
		Game.instance = this;
	else
		throw 'Game already instantiated';

	var isPlaying = true;
	this.debug = false;
	this.paused = false;
	this.canvas = canvas;
	this.inputHandler = new InputHandler();
	this.keyConfig = new KeyConfig();
	this.renderer = new Renderer();
	this.level = new Level();
	this.player = new Player();
	this.tick = 0;

	this.interfaces = {};
	this.interfaces.userInterface = new UserInterface();

	var isFrameByFrame = false;

	var self = this;
	this.setMenu = function(menu) {
		if(menu === null && self.interfaces.currentMenu !== undefined)
			delete self.interfaces.currentMenu;
		else
			self.interfaces.currentMenu = menu;
	};

	this.togglePause = function() {
		self.paused = !self.paused;

		if(!self.paused)
			self.setMenu(null);
		else
			self.setMenu(new MainMenu());
	};

	this.stop = function() {
		isPlaying = false;
	};

	this.run = function() {
		if(self.inputHandler.isActivated(self.keyConfig.key_esc)) {
			self.togglePause();
		}

		if(self.inputHandler.isActivated(self.keyConfig.key_debug_pause)) {
			//isFrameByFrame = !isFrameByFrame;
			self.level.toFileFormat();
		}

		if(!self.paused) {
			if(self.player.health <= 0) {
				self.interfaces.deadInterface = new DeathScreen();
				self.paused = true;
			} else {
				self.player.onTick();
				self.level.onTick();
			}

			if(self.inputHandler.isActivated(self.keyConfig.key_debug)) {
				self.debug = !self.debug;

				if(self.debug)
					self.interfaces.userInterface = new DebugInterface();
				else
					self.interfaces.userInterface = new UserInterface();
			}
		}

		self.renderer.render();

		if(isFrameByFrame)
			self.paused = true;

		self.tick++;
	};

	var cleanup = function() {
		self.renderer.wipe();
		self.inputHandler.clearListeners();
	};

	var loop = function() {
		self.run();
		if(isPlaying)
			requestAnimFrame(loop);
		else
			cleanup();
	};

	this.level.init();
	loop();
};

Game.instance = null;
Game.loader = {
	loadingBar: null,
	loadingDesc: null,
	self: this,

	loadScripts: function(canvas, callback) {
		var filenames = [
			'entities/Entity.class.js',
			'entities/Player.class.js',
			'entities/Item.class.js',
			'entities/ItemEnergy.class.js',
			'entities/ItemMedkit.class.js',
			'entities/Note.class.js',
			'entities/SpawnPoint.class.js',
			'gui/Interface.class.js',
			'handler/Renderer.class.js',
			'handler/InputHandler.class.js',
			'handler/KeyConfig.class.js',
			'handler/Level.class.js',
			'handler/Renderer.class.js',
			'handler/Logger.class.js',
			'tiles/Platform.class.js'
		];


		var progress = 0;
		var error = null;
		var message = null;
		var context = canvas.getContext("2d");
		context.font = '20pt Monospace';
		var updateProgress = function() {
			context.clearRect(0,0,canvas.width,canvas.height);

			var middle = { x: canvas.width >> 1, y: canvas.height >> 1 };
			context.fillStyle = "#ffffff";
			context.fillText('Loading...', middle.x-(context.measureText('Loading...').width >> 1), 55);
			context.fillRect(50, middle.y-20, canvas.width-100, 40);
			if(error !== null) {
				context.fillText(error, middle.x-(context.measureText(error).width >> 1), middle.y+55);
			}

			if(message !== null) {
				context.fillText(message, middle.x-(context.measureText(message).width >> 1), middle.y-40);
			}

			context.fillStyle = "#0000ff";
			context.fillRect(50, middle.y-20, (canvas.width-100)*progress, 40);
		};

		updateProgress();

		var head = document.getElementsByTagName('head')[0];
		var percent = (100/filenames.length)/100;
		var j = 0;
		self.loading = 0;
		for(var i = 0; i < filenames.length; i++) {
			var script = document.createElement('script');
			script.setAttribute("src", 'assets/js/'+filenames[i]);

			script.addEventListener('error', function() {
				error = 'Failled to download '+this.src;
				updateProgress();
			});

			script.addEventListener('load', function() {
				progress = percent*(j+1);
				message = 'Downloaded '+this.src;
				updateProgress();
				j++;

				if(j === filenames.length) {
					callback();
				}
			}, false);

			head.appendChild(script);
		}
	}
};

Game.init = function(canvas) {
	canvas.height = window.innerHeight;
	canvas.width = window.innerWidth;
	Game.loader.loadScripts(canvas, function() {
		var game = new Game(canvas);
	});
};