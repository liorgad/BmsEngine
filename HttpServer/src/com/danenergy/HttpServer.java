package com.danenergy;

import com.danenergy.common.Configuration;
import com.danenergy.common.EventBusMessages.ClientRequest;
import com.danenergy.common.EventBusMessages.ClusterUpdatedMessage;
import com.danenergy.common.EventBusMessages.ResponseToClientRequest;
import com.danenergy.common.IPlugin;
import com.danenergy.common.ResourcesGuiceModule;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import fi.iki.elonen.NanoHTTPD;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class HttpServer extends NanoHTTPD implements IPlugin {

    //logging
    //final static Logger logger = Logger.getLogger(HttpServer.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    private String response;
    private EventBus eventBus;
    private Configuration config;
    private Semaphore responseWaitSignal;

    private String responseToClient;

    @Inject
    public HttpServer(EventBus eventBus,Configuration config)
    {
        super(config.getHttpServerListenIp(),config.getHttpServerListenPort());
        try {

            this.responseWaitSignal = new Semaphore(-1);
            this.config = config;
            this.eventBus = eventBus;

            this.eventBus.register(this);
        }
        catch(Exception e)
        {
            logger.error(e.getStackTrace(), e);
        }
    }


    public HttpServer()
    {
        super(8080);
        this.responseWaitSignal = new Semaphore(-1);
    }

    public HttpServer(String ip,int port)
    {
        super(ip,port);
        this.responseWaitSignal = new Semaphore(-1);
    }

    public synchronized String getResponseToClient() {
        return responseToClient;
    }

    public synchronized void setResponseToClient(String responseToClient) {
        this.responseToClient = responseToClient;
    }

    public synchronized String getResponse() {
        return response;
    }

    public synchronized void setResponse(String response) {
        this.response = response;
    }

    public static void main(String[] args) {
        try {
            Injector guice = Guice.createInjector(new ResourcesGuiceModule());
            HttpServer server = guice.getInstance(HttpServer.class);
            server.start();

            server.setResponse("empty");
            System.out.println("Http Server start listening...");

            try
            {
                System.in.read();
            }
            catch (Exception e)
            {

            }
            finally {
                server.Stop();
            }

        }
        catch(IOException e)
        {
            System.out.println(e);
        }

    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();

//        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
//            try {
//                session.parseBody(files);
//
//                // get the POST body
//                String postBody = session.getQueryParameterString();
//                // or you can access the POST request's parameters
//
//                List<String> postParameter = session.getParameters().get("parameter");
//
//                // return new Response(postBody); // Or postParameter.
//
//            } catch (IOException ioe) {
//                //return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
//            } catch (ResponseException re) {
//                //return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
//            }
//        }
        //else if(Method.GET.equals(method))
        if(Method.GET.equals(method))
        {
            String queryString = "Empty";
            try {
                queryString = session.getQueryParameterString();
                Map map = session.getParameters();

                if(map.containsKey("name"))
                {
                    String nameVal = ((List<String>)map.get("name")).get(0);

                    eventBus.post(new ClientRequest(nameVal));

                    //responseWaitSignal.acquire();

                    //String msg = "<html><body><h1></h1>\n" + getResponseToClient() + "</body></html>\n";
                    String msg = getResponseToClient();
//        Map<String, List<String>> parms = session.getParameters();
//        if (parms.get("username") == null) {
//            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//        } else {
//            msg += "<p>Hello, " + parms.get("username").get(0) + "!</p>";
//        }
                    return newFixedLengthResponse(Response.Status.OK,"application/json",msg);
                }

            }
            catch(Exception e)
            {
                logger.error(e);
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT,"Parameter sent: " + queryString + " " + e);
            }
        }

        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT,"Received method :" + method);
    }

    @Override
    public void Start() {
        try
        {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            String listenIp  = config.getHttpServerListenIp();
            int listenPort = config.getHttpServerListenPort();
            logger.info("Running! Point your browsers to http://"+listenIp+":"+listenPort +"/ \n");
        }
        catch (IOException e)
        {
            System.err.println("\nError running ! IOException : " + e.getMessage() + "\n" + e.getStackTrace());
        }

    }

    @Override
    public void Stop() {
        stop();
    }

    @Override
    public void Dispose() {
        this.closeAllConnections();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Subscribe
    public void handleResponse(ClusterUpdatedMessage msg)
    {
        setResponse(msg.message);
    }

    @Subscribe
    public void handleResponseToClientRequest(ResponseToClientRequest response)
    {
        setResponseToClient(response.getResponse());
        //responseWaitSignal.release();
    }
}
