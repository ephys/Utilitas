// =====================================================
//  Class ExitDoor
//  The exit of the level
// =====================================================

var ExitDoor = function(position) {
	Entity.apply(this, [position.x, position.y, 128, 128]);
	this.isRenderable = true;
};

ExitDoor.prototype = new Entity();

ExitDoor.prototype.onUse = function(player) {
	console.log("win !");
};

ExitDoor.prototype.render = function(context) {
	var door_lower = Game.instance.renderer.loadItemSprite('door_lower');
	var door_upper = Game.instance.renderer.loadItemSprite('door_upper');
	context.globalAlpha = 1.0;
	context.shadowBlur = 0;
	context.shadowOffsetX = 0;
	context.shadowOffsetY = 0;
	context.drawImage(door_upper, this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y);
	context.drawImage(door_lower, this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y+64);

	context.scale(-1, 1);
	context.drawImage(door_upper, -(this.position.x-this.level.scrolling.x+128), this.position.y-this.level.scrolling.y);
	context.drawImage(door_lower, -(this.position.x-this.level.scrolling.x+128), this.position.y-this.level.scrolling.y+64);
	context.scale(-1, 1);
};