package multicall;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SocketResponseParser {
	public static void parseByte(byte[] bytes) {
        int pointer = 0;
        int startOffset;

        int actionCode = unsignedToBytes(bytes[pointer]);
        pointer++;


        startOffset = pointer + 1;
        pointer = pointer + 16;
        byte[] bytRegNo = getBinary(bytes, startOffset, pointer);
        pointer++;


        int emailRegistered = unsignedToBytes(bytes[pointer]);
        pointer++;

        int  telRegistered = unsignedToBytes(bytes[pointer]);
        pointer++;


        String regNo = null;

        try {

            regNo = new String(bytRegNo, "US-ASCII");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


	    System.out.println("***************************************");
		System.out.println("action_code = " + actionCode);
	    System.out.println("***************************************");
        System.out.println("regNo = " + regNo);
	    System.out.println("***************************************");
		System.out.println("emailRegistered = " + emailRegistered);
	    System.out.println("***************************************");
		System.out.println(" telRegistered = " + telRegistered);

        
        return;
    }

	public static byte[] getBinary(byte[] byt, int startOffset, int endOffset) {
		int count = 0;
		int size = endOffset - startOffset + 1;
		byte[] result = new byte[size];

		for (int i = startOffset; i <= endOffset; i++) {
			result[count] = byt[i];
			count++;
		}
		return result;
	}

	public static int unsignedToBytes(byte b) {
		return b & 0xFF;
	}
}
