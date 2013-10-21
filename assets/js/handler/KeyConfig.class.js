/* KEY CONFIG CLASS */
var Key = function(key_name, default_key) {
	var binded_key = default_key;

	this.getCode = function() {
		return binded_key;
	};

	this.getName = function() {
		return key_name;
	};

	this.set = function(keycode) {
		if(typeof keycode !== 'number')
			binded_key = default_key;
		else
			binded_key = keycode;
	};
};

var KeyConfig = function() {
	this.key_jump = new Key('Jump', 32);
	this.key_left = new Key('Left', 37);
	this.key_right = new Key('Right', 39);
	this.key_activate = new Key('Interact', 38);

	this.key_run = new Key('Run', 16);
	this.key_esc = new Key('Escape', 27);

	this.key_debug = new Key('Debug', 114);
	this.key_debug_pause = new Key('Debug pause', 123);

	var self = this;
	var map = null;
	this.getMap = function() {
		if(map === null) {
			map = [];
			for(var i in self) {
				if(self[i] instanceof Key)
					map.push(self[i]);
			}
		}

		return map;
	};
};

var KeyMap = function() {
	var keyMap = {};
	keyMap[16] = 'Left Shift';

	keyMap[27] = 'Escape';

	keyMap[32] = 'Space';

	keyMap[37] = 'Left Arrow';
	keyMap[38] = 'Up Arrow';
	keyMap[39] = 'Right Arrow';

	keyMap[114] = 'F3';

	keyMap[123] = 'F12';

	this.getKeyName = function(keycode) {
		if(keyMap[keycode] === undefined)
			return keycode;
		else
			return keyMap[keycode];
	};
};