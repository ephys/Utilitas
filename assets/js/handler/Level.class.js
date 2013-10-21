/* LEVEL CLASS */
var Level = function() {
	var self = this;

	this.game = Game.instance;
	this.gravity = 0.8;
	this.width = 128*32;
	this.height = 64*32;

	this.scrolling = { x: 0, y: 0 };

	// defines stylesheet, null means it's only colors here
	this.type = null;
	this.textures = {};

	//this.textures.background = new Image();
	//this.textures.background = '#00aba9';

	var dots = [];

	this.textures.background = function(context) {
		// var r = Game.instance.tick%500;
		// if(r > 250)
		// 	r = 500-r;

		// var g = (Game.instance.player.position.x>>4)%500;
		// if(g > 250)
		// 	g = 500-r;

		// var b = (Game.instance.player.position.y>>4)%500;
		// if(b > 500)
		// 	b = 500-b;

		// context.fillStyle = 'rgb('+r+', '+g+', '+b+')';
		// context.fillRect(0, 0, context.canvas.width, context.canvas.height);

		var b = (Game.instance.tick>>4)%100;
		if(b > 50)
			b = 100-b;

		b+=20;

		context.fillStyle = 'rgb(0, 0, '+b+')';
		context.fillRect(0, 0, context.canvas.width, context.canvas.height);

		context.fillStyle = '#ffffff';
		for(var j = 0; j < dots.length; j++) {
			context.fillRect((dots[j].x+(Game.instance.tick>>1))%context.canvas.width, dots[j].y, 1, 1);
		}
	};

	this.textures.material = {};
	this.game.renderer.addRenderer(this);

	this.platforms = [];
	this.entities = [];

	var displayedPlatforms = [];
	var collidingPlatforms = [];
	var displayedEntities  = [];
	var collidingEntities  = [];

	this.getCollidingEntities = function(boundingBox) {
		calculateCollidingEntities(boundingBox);
		return collidingEntities;
	};

	var lastBoundingBox = {};
	this.getCollidingPlatformList = function(boundingBox) {
		if(boundingBox.yTop !== lastBoundingBox.yTop || boundingBox.xLeft !== lastBoundingBox.xLeft) {
			lastBoundingBox = boundingBox;
			calculateCollidingPlatformList(boundingBox);
		}

		return collidingPlatforms;
	};

	this.getDisplayedEntitiesList = function() {
		calculateDisplayedEntitiesList();
		return displayedEntities;
	};

	this.getDisplayedPlatformList = function() {
		return displayedPlatforms;
	};

	var calculateDisplayedPlatformList = function() {
		displayedPlatforms = [];

		for(var i = 0; i < self.platforms.length; i++) {
			if(self.platforms[i].position.x < self.scrolling.x+self.game.canvas.width && self.platforms[i].position.x+self.platforms[i].width > self.scrolling.x) {
				displayedPlatforms.push(i);
			}
		}
	};

	var calculateDisplayedEntitiesList = function() {
		displayedEntities = [];

		for(var i = 0; i < self.entities.length; i++) {
			if(self.entities[i].position.x < self.scrolling.x+self.game.canvas.width && self.entities[i].position.x+self.entities[i].width > self.scrolling.x) {
				displayedEntities.push(i);
			}
		}
	};

	var calculateCollidingPlatformList = function(boundingBox) {
		collidingPlatforms = [];
		for(var i = 0; i < displayedPlatforms.length; i++) {
			var platform = self.platforms[i];
			if(platform.solidity == Platform.nonSolid)
				continue;

			if((platform.boundingBox.yBottom >= boundingBox.yTop && platform.boundingBox.yTop <= boundingBox.yBottom) || (platform.boundingBox.yBottom <= boundingBox.yBottom && boundingBox.yBottom <= platform.boundingBox.yTop) || (platform.boundingBox.yBottom < boundingBox.yTop && boundingBox.yTop < platform.boundingBox.yTop)) {
				if((platform.boundingBox.xRight <= boundingBox.xRight && platform.boundingBox.xLeft >= boundingBox.xLeft) || (platform.boundingBox.xLeft <= boundingBox.xLeft && boundingBox.xLeft <= platform.boundingBox.xRight) || (platform.boundingBox.xLeft <= boundingBox.xRight && boundingBox.xRight <= platform.boundingBox.xRight)) {
					collidingPlatforms.push(i);
				}
			}
		}
	};

	var calculateCollidingEntities = function(boundingBox) {
		collidingEntities = [];
		for(var i = 0; i < displayedEntities.length; i++) {
			var entity = self.entities[i];
			if((entity.boundingBox.yBottom >= boundingBox.yTop && entity.boundingBox.yTop <= boundingBox.yBottom) || (entity.boundingBox.yBottom <= boundingBox.yBottom && boundingBox.yBottom <= entity.boundingBox.yTop) || (entity.boundingBox.yBottom < boundingBox.yTop && boundingBox.yTop < entity.boundingBox.yTop)) {
				if((entity.boundingBox.xRight <= boundingBox.xRight && entity.boundingBox.xLeft >= boundingBox.xLeft) || (entity.boundingBox.xLeft <= boundingBox.xLeft && boundingBox.xLeft <= entity.boundingBox.xRight) || (entity.boundingBox.xLeft <= boundingBox.xRight && boundingBox.xRight <= entity.boundingBox.xRight)) {
					collidingEntities.push(i);
				}
			}
		}
	};

	this.scroll = function() {
		var scrolled = false;

		var ppos = this.game.player.position.x-this.scrolling.x;

		if((ppos > this.game.canvas.width >> 1 && this.scrolling.x+this.game.canvas.width < this.width) || (this.scrolling.x > 0 && ppos < this.game.canvas.width >> 2)) {
			this.scrolling.x += this.game.player.velocity.x;
			scrolled = true;
		}

		if(this.scrolling.x < 0)
			this.scrolling.x = 0;
		else if(this.scrolling.x+this.game.canvas.width > this.width)
			this.scrolling.x = this.width-this.game.canvas.width;

		ppos = this.game.player.position.y-this.scrolling.y;

		if((ppos < this.game.canvas.height >> 2 && -(this.scrolling.y-this.game.canvas.height) < this.height) || (ppos > this.game.canvas.height >> 1 && -this.scrolling.y > 0)) {
			this.scrolling.y += this.game.player.velocity.y;
			scrolled = true;
		}

		if(this.scrolling.y > 0)
			this.scrolling.y = 0;
		else if(-(this.scrolling.y-this.game.canvas.height) > this.height)
			this.scrolling.y = -(this.scrolling.y-this.game.canvas.height);

		if(scrolled)
			calculateDisplayedPlatformList();
	};

	this.init = function() {
		this.platforms.push(new Platform(0, 2, 128, 1, '#bbbbbb', Platform.solid));
		this.platforms.push(new Platform(0, 0, 128, 2, '#ffffff', Platform.nonSolid));

		this.platforms.push(new Platform(24, 3, 6, 5, '#60a917', Platform.nonSolid));
		this.platforms.push(new Platform(24, 8, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 10, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 12, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 14, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 16, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 18, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 20, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 22, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(24, 24, 6, 1, '#008a00', Platform.semiSolid));
		this.platforms.push(new Platform(34, 4, 3, 25, '#008a00', Platform.solid));
		this.platforms.push(new Platform(12, 3, 10, 2, '#60a917', Platform.solid));

		this.entities.push(new ItemEnergy({ x: 13, y: 5 }, 20));
		this.entities.push(new ItemMedkit({ x: 17, y: 5 }, 20));
		this.entities.push(new Note({ x: 1, y: 16 }, 'hey ! use arrows to move around'));
		this.entities.push(new Note({ x: 1, y: 15 }, 'and space to jump !'));
		this.entities.push(new Note({ x: 16, y: 8 }, 'healing'));
		this.entities.push(new Note({ x: 12, y: 8 }, 'energy'));

		this.entities.push(new SpawnPoint({ x: 4, y: 4 }));

		for(var i = 0; i < this.entities.length; i++) {
			if(this.entities[i] instanceof SpawnPoint) {
				var player = Game.instance.player;
				player.position.x = this.entities[i].position.x;
				player.position.y = this.entities[i].position.y-Game.instance.player.height+32;
				break;
			}
		}

		calculateDisplayedEntitiesList();
		calculateDisplayedPlatformList();

		for(var i = 0; i < 800; i++) {
			dots.push({x:Math.random()*Game.instance.canvas.width, y:Math.random()*Game.instance.canvas.height});
		}
	};
};

Level.prototype.toFileFormat = function() {
	var levelData = {
		platforms: [],
		entities: []
	};

	for(var i = 0; i < this.platforms.length; i++) {
		levelData.platforms[i] = this.platforms[i].toFileFormat();
	}

	for(var i = 0; i < this.entities.length; i++) {
		levelData.entities.push(this.entities[i].toFileFormat());
	}


	console.log(JSON.stringify(levelData));
};

Level.prototype.fromFileFormat = function() {
};

Level.prototype.onTick = function() {
	this.scroll();
};

Level.prototype.snapToGrid = function(f) {
	if(f < 0)
		f -= 32;

	return ((f / 32) >> 0) * 32;
};

Level.prototype.loadTexture = function(name) {
	if(this.textures.material[name] !== undefined)
		return this.textures.material[name];

	try {
		console.log("loading texture '"+name+"' from sprite list "+this.type);
		this.textures.material[name] = new Image();
		this.textures.material[name].src = 'assets/img/'+this.type+'/materials/'+name+'.png';
	} catch(e) {
		console.log(e);
	}

	return this.textures.material[name];
};

Level.prototype.render = function(context) {
	if(this.textures.background instanceof Image) {
		context.translate(-this.scrolling.x, -this.scrolling.y);
		context.fillStyle = context.createPattern(this.textures.background, 'repeat');
		context.fillRect(this.scrolling.x, this.scrolling.y, context.canvas.width, context.canvas.height);
		context.translate(this.scrolling.x, this.scrolling.y);
	} else if(typeof this.textures.background === 'function') {
		this.textures.background(context);
	} else {
		context.fillStyle = this.textures.background;
		context.fillRect(0, 0, context.canvas.width, context.canvas.height);
	}

	var platforms = this.getDisplayedPlatformList();
	var lastMaterial, i;
	for(i in platforms) {
		var platform = this.platforms[platforms[i]];

		if(platform.material !== lastMaterial) {
			if(platform.material.length === 7 && platform.material.indexOf('#') == 0)
				context.fillStyle = platform.material;
			else {
				var material = this.loadTexture(platform.material);

				context.fillStyle = context.createPattern(material, 'repeat');
			}

			lastMaterial = platform.material;
		}

		context.shadowColor = platform.hasShadow?"rgba(0,0,0,0.8)":"transparent";
		context.translate(platform.position.x-this.scrolling.x, platform.position.y-this.scrolling.y);
		context.fillRect(0, 0, platform.width, platform.height);
		context.translate(-(platform.position.x-this.scrolling.x), -(platform.position.y-this.scrolling.y));
		if(this.game.debug && platform.solidity !== Platform.nonSolid) {
			context.strokeStyle = (platform.solidity === Platform.solid)?"red":"blue";
			context.beginPath();
			context.moveTo(platform.boundingBox.xLeft-this.scrolling.x, platform.boundingBox.yBottom-this.scrolling.y);
			context.lineTo(platform.boundingBox.xRight-this.scrolling.x, platform.boundingBox.yBottom-this.scrolling.y);
			context.lineTo(platform.boundingBox.xRight-this.scrolling.x, platform.boundingBox.yTop-this.scrolling.y);
			context.lineTo(platform.boundingBox.xLeft-this.scrolling.x, platform.boundingBox.yTop-this.scrolling.y);
			context.lineTo(platform.boundingBox.xLeft-this.scrolling.x, platform.boundingBox.yBottom-this.scrolling.y);
			context.stroke();
		}
	}

	var entities = this.getDisplayedEntitiesList();
	for(i in entities) {
		if(this.entities[entities[i]].isRenderable)
			this.entities[entities[i]].render(context);
	}
};