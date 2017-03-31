package multicall;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MulticallRequest {
    private static final byte REG_USER_REQ  = 32;
    private static final byte EMAIL_OTP_REQ = 33;
    private static final byte SMS_OTP_REQ   = 34;

    private byte[] request;

    private MulticallRequest(byte[] packet) {
        this.request = packet;
    }

    public byte[] getBytes() { return this.request; }

    public static MulticallRequest buildRegUserRequest(String name,
                                                       String email,
                                                       String telnum,
                                                       String regnum)
    {
        assert(name.length() >= 3 && name.length() <= 64);
        assert(email.length() >= 3 && email.length() < 32);
        assert(telnum.length() >= 10 && telnum.length() < 32);
        assert(regnum.length() == 16);

        MulticallRequest req = null;

        try {
            byte[] asciiName = name.getBytes("US-ASCII"); // or "ISO-8859-1"
            byte[] asciiEmail = email.getBytes("US-ASCII");
            byte[] asciitTel = telnum.getBytes("US-ASCII");
            byte[] asciiReg = regnum.getBytes("US-ASCII");

            ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            output.write(REG_USER_REQ);
            output.write(asciiName.length);
            output.write(asciiEmail.length);
            output.write(asciitTel.length);
            output.write(asciiReg);
            output.write(asciiName);
            output.write(asciiEmail);
            output.write(asciitTel);

            req = new MulticallRequest(output.toByteArray());

        } catch (UnsupportedEncodingException encex) {
        } catch (IOException ioex) {
        }

        return req;
    }

    public static void main(String[] args)
    {
        MulticallRequest mcreq = MulticallRequest.buildRegUserRequest(
                "devi prasad",
                "devi.prasad@vlead.ac.in",
                "9867850032",
                "92f72f474c9ecf6f");
        byte[] reqbytes = mcreq.getBytes();
        assert(reqbytes.length ==  4+11+23+10+16);
        assert(reqbytes[0] == MulticallRequest.REG_USER_REQ);
        assert(reqbytes[1] == 11); // "devi prasad"
        assert(reqbytes[2] == 23); // "devi.prasad@vlead.ac.in"
        assert(reqbytes[3] == 10); // "9867850032"

        byte[] bytes = null;

        bytes = Arrays.copyOfRange(reqbytes, 4, 15);
        assert(new String(bytes).equals("devi prasad"));

        bytes = Arrays.copyOfRange(reqbytes, 15, 38);
        assert(new String(bytes).equals("devi.prasad@vlead.ac.in"));

        bytes = Arrays.copyOfRange(reqbytes, 38, 48);
        assert(new String(bytes).equals("9867850032"));

        bytes = Arrays.copyOfRange(reqbytes, 48, reqbytes.length);
        assert(new String(bytes).equals("92f72f474c9ecf6f"));
    }

    public static byte[] getRequestBytes()
    {
        MulticallRequest mcreq = MulticallRequest.buildRegUserRequest(
                "devi prasad",
                "devi.prasad@vlead.ac.in",
                "9867850032",
                "92f72f474c9ecf6f");
        byte[] reqbytes = mcreq.getBytes();
        assert(reqbytes.length ==  4+11+23+10+16);
        assert(reqbytes[0] == MulticallRequest.REG_USER_REQ);
        assert(reqbytes[1] == 11); // "devi prasad"
        assert(reqbytes[2] == 23); // "devi.prasad@vlead.ac.in"
        assert(reqbytes[3] == 10); // "9867850032"

        byte[] bytes = null;

        bytes = Arrays.copyOfRange(reqbytes, 4, 20);
        assert(new String(bytes).equals("92f72f474c9ecf6f"));   

        bytes = Arrays.copyOfRange(reqbytes, 20, 31);
        assert(new String(bytes).equals("devi prasad"));

        bytes = Arrays.copyOfRange(reqbytes, 31, 54);
        assert(new String(bytes).equals("devi.prasad@vlead.ac.in"));

        bytes = Arrays.copyOfRange(reqbytes, 54, 64);
        assert(new String(bytes).equals("9867850032"));

        
        return  mcreq.getBytes();
    }
}
