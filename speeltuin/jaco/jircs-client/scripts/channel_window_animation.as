/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

onClipEvent(enterFrame) {
	if (_root.chatbox.channel_win.showWindow) {
		if (_root.chat.channel_window._alpha < 100) {
			_root.chat.channel_window._alpha += 10;
		}
		if (_root.chat.channel_window._alpha == 100) {
			_root.chatbox.channel_win.stop();
			Selection.setFocus("_root.channel_window.dialog_window.myInput");
		}
	}
	
	if (_root.chatbox.channel_win.hideWindow) {
		if (_root.chat.channel_window._alpha > 0) {
			_root.chat.channel_window._alpha -= 10;
		}
		if (_root.chat.channel_window._alpha <= 0) {
			_root.chat.channel_window._visible = false;
			if (_root.chatbox.channel_win.showHide) {
				_root.chatbox.channel_win.showHide = false;
				_root.chatbox.channel_win.show();			
			} else {
				_root.chatbox.channel_win.stop();	
			}
		}
	}
}