package multicall;

class MulticallUserRegTest {
    final static String LOCALHOST_MOCK_WS_URL = "ws://127.0.0.1:12345";
    final static String MOCK_WS_SUBPROTOCOL   = "multicall-user";

    private static void testTrivialUserReg() {
        UserRegController controller = new UserRegController(LOCALHOST_MOCK_WS_URL,
                                                             MOCK_WS_SUBPROTOCOL);
        MulticallUser dp = new MulticallUser("devi prasad",
                                             "devi.prasad@vlead.ac.in",
                                             "9867850032");
        controller.setMulticallUser(dp);
        controller.setResponseObserver(
            new ResponseObserver() {
                public void onResponse(MulticallResponse response) {
                    assert(response.getCode() == 
                	           MulticallResponse.MULTICALL_USER_REG_RESPONSE);
                    assert(response.isEmailRegistered() == false);
                    System.out.println("user registration succeeded!");
                    //controller.disconnectWithGateway();
                }
            });
        controller.connectWithGateway();
    }

    public static void main(String args[]) {
        testTrivialUserReg();
    }
}