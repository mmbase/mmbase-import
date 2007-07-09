  function getDataTypes() { 
    var datatypes = document.getElementsByTagName("select");
    for (var i = 0; i < datatypes.length; i++) {
    if (datatypes[i].className == "datatype") {
      datatypes[i].name = "datatype_off";
      datatypes[i].style.display = "none";
      }
    }
    var descriptions = document.getElementsByTagName("span");
    for (var i = 0; i < descriptions.length; i++) {
    if (descriptions[i].className == "description") {
      descriptions[i].style.display = "none";
      }
    }
    var selectedDataTypeId = document.getElementById("mmbasetype").value;
    document.getElementById("description_" + selectedDataTypeId).style.display = "block";
    var length = document.getElementById("haslength_" + selectedDataTypeId);
    var sizeEl = document.getElementById("dbsize");
    if (sizeEl) sizeEl.style.visibility = length ? "visible" : "hidden";
    var selectedDataType = document.getElementById("datatype_" + selectedDataTypeId);
    selectedDataType.name = "datatype";
    selectedDataType.style.display = "block";
    selectedSpec = selectedDataType.value;
    if (selectedSpec != "") {
      document.getElementById("description_" + selectedSpec).style.display = "block";
    }


}