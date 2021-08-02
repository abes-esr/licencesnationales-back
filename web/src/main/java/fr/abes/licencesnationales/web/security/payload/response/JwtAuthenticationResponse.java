package fr.abes.licencesnationales.web.security.payload.response;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String userId;
    private String userSiren;
    private String userNameEtab;
    private boolean isAdmin;


    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, String id, String siren, String nameEtab, boolean isAdmin) {
        this.accessToken = accessToken;
        this.userId = id;
        this.userSiren = siren;
        this.userNameEtab = nameEtab;
        this.isAdmin = isAdmin;
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
    public String getNameEtab() {
        return userNameEtab;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

}
