//*****
//global vars
  
  //deze variabel wordt bij body.onLoad gezet.
  var isEditor=false;


//******************************************
//callback functies

 function selectLeaf(title, code) {
      //dit is de callback functie voor als er op een vraag wordt geklikt
      //roep pagina aan zodat vraag wordt getoond
      document.location="index.jsp?node="+currentFolder.getAttribute("node")+"&qnode="+code+"&expanded="+getExpandedFolders();
  }
  
  
  function selectFolder(node) {
    //dit is de callback functie voor als er op een folder wordt geklikt
    //om de complete staat van de tree bij te kunnen houden
    // en om de current node een andere image te geven
    if(node.getAttribute("expanded")=="true"){
      node.setAttribute("expanded","false");
    }else{
      node.setAttribute("expanded","true");
    }
    
    //en nu de image voor de current node
    //eerst bepalen of de hidden varianten van de images getoond moeten worden

    var hidden="";
    if(currentFolder!=null){
      if(isEditor && currentFolder.getAttribute("visible")=="false") hidden="hidden";
      getImageNode(currentFolder).setAttribute("src",IMGDIR+"folder"+hidden+".gif");
    }
    currentFolder=node;
    hidden="";
    if(isEditor && currentFolder.getAttribute("visible")=="false") hidden="hidden";
    getImageNode(currentFolder).setAttribute("src",IMGDIR+"currentFolder"+hidden+".gif");
  
    
    //en nu nog even de toolbar weer aanzetten (mocht die nog uitstaan
    try{
      document.getElementById("toolbar").style.display="block";
    }catch(error){
      //do nothing 
    }
  }

//einde callbacks
//*********************************************


/*****************************************************************************
Name : getExpandedFolders
Parameters  :  none
Returns     :  String with comma-seperated nodenumbers of currently opened folders
Description : is used to create the 'expanded' parameter for url's in various places
Author : Ernst Bunders
*****************************************************************************/
function getExpandedFolders(){
      var folders=document.getElementsByTagName("a");
      var foldersToString=","
      for (i=0;i<folders.length;i++){
        var element=folders[i];
        if( element.getAttribute("expanded")=="true"){
          foldersToString=foldersToString+element.getAttribute("node")+",";
        }
      }
      return foldersToString;
}
/*****************************************************************************
Name : setCurrentFolder
Parameters :  String number, containing number of node (DOM node of <a> tag)
Description : is being called by 'body.onload' event and wil init the currentFolder var
Author : Ernst Bunders
*****************************************************************************/
	function setCurrentFolder(number){
    //alert("currentFolder wordt gezet: "+number);
    currentFolder=findNode(number);
    getImageNode(currentFolder).setAttribute("src",IMGDIR+CURRENTFOLDERIMAGE);
    
    //en nu nog even de toolbar weer aanzetten. de catch is voor ed edit en add
    //pagina's die geen toolbar hebben
    try{
      document.getElementById("toolbar").style.display="block";
    }catch(hoi){}
  }
  
/*****************************************************************************
Name : setEditor
Parameters :  String  containing true or false based on the user being logged in or not
Description : is being called by 'body.onload' event to make visible to the clientside script if a user is
logged in. if so, the hidden folders and questions will have to be displayed with different icons,
i.e. the same, but with names like [iconname]hidden.gif
Author : Ernst Bunders
*****************************************************************************/
  function setEditor(loginStatus){
    isEditor=loginStatus  ; 
  }

	function setCurrentFolder(number){
    //alert("currentFolder wordt gezet: "+number);
    currentFolder=findNode(number);
    getImageNode(currentFolder).setAttribute("src",IMGDIR+CURRENTFOLDERIMAGE);
    
    //en nu nog even de toolbar weer aanzetten. de catch is voor ed edit en add
    //pagina's die geen toolbar hebben
    try{
      document.getElementById("toolbar").style.display="block";
    }catch(hoi){}
  }  
  
/*****************************************************************************
Name : findNode
Parameters  :  number String containing nodeNumber
Returns     :  DOM node of type '<a>' representing folder
Description :  will iterate through all the <a> DOM elements to find one with 'node' attribute matching 'number' param
Author : Ernst Bunders
*****************************************************************************/   
  function findNode(number){
      var folders=document.getElementsByTagName("A");
      for (i=0;i<folders.length;i++){
        var element=folders[i];
        if(element.getAttribute("node")==number){
          var result=element;
        }
      }
      return result;
  }
