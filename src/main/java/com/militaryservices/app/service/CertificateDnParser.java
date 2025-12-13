package com.militaryservices.app.service;

public final class CertificateDnParser {

    public static ParsedCertData parse(String dn) {
        String username = null;
        String unitName = null;
        String authority = null;

        for (String part : dn.split(",")) {
            part = part.trim();

            if (part.startsWith("CN=")) {
                username = part.substring(3).trim();
            }

            else if (part.startsWith("OU=")) {
                String ouValue = part.substring(3).trim();

                if (ouValue.contains(" - ")) {
                    String[] ouParts = ouValue.split(" - ", 2);
                    unitName = ouParts[0].trim();

                    authority = ouParts[1]
                            .trim()
                            .toLowerCase()
                            .replaceFirst("^role_", "");
                } else {
                    unitName = ouValue;
                }
            }
        }

        if (username == null || unitName == null || authority == null) {
            throw new IllegalArgumentException("Invalid certificate DN: " + dn);
        }

        return new ParsedCertData(username, unitName, authority);
    }

    /* ==========================
       DTO
    ========================== */
    public static class ParsedCertData {

        private final String username;
        private final String unitName;
        private final String authority;

        public ParsedCertData(String username, String unitName, String authority) {
            this.username = username;
            this.unitName = unitName;
            this.authority = authority;
        }

        public String getUsername() {
            return username;
        }

        public String getUnitName() {
            return unitName;
        }

        public String getAuthority() {
            return authority;
        }
    }
}
