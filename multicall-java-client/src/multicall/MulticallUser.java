package multicall;


import java.util.concurrent.TimeUnit;


public class MulticallUser {
    String name, email, telnum;

    public MulticallUser(String name, String email, String telnum) {
    	assert(name.length() > 0 && name.length() < 32);
        this.name = name;
        assert(email.length() > 0 && email.length() < 32);
        this.email = email;
        assert(telnum.length() > 0 && telnum.length() < 16);
        this.telnum = telnum;
    }

    public String getName()   { return this.name; }
    public String getEmail()  { return this.email; }
    public String getTelnum() { return this.telnum; }
}
