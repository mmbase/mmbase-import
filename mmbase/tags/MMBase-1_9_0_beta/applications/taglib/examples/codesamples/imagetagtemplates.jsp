<%@page  pageEncoding="UTF-8" %>
<p>Resize   <mm:image template="s(100x100>)" mode="img" /></p>
<p>Rotate   <mm:image template="s(100x100!)+r(90)" mode="img" />
            <mm:image template="s(100x100!)+r(-45)" mode="img" />
</p>
<p>Contrast low: <mm:image template="s(200)+lowcontrast" mode="img"  /> 
            high: <mm:image template="s(200)+highcontrast" mode="img" /></p>
<p>Text
<mm:write value="hello">
  <mm:image template="s(300x100!)+font(Times-New-Roman-Bold-Italic)
                      +fill(red)+pointsize(20)+gravity(East)+text(0,0,'MM $_ €ĉé $euro Base')" mode="img" />
</mm:write>

<p>Borders, modulation <mm:image template="s(100x200!)+modulate(120,0)+gamma(1/1/2)+bordercolor(8c9c23)+border(10x0)" mode="img" /></p>
<p>Drawing <mm:image template="s(200)+fill(ffffff)+circle(20,20 30,30)" mode="img" /></p>
<p>Cutting <mm:image template="s(200x200!)+part(100,100,150,150)" mode="img" /></p>
<p>More trickery <mm:image template="f(png)+s(200)+fill(ffffff)+draw(rectangle 100,100 150,150)+dia+flipx+transparent(black)" mode="img" />
 <mm:image template="s(200)+colorizehex(f01010)" mode="img" />
</p>
