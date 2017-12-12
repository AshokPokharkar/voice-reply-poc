package com.twilio.clicktocall;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twilio.clicktocall.twilio.TwilioLine;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

@Controller
public class CallController {

    private TwilioLine twilioLine;

    @Autowired
    public CallController(TwilioLine twilioLine) {
        this.twilioLine = twilioLine;
    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "call", produces = "text/plain")
    public ResponseEntity<String> call(HttpServletRequest request) {
        String userPhone = request.getParameter("userPhone");
        String salesPhone = request.getParameter("salesPhone");
        System.out.println("CallController :userPhone :  "+ userPhone );
        System.out.println("CallController :salesPhone : "+ salesPhone );
        if (isBlank(userPhone) || isBlank(salesPhone)) {
            return new ResponseEntity<>("Both user and sales phones must be provided", HttpStatus.BAD_REQUEST);
        } else {
            return tryToCallTwilioUsing(userPhone, buildResponseUrl(salesPhone, request));
        }
    }

    private boolean isBlank(String userPhone) {
        return userPhone == null || userPhone.isEmpty();
    }

    private ResponseEntity<String> tryToCallTwilioUsing(String userPhone, String responseUrl) {
        ResponseEntity<String> response = new ResponseEntity<>("Phone call incoming!", HttpStatus.ACCEPTED);

        try {
            twilioLine.call(userPhone, responseUrl);
        } catch (Exception e) {
            String errorMessage = "Problem while processing request: " +
                    e.getMessage();
            response = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    private String buildResponseUrl(String salesPhone, HttpServletRequest request) {
        //String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String host = "http://60ad2f67.ngrok.io";
        try {
            return host + "/connect/" + URLEncoder.encode(salesPhone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CallException(e);
        }
    }
    
    @RequestMapping(value = "handle-key", produces = "text/plain")
    public ResponseEntity<String> service(HttpServletRequest request) throws TwiMLException{
        ResponseEntity<String> response = new ResponseEntity<>("Phone call incoming!", HttpStatus.ACCEPTED);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        String digits = request.getParameter("Digits");
        System.out.println("================================================");
        System.out.println("digits pressed by user is : "+ digits);
        System.out.println("================================================");
        VoiceResponse twiml;
     // Create a TwiML response and add our friendly message.
        VoiceResponse.Builder builder = new VoiceResponse.Builder();

        // Check if the user pressed "1" on their phone
        if (digits != null) {
        		 switch (digits) {
                 case "1":
                     builder.say(new Say.Builder("You pressed 1 - Sales. Good for Sales!").build());
                     break;
                 case "2":
                     builder.say(new Say.Builder("You pressed 2 - for support. We will help you.!").build());
                     break;
                 case "3":
                     builder.say(new Say.Builder("You pressed 3 - for ADMIN. Ahh, you want to talk to Admin. Keep patience.!").build());
                     break;
                 case "4":
                     builder.say(new Say.Builder("You pressed 4 - for Good Bye... Bye byee. ").build());
                     break;
                 default:
                     builder.say(new Say.Builder("Sorry, I don\'t understand that choice. Please pay attention.. and listen carefully..").build());                     
                     break;
             }
       
            // Connect 310 555 1212 to the incoming caller.
            /*Number number = new Number.Builder("+2143008708").build();
            Dial dial = new Dial.Builder().number(number).build();
             */
            // If the above dial failed, say an error message.
        	//Say say = new Say.Builder("The call sucess, you have presseed 1. Thanks a lot.").build();
        	System.out.println("PRESSED digit..>>>>>>>>>>>>>>>>>>>>>>>>");
            twiml = builder.build();
            return new ResponseEntity<>(
            		twiml.toXml(),
                    httpHeaders,
                    HttpStatus.OK
                  );
        } else {
            // If they didn't press 1, redirect them to the TwilioServlet
        	Say say = new Say.Builder("The call failed.. or the remote party hung up. Goodbye.").build();
            System.out.println("FAILED..**********************");	
            twiml = new VoiceResponse.Builder().say(say).build();
            return new ResponseEntity<>(
            		twiml.toXml(),
                    httpHeaders,
                    HttpStatus.OK
                  );
        }
        
    }

}
