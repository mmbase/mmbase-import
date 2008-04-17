package com.finalist.newsletter.publisher;

import com.finalist.newsletter.domain.Subscription;

public interface Personaliser {
   public String personalise(String rawHtmlContent, Subscription subscription);
}
