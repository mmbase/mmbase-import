<%@ tag body-content="empty"  %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<mm:cloud method="asis" jspvar="cloud">
    <div id="header">
        <c:set var="username" scope="request"><%= "" + cloud.getUser().getIdentifier() %></c:set>
        <div id="path">&nbsp;</div>
        <h6 style="margin-bottom: 5px;">
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/heart0.png" id="__heartbeat_btn" enabled="false"/>
            ${username}
            <i onclick="alert('Veel plezier met deze redactieomgeving!\nRob Vermeulen & Jerry Den Ambtman\nEn koop een spaarlamp!');">|</i>
            <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/logout.jsp">uitloggen</a>
        </h6>
    </div>
    
    <script type="text/javascript">
        //heartbeat code
        var icon_enabled = "${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/heart1.png";
        var icon_disabled = "${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/heart0.png";
        
        var __heartbeat1;
        var __heartbeat2;
        
        $(function(){
            //#1 mouse over and disabled: light up,
            $("img#__heartbeat_btn").mouseover(function(){
                if( $(this).attr("enabled") == "false"){
                    this.src = icon_enabled;
                }
            });
            $("img#__heartbeat_btn").mouseout(function(){
                if( $(this).attr("enabled") == "false"){
                    this.src = icon_disabled;
                }
            });
            $("img#__heartbeat_btn").click(function(){
                if( $(this).attr("enabled") == "false"){
                    enableHeartbeat();
                }else{
                    disableHeartbeat();
                }
            });  
        });
        
        
        function enableHeartbeat(){
            var button = $("img#__heartbeat_btn");
            button.attr("src",icon_enabled).attr("enabled","true").fadeTo("slow", 0.5);
            __heartbeat1 = setInterval("pulse()", 3000);
            __heartbeat2 = setInterval("refresh()", 60 * 1000);
            refresh("true");
        }
        
        function disableHeartbeat(){
            var button = $("img#__heartbeat_btn");
            button.attr("src",icon_disabled).attr("enabled","false").fadeTo("fast", 1);
            clearInterval(__heartbeat1);
            clearInterval(__heartbeat2);
            refresh("false");
        }
        
        function pulse(){
            var hf = $("form#__heartbeat_frm");
            hf.submit();
            $("img#__heartbeat_btn").fadeTo("slow", 1, function(){
                $(this).fadeTo("slow", 0.5);
            });
        }
        
        function refresh(state){
            $.ajax({
               url: "${pageContext.request.contextPath}/mmbase/vpro-wizards/system/heartbeat.jsp",
               type: "POST",
               data: "enabled=" + (state == undefined ? "" : state)
             });
        }
        
    </script>
    <c:if test="${sessionScope.__heartbeat == 'true'}">
        <script language="javascript">enableHeartbeat();</script>
    </c:if>
    
</mm:cloud>
