// =====================================================
//  Class SpawnPoint
// =====================================================

var SpawnPoint = function(position) {
	Entity.apply(this, [position.x, position.y, 1, 1]);
	this.isRenderable = true;
};

SpawnPoint.prototype = new Entity();

SpawnPoint.prototype.render = function(context) {
	if(!Game.instance.debug)
		return;

	context.fillRect(this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y, 32, 32);
	context.fillText('S', this.position.x-this.level.scrolling.x, this.position.y-this.level.scrolling.y);

};