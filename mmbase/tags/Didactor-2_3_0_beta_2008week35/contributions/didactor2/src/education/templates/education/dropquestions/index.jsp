<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.List, java.util.Iterator, java.util.ArrayList, java.util.Collections"%>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="question" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<script type="text/javascript">

var dropnumbers;

var getXPos;

var getYPos;

var dragImage;

var offsetX =-1;

var offsetY =-1;

function getXPos(evt) {

    if (window.event) {

	return event.x+document.body.scrollLeft;

    }

    return evt.pageX;

}



function getYPos(evt) {

    if (window.event) { 

	return event.y+document.body.scrollTop;

    }

    return evt.pageY;

    

}

function endDrag() {

    document.onmousemove=null;

    if (dragImage) {

	var pos = Math.round(dragImage.style.top.replace(/px/,'')/ 200);

	if (pos > dragImage.max) {

	    pos = dragImage.max;

	}

	dragImage.style.zIndex=2;

	if (dragImage.style.left.replace(/px/,"") < 100) {

	    dragImage.style.left = "0px";

	    dragImage.style.top = (pos*200)+"px";

	    document.forms[0].elements['drop'+dragImage.questionnumber+"."+dragImage.imagenumber].value = 0;

//	    alert('drop'+dragImage.questionnumber+"."+dragImage.imagenumber+"="+0);

	}

	else {

	    dragImage.style.left = "266px";

	    dragImage.style.top = (33+pos*200)+"px";

	    document.forms[0].elements['drop'+dragImage.questionnumber+"."+dragImage.imagenumber].value =

		dropnumbers[""+dragImage.questionnumber][pos];

//	    alert('drop'+dragImage.questionnumber+"."+dragImage.imagenumber+"="+dropnumbers[""+dragImage.questionnumber][pos]);

	}

    }

    dragImage = null;

    offsetX=-1;

    offsetY=-1;



//    window.status="dropped";

}



function doDrag( evt ) {

    var x = getXPos(evt);

    var y = getYPos(evt);

    if (offsetX == -1 && offsetY == -1) {

	offsetX = dragImage.style.left.replace(/px/,"")-x;

	offsetY = dragImage.style.top.replace(/px/,"")-y;

	dragImage.style.zIndex=3;

    }

    var nx = x + offsetX;

    var ny = y + offsetY;

//    window.status="dragging ("+x+","+y+") - ("+nx+","+ny+") - "+dragImage.style.top+" "+dragImage.style.left;

    dragImage.style.top = ny+"px";

    dragImage.style.left= nx+"px";

}



function startDrag( image, qnum,inum, max ) {

    dragImage = image;

    dragImage.questionnumber =qnum;

    dragImage.imagenumber=inum;

    dragImage.max = max;

    if (dragImage.focus) {

        dragImage.focus();

    }

    document.onmousemove=doDrag;

    document.onmouseup=endDrag;

//    window.status="Start dragging";

    return false;

}

window.onmouseup=endDrag;



if (!dropnumbers) {

    dropnumbers = new Object();

}

dropnumbers['<mm:write referid="question"/>'] = new Array();





</script>

<mm:node number="$question">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <p/>

  <mm:field name="text" escape="none"/>

  <p/>


  <mm:import id="numdragquestions" jspvar="numDragQuestions" vartype="Integer"><mm:countrelations role="dragimagerel"/></mm:import>

  <mm:import id="numdropquestions" jspvar="numDropQuestions" vartype="Integer"><mm:countrelations role="dropimagerel"/></mm:import>

  



  <% 

    int totalBoxHeight = numDragQuestions.intValue() > numDropQuestions.intValue() ?  numDragQuestions.intValue() : numDropQuestions.intValue();

  %>

  <div class="images" style="position: relative; width:450px; height: <%= totalBoxHeight * 200 %>px">





  <%  int dragCounter = 0; %>

  <mm:list nodes="$question" path="dropquestions,dragimagerel,images" fields="images.number" orderby="dragimagerel.pos" distinct="true">

    <mm:import id="image" reset="true"><mm:field name="images.number"/></mm:import>

	<mm:node referid="image">

	    <div

		style="position: absolute; top: <%= 200 * dragCounter++ %>px; left: 0px; width: 200px; height: 200px;" 

		title="<mm:field name="title"/>"
    
     alt="<mm:field name="title"/>" 

		class="dragimage"

		onmousedown="startDrag(this,<mm:write referid="question"/>,<%= dragCounter %>,<%= totalBoxHeight %>);"

		

	    />

	    <%= dragCounter %>   
            <mm:field name="showtitle">
              <mm:compare value="1">
                - <mm:field name="title"/>
              </mm:compare>
            </mm:field> 
            <br>

	    <img src="<mm:image template="s(150x150)"/>" title="<mm:field name="title"/>" alt="<mm:field name="title"/>">

	    <br/>

	    <script type="text/javascript">

		document.write('<input type="hidden" name="drop<mm:write referid="question"/>.<%= dragCounter %>" value="0">');

	    </script>

	    <noscript>

		<select name="drop<mm:write referid="question"/>.<%= dragCounter %>">

		  <option value="0">----</option>

		<% int cnt = 1; %>

		<mm:list nodes="$question" path="dropquestions,dropimagerel,images" fields="images.number" orderby="dropimagerel.pos" distinct="true">

		    <mm:import id="imnum" reset="true"><mm:field name="images.number"/></mm:import>

		    <option value="<%= cnt %>"><%= cnt++ %><mm:node number="$imnum"
                                                             ><mm:field name="showtitle"
                                                               ><mm:compare value="1"
                                                                 >- <mm:field name="title"
                                                               /></mm:compare
                                                             ></mm:field
                                                           ></mm:node>

		</mm:list>

		</select>

	    </noscript>

	    </div>

        </mm:node>

    </mm:list>

	

	<% int dropCounter = 0; %>

	<mm:list nodes="$question" path="dropquestions,dropimagerel,images" fields="images.number" orderby="dropimagerel.pos" distinct="true">

	<mm:import id="image" reset="true"><mm:field name="images.number"/></mm:import>

	<mm:node referid="image">

	    <div 

		style="position: absolute; top: <%= 200 * dropCounter++ %>px; left: 300px;" 

		class="dropimage"

	    />

	    <%= dropCounter %>   
            <mm:field name="showtitle">
              <mm:compare value="1">
                - <mm:field name="title"/>
              </mm:compare>
            </mm:field> 
            <br>

	    <img src="<mm:image template="s(150x150)"/>" title="<mm:field name="title"/>" alt="<mm:field name="title"/>" >

	    </div>

	    <script type="text/javascript">

		dropnumbers['<mm:write referid="question"/>'].push(<%= dropCounter %>);

	    </script>

        </mm:node>

	</mm:list>

    </div>

  

	

</mm:node>

</mm:cloud>

</mm:content>

