// basic classes
var Entity = function(posX, posY, width, height) {
	if(arguments.length === 0)
		return;

	this.isRenderable = false;
	this.level = Game.instance.level;
	this.width = width;
	this.height = height;
	this.position = { x: this.level.snapToGrid(posX*32), y: this.level.snapToGrid(Game.instance.canvas.height-(posY*32)-(this.height-32)) };

	this.boundingBox = {
		yTop: this.position.y,
		yBottom: this.position.y+this.height,
		xLeft: this.position.x,
		xRight: this.position.x+this.width
	};
};

Entity.prototype.toFileFormat = function() {
	return null;
}

Entity.prototype.onTouch = function(toucher) {
	if(toucher instanceof Player && this.level.game.inputHandler.isPressed(this.level.game.keyConfig.key_activate))
		this.onUse(toucher);
};

Entity.prototype.onUse = function() {};

var LivingEntity = function(posX, posY, width, height) {
	if(arguments.length === 0)
		return;

	Entity.apply(this, [posX, posY, width, height]);

	this.velocity = {x: 0, y:0};
	this.health = 20;
	this.maxHealth = 20;
	this.armor = 1;
};

LivingEntity.getName = function() {
	return 'null';
};

LivingEntity.prototype = new Entity();

var Item = function(posX, posY) {
	if(arguments.length === 0)
		return;

	Entity.apply(this, [posX, posY, 32, 32]);
	this.isRenderable = true;

	this.texture = '';
	this.opacity = 1.0;
};

Item.prototype = new Entity();

Item.prototype.render = function(context) {
	var texture = this.level.game.renderer.loadItemSprite(this.texture);
	var formerAlpha = context.globalAlpha;

	context.globalAlpha = this.opacity;
	context.shadowBlur = 0;
	context.shadowOffsetX = 1;
	context.shadowOffsetY = 0;
	context.shadowColor = "black";
	context.drawImage(texture, this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y);

	context.globalAlpha = formerAlpha;
};