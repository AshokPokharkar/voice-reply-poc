package com.twilio.clicktocall.twilio;

import com.twilio.twiml.Dial;
import com.twilio.twiml.Gather;
import com.twilio.twiml.Method;
import com.twilio.twiml.Number;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

public class TwiMLUtil {

    public static String buildVoiceResponseAndDial(String say, String salesPhone) throws TwiMLException {
        Number number = new Number.Builder(salesPhone).build();
        return new VoiceResponse.Builder()
                .say(new Say.Builder(say).build())
                .dial(new Dial.Builder().number(number).build())
                .build()
                .toXml();
    }
    
    /*public static String buildVoiceResponseAndGetInput(String say, String salesPhone) throws TwiMLException {
        Number number = new Number.Builder(salesPhone).build();
        return new VoiceResponse.Builder()
                .say(new Say.Builder(say).build())
                .gather(new Gather.Builder()
                        .action("/handle-key")
                        .method(Method.POST)
                        .numDigits(1)
                        .say(new Say
                                .Builder("To speak to a real monkey, press 1. Press any other key to start over.")
                                .build()).build()).build()
                .toXml();
    }*/
    
    public static String buildVoiceResponseAndGetInput(String say, String salesPhone) throws TwiMLException {
        Number number = new Number.Builder(salesPhone).build();
        return new VoiceResponse.Builder()
                .say(new Say.Builder(say).build())
                .gather(new Gather.Builder()
                        .action("/handle-key")
                        .method(Method.POST)
                        .numDigits(1)
                        .build()).build()
                .toXml();
    }
}
