package com.finalist.newsletter.publisher;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;

public interface Personaliser {
   public String personalise(String rawHtmlContent, Subscription subscription, Publication publication);
}
