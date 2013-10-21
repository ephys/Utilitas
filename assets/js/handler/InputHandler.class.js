/* INPUT HANDLER CLASS */
var InputHandler = function(game) {
	this.keys = {};
	this.mouse = {};
	this.mouseHold = {};
	this.mousePos = {x:0,y:0};
	this.game = game;
	this.lastPressed = null;

	var self = this;
	var keyDownListener = function (e) {
		self.keys[e.keyCode] = true;
		self.mouse = [];
		self.lastPressed = e.keyCode;

		e.preventDefault();
	};

	var keyUpListener = function (e) {
		self.keys[e.keyCode] = false;
		self.mouse = [];
		e.preventDefault();
	};

	var mouseDownListener = function (e) {
		self.mouse[e.which] = true;
		self.mouseHold[e.which] = true;
		e.preventDefault();
	};

	var mouseUpListener = function (e) {
		self.mouseHold[e.which] = false;
		e.preventDefault();
	};

	var mouseMoveListener = function (e) {
		self.mouse = {};
		self.mousePos.x = e.x;
		self.mousePos.y = e.y;
		e.preventDefault();
	};

	document.body.addEventListener("keydown", keyDownListener, false);
	document.body.addEventListener("keyup", keyUpListener, false);
	document.body.addEventListener("mousedown", mouseDownListener, false);
	document.body.addEventListener("mouseup", mouseUpListener, false);
	document.body.addEventListener("mousemove", mouseMoveListener, false);

	this.clearListeners = function() {
		document.body.removeEventListener("keydown", keyDownListener, false);
		document.body.removeEventListener("keyup", keyUpListener, false);
		document.body.removeEventListener("mousedown", mouseDownListener, false);
		document.body.removeEventListener("mouseup", mouseUpListener, false);
		document.body.removeEventListener("mousemove", mouseMoveListener, false);
	};
};

InputHandler.prototype.isPressed = function(key) {
	return (this.keys[key.getCode()] !== undefined) && (this.keys[key.getCode()]);
};

InputHandler.prototype.isActivated = function(key, debug) {
	if((this.keys[key.getCode()] !== undefined) && (this.keys[key.getCode()])) {
		this.keys[key.getCode()] = false;
		return true;
	}

	return false;
};

InputHandler.prototype.isLeftClicking = function(key) {
	return (this.mouseHold[1] !== undefined) && (this.mouseHold[1]);
};

InputHandler.prototype.hasLeftClicked = function(key) {
	if(this.mouseHold[1] === false && this.mouse[1] === true) {
		this.mouse[1] = false;
		return true;
	}
};