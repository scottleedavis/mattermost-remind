package me.scottleedavis.mattermostremind.reminders;

import org.springframework.stereotype.Component;

@Component
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
        int subString = -1;

        subString = text.indexOf(" at ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" in ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" on ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" every ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        throw new Exception("No when found");

    }
}
