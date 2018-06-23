package me.scottleedavis.mattermostremind.parser;

import org.springframework.stereotype.Service;

@Service
public class ReminderRequest {

    public String findTarget(String text) throws Exception {

        String[] parts = text.split("\\s+");

        if (parts[0].equals("") )
            throw new Exception("Empty target");

        if (parts[0].equals("me") )
            return parts[0];

        if (parts[0].equals("list") )
            return parts[0];

        if (parts[0].equals("help") )
            return parts[0];

        if (parts[0].charAt(0) == '@')
            return parts[0];

        if (parts[0].charAt(0) == '#')
            return parts[0];

        throw new Exception("Unrecognized target");

    }

    public String findWhen(String text) throws Exception {
        //find in, at, on, every starting from the back
        String whenString = "";
        int subString = -1;

        subString = text.lastIndexOf("at");
        if( subString > -1 ) {
            whenString = text.substring(subString);
        }


        return "foo";
    }
}
