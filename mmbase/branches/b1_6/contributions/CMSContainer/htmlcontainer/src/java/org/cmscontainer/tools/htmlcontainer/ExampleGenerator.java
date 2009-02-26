package org.cmscontainer.tools.htmlcontainer;

import java.util.Set;

public class ExampleGenerator {

	private HtmlContainer container;
	
	public ExampleGenerator(HtmlContainer container) {
		this.container = container;
	}

	public String generateRandom() {
		Layout layout = container.getLayout(MathUtil.random(container.getLayoutCount()));
		Set<String> positions = layout.getPositions();
		
		Page page = new Page(layout.getHtml(), container);
		for(String position:positions) {
			if(layout.isRequired(position)) {
				page.addView(position, layout.getFirstView(position));
			}
			else {
				String[] views = layout.getViews(position);
				if(views.length > 0) {
					page.addView(position, views[MathUtil.random(views.length)]);
				}
			}
		}
		
		return page.build();
	}

}
