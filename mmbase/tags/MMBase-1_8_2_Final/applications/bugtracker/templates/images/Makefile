SOURCE=arrow.fig
SOURCE2=arrow.png
SCALE=-transparent white -scale 10x10
FORMAT=png
all:
	fig2dev -L png -b 0 arrow.fig arrow.png
	convert $(SCALE) $(SOURCE2) arrow-right.$(FORMAT)
	convert -rotate 90   $(SCALE) $(SOURCE2) arrow-bottom.$(FORMAT)
	convert -rotate 180   $(SCALE) $(SOURCE2) arrow-left.$(FORMAT)
	convert -rotate 270   $(SCALE) $(SOURCE2) arrow-top.$(FORMAT)
