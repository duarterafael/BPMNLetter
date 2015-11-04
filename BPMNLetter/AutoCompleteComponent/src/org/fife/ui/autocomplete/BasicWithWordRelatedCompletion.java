package org.fife.ui.autocomplete;

import java.util.List;

public class BasicWithWordRelatedCompletion extends BasicCompletion{
	
	public List<String> listRelatedWords;

	public BasicWithWordRelatedCompletion(CompletionProvider provider,
			String replacementText, List<String> listRelatedWords) {
		super(provider, replacementText);
		this.listRelatedWords = listRelatedWords;
	}

	
}
