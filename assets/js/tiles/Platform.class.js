/* PLATFORM CLASS */

var Platform = function(posX, posY, width, height, material, solidity, hasShadow) {
	var self = this;
	var level = Game.instance.level;

	this.hasShadow = (hasShadow === undefined)?false:hasShadow;
	this.material = material;
	this.solidity = solidity;
	this.width = width*32;
	this.height = height*32;

	this.position = { x: level.snapToGrid(posX*32), y:  level.snapToGrid(Game.instance.canvas.height-(posY*32)-(this.height-32)) };

	this.boundingBox = {
		yTop: this.position.y+this.height,
		yBottom: this.position.y,
		xLeft: this.position.x,
		xRight: this.position.x+this.width
	};

	this.id = Platform.count++;

	this.toFileFormat = function() {
		return {
			hasShadow: self.hasShadow,
			solidity: self.solidity,
			material: self.material,
			width: self.width,
			height: self.height,
			position: self.position,
			id: self.id,
			type: Platform.types.Platform
		};
	};
};

Platform.types = {
	0: Platform,
	Platform: 0
};

Platform.count = 0;
Platform.solid = 0;
Platform.semiSolid = 1;
Platform.nonSolid = 2;