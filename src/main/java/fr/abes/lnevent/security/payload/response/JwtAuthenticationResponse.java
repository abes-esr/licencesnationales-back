package fr.abes.lnevent.security.payload.response;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String userId;
    private String userSiren;
    private String userRole;


    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, String id, String siren, String isAdmin) {
        this.accessToken = accessToken;
        this.userId = id;
        this.userSiren = siren;
        this.userRole = isAdmin;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public String getTokenType() {
        return tokenType;
    }
    public String getId() {
        return userId;
    }
    public String getSiren() {
        return userSiren;
    }
    public String getIsAdmin() {
        return userRole;
    }

}
