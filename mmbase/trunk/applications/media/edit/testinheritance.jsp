<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
      <html>
        <head>
          <title>Test</title>
        </head>
        <body>
        <mm:import id="language">nl</mm:import>
        <mm:locale language="$language">
        <mm:cloud logon="admin" pwd="admin2k">
       <hr />
        <mm:list path="mediafragments,mediasources">
          <mm:node element="mediafragments">
               <mm:nodeinfo type="gui" />
          </mm:node>
          <mm:node element="mediasources">
               <mm:field name="number" /><mm:nodeinfo type="guinodemanager" /> 
               <mm:field name="url" />
          </mm:node>
           <br />
         </mm:list> 
         <mm:listnodes type="mediafragments">
               <mm:field name="gui()" />
         </mm:listnodes>

         <hr />

         <mm:listnodes type="news">
            <mm:field name="title" />
            <mm:relatednodes role="posrel" type="images"><img src="<mm:image />" /></mm:relatednodes>
            <br />
         </mm:listnodes>

         <hr />

         <mm:list path="news,posrel,images" orderby="news.title,posrel.pos" fields="news.title,posrel.pos">
           <mm:node element="news">
            <mm:field name="title" />
           </mm:node>
           <mm:field name="posrel.pos" />
           <mm:node element="images">
             <img src="<mm:image />" />
              </mm:node><br />
         </mm:list>


         <hr />

         <mm:list path="news,object">
           <mm:node element="news">
            <mm:field name="title" />
           </mm:node> | 
           <mm:node element="object">
              <mm:field name="gui()" />
            </mm:node><br />
         </mm:list>
         </mm:cloud>
         </mm:locale>
        </body>
      </html>
