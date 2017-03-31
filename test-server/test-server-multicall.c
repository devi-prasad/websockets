
#include "test-server.h"

/* lws-multicall protocol */

int
callback_multicall_user_request(struct lws *wsi, 
                                enum lws_callback_reasons reason,
                                void *user, void *in, size_t len)
{
    //struct per_session_data__lws_multicall *sdata =
    //                (struct per_session_data__lws_multicall *)user;
    unsigned char resbuf[LWS_PRE + 256];
    int res = 0;    

    switch (reason) {
        case LWS_CALLBACK_PROTOCOL_INIT: {
            /** One-time call per protocol, per-vhost using it, so it can
              * do initial setup / allocations etc */
            lwsl_notice("\tMulticall-user: protocol init...\n");
        }
        break;

        case LWS_CALLBACK_CLIENT_FILTER_PRE_ESTABLISH: {
            lwsl_notice("\tMulticall-user: filter client pre-connection establishment...\n");
        }
        break;

        case LWS_CALLBACK_ESTABLISHED: {
            lwsl_notice("\tMulticall-user: websocket connection established...\n");
        }
        break;
    
        case LWS_CALLBACK_RECEIVE: {
            /** data has appeared for this server endpoint from a remote client, 
              * it can be found at 'in' and is 'len' bytes long */
            lwsl_notice("\tMulticall-user: data receive...\n");
            lwsl_notice("\tMulticall-user-data: len: %lu\n", len);
            uint8_t name_len = 0, email_len = 0, telnum_len = 0;
            char regnum[17], name[64], email[64], telnum[64];
            char *str;
            res = -1;
            if (len > 0) {
                uint8_t *p = (uint8_t *)in;

                if (*p == 32 && len > 20) {
                    ++p; name_len = *p;
                    ++p; email_len = *p;
                    ++p; telnum_len = *p;
                    str = (char *)in + 4;
                    strncpy(regnum, str, 16); regnum[16] ='\0';
                    str += 16;
                    strncpy(name, str, name_len); name[name_len] ='\0';
                    str += name_len;
                    strncpy(email, str, email_len); email[email_len] ='\0';
                    str += email_len;
                    strncpy(telnum, str, telnum_len); telnum[telnum_len] ='\0';
                    res = 0;
                }
            }
      
            if (res == 0) {
                unsigned char *p = &resbuf[LWS_PRE];
                int n, m;
                n = sprintf((char *)p, 
                            "{\"code\": 127, \"regnum\": \"%s\", "
                              "\"name\": \"%s\", \"email\": \"%s\", "
                              "\"telnum\": \"%s\""
                             "}", 
                            regnum, name, email, telnum);
                m = lws_write(wsi, p, n, LWS_WRITE_TEXT);
                if (m < n) {
                    lwsl_err("ERROR %d writing to client socket\n", n);
                    res = -1;
                } else {
                    lwsl_notice("\tMulticall-user: n: %d, json: %s\n", n, p);
                }
                /* try to register the user and give back the response */
                /* lws_callback_on_writable(wsi); */
            }
        }
        break;

        case LWS_CALLBACK_SERVER_WRITEABLE: {
            /** If you call lws_callback_on_writable() on a connection, you will
              * get one of these callbacks coming when the connection socket
              * is able to accept another write packet without blocking.
              * If it already was able to take another packet without blocking,
              * you'll get this callback at the next call to the service loop
              * function. */
            lwsl_notice("\tMulticall-user: allow server write...\n");
        }
        break;

        case LWS_CALLBACK_WS_PEER_INITIATED_CLOSE: {
            /** The peer has sent an unsolicited Close WS packet.  in and
              * len are the optional close code. If you return 0 lws will echo 
              * the close and then close the connection. If you return nonzero 
              * lws will just close the connection. */
            lwsl_notice("\tMulticall-user: peer initiated close...\n");
        }
        break;

        case LWS_CALLBACK_CLOSED: {
            /** indicates this protocol won't get used at all after this callback.
              * The vhost is getting destroyed. 
              * We receive two calls. Check the difference in calls.
              */
            lwsl_notice("\tMulticall-user: connection closed!...\n");
        }
        break;
    
        case LWS_CALLBACK_PROTOCOL_DESTROY: {
            lwsl_notice("\tMulticall-user: protocol destroy...\n");
        }
        break;

        default: {
            lwsl_notice("\tMulticall-user: unknown reason: %u\n", reason);
        }
        break;
    }

    return res;
}
