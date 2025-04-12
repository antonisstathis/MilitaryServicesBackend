package com.militaryservices.app.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class SanitizationUtil {

    // Define the most restrictive sanitization policy
    private static final PolicyFactory RESTRICTIVE_POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "p", "u", "strong", "em")  // Only allow basic formatting tags
            .toFactory();

    // Method to sanitize any user input
    public static String sanitize(String input) {
        return RESTRICTIVE_POLICY.sanitize(input);  // Apply the restrictive policy
    }

}
