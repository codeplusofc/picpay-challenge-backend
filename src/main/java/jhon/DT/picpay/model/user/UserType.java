package jhon.DT.picpay.model.user;

public enum UserType {

    COMMON("Common User", 1),
    MERCHANT("Merchant User", 2);

    private final String description;
    private final int code;

    UserType(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}



