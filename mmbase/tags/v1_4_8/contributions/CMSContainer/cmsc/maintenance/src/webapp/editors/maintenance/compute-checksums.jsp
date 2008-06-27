<%@page language="java" contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@include file="../globals.jsp" %>
<mm:cloud loginpage="../login.jsp" rank="basic user">
   <html>
      <head>
         <title>Compute checksum fields</title>
         <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
         <link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
         <link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
         <link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
      </head>
      <body>
  <div class="editor">
    <div class="body">
         <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp" rank="administrator">
            <mm:import externid="doProcess" />
            <mm:notpresent referid="doProcess">
               <h1>Compute checksum fields</h1>
               <p>This maintenance page computes the checksum fields for binary types (attachments and images). Note that this operation may take a very long time!</p>
               <p>When the process stalls, it is likely a deadlock situation is created by Luceus. In order to solve this you should:</p>
               <ol>
                  <li>Stop the webserver</li>
                  <li>Set the status of Luceus to <b>inactive</b></li>
                  <li>Start the webserver and run the script again</li>
                  <li>Stop the webserver once more and re-enable Luceus</li>
                  <li>Start the webserver</li>
               </ol>
               <form method="post">
                  <input type="hidden" name="doProcess" value="yes" />
                  <input type="submit" value="Start" />
               </form>
            </mm:notpresent>
            <mm:present referid="doProcess">
	           <% long tic = System.currentTimeMillis(); %>
	           <h1>Processing attachments</h1>
               <table>
                  <thead>
                     <tr>
                        <th>Node number</th>
                        <th>Checksum</th>
                     </tr>
                  </thead>
                  <tbody>
                     <mm:listnodes type="attachments">
                        <tr>
                           <td><mm:field name="number"/></td>
                           <td><mm:field name="checksum"/></td>
                        </tr>
                     </mm:listnodes>
                  </tbody>
               </table>
	           <% long toc = System.currentTimeMillis(); %>
	           <p>Checksum computed for all attachments in <%= (toc-tic) %>ms.</p>
 	           <% tic = System.currentTimeMillis(); %>
	           <h1>Processing images</h1>
               <table>
                  <thead>
                     <tr>
                        <th>Node number</th>
                        <th>Checksum</th>
                     </tr>
                  </thead>
                  <tbody>
                     <mm:listnodes type="attachments">
                        <tr>
                           <td><mm:field name="number"/></td>
                           <td><mm:field name="checksum"/></td>
                        </tr>
                     </mm:listnodes>
                  </tbody>
               </table>
	           <% toc = System.currentTimeMillis(); %>
	           <p>Checksum computed for all images in <%= (toc-tic) %>ms.</p>
            </mm:present>
         </mm:cloud>
      </body>
</div></div>
   </html>
</mm:cloud>