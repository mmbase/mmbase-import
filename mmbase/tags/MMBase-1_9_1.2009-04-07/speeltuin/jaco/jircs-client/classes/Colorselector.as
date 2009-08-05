/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Colorselector
{
	var channel_win:Channel_window;
	var priv:Private;
	
	public function Colorselector(channel_win:Channel_window, priv:Private)
	{
		this.show();
		if(channel_win != null)
		{
			this.channel_win = channel_win;
			this.priv = null;
		}
		else	
		{
			if(priv != null)
			{
				this.priv = priv;
				this.channel_win = null;
			}		
		}
	}
	
	public function show() {
		if(channel_win)
			_root.chat.channel_window.colorselector._visible = true;
		else
		{
			if(priv)
				_root[priv.privated].colorselector._visible = true;
		}
	}

	public function hide() {
		if(channel_win)
			_root.chat.channel_window.colorselector._visible = false;
		else
		{
			if(priv)
				_root[priv.privated].colorselector._visible = false;
		}
	}

	public function switchColor(color) {
		if(channel_win)		
			channel_win.setButtonColor(color);
		else
		{
			if(priv)
				priv.setButtonColor(color)
		}
		this.hide();
	}
}