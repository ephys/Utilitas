/* RENDERER CLASS */
var Renderer = function() {
	this.canvas = Game.instance.canvas;
	this.context = this.canvas.getContext("2d");

	this.game = Game.instance;

	this.itemSprites = {};

	this.renderList = [];

	this.init();
};

Renderer.prototype.addRenderer = function(renderer) {
	this.renderList.push(renderer);
};

Renderer.prototype.init = function() {
	var e = document.documentElement,
		g = document.getElementsByTagName('body')[0],
		width = window.innerWidth || e.clientWidth || g.clientWidth,
		height = window.innerHeight|| e.clientHeight|| g.clientHeight;

	this.canvas.height = height;
	this.canvas.width = width;

	this.fps = 0;
	this.lastCalledTime = new Date().getTime();
};

Renderer.prototype.loadItemSprite = function(spriteName) {
	if(this.itemSprites[spriteName] === undefined) {
		this.itemSprites[spriteName] = new Image();
		this.itemSprites[spriteName].src = 'assets/img/'+this.game.level.type+'/items/'+spriteName+'.png';
	}

	return this.itemSprites[spriteName];
};

Renderer.prototype.render = function() {
	var i;
	for(i in this.renderList) {
		this.renderList[i].render(this.context);
	}

	this.context.shadowBlur = 5;
	this.context.shadowOffsetX = this.context.shadowOffsetY = 0;

	for(i in this.game.interfaces) {
		if(this.game.interfaces[i] !== undefined)
			this.game.interfaces[i].render(this.context);
	}

	// calculate FPS
	if((new Date().getTime()%30) == 1) {
		var delta = (new Date().getTime() - this.lastCalledTime)/1000;
		this.fps = 1/delta >> 0;
	}
	this.lastCalledTime = new Date().getTime();
};

Renderer.prototype.wipe = function() {
	var canvas = this.context.canvas;
	var goodbye = "Thank you for participating in this experiment,";
	var goodbye_2 = "Goodbye.";

	this.context.clearRect(0, 0, canvas.width, canvas.height);
	this.context.fillStyle = "#ffffff";
	this.context.fillText(goodbye, canvas.width >> 3, (canvas.height >> 1) - 45);
	this.context.fillText(goodbye_2, (canvas.width >> 3) + this.context.measureText(goodbye).width, canvas.height >> 1);
};