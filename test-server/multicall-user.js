(function () {
    var mcu_gateway;

    function initControls() {
        var button;
        var wsurl = obtain_websocket_url();

        function mgw_conn_res_handler(event) {
            console.log("mgw connection: " + event.data);
        }

        function user_reg_res_handler(event) {
            console.log("user registration response -- " + event.data);
            res = JSON.parse(event.data)
            if (res.code == 127) {
                console.log("user registration succeeded!");
            } else {
                console.log("user registration failed -- " + res);
            }
        }

        button = document.getElementById('multicall-connect');
        button.addEventListener("click",
            function() {
                mcu_gateway = new WebSocket(wsurl, "multicall-user");
                mcu_gateway.onmessage = mgw_conn_res_handler;
                console.log("connected to gateway");
            }
        );

        button = document.getElementById('multicall-register-user');
        button.addEventListener("click",
            function() {
                if (mcu_gateway && mcu_gateway.readyState == WebSocket.OPEN) {
                    mcu_gateway.send("devi.prasad@vlead.ac.in");
                    mcu_gateway.onmessage = user_reg_res_handler;
                    console.log("user registration requested");
                }
            }
        );

        button = document.getElementById('multicall-gateway-shutdown');
        button.addEventListener("click",
            function() {
            	if (mcu_gateway && mcu_gateway.readyState == WebSocket.OPEN) {
            		mcu_gateway.close(1000, "OK");
            		mcu_gateway = undefined;
                    console.log("closed the multicall gatway");
                }
            }
        );
    }

    document.addEventListener("DOMContentLoaded", function(event) {
        console.log("DOM fully loaded and parsed");

        initControls();
    });

    function obtain_websocket_url()
    {
        var protocol;
        var durl = document.URL;
 
        if (durl.substring(0, 5) == "https") {
            protocol = "wss://";
            durl = durl.substr(8);
        } else {
            protocol = "ws://";
            if (durl.substring(0, 4) == "http") durl = durl.substr(7);
        }
        durl = durl.split('/');
        return (protocol + durl[0]);
    }

})()
