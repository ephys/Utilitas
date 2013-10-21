// =====================================================
//  Class Note
// =====================================================

var Note = function(position, message) {
	var context = Game.instance.renderer.context;
	context.font = "bold 20px monospace";
	this.message = {};
	this.message.text = message;
	this.message.width = context.measureText(message).width;
	this.message.height = 26;

	Entity.apply(this, [position.x, position.y, this.message.width, this.message.height]);
	this.isRenderable = true;
};

Note.prototype = new Entity();

Note.prototype.render = function(context) {
	context.fillStyle = "rgba(0,0,0,0.5)";
	context.fillRect(this.position.x-this.level.scrolling.x-7, this.position.y-this.level.scrolling.y+5, this.message.width+15, this.message.height+6);
	context.fillStyle = "#ffffff";
	context.fillText(this.message.text, this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y+this.message.height);
};