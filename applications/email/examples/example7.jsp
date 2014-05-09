<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>E-mail attachments</title>
    <style type="text/css" media="screen">
      /* <![CDATA[ */
body { font-family: "Lucida Grande", Arial, sans-serif; font-size: 0.8em; }
label { font-weight: bold; }
fieldset { width: 360px; }
/* ]]> */
</style>
    <script type="text/javascript" language="javascript">
      // <![CDATA[
/* Toggle a div's visibility */
function toggle(targetId){
  if (document.getElementById){
        target = document.getElementById(targetId);
            if (target.style.display == "none"){
                target.style.display = "";
            } else {
                target.style.display = "none";
            }
    }
}
// ]]>
</script>
  </head>
  <!--
      TODO, This is no go, we cannot except letting this work via files.
  -->
  <body>
    <h2>E-mail attachments</h2>
    <p>Please change the following settings in the file 'example7_process.jsp':</p>
    <ul>
      <li>webappDir<br />
        Should be the location of your webapp.</li>
      <li>uploadDir<br />
        Should be a directory in your webapp in which your app server can write files.</li>
      <li>E-mail address to send mail to<br />
        Or Daniel gets all your precious files ;-)</li>
    </ul>
    <p>Notes:</p>
    <ul>
      <li>Using Commons Fileupload has some weird quirks you need to remember
        when using it for your own pages. I do not know if they are native to Commons
        Fileupload but just in case: your &lt;input type="file" /&gt; must start at 1 not 0.
        So it needs to be &lt;input type="file" name="file1" /&gt;.</li>
      <li>For some reason you can not submit to the same JSP, you must submit to
        a second page. That is why there is 'example7_process.jsp'.</li>
      <li>When you need to import variables in your page, f.e. with &lt;mm:import /&gt;,
        be sure to these after all the Commons Fileupload code.</li>
    </ul>

    <form action="example7_process.jsp" method="post" enctype="multipart/form-data">
      <fieldset>
        <label for="name">Name</label><br />
        <input name="name" id="name" type="text" size="42" maxlength="255" tabindex="1" accesskey="1" value="" /><br />
        <label for="email">E-mail</label><br />
        <input name="email" id="email" type="text" size="42" maxlength="255" tabindex="2" accesskey="2" value="" /><br />
        <label for="subject">Subject</label><br />
        <input name="subject" id="subject" type="text" size="42" maxlength="255" tabindex="3" accesskey="3" value="" /><br />
        <label for="message">Message</label><br />
        <textarea id="message" name="message" rows="5" cols="42" tabindex="4"
                  accesskey="4"></textarea>
        <br />

        <label for="file1">File(s)</label><br />
        Click <img src="<mm:url page="/mmbase/edit/my_editors/img/mmbase-new.gif" />" alt="add" width="21" height="20" /> to add more files.
        <div id="f1">
          <input name="file1" type="file" tabindex="6" />
          <a href="#" onclick="toggle('f2');return false;"><img src="<mm:url page="/mmbase/edit/my_editors/img/mmbase-new.gif" />" alt="add" width="21" height="20" /></a>
        </div>
        <div id="f2" style="display: none;">
          <input name="file2" type="file" tabindex="7" />
          <a href="#" onclick="toggle('f3');return false;"><img src="<mm:url page="/mmbase/edit/my_editors/img/mmbase-new.gif" />" alt="add" width="21" height="20" /></a>
        </div>
        <div id="f3" style="display: none;">
          <input name="file3" type="file" tabindex="8" />
          <a href="#" onclick="toggle('f4');return false;"><img src="<mm:url page="/mmbase/edit/my_editors/img/mmbase-new.gif" />" alt="add" width="21" height="20" /></a>
        </div>
        <div id="f4" style="display: none;">
          <input name="file4" type="file" tabindex="9" />
          <a href="#" onclick="toggle('f5');return false;"><img src="<mm:url page="/mmbase/edit/my_editors/img/mmbase-new.gif" />" alt="add" width="21" height="20" /></a>
        </div>
        <div id="f5" style="display: none;" >
          <input name="file5" type="file" tabindex="10" />
        </div>
        <input name="action" id="action" type="submit" value="Send" tabindex="11" accesskey="7" />
      </fieldset>
    </form>
  </body>
</html>
