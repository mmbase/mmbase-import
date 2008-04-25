

// Frame busting



if (top != self)

	top.location.replace(self.location.href)



// For an explanation of this code see http://www.xs4all.nl/~ppk/js/index.html?dhtmlnavi.html



var remember = new Array();

var remember2 = new Array();

var checkIt = 0;

var DHTML;



function show(name,lvl,obj)

{

	DHTML = (document.getElementById || document.all || document.layers);

	if (!DHTML) return;

	checkUserInput();

	if (remember[lvl] && remember[lvl] == name) return;

	if (remember[lvl])

	{

		closeAll(lvl);

	}

	if (name)

	{

		var x = getObj(name);

//		if (!document.layers) x = x.style;

		x.visibility = 'visible';

	}

	remember[lvl] = name;

	if (obj.parentNode) y = obj.parentNode;

	else if (obj.parentElement) y = obj.parentElement;

	else return;

	if (obj.tagName == 'A')

	{

		if (y.className) return;
                // && y.className != 'over') return;

		y.className = 'over';

	}

	if (remember2[lvl]) remember2[lvl].className = '';
	//if (remember2[lvl]) remember2[lvl].className = 'normal';

	if (obj.tagName == 'A') remember2[lvl] = y;

}



function closeAll(lvl)

{

	for (i=remember.length - 1;i>=lvl;i--)

	{

		if (remember[i]) //IE4 Mac

		{

			var x = getObj(remember[i]);

			x.visibility = 'hidden';

		}

		remember[i] = null;

		if (remember2[i])

		{

			remember2[i].className = '';
			//remember2[i].className = 'normal';

			remember2[i] = null;

		}

	}



}



function checkUserInput()

{

	if (checkIt) clearTimeout(checkIt);

	checkIt = setTimeout('closeAll(0)',1600);

}



function getObj(name)

{

  if (document.getElementById)

  {

    return document.getElementById(name).style;

  }

  else if (document.all)

  {

    return document.all[name].style;

  }

  else if (document.layers)

  {

    return document.layers[name];

  }

  else return false;

}



