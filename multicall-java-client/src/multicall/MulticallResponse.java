package multicall;

import java.util.Arrays;

public class MulticallResponse {
    public static final int MULTICALL_USER_REG_RESPONSE = 127;
    
	Byte code;
    String regnum;
    boolean email_registered;
    boolean telnum_registered;

    private MulticallResponse(Byte code, String regnum, boolean er, boolean tr) {
    	this.code = code;
        this.regnum = regnum;
        this.email_registered = er;
        this.telnum_registered = tr;
    }
    
    public byte getCode() { return this.code; }
    public String getRegistrationNumber() { return this.regnum; }
    public boolean isEmailRegistered()    { return this.email_registered; }
    public boolean isTelephoneNumRegistered() { return this.telnum_registered; }

    public static MulticallResponse parse(byte[] payload, int offset, int len) {
    	MulticallResponse response = null;

        assert(payload != null && len > 0 && offset >= 0 && offset < len);
        if (payload != null && len > 0 && offset >= 0 && offset < len) {
            byte code = payload[offset];
            switch (code) {
                case MulticallResponse.MULTICALL_USER_REG_RESPONSE:
                    response = parseUserRegResponse(payload, offset, len);
                    break;
                default:
                    assert(false);
            }
        }

        return response;
    }

    private static MulticallResponse parseUserRegResponse(byte[] payload, int offset, int len) {
        assert(len > 0 && len >= 19);
        byte code = payload[offset];
        assert(code == MulticallResponse.MULTICALL_USER_REG_RESPONSE);
        String regnum = new String(Arrays.copyOfRange(payload, offset + 1, 16));
        boolean email_registered = (payload[offset + 17] > 0);
        boolean telnum_registered = (payload[offset + 18] > 0);

        System.out.println("status code: " + code);
        System.out.println("registration num: " + regnum);
        System.out.println("email registered: " + email_registered);
        System.out.println("registration num: " + telnum_registered);

        return new MulticallResponse(code, regnum, email_registered, telnum_registered);
    }
}
