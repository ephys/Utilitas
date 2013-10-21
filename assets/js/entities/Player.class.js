/* PLAYER CLASS */
var Player = function() {
	LivingEntity.apply(this, [0, 0, 64, 128]);
	this.game = Game.instance;

	this.boost = 0;
	this.maxBoost = 50;

	this.jumpCount = 0;
	this.maxJump = 1;
	this.maxSpeed = {x: 9, y: 11};
	this.acceleration = 1;
	this.deceleration = 1;
	this.onGround = false;

	this.immunityTime = 0;
	this.lastDamager = null;

	this.levelY = 1;

	this.playerTexture = new Image();
	this.playerTexture.src = 'assets/img/player.png';
	this.game.renderer.addRenderer(this);
};

Player.prototype = new LivingEntity();

Player.prototype.getName = function() {
	return 'the player';
};

Player.prototype.onTick = function() {
	this.executeMovement();
};

Player.prototype.executeMovement = function() {
	var oldVelocity = this.velocity.x;

	if(this.game.inputHandler.isPressed(this.game.keyConfig.key_run) && this.boost > 0) {
		this.maxSpeed.x = 15;
		this.maxSpeed.y = 15;
		this.maxJump = 2;

		if(this.isMoving() || this.game.inputHandler.isPressed(this.game.keyConfig.key_jump))
			this.boost -= 0.1;
	} else {
		this.maxSpeed.x = 9;
		this.maxSpeed.y = 11;
		this.maxJump = 1;
	}

	if(this.game.inputHandler.isPressed(this.game.keyConfig.key_right) && (this.velocity.x < this.maxSpeed.x)) {
		this.velocity.x += this.acceleration;
	}

	if(this.game.inputHandler.isPressed(this.game.keyConfig.key_left) && (this.velocity.x > -this.maxSpeed.x)) {
		this.velocity.x -= this.acceleration;
	}

	if(!this.isMoving()) {
		this.slowDown();
	}

	// ===============================================
	//   JUMP
	// ===============================================
	if(this.game.inputHandler.isPressed(this.game.keyConfig.key_jump)) {
		var mayJump = this.jumpCount < this.maxJump && this.velocity.y >= 0;
		if(mayJump) {
			this.velocity.y = -this.maxSpeed.y;
			this.jumpCount++;
		}
	}

	this.boundingBox = {
		yTop: this.position.y,
		yBottom: this.position.y+this.height,
		xLeft: this.position.x,
		xRight: this.position.x+this.width
	};

	var entities = this.game.level.getCollidingEntities(this.boundingBox);
	for(var i in entities) {
		this.game.level.entities[entities[i]].onTouch(this);
	}

	this.nextBoundingBox = {
		yTop: this.position.y+this.velocity.y,
		yBottom: this.position.y+this.height+this.velocity.y,
		xLeft: this.position.x+this.velocity.x,
		xRight: this.position.x+this.width+this.velocity.x
	};

	var platforms = this.game.level.getCollidingPlatformList(this.nextBoundingBox);
	var collision;
	if((collision = this.collide_x(platforms)) === false) {
		this.position.x += this.velocity.x;
	} else {
		this.position.x = collision;
		this.velocity.x = oldVelocity;
		if(this.isMoving()) {
			this.slowDown();
		}
	}

	if((collision = this.collide_y(platforms)) === false) {
		this.velocity.y += this.game.level.gravity;
		this.position.y += this.velocity.y;
	} else {
		this.position.y = collision-this.height;
		this.velocity.y = 0;
		this.jumpCount = 0;
	}

	this.levelY = (-(this.position.y-Game.instance.canvas.height+(this.height-32))/32) >> 0;

	if(this.levelY < -10)
		this.receiveDamage(1, null);

	if(this.immunityTime !== 0)
		this.immunityTime--;
};

Player.prototype.receiveDamage = function(count, damager) {
	if(this.immunityTime !== 0 || this.health <= 0)
		return;

	this.health -= (count/this.armor);

	if(damager !== null)
		this.immunityTime = 35;

	this.lastDamager = damager;
};

Player.prototype.getDeathMessage = function() {
	if(this.lastDamager === null)
		return 'you died';

	if(this.lastDamager instanceof LivingEntity)
		return 'you were killed by '+this.lastDamager.getName();
};

Player.prototype.slowDown = function() {
	if(this.velocity.x > 0)
		this.velocity.x -= this.deceleration;
	else if(this.velocity.x < 0)
		this.velocity.x += this.deceleration;
};

Player.prototype.collide_y = function(platforms) {
	var movingUp = this.velocity.y < 0;

	for(var i in platforms) {
		var platform = this.game.level.platforms[platforms[i]];
		if(platform.solidity == Platform.nonSolid)
			continue;

		// sidenote: on platforms, bottom is the visual top /o\
		if(!movingUp && this.boundingBox.yBottom <= platform.boundingBox.yBottom && this.nextBoundingBox.yBottom >= platform.boundingBox.yBottom)
			return platform.boundingBox.yBottom;

		if(platform.solidity == Platform.solid && movingUp && this.boundingBox.yTop >= platform.boundingBox.yTop && this.nextBoundingBox.yTop <= platform.boundingBox.yTop)
			return platform.boundingBox.yTop+this.height;
	}

	return false;
};

Player.prototype.collide_x = function(platforms) {
	if(this.velocity.x === 0)
		return false;

	var movingRight = this.velocity.x > 0;

	if(movingRight && this.nextBoundingBox.xRight >= this.game.level.width)
		return this.game.level.width-this.width;
	else if(!movingRight && this.nextBoundingBox.xLeft <= 0)
		return 0;

	for(var i in platforms) {
		var platform = this.game.level.platforms[platforms[i]];

		if(platform.solidity !== Platform.solid)
			continue;

		if(movingRight) {
			if(this.boundingBox.xRight <= platform.boundingBox.xLeft && this.nextBoundingBox.xRight >= platform.boundingBox.xLeft)
				return platform.boundingBox.xLeft-this.width;
		} else {
			if(this.boundingBox.xLeft >= platform.boundingBox.xRight && this.nextBoundingBox.xLeft <= platform.boundingBox.xRight)
				return platform.boundingBox.xRight;
		}
	}

	return false;
};

Player.prototype.isMoving = function() {
	return this.game.inputHandler.isPressed(this.game.keyConfig.key_right) ||
			this.game.inputHandler.isPressed(this.game.keyConfig.key_left);
};

Player.prototype.render = function(context) {
	var player = this;
	context.shadowBlur = Math.abs(player.velocity.x);
	context.shadowOffsetX = -player.velocity.x/1.5;
	context.shadowOffsetY = -player.velocity.y;

	context.shadowColor = "rgba(0,0,0,0.8)";
	context.drawImage(player.playerTexture, player.position.x-player.game.level.scrolling.x, player.position.y-player.game.level.scrolling.y);
	context.shadowColor = "transparent";
	if(this.game.debug) {
		context.fillStyle = "rgba(0,255,0,0.5)";
		var platforms = this.game.level.getCollidingPlatformList(this.nextBoundingBox);
		for(var i in platforms) {
			var platform = this.game.level.platforms[platforms[i]];
			context.fillRect(platform.position.x-this.game.level.scrolling.x, platform.position.y-this.game.level.scrolling.y, platform.width, platform.height);
		}
	}
};