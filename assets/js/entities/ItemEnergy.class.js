// =====================================================
//  Class ItemEnergy
//  Refill boost bar
// =====================================================

var ItemEnergy = function(position, energy) {
	Item.apply(this, [position.x, position.y]);
	this.maxEnergy = energy;
	this.energy = energy;
	this.isRenderable = true;
};

ItemEnergy.prototype = new Item();

ItemEnergy.prototype.onTouch = function(player) {
	if(this.energy !== 0 && player.boost < player.maxBoost) {
		player.boost++;
		this.energy--;
		this.opacity = this.energy/this.maxEnergy;
	}

	Item.prototype.onUse.apply(this, arguments);
};

ItemEnergy.prototype.render = function(ctx) {
	var drawStar = function(context, xCenter, yCenter, nPoints, outerRadius, innerRadius) {
		context.beginPath();
		for (var ixVertex = 0; ixVertex <= 2 * nPoints; ++ixVertex) {
			var angle = ixVertex * Math.PI / nPoints - Math.PI / 2;
			var radius = ixVertex % 2 == 0 ? outerRadius : innerRadius;
			context.lineTo(xCenter + radius * Math.cos(angle), yCenter + radius * Math.sin(angle));
        }
        context.fill();
    }

	var formerCtx = {
		shadowColor: ctx.shadowColor,
		shadowBlur: ctx.shadowBlur,
		fillStyle: ctx.fillStyle,
		globalAlpha: ctx.globalAlpha
	}

	ctx.fillStyle = '#fa6800';
	ctx.shadowColor = '#f0a30a';
	ctx.globalAlpha = this.opacity;

	var tick = (Game.instance.tick >> 2) % 20;

	if(tick <= 10)
		ctx.shadowBlur = tick+1;
	else
		ctx.shadowBlur = 20-tick+1;

    drawStar(ctx, this.position.x - Game.instance.level.scrolling.x, this.position.y - Game.instance.level.scrolling.y + 10, 5, 15, 8);

	ctx.fillStyle = formerCtx.fillStyle;
	ctx.shadowColor = formerCtx.shadowColor;
	ctx.shadowBlur = formerCtx.shadowBlur;
	ctx.globalAlpha = formerCtx.globalAlpha;
}