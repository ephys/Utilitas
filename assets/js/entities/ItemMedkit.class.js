// =====================================================
//  Class ItemMedkit
//  Refill healthbar
// =====================================================

var ItemMedkit = function(position, energy) {
	Item.apply(this, [position.x, position.y]);
	this.maxEnergy = energy;
	this.energy = energy;
	this.isRenderable = true;
};

ItemMedkit.prototype = new Item();

ItemMedkit.prototype.render = function(ctx) {
	var formerCtx = {
		shadowColor: ctx.shadowColor,
		shadowBlur: ctx.shadowBlur,
		fillStyle: ctx.fillStyle
	}

	ctx.fillStyle = '#000000';
	ctx.shadowColor = '#ffffff';

	var tick = (Game.instance.tick >> 2) % 20;

	if(tick <= 10)
		ctx.shadowBlur = tick+1;
	else
		ctx.shadowBlur = 20-tick+1;

	ctx.beginPath();

	var pos = { x: this.position.x - Game.instance.level.scrolling.x, y: this.position.y - Game.instance.level.scrolling.y - 5 }
	ctx.moveTo( pos.x + 18.75, pos.y + 20);
	ctx.bezierCurveTo(pos.x + 18.75, pos.y + 9.25,   pos.x + 17.5,  pos.y + 6.25,   pos.x + 12.5,  pos.y + 6.25);
	ctx.bezierCurveTo(pos.x + 5,   pos.y + 6.25,   pos.x + 5,  pos.y + 15.375, pos.x + 5,  pos.y + 15.675);
	ctx.bezierCurveTo(pos.x + 5,   pos.y + 20,   pos.x + 10,  pos.y + 25.5,  pos.x + 18.75,  pos.y + 30);
	ctx.bezierCurveTo(pos.x + 27.5,  pos.y + 25.5,  pos.x + 32.5, pos.y + 20,   pos.x + 32.5, pos.y + 16.675);
	ctx.bezierCurveTo(pos.x + 32.5,  pos.y + 15.675, pos.x + 32.5, pos.y + 6.25,   pos.x + 25, pos.y + 6.25);
	ctx.bezierCurveTo(pos.x + 21.25, pos.y + 6.25,   pos.x + 17.75,  pos.y + 9.25,   pos.x + 18.75,  pos.y + 10);
	ctx.fill();

	ctx.fillStyle = formerCtx.fillStyle;
	ctx.shadowColor = formerCtx.shadowColor;
	ctx.shadowBlur = formerCtx.shadowBlur;
}

ItemMedkit.prototype.onTouch = function(player) {
	if(this.energy > 0 && player.health < player.maxHealth) {
		player.health++;
		this.energy--;
		this.opacity = this.energy/this.maxEnergy;
	}

	Item.prototype.onUse.apply(this, arguments);
};