package multicall;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MulticallRequest {
    public static final byte MULTICALL_USER_REG_REQUEST  = 32;
    public static final byte MULTICALL_EMAIL_OTP_REQUEST = 33;
    public static final byte MULTICALL_SMS_OTP_REQ       = 34;

    private byte[] request;

    private MulticallRequest(byte[] request) {
        this.request = request;
    }

    public byte[] getBytes() { return this.request; }

    public static MulticallRequest createUserRegRequest(String name,
                                                        String email,
                                                        String telnum)
    {
        assert(name.length() >= 3 && name.length() <= 64);
        assert(email.length() >= 3 && email.length() < 32);
        assert(telnum.length() >= 10 && telnum.length() < 32);

        byte[] regnum = new byte[16]; // will be initialized to zeros.
        MulticallRequest req = null;

        try {
            byte[] asciiName = name.getBytes("US-ASCII"); // or "ISO-8859-1"
            byte[] asciiEmail = email.getBytes("US-ASCII");
            byte[] asciitTel = telnum.getBytes("US-ASCII");

            ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            output.write(MULTICALL_USER_REG_REQUEST);
            output.write(asciiName.length);
            output.write(asciiEmail.length);
            output.write(asciitTel.length);
            output.write(regnum);
            output.write(asciiName);
            output.write(asciiEmail);
            output.write(asciitTel);

            req = new MulticallRequest(output.toByteArray());

        } catch (UnsupportedEncodingException encex) {
        } catch (IOException ioex) {
        }

        return req;
    }
}
