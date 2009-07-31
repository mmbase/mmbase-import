<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content  type="image/png" postprocessor="gnuplot">
set output
set term png
plot sin(x)
</mm:content>